package io.github.shrhang.export_language_keys_for_compasses.network;

import io.github.shrhang.export_language_keys_for_compasses.ELKFCompasses;
import io.github.shrhang.export_language_keys_for_compasses.export.EntryKind;
import io.github.shrhang.export_language_keys_for_compasses.export.LangKeyEntry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public record ClientsideExportPacket(String target, String mode, String language, List<LangKeyEntry> entries)
        implements CustomPacketPayload {

    private static final int MAX_ENTRIES = 100_000;
    public static final Type<ClientsideExportPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(ELKFCompasses.MODID, "clientside_export")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientsideExportPacket> STREAM_CODEC =
            StreamCodec.ofMember(ClientsideExportPacket::encode, ClientsideExportPacket::decode);

    public ClientsideExportPacket {
        entries = List.copyOf(entries);
    }

    public static ClientsideExportPacket decode(RegistryFriendlyByteBuf buffer) {
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

    private void encode(RegistryFriendlyByteBuf buffer) {
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

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
