package gurumirum.magialucis.contents;

import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.contents.entity.EnderChestPortal;
import gurumirum.magialucis.contents.entity.LesserIceProjectile;
import gurumirum.magialucis.contents.entity.templeguardian.TempleGuardian;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

@EventBusSubscriber(modid = MagiaLucisMod.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class ModEntities {
	private ModEntities() {}

	public static final DeferredHolder<EntityType<?>, EntityType<EnderChestPortal>> ENDER_CHEST_PORTAL = Contents.ENTITY_TYPES.register("ender_chest_portal",
			() -> EntityType.Builder.<EnderChestPortal>of(EnderChestPortal::new, MobCategory.MISC)
					.sized(.75f, .75f)
					.clientTrackingRange(10)
					.updateInterval(10)
					.build("ender_chest_portal"));

	public static final DeferredHolder<EntityType<?>, EntityType<LesserIceProjectile>> LESSER_ICE_PROJECTILE = Contents.ENTITY_TYPES.register("lesser_ice_projectile",
			() -> EntityType.Builder.<LesserIceProjectile>of(LesserIceProjectile::new, MobCategory.MISC)
					.sized(0.25f, 0.25f)
					.clientTrackingRange(4)
					.updateInterval(10)
					.build("lesser_ice_projectile"));

	public static final DeferredHolder<EntityType<?>, EntityType<TempleGuardian>> TEMPLE_GUARDIAN = Contents.ENTITY_TYPES.register("temple_guardian",
			() -> EntityType.Builder.of(TempleGuardian::new, MobCategory.MONSTER)
					.sized(0.6f, 1.8f)
					.clientTrackingRange(10)
					.build("temple_guardian"));

	@SubscribeEvent
	public static void onAttributeCreation(EntityAttributeCreationEvent event) {
		event.put(ModEntities.TEMPLE_GUARDIAN.get(), TempleGuardian.createAttributes().build());
	}

	public static void init() {}
}
