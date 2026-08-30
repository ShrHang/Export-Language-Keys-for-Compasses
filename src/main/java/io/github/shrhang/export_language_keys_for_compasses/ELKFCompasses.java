package io.github.shrhang.export_language_keys_for_compasses;

import io.github.shrhang.export_language_keys_for_compasses.command.ExportLanguageKeysCommand;
import io.github.shrhang.export_language_keys_for_compasses.network.ExportNetwork;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(ELKFCompasses.MODID)
public class ELKFCompasses {

    public static final String MODID = "export_language_keys_for_compasses";

    public ELKFCompasses(IEventBus modEventBus) {
        modEventBus.addListener(ExportNetwork::registerPayloadHandlers);
        NeoForge.EVENT_BUS.addListener(ExportLanguageKeysCommand::registerCommands);
    }
}
