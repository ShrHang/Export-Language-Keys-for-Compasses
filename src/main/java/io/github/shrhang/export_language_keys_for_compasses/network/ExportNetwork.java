package io.github.shrhang.export_language_keys_for_compasses.network;

import io.github.shrhang.export_language_keys_for_compasses.client.ELKFCompassesClient;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class ExportNetwork {

    private static final String PROTOCOL_VERSION = "2";

    public static void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        event.registrar(PROTOCOL_VERSION)
                .optional()
                .playToClient(
                        ClientsideExportPacket.TYPE,
                        ClientsideExportPacket.STREAM_CODEC,
                        ELKFCompassesClient::handleClientsideExport
                );
    }

    public static boolean isClientInstalled(ServerPlayer player) {
        return player.connection.hasChannel(ClientsideExportPacket.TYPE);
    }

    public static void sendExport(ServerPlayer player, ClientsideExportPacket packet) {
        PacketDistributor.sendToPlayer(player, packet);
    }
}
