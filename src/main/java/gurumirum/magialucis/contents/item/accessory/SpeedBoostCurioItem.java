package gurumirum.magialucis.contents.item.accessory;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import gurumirum.magialucis.contents.ModDataComponents;
import gurumirum.magialucis.utils.NumberFormats;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;
import java.util.Map;

public class SpeedBoostCurioItem extends LuxContainerCurioItem implements ICurioItem {
	public final String speedBoostKey;
	public final double speedBoostAmount;

	public final int speedBoostChargeTicks;
	public final long luxCostPerCharge;

	private final Map<ResourceLocation, Multimap<Holder<Attribute>, AttributeModifier>> attributes = new Object2ObjectOpenHashMap<>();

	public SpeedBoostCurioItem(Properties properties, String speedBoostKey, double speedBoostAmount,
	                           int speedBoostChargeTicks, long luxCostPerCharge) {
		super(properties);
		this.speedBoostKey = speedBoostKey;
		this.speedBoostAmount = speedBoostAmount;

		this.speedBoostChargeTicks = speedBoostChargeTicks;
		this.luxCostPerCharge = luxCostPerCharge;
	}

	@Override
	public void curioTick(SlotContext slotContext, ItemStack stack) {
		LivingEntity entity = slotContext.entity();
		if (entity.level().isClientSide) return;

		if (entity.walkDist > entity.walkDistO) {
			int speedBoostCharge = stack.getOrDefault(ModDataComponents.SPEED_BOOST_CHARGE, 0);
			if (speedBoostCharge > 0) speedBoostCharge--;

			if (speedBoostCharge <= 0) {
				long luxCharge = stack.getOrDefault(ModDataComponents.LUX_CHARGE, 0L);
				if (luxCharge >= this.luxCostPerCharge) {
					stack.set(ModDataComponents.LUX_CHARGE, luxCharge - this.luxCostPerCharge);
					speedBoostCharge = this.speedBoostChargeTicks;
				}
			}

			stack.set(ModDataComponents.SPEED_BOOST_CHARGE, speedBoostCharge);
		}
	}

	@Override
	public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
		int speedBoostCharge = stack.getOrDefault(ModDataComponents.SPEED_BOOST_CHARGE, 0);
		if (speedBoostCharge <= 0) return ImmutableMultimap.of();

		return this.attributes.computeIfAbsent(id, _id -> ImmutableMultimap.of(
				Attributes.MOVEMENT_SPEED,
				new AttributeModifier(
						_id.withSuffix("." + this.speedBoostKey),
						this.speedBoostAmount,
						AttributeModifier.Operation.ADD_MULTIPLIED_BASE)));
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
		tooltip.add(Component.translatable("item.magialucis.speed_ring.tooltip.0",
				NumberFormats.pct(this.speedBoostAmount, ChatFormatting.YELLOW)));
		super.appendHoverText(stack, context, tooltip, flag);
	}
}
