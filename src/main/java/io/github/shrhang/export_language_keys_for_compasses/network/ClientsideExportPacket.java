package io.github.shrhang.export_language_keys_for_compasses.network;

import io.github.shrhang.export_language_keys_for_compasses.client.ClientExportHandler;
import io.github.shrhang.export_language_keys_for_compasses.export.EntryKind;
import io.github.shrhang.export_language_keys_for_compasses.export.LangKeyEntry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public record ClientsideExportPacket(String target, String mode, String language, List<LangKeyEntry> entries) {

    private static final int MAX_ENTRIES = 100_000;

    public ClientsideExportPacket {
        entries = List.copyOf(entries);
    }

    public static ClientsideExportPacket decode(FriendlyByteBuf buffer) {
        String target = buffer.readUtf(32);
        String mode = buffer.readUtf(32);
        String language = buffer.readUtf(32);
        int size = buffer.readVarInt();
        if (size < 0 || size > MAX_ENTRIES) {
            throw new IllegalArgumentException("Invalid language key entry count: " + size);
        }

        List<LangKeyEntry> entries = new ArrayList<>(size);
        for (int index = 0; index < size; index++) {
            String key = buffer.readUtf();
            String fallback = buffer.readUtf();
            EntryKind kind = buffer.readEnum(EntryKind.class);
            ResourceLocation sourceId = buffer.readResourceLocation();
            String sourceModId = buffer.readUtf();
            entries.add(new LangKeyEntry(key, fallback, kind, sourceId, sourceModId));
        }

        return new ClientsideExportPacket(target, mode, language, entries);
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(target, 32);
        buffer.writeUtf(mode, 32);
        buffer.writeUtf(language, 32);
        buffer.writeVarInt(entries.size());

        for (LangKeyEntry entry : entries) {
            buffer.writeUtf(entry.key());
            buffer.writeUtf(entry.fallback());
            buffer.writeEnum(entry.kind());
            buffer.writeResourceLocation(entry.sourceId());
            buffer.writeUtf(entry.sourceModId());
        }
    }

    public static void handle(ClientsideExportPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(
                Dist.CLIENT,
                () -> () -> ClientExportHandler.handle(packet)
        ));
        context.setPacketHandled(true);
    }
}
