package io.github.shrhang.export_language_keys_for_compasses.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.shrhang.export_language_keys_for_compasses.ELKFCompasses;
import io.github.shrhang.export_language_keys_for_compasses.compat.ExplorersCompassCollector;
import io.github.shrhang.export_language_keys_for_compasses.compat.NaturesCompassCollector;
import io.github.shrhang.export_language_keys_for_compasses.export.LangKeyEntry;
import io.github.shrhang.export_language_keys_for_compasses.network.ClientboundExportPacket;
import io.github.shrhang.export_language_keys_for_compasses.network.ExportNetwork;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = ELKFCompasses.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ExportLanguageKeysCommand {

    private static final String NATURES_COMPASS_MOD_ID = "naturescompass";
    private static final String EXPLORERS_COMPASS_MOD_ID = "explorerscompass";

    private ExportLanguageKeysCommand() {
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        LiteralArgumentBuilder<CommandSourceStack> export = Commands.literal("export")
                .then(createTarget("biome"))
                .then(createTarget("structure"))
                .then(createTarget("all"));

        event.getDispatcher().register(
                Commands.literal("elkfc")
                        .requires(source -> source.hasPermission(2))
                        .then(export)
        );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> createTarget(String target) {
        return Commands.literal(target)
                .then(createMode(target, "all"))
                .then(createMode(target, "missing"));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> createMode(String target, String mode) {
        return Commands.literal(mode)
                .executes(context -> export(context.getSource(), target, mode, ""))
                .then(Commands.argument("language", StringArgumentType.word())
                        .executes(context -> export(
                                context.getSource(),
                                target,
                                mode,
                                StringArgumentType.getString(context, "language")
                        )));
    }

    private static int export(CommandSourceStack source, String target, String mode, String language) throws CommandSyntaxException {
        if (!language.isEmpty() && (language.length() > 32 || !language.matches("[A-Za-z0-9_-]+"))) {
            source.sendFailure(Component.literal("Invalid language selector: " + language));
            return 0;
        }

        ServerPlayer player = source.getPlayerOrException();
        if (!ExportNetwork.isPresent(player.connection.connection)) {
            source.sendFailure(Component.literal("The executing player's client does not have this mod's export channel."));
            return 0;
        }

        ServerLevel level = source.getLevel();
        List<LangKeyEntry> entries = collectEntries(source, level, target);
        if (entries.isEmpty()) {
            source.sendFailure(Component.literal("No language keys were collected for target '" + target + "'."));
            return 0;
        }

        ExportNetwork.sendTo(player, new ClientboundExportPacket(target, mode, language, entries));
        source.sendSuccess(
                () -> Component.literal("Sent " + entries.size() + " candidate language keys to the client for export."),
                false
        );
        return entries.size();
    }

    private static List<LangKeyEntry> collectEntries(CommandSourceStack source, ServerLevel level, String target) {
        List<LangKeyEntry> entries = new ArrayList<>();

        if ("biome".equals(target) || "all".equals(target)) {
            if (ModList.get().isLoaded(NATURES_COMPASS_MOD_ID)) {
                entries.addAll(NaturesCompassCollector.collect(level));
            } else if ("biome".equals(target)) {
                source.sendFailure(Component.literal("Nature's Compass is not loaded; biome export is unavailable."));
            }
        }

        if ("structure".equals(target) || "all".equals(target)) {
            if (ModList.get().isLoaded(EXPLORERS_COMPASS_MOD_ID)) {
                entries.addAll(ExplorersCompassCollector.collect(level));
            } else if ("structure".equals(target)) {
                source.sendFailure(Component.literal("Explorer's Compass is not loaded; structure export is unavailable."));
            }
        }

        return entries;
    }
}
