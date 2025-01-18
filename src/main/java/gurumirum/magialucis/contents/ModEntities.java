package gurumirum.magialucis.contents;

import gurumirum.magialucis.contents.entity.EnderChestPortal;
import gurumirum.magialucis.contents.entity.GemGolemEntity;
import gurumirum.magialucis.contents.entity.LesserIceProjectile;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class ModEntities {
	private ModEntities() {}

	public static final DeferredHolder<EntityType<?>, EntityType<EnderChestPortal>> ENDER_CHEST_PORTAL = Contents.ENTITY_TYPES.register("ender_chest_portal",
			() -> EntityType.Builder.<EnderChestPortal>of(EnderChestPortal::new, MobCategory.MISC)
					.sized(.75f, .75f)
					.clientTrackingRange(10)
					.updateInterval(10)
					.build("ender_chest_portal"));

	public static final DeferredHolder<EntityType<?>, EntityType<GemGolemEntity>> GEM_GOLEM = Contents.ENTITY_TYPES.register("gem_golem",
			() -> EntityType.Builder.of(GemGolemEntity::new, MobCategory.MONSTER)
					.sized(1.4F, 2.7F)
					.clientTrackingRange(10)
					.build("gem_golem"));

	public static final DeferredHolder<EntityType<?>, EntityType<LesserIceProjectile>> LESSER_ICE_PROJECTILE = Contents.ENTITY_TYPES.register("lesser_ice_projectile",
			() -> EntityType.Builder.<LesserIceProjectile>of(LesserIceProjectile::new, MobCategory.MISC)
					.sized(0.25f, 0.25f)
					.clientTrackingRange(4)
					.updateInterval(10)
					.build("lesser_ice_projectile"));

	public static void init() {}
}
