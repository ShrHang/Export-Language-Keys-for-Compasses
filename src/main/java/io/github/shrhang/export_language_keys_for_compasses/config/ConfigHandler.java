package io.github.shrhang.export_language_keys_for_compasses.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class ConfigHandler {

    public static final Client CLIENT;
    public static final ModConfigSpec CLIENT_SPEC;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        CLIENT = new Client(builder);
        CLIENT_SPEC = builder.build();
    }

    public static final class Client {

        public final ModConfigSpec.ConfigValue<String> defaultLanguage;

        private Client(ModConfigSpec.Builder builder) {
            defaultLanguage = builder
                    .comment(
                            "The language used when the export command omits its language argument.",
                            "Use 'current' to follow the language selected in the Minecraft client."
                    )
                    .define("defaultLanguage", "current", ConfigHandler::isValidLanguageSelector);
        }
    }

    private static boolean isValidLanguageSelector(Object value) {
        return value instanceof String language
                && !language.isBlank()
                && language.matches("[A-Za-z0-9_-]+");
    }
}
