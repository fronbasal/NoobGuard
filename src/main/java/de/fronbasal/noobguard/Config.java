package de.fronbasal.noobguard;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@EventBusSubscriber(modid = NoobGuard.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // Configurable values
    private static final ModConfigSpec.BooleanValue PREVENT_SPAWNING = BUILDER.comment("Whether to prevent spawning of hostile entities near guarded players").define("preventSpawning", false);
    private static final ModConfigSpec.BooleanValue DROP_LOOT = BUILDER.comment("Whether to drop loot of killed entities").define("dropLoot", false);
    private static final ModConfigSpec.IntValue ALONE_DISTANCE = BUILDER.comment("The distance in blocks a player has to be away from other players to be considered alone").defineInRange("aloneDistance", 32, 0, 8192);
    private static final ModConfigSpec.ConfigValue<List<? extends String>> GUARDED_USERS = BUILDER.comment("A list of guarded user's UUIDs").defineListAllowEmpty("guarded-users", ArrayList<String>::new, Config::validateUUID);

    // Built config specification
    static final ModConfigSpec SPEC = BUILDER.build();

    // Public static variables for config values
    public static boolean preventSpawning;
    public static boolean dropLoot;
    public static int aloneDistance;
    public static HashSet<UUID> guardedUsers;

    // Validates that the provided string is a valid UUID
    private static boolean validateUUID(final Object id) {
        try {
            UUID.fromString((String) id);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // Loads config values when ModConfigEvent fires
    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        dropLoot = DROP_LOOT.get();
        aloneDistance = ALONE_DISTANCE.get();
        preventSpawning = PREVENT_SPAWNING.get();
        guardedUsers = GUARDED_USERS.get().stream()
                .filter(Config::validateUUID)
                .map(UUID::fromString)
                .collect(Collectors.toCollection(HashSet::new));
    }
}
