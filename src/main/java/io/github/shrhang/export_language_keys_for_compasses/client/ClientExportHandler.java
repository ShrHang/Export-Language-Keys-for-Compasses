package io.github.shrhang.export_language_keys_for_compasses.client;

import com.mojang.logging.LogUtils;
import io.github.shrhang.export_language_keys_for_compasses.config.ConfigHandler;
import io.github.shrhang.export_language_keys_for_compasses.export.LangKeyEntry;
import io.github.shrhang.export_language_keys_for_compasses.network.ClientsideExportPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

@OnlyIn(Dist.CLIENT)
public final class ClientExportHandler {

    private static final Logger LOGGER = LogUtils.getLogger();

    private ClientExportHandler() {
    }

    public static void handle(ClientsideExportPacket packet) {
        Minecraft minecraft = Minecraft.getInstance();
        String language = resolveLanguage(minecraft, packet.language());
        if (language == null) {
            return;
        }

        ClientLanguage requestedLanguage = ClientLanguage.loadFrom(
                minecraft.getResourceManager(),
                List.of(language),
                false
        );
        ClientLanguage defaultLanguage = language.equals(LanguageManager.DEFAULT_LANGUAGE_CODE)
                ? requestedLanguage
                : ClientLanguage.loadFrom(
                        minecraft.getResourceManager(),
                        List.of(LanguageManager.DEFAULT_LANGUAGE_CODE),
                        false
                );

        Map<String, String> translations = resolveTranslations(
                packet.entries(),
                packet.mode(),
                requestedLanguage,
                defaultLanguage
        );

        try {
            Path output = ExportFileWriter.write(
                    FMLPaths.GAMEDIR.get(),
                    language,
                    packet.target(),
                    packet.mode(),
                    translations
            );
            sendMessage(Component.translatable(
                    "message.export_language_keys_for_compasses.export.success",
                    translations.size(),
                    language,
                    createFileLink(output)
            ));
        } catch (IOException exception) {
            LOGGER.error("Failed to export language keys", exception);
            sendMessage(Component.translatable(
                    "message.export_language_keys_for_compasses.export.failure",
                    exception.getMessage()
            ));
        }
    }

    private static Map<String, String> resolveTranslations(
            List<LangKeyEntry> entries,
            String mode,
            ClientLanguage requestedLanguage,
            ClientLanguage defaultLanguage
    ) {
        Map<String, String> translations = new TreeMap<>();
        boolean missingOnly = "missing".equals(mode);

        for (LangKeyEntry entry : entries) {
            if (missingOnly && requestedLanguage.has(entry.key())) {
                continue;
            }

            String value = requestedLanguage.getOrDefault(
                    entry.key(),
                    defaultLanguage.getOrDefault(entry.key(), entry.fallback())
            );
            translations.putIfAbsent(entry.key(), value);
        }

        return translations;
    }

    private static String resolveLanguage(Minecraft minecraft, String commandLanguage) {
        String language = commandLanguage.isBlank()
                ? ConfigHandler.CLIENT.defaultLanguage.get()
                : commandLanguage;

        if ("current".equalsIgnoreCase(language)) {
            return minecraft.getLanguageManager().getSelected();
        }

        String normalizedLanguage = language.toLowerCase(Locale.ROOT);
        if (minecraft.getLanguageManager().getLanguage(normalizedLanguage) == null) {
            sendMessage(Component.translatable(
                    "message.export_language_keys_for_compasses.language.unavailable",
                    language
            ));
            return null;
        }

        return normalizedLanguage;
    }

    private static Component createFileLink(Path output) {
        Path absoluteOutput = output.toAbsolutePath().normalize();
        return Component.literal(output.getFileName().toString())
                .withStyle(ChatFormatting.UNDERLINE)
                .withStyle(style -> style.withClickEvent(new ClickEvent(
                        ClickEvent.Action.OPEN_FILE,
                        absoluteOutput.toString()
                )));
    }

    private static void sendMessage(Component message) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null) {
            minecraft.player.displayClientMessage(message, false);
        }
    }
}
