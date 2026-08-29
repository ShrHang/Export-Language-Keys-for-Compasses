package io.github.shrhang.export_language_keys_for_compasses.network;

import io.github.shrhang.export_language_keys_for_compasses.ELKFCompasses;
import net.minecraft.network.Connection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public final class ExportNetwork {

    private static final String PROTOCOL_VERSION = "2";
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(ELKFCompasses.MODID, "main"),
            () -> PROTOCOL_VERSION,
            NetworkRegistry.acceptMissingOr(PROTOCOL_VERSION),
            NetworkRegistry.acceptMissingOr(PROTOCOL_VERSION)
    );

    private static boolean registered;

    private ExportNetwork() {
    }

    public static void register() {
        if (registered) {
            return;
        }

        CHANNEL.registerMessage(
                0,
                ClientboundExportPacket.class,
                ClientboundExportPacket::encode,
                ClientboundExportPacket::decode,
                ClientboundExportPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        );
        registered = true;
    }

    public static boolean isPresent(Connection connection) {
        return CHANNEL.isRemotePresent(connection);
    }

    public static void sendTo(ServerPlayer player, ClientboundExportPacket packet) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }
}
