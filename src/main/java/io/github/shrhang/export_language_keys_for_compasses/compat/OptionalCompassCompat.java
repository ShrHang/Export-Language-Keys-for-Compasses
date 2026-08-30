package io.github.shrhang.export_language_keys_for_compasses.compat;

import com.mojang.logging.LogUtils;
import io.github.shrhang.export_language_keys_for_compasses.compat.explorerscompass.ExplorersCompassAdapter;
import io.github.shrhang.export_language_keys_for_compasses.compat.naturescompass.NaturesCompassAdapter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;
import org.slf4j.Logger;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public final class OptionalCompassCompat {

    public static final String NATURES_COMPASS_MOD_ID = "naturescompass";
    public static final String EXPLORERS_COMPASS_MOD_ID = "explorerscompass";

    private static final Logger LOGGER = LogUtils.getLogger();

    private static boolean isnNaturesCompassLoaded() {
        return !ModList.get().isLoaded(NATURES_COMPASS_MOD_ID);
    }

    private static boolean isnExplorersCompassLoaded() {
        return !ModList.get().isLoaded(EXPLORERS_COMPASS_MOD_ID);
    }

    public static Optional<Set<ResourceLocation>> getAllowedBiomeIds(Level level) {
        if (isnNaturesCompassLoaded()) {
            return Optional.empty();
        }

        return invokeAdapter(
                NATURES_COMPASS_MOD_ID,
                "getAllowedBiomeIds",
                () -> NaturesCompassAdapter.getAllowedBiomeIds(level)
        );
    }

    public static Optional<Set<ResourceLocation>> getAllowedStructureIds(ServerLevel level) {
        if (isnExplorersCompassLoaded()) {
            return Optional.empty();
        }

        return invokeAdapter(
                EXPLORERS_COMPASS_MOD_ID,
                "getAllowedStructureIds",
                () -> ExplorersCompassAdapter.getAllowedStructureIds(level)
        );
    }

    public static Optional<Map<ResourceLocation, ResourceLocation>> getStructureGroups(ServerLevel level) {
        if (isnExplorersCompassLoaded()) {
            return Optional.empty();
        }

        return invokeAdapter(
                EXPLORERS_COMPASS_MOD_ID,
                "getStructureGroups",
                () -> ExplorersCompassAdapter.getStructureGroups(level)
        );
    }

    private static <T> Optional<T> invokeAdapter(
            String modId,
            String operation,
            Supplier<T> invocation
    ) {
        try {
            T result = invocation.get();
            if (result == null) {
                LOGGER.warn("Optional {} adapter operation {} returned null; using registry fallback", modId, operation);
                return Optional.empty();
            }
            return Optional.of(result);
        } catch (LinkageError | RuntimeException exception) {
            LOGGER.warn("Optional {} adapter operation {} failed; using registry fallback", modId, operation, exception);
            return Optional.empty();
        }
    }
}
