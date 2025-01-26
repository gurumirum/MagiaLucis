package gurumirum.magialucis.contents;

import gurumirum.magialucis.contents.mobeffect.BaseMobEffect;
import gurumirum.magialucis.contents.mobeffect.RecallFatigueMobEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class ModMobEffects {
	private ModMobEffects() {}

	public static final DeferredHolder<MobEffect, MobEffect> NATURES_BLESSING = Contents.MOB_EFFECTS.register("natures_blessing",
			() -> new BaseMobEffect(MobEffectCategory.BENEFICIAL, 0x228a2b));

	public static final DeferredHolder<MobEffect, MobEffect> RECALL_FATIGUE = Contents.MOB_EFFECTS.register("recall_fatigue",
			RecallFatigueMobEffect::new);

	public static final DeferredHolder<MobEffect, MobEffect> DOUBLE_MAGIC_DAMAGE = Contents.MOB_EFFECTS.register("double_magic_damage",
			() -> new BaseMobEffect(MobEffectCategory.HARMFUL, -1));

	public static void init() {}
}
