package gurumirum.gemthing.contents.mobeffect;

import gurumirum.gemthing.capability.GemStats;
import gurumirum.gemthing.impl.RGB332;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.common.EffectCure;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class RecallFatigueMobEffect extends MobEffect {
	public RecallFatigueMobEffect() {
		super(MobEffectCategory.NEUTRAL, RGB332.toRGB32(GemStats.AQUAMARINE.color()));
	}

	@Override
	public void fillEffectCures(@NotNull Set<EffectCure> cures, @NotNull MobEffectInstance effectInstance) {} // none
}
