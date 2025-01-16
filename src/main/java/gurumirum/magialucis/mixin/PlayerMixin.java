package gurumirum.magialucis.mixin;

import gurumirum.magialucis.contents.Contents;
import gurumirum.magialucis.contents.Wands;
import gurumirum.magialucis.contents.item.wand.LapisShieldItem;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
	protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
		super(entityType, level);
	}

	@Inject(method = "disableShield", at = @At("HEAD"), cancellable = true)
	public void magialucis$onDisableShield(CallbackInfo info) {
		ItemStack stack = getUseItem();

		if (stack.is(Wands.LAPIS_SHIELD.asItem())) {
			long charge = stack.getOrDefault(Contents.LUX_CHARGE, 0L);
			stack.set(Contents.LUX_CHARGE, Math.max(0, charge - LapisShieldItem.COST_PER_SHIELD_DISABLE));
			if (charge >= LapisShieldItem.COST_PER_SHIELD_DISABLE) info.cancel();
		}
	}
}
