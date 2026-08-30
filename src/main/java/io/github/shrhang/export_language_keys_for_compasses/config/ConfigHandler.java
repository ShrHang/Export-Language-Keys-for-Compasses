package io.github.shrhang.export_language_keys_for_compasses.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class ConfigHandler {

    public static final Client CLIENT;
    public static final ForgeConfigSpec CLIENT_SPEC;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        CLIENT = new Client(builder);
        CLIENT_SPEC = builder.build();
    }

    public static final class Client {

        public final ForgeConfigSpec.ConfigValue<String> defaultLanguage;

        private Client(ForgeConfigSpec.Builder builder) {
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
