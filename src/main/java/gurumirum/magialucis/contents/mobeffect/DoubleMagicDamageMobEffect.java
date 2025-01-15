package gurumirum.magialucis.contents.mobeffect;

import gurumirum.magialucis.capability.GemStats;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class DoubleMagicDamageMobEffect extends MobEffect {

    public DoubleMagicDamageMobEffect() {
        super(MobEffectCategory.HARMFUL, GemStats.DIAMOND.color());
    }
}
