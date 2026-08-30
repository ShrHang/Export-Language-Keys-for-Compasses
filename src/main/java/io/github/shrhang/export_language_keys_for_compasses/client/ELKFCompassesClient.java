package io.github.shrhang.export_language_keys_for_compasses.client;

import io.github.shrhang.export_language_keys_for_compasses.ELKFCompasses;
import io.github.shrhang.export_language_keys_for_compasses.config.ConfigHandler;
import io.github.shrhang.export_language_keys_for_compasses.network.ClientsideExportPacket;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@Mod(value = ELKFCompasses.MODID, dist = Dist.CLIENT)
public final class ELKFCompassesClient {

    public ELKFCompassesClient(ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, ConfigHandler.CLIENT_SPEC);
    }

    public static void handleClientsideExport(ClientsideExportPacket packet, IPayloadContext context) {
        ClientExportHandler.handle(packet);
    }
}
