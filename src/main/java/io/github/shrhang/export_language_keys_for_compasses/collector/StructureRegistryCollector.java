package io.github.shrhang.export_language_keys_for_compasses.collector;

import io.github.shrhang.export_language_keys_for_compasses.export.EntryKind;
import io.github.shrhang.export_language_keys_for_compasses.export.LangKeyEntry;
import io.github.shrhang.export_language_keys_for_compasses.export.LangKeyFormatter;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public final class StructureRegistryCollector {

    private static final ResourceLocation NO_GROUP_ID = ResourceLocation.fromNamespaceAndPath("explorerscompass", "none");

    public static List<LangKeyEntry> collect(
            ServerLevel level,
            Predicate<ResourceLocation> structureFilter,
            Map<ResourceLocation, ResourceLocation> preferredStructureGroups
    ) {
        Registry<Structure> structureRegistry = level.registryAccess().lookupOrThrow(Registries.STRUCTURE);
        Map<ResourceLocation, ResourceLocation> registryStructureGroups = collectStructureGroups(level, structureRegistry);
        List<LangKeyEntry> entries = new ArrayList<>();

        for (ResourceLocation structureId : structureRegistry.keySet()) {
            if (!structureFilter.test(structureId)) {
                continue;
            }

            entries.add(createEntry(structureId, EntryKind.STRUCTURE));
            ResourceLocation groupId = preferredStructureGroups.get(structureId);
            if (groupId == null) {
                groupId = registryStructureGroups.getOrDefault(structureId, NO_GROUP_ID);
            }
            entries.add(createEntry(groupId, EntryKind.STRUCTURE_GROUP));
        }

        return entries;
    }

    private static Map<ResourceLocation, ResourceLocation> collectStructureGroups(
            ServerLevel level,
            Registry<Structure> structureRegistry
    ) {
        Registry<StructureSet> structureSetRegistry = level.registryAccess().lookupOrThrow(Registries.STRUCTURE_SET);
        Map<ResourceLocation, ResourceLocation> structureGroups = new LinkedHashMap<>();

        for (StructureSet structureSet : structureSetRegistry) {
            ResourceLocation groupId = structureSetRegistry.getKey(structureSet);
            if (groupId == null) {
                continue;
            }

            for (StructureSet.StructureSelectionEntry selectionEntry : structureSet.structures()) {
                ResourceLocation structureId = structureRegistry.getKey(selectionEntry.structure().value());
                if (structureId != null) {
                    structureGroups.putIfAbsent(structureId, groupId);
                }
            }
        }

        return structureGroups;
    }

    private static LangKeyEntry createEntry(ResourceLocation id, EntryKind kind) {
        return new LangKeyEntry(
                LangKeyFormatter.makeLangKey("structure", id),
                LangKeyFormatter.humanize(id),
                kind,
                id,
                id.getNamespace()
        );
    }
}
