package io.github.shrhang.export_language_keys_for_compasses;

import io.github.shrhang.export_language_keys_for_compasses.config.ConfigHandler;
import io.github.shrhang.export_language_keys_for_compasses.network.ExportNetwork;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ELKFCompasses.MODID)
public class ELKFCompasses {

    public static final String MODID = "export_language_keys_for_compasses";

    public ELKFCompasses(FMLJavaModLoadingContext context) {
        context.registerConfig(ModConfig.Type.CLIENT, ConfigHandler.CLIENT_SPEC);
        ExportNetwork.register();
    }
}
