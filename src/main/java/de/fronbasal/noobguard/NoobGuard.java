package de.fronbasal.noobguard;

import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.event.entity.living.*;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.List;

@Mod(NoobGuard.MODID)
public class NoobGuard {
    public static final HashSet<EntityType<?>> HOSTILE_ENTITIES = new HashSet<>(List.of(
            EntityType.SPIDER, EntityType.CAVE_SPIDER, EntityType.ENDERMAN, EntityType.PIGLIN, EntityType.ZOMBIFIED_PIGLIN,
            EntityType.EVOKER, EntityType.VINDICATOR, EntityType.ILLUSIONER, EntityType.RAVAGER, EntityType.VEX,
            EntityType.ENDERMITE, EntityType.GUARDIAN, EntityType.ELDER_GUARDIAN, EntityType.SHULKER, EntityType.HUSK,
            EntityType.STRAY, EntityType.PHANTOM, EntityType.CREEPER, EntityType.GHAST, EntityType.MAGMA_CUBE,
            EntityType.SILVERFISH, EntityType.SKELETON, EntityType.SLIME, EntityType.ZOMBIE, EntityType.ZOMBIE_VILLAGER,
            EntityType.DROWNED, EntityType.WITHER_SKELETON, EntityType.WITCH, EntityType.HOGLIN, EntityType.ZOGLIN,
            EntityType.PIGLIN_BRUTE, EntityType.WITHER
    ));

    public static final String MODID = "noobguard";
    private static final Logger LOGGER = LogUtils.getLogger();

    public NoobGuard(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    @EventBusSubscriber(modid = NoobGuard.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.DEDICATED_SERVER)
    static class GameEvents {
        @SubscribeEvent
        public static void onTargetChange(LivingChangeTargetEvent event) {
            if (event.getNewAboutToBeSetTarget() == null) return;
            // ignore entities that are not hostile
            if (!HOSTILE_ENTITIES.contains(event.getEntity().getType())) return;
            // ignore entities with a name tag
            if (event.getEntity().getCustomName() != null) return;
            if (event.getNewAboutToBeSetTarget().getType() != EntityType.PLAYER) return;
            if (!Config.guardedUsers.contains(event.getNewAboutToBeSetTarget().getUUID())) return;
            // check if the user is wandering alone or only with other guarded users
            if (!isPlayerGuardable(event.getEntity())) return;
            LOGGER.debug("Prevented hostile entity {} from targeting player {}", event.getEntity(), event.getNewAboutToBeSetTarget());
            event.setCanceled(true);
            handleHostileEntity(event.getEntity());
        }

        @SubscribeEvent
        public static void onEndermanAngered(EnderManAngerEvent event) {
            if (!Config.guardedUsers.contains(event.getPlayer().getUUID())) return;
            // check if the user is wandering alone or only with other guarded users
            if (!isPlayerGuardable(event.getEntity())) return;
            event.setCanceled(true);
            handleHostileEntity(event.getEntity());
            LOGGER.debug("Prevented enderman {} from targeting player {}", event.getEntity(), event.getPlayer());
        }

        @SubscribeEvent
        public static void onMobSpawn(FinalizeSpawnEvent event) {
            if (!Config.preventSpawning) return;
            if (!HOSTILE_ENTITIES.contains(event.getEntity().getType())) return;
            if (!isPlayerGuardable(event.getEntity())) return;
            event.setSpawnCancelled(true);
            LOGGER.debug("Prevented hostile entity {} from spawning", event.getEntity());
        }

        @SubscribeEvent
        public static void onLivingDamage(LivingIncomingDamageEvent event) {
            if (event.getSource().getEntity() == null) return;
            if (event.getSource().getEntity() instanceof Player) return;
            if (!(event.getSource().getEntity() instanceof LivingEntity entity)) return;
            if (!HOSTILE_ENTITIES.contains(entity.getType())) return;
            if (event.getSource().getEntity().getCustomName() != null) return;
            if (event.getEntity().getType() != EntityType.PLAYER) return;
            if (!Config.guardedUsers.contains(event.getEntity().getUUID())) return;
            // check if the user is wandering alone or only with other guarded users
            if (!isPlayerGuardable(entity)) return;
            event.setCanceled(true);
            handleHostileEntity(entity);
        }

        private static void handleHostileEntity(LivingEntity entity) {
            if (Config.dropLoot) {
                var ds = entity.damageSources().genericKill();
                entity.hurt(ds, entity.getHealth() + 5);
            } else {
                entity.remove(Entity.RemovalReason.DISCARDED);
            }
        }

        @SuppressWarnings("BooleanMethodIsAlwaysInverted")
        private static boolean isPlayerGuardable(LivingEntity entity) {
            List<Player> surroundingEntities = entity.getCommandSenderWorld().getNearbyPlayers(TargetingConditions.forCombat(), entity, entity.getBoundingBox().inflate(Config.aloneDistance));

            if (surroundingEntities.isEmpty()) {
                // leave it up to the event handler to decide what to do
                return true;
            }

            return surroundingEntities
                    .stream()
                    .allMatch(e -> Config.guardedUsers.contains(e.getUUID()));
        }
    }
}
