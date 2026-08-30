package io.github.shrhang.export_language_keys_for_compasses.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TreeMap;

public final class ExportFileWriter {

    public static final String OUTPUT_DIRECTORY_NAME = "export_language_keys_for_compasses";

    private static final DateTimeFormatter FILE_TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static Path write(
            Path gameDirectory,
            String language,
            String target,
            String mode,
            Map<String, String> translations
    ) throws IOException {
        Path outputDirectory = gameDirectory.resolve(OUTPUT_DIRECTORY_NAME);
        Files.createDirectories(outputDirectory);
        String timestamp = FILE_TIMESTAMP.format(LocalDateTime.now());
        Path output = outputDirectory.resolve(language + "-" + target + "-" + mode + "-" + timestamp + ".json");

        JsonObject json = new JsonObject();
        new TreeMap<>(translations).forEach(json::addProperty);
        try (Writer writer = Files.newBufferedWriter(output, StandardCharsets.UTF_8)) {
            GSON.toJson(json, writer);
        }

        return output;
    }
}
