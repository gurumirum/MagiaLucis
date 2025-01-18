package gurumirum.magialucis.contents;

import gurumirum.magialucis.contents.mobeffect.DoubleMagicDamageMobEffect;
import gurumirum.magialucis.contents.mobeffect.RecallFatigueMobEffect;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class ModMobEffects {
	private ModMobEffects() {}

	public static final DeferredHolder<MobEffect, MobEffect> RECALL_FATIGUE = Contents.MOB_EFFECTS.register("recall_fatigue",
			RecallFatigueMobEffect::new);

	public static final DeferredHolder<MobEffect, MobEffect> DOUBLE_MAGIC_DAMAGE = Contents.MOB_EFFECTS.register("double_magic_damage",
			DoubleMagicDamageMobEffect::new);

	public static void init() {}
}
