package io.github.shrhang.export_language_keys_for_compasses.network;

import io.github.shrhang.export_language_keys_for_compasses.ELKFCompasses;
import net.minecraft.network.Connection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.ConnectionData;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkHooks;
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

    public static void register() {
        if (registered) {
            return;
        }

        CHANNEL.registerMessage(
                0,
                ClientsideExportPacket.class,
                ClientsideExportPacket::encode,
                ClientsideExportPacket::decode,
                ClientsideExportPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        );
        registered = true;
    }

    public static boolean isClientInstalled(ServerPlayer player) {
        Connection connection = player.connection.connection;
        if (connection.isMemoryConnection()) {
            return true;
        }

        ConnectionData connectionData = NetworkHooks.getConnectionData(connection);
        return connectionData != null && connectionData.getModList().contains(ELKFCompasses.MODID);
    }

    public static void sendExport(ServerPlayer player, ClientsideExportPacket packet) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }
}
