package io.github.shrhang.export_language_keys_for_compasses.compat;

import com.chaosthedude.explorerscompass.util.StructureUtils;
import io.github.shrhang.export_language_keys_for_compasses.export.EntryKind;
import io.github.shrhang.export_language_keys_for_compasses.export.LangKeyEntry;
import io.github.shrhang.export_language_keys_for_compasses.export.LangKeyWriter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ExplorersCompassCollector {

    private ExplorersCompassCollector() {
    }

    public static List<LangKeyEntry> collect(ServerLevel level) {
        List<ResourceLocation> structureIds = StructureUtils.getAllowedStructureKeys(level);
        Map<ResourceLocation, ResourceLocation> structureGroups = StructureUtils.getStructureKeysToTypeKeys(level);
        List<LangKeyEntry> entries = new ArrayList<>();

        for (ResourceLocation structureId : structureIds) {
            entries.add(createEntry(structureId, EntryKind.STRUCTURE));

            ResourceLocation groupId = structureGroups.get(structureId);
            if (groupId != null) {
                entries.add(createEntry(groupId, EntryKind.STRUCTURE_GROUP));
            }
        }

        return entries;
    }

    private static LangKeyEntry createEntry(ResourceLocation id, EntryKind kind) {
        return new LangKeyEntry(
                LangKeyWriter.makeLangKey("structure", id),
                LangKeyWriter.humanize(id),
                kind,
                id,
                id.getNamespace()
        );
    }
}
