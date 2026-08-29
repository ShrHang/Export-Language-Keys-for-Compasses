package io.github.shrhang.export_language_keys_for_compasses.export;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public final class LangKeyWriter {

    public static final String OUTPUT_DIRECTORY_NAME = "export_language_keys_for_compasses";

    private static final DateTimeFormatter FILE_TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private LangKeyWriter() {
    }

    public static String makeLangKey(String type, ResourceLocation id) {
        return Util.makeDescriptionId(type, id);
    }

    public static String humanize(ResourceLocation id) {
        String[] words = id.getPath().replace('/', ' ').replace('_', ' ').split(" +");
        StringBuilder fallback = new StringBuilder();

        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }
            if (!fallback.isEmpty()) {
                fallback.append(' ');
            }
            fallback.append(word.substring(0, 1).toUpperCase(Locale.ROOT));
            fallback.append(word.substring(1));
        }

        return fallback.toString();
    }

    public static Path createOutputPath(Path gameDirectory, String language, String target, String mode) throws IOException {
        Path outputDirectory = gameDirectory.resolve(OUTPUT_DIRECTORY_NAME);
        Files.createDirectories(outputDirectory);
        String timestamp = FILE_TIMESTAMP.format(LocalDateTime.now());
        return outputDirectory.resolve(language + "-" + target + "-" + mode + "-" + timestamp + ".json");
    }

    public static void writeJson(Path output, Map<String, String> translations) throws IOException {
        Map<String, String> sortedTranslations = new TreeMap<>(translations);

        JsonObject json = new JsonObject();
        sortedTranslations.forEach(json::addProperty);

        try (Writer writer = Files.newBufferedWriter(output, StandardCharsets.UTF_8)) {
            GSON.toJson(json, writer);
        }
    }
}
