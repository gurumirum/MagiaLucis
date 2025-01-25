package gurumirum.magialucis.contents.item.accessory;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.contents.ModDataComponents;
import gurumirum.magialucis.utils.NumberFormats;
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

import java.util.List;

public class SoulCrownItem extends BaseCurioItem {
	private static final double ATTACK_INCREASE_PER_DEBUFF = 0.25;
	private static final ResourceLocation ATTACK_INCREASE_ID = MagiaLucisMod.id("soul_crown");

	public SoulCrownItem(Properties properties) {
		super(properties);
	}

	@Override
	public void curioTick(SlotContext slotContext, ItemStack stack) {
		LivingEntity entity = slotContext.entity();
		if (entity.level().isClientSide) return;

		int newPower = Math.max(0, entity.getActiveEffects().stream()
				.mapToInt(i -> switch (i.getEffect().value().getCategory()) {
					case BENEFICIAL -> -1;
					case HARMFUL -> 1;
					case NEUTRAL -> 0;
				}).sum());

		int power = stack.getOrDefault(ModDataComponents.POWER, 0);
		if (newPower != power) stack.set(ModDataComponents.POWER, newPower);
	}

	@Override
	public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext,
	                                                                            ResourceLocation id,
	                                                                            ItemStack stack) {
		var m = LinkedListMultimap.<Holder<Attribute>, AttributeModifier>create();

		int power = stack.getOrDefault(ModDataComponents.POWER, 0);
		if (power > 0) {
			m.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ATTACK_INCREASE_ID,
					ATTACK_INCREASE_PER_DEBUFF * power,
					AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
		}

		return m;
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
	                            @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
		tooltipComponents.add(Component.translatable("item.magialucis.soul_crown.tooltip.0",
				NumberFormats.pct(ATTACK_INCREASE_PER_DEBUFF, ChatFormatting.YELLOW)));
		tooltipComponents.add(Component.translatable("item.magialucis.soul_crown.tooltip.1"));
	}
}
