package io.github.shrhang.export_language_keys_for_compasses.compat.naturescompass;

import com.chaosthedude.naturescompass.util.BiomeUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.Set;

public final class NaturesCompassAdapter {
    public static Set<ResourceLocation> getAllowedBiomeIds(Level level) {
        return Set.copyOf(BiomeUtils.getAllowedBiomeKeys(level));
    }
}
