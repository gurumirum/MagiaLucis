package gurumirum.magialucis.contents.item.accessory;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import gurumirum.magialucis.contents.ModDataComponents;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class AttributeModifyRingItem extends LuxContainerCurioItem implements ICurioItem {
	private final Multimap<Holder<Attribute>, AttributeModifier> attribute = LinkedHashMultimap.create();
	private final int luxConsumeAmount;

	public AttributeModifyRingItem(Properties properties, int luxConsumeAmount) {
		super(properties);
		this.luxConsumeAmount = luxConsumeAmount;
	}

	public AttributeModifyRingItem addAttribute(Holder<Attribute> holder, AttributeModifier modifier) {
		attribute.put(holder, modifier);
		return this;
	}

	@Override
	public void curioTick(SlotContext slotContext, ItemStack stack) {
		ICurioItem.super.curioTick(slotContext, stack);
		long lux = stack.getOrDefault(ModDataComponents.LUX_CHARGE, 0L);
		boolean turnOn = stack.getOrDefault(ModDataComponents.POWERED_ON, false);
		if (turnOn && lux >= luxConsumeAmount) {
			stack.set(ModDataComponents.LUX_CHARGE, lux - luxConsumeAmount);
		} else if (lux < luxConsumeAmount) {
			stack.set(ModDataComponents.POWERED_ON, false);
		}
	}

	@Override
	public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand usedHand) {
		if (level.isClientSide) return InteractionResultHolder.consume(player.getItemInHand(usedHand));
		if (!player.isSecondaryUseActive()) return super.use(level, player, usedHand);
		ItemStack stack = player.getItemInHand(usedHand);
		boolean turnOn = stack.getOrDefault(ModDataComponents.POWERED_ON, false);
		stack.set(ModDataComponents.POWERED_ON, !turnOn);
		return InteractionResultHolder.consume(stack);
	}

	@Override
	public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
		Multimap<Holder<Attribute>, AttributeModifier> attributes = ICurioItem.super.getAttributeModifiers(slotContext, id, stack);
		boolean turnOn = stack.getOrDefault(ModDataComponents.POWERED_ON, false);
		if (turnOn)
			attributes.putAll(this.attribute);
		return attributes;
	}
}
