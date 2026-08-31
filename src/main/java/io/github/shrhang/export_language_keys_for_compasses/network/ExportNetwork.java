package io.github.shrhang.export_language_keys_for_compasses.network;

import io.github.shrhang.export_language_keys_for_compasses.ELKFCompasses;
import net.minecraft.network.Connection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

public final class ExportNetwork {

    private static final int PROTOCOL_VERSION = 2;
    private static final SimpleChannel CHANNEL = ChannelBuilder
            .named(ResourceLocation.fromNamespaceAndPath(ELKFCompasses.MODID, "main"))
            .networkProtocolVersion(PROTOCOL_VERSION)
            .optional()
            .simpleChannel();

    private static boolean registered;

    public static void register() {
        if (registered) {
            return;
        }

        CHANNEL.messageBuilder(ClientsideExportPacket.class, 0, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ClientsideExportPacket::encode)
                .decoder(ClientsideExportPacket::decode)
                .consumerMainThread(ClientsideExportPacket::handle)
                .add();
        registered = true;
    }

    public static boolean isClientInstalled(ServerPlayer player) {
        Connection connection = player.connection.getConnection();
        if (connection.isMemoryConnection()) {
            return true;
        }

        return CHANNEL.isRemotePresent(connection);
    }

    public static void sendExport(ServerPlayer player, ClientsideExportPacket packet) {
        CHANNEL.send(packet, PacketDistributor.PLAYER.with(player));
    }
}
