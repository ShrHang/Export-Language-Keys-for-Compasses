package io.github.shrhang.export_language_keys_for_compasses.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.shrhang.export_language_keys_for_compasses.ELKFCompasses;
import io.github.shrhang.export_language_keys_for_compasses.collector.LanguageKeyCollector;
import io.github.shrhang.export_language_keys_for_compasses.export.LangKeyEntry;
import io.github.shrhang.export_language_keys_for_compasses.network.ClientsideExportPacket;
import io.github.shrhang.export_language_keys_for_compasses.network.ExportNetwork;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.regex.Pattern;

@Mod.EventBusSubscriber(modid = ELKFCompasses.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ExportLanguageKeysCommand {

    private static final Pattern LANGUAGE_SELECTOR = Pattern.compile("[A-Za-z0-9_-]+");

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        LiteralArgumentBuilder<CommandSourceStack> export = Commands.literal("export")
                .then(createTarget("biome"))
                .then(createTarget("structure"))
                .then(createTarget("all"));

        event.getDispatcher().register(
                Commands.literal("elkfc")
                        .requires(source -> source.getEntity() instanceof ServerPlayer)
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
        if (!language.isEmpty() && (language.length() > 32 || !LANGUAGE_SELECTOR.matcher(language).matches())) {
            source.sendFailure(Component.translatableWithFallback(
                    "command.export_language_keys_for_compasses.language.invalid",
                    "Invalid language selector: %s",
                    language
            ));
            return 0;
        }

        ServerPlayer player = source.getPlayerOrException();
        if (!ExportNetwork.isClientInstalled(player)) {
            source.sendFailure(Component.literal(
                    "The executing player's client does not have mod '" + ELKFCompasses.MODID + "' installed."
            ));
            return 0;
        }

        ServerLevel level = source.getLevel();
        List<LangKeyEntry> entries = LanguageKeyCollector.collect(level, target);
        if (entries.isEmpty()) {
            source.sendFailure(Component.translatableWithFallback(
                    "command.export_language_keys_for_compasses.entries.empty",
                    "No language keys were collected for target '%s'.",
                    target
            ));
            return 0;
        }

        ExportNetwork.sendExport(player, new ClientsideExportPacket(target, mode, language, entries));
        return entries.size();
    }
}
