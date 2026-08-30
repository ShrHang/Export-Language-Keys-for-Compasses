package io.github.shrhang.export_language_keys_for_compasses.export;

import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;

import java.util.Locale;

public final class LangKeyFormatter {

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
}
