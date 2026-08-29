package io.github.shrhang.export_language_keys_for_compasses.compat;

import com.chaosthedude.naturescompass.util.BiomeUtils;
import io.github.shrhang.export_language_keys_for_compasses.export.EntryKind;
import io.github.shrhang.export_language_keys_for_compasses.export.LangKeyEntry;
import io.github.shrhang.export_language_keys_for_compasses.export.LangKeyWriter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

import java.util.ArrayList;
import java.util.List;

public final class NaturesCompassCollector {

    private NaturesCompassCollector() {
    }

    public static List<LangKeyEntry> collect(ServerLevel level) {
        List<LangKeyEntry> entries = new ArrayList<>();

        for (ResourceLocation biomeId : BiomeUtils.getAllowedBiomeKeys(level)) {
            entries.add(new LangKeyEntry(
                    LangKeyWriter.makeLangKey("biome", biomeId),
                    LangKeyWriter.humanize(biomeId),
                    EntryKind.BIOME,
                    biomeId,
                    biomeId.getNamespace()
            ));
        }

        return entries;
    }
}
