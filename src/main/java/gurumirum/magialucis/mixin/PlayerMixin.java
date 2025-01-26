package gurumirum.magialucis.mixin;

import gurumirum.magialucis.contents.Accessories;
import gurumirum.magialucis.contents.ModCurioSlots;
import gurumirum.magialucis.contents.ModDataComponents;
import gurumirum.magialucis.contents.Wands;
import gurumirum.magialucis.contents.item.accessory.AmberWreathItem;
import gurumirum.magialucis.contents.item.wand.LapisShieldItem;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
	protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
		super(entityType, level);
	}

	@Inject(method = "disableShield", at = @At("HEAD"), cancellable = true)
	public void magialucis$onDisableShield(CallbackInfo info) {
		ItemStack stack = getUseItem();

		if (stack.is(Wands.LAPIS_SHIELD.asItem())) {
			long charge = stack.getOrDefault(ModDataComponents.LUX_CHARGE, 0L);
			stack.set(ModDataComponents.LUX_CHARGE, Math.max(0, charge - LapisShieldItem.COST_PER_SHIELD_DISABLE));
			if (charge >= LapisShieldItem.COST_PER_SHIELD_DISABLE) info.cancel();
		}
	}

	private boolean magialucis$eatReentrantFlag;

	@SuppressWarnings("DataFlowIssue")
	@Inject(method = "eat", at = @At("HEAD"), cancellable = true)
	public void magialucis$eat(Level level, ItemStack food, FoodProperties foodProperties,
	                           CallbackInfoReturnable<ItemStack> info) {
		if (this.magialucis$eatReentrantFlag) return;

		Player player = (Player)(Object)this;

		if (food.is(Items.APPLE)) {
			ICuriosItemHandler h = CuriosApi.getCuriosInventory(player).orElse(null);
			if (h != null) {
				ICurioStacksHandler curios = h.getCurios().get(ModCurioSlots.HEADWEAR);
				if (curios != null) {
					IDynamicStackHandler stacks = curios.getStacks();
					for (int i = 0; i < stacks.getSlots(); ++i) {
						ItemStack stack = stacks.getStackInSlot(i);
						if (stack.is(Accessories.DRUID_WREATH.asItem()) || stack.is(Accessories.DRYAD_WREATH.asItem())) {
							this.magialucis$eatReentrantFlag = true;
							try {
								ItemStack result = this.eat(level, food, AmberWreathItem.APPLE_FOOD_PROPERTIES);
								info.setReturnValue(result);
							} finally {
								this.magialucis$eatReentrantFlag = false;
							}
							return;
						}
					}
				}
			}
		}
	}
}
