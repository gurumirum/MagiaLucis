package gurumirum.magialucis.contents;

import gurumirum.magialucis.api.MagiaLucisApi;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

public final class ModDamageTypes {
	private ModDamageTypes() {}

	public static final ResourceKey<DamageType> LESSER_ICE_PROJECTILE = damageType("lesser_ice_projectile");

	private static ResourceKey<DamageType> damageType(String id) {
		return ResourceKey.create(Registries.DAMAGE_TYPE, MagiaLucisApi.id(id));
	}
}
