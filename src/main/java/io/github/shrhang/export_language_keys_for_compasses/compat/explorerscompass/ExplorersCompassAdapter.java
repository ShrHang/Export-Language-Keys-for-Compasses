package io.github.shrhang.export_language_keys_for_compasses.compat.explorerscompass;

import com.chaosthedude.explorerscompass.util.StructureUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

import java.util.Map;
import java.util.Set;

public final class ExplorersCompassAdapter {

    public static Set<ResourceLocation> getAllowedStructureIds(ServerLevel level) {
        return Set.copyOf(StructureUtils.getAllowedStructureKeys(level));
    }

    public static Map<ResourceLocation, ResourceLocation> getStructureGroups(ServerLevel level) {
        return Map.copyOf(StructureUtils.getStructureKeysToTypeKeys(level));
    }
}
