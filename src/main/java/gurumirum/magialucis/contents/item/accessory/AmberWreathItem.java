package gurumirum.magialucis.contents.item.accessory;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.contents.ModDataComponents;
import gurumirum.magialucis.utils.NumberFormats;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class AmberWreathItem extends LuxContainerCurioItem implements ICurioItem {
	public static final int COST_PER_UPDATE = 1;

	private static final double MOVEMENT_SPEED_INCREASE_AMOUNT = 0.15;
	private static final double ATTACK_DAMAGE_INCREASE_AMOUNT = 1;

	private static Multimap<Holder<Attribute>, AttributeModifier> attribute;

	private final boolean day;

	public AmberWreathItem(Properties properties, boolean day) {
		super(properties);
		this.day = day;
	}

	@Override
	public void curioTick(SlotContext slotContext, ItemStack stack) {
		LivingEntity entity = slotContext.entity();
		Level level = entity.level();

		if (level.isClientSide || level.getGameTime() % 10 != 0) return;

		BlockPos pos = entity.blockPosition();
		long luxCharge = stack.getOrDefault(ModDataComponents.LUX_CHARGE, 0L);
		boolean healTick = level.getGameTime() % 60 == 0;
		boolean active = (!healTick || luxCharge >= COST_PER_UPDATE) &&
				level.isDay() == this.day &&
				level.getBrightness(LightLayer.SKY, pos) > 12;

		boolean stackActive = stack.getOrDefault(ModDataComponents.ACTIVE, false);
		if (stackActive != active) {
			stack.set(ModDataComponents.ACTIVE, active);
		}

		if (healTick && active) {
			entity.heal(1);
			stack.set(ModDataComponents.LUX_CHARGE, luxCharge - COST_PER_UPDATE);
		}
	}

	@Override
	public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
		if (stack.getOrDefault(ModDataComponents.ACTIVE, false)) {
			if (attribute == null) attribute = ImmutableMultimap.of(
					Attributes.MOVEMENT_SPEED, new AttributeModifier(MagiaLucisMod.id("amber_wreath_attack_damage"),
							MOVEMENT_SPEED_INCREASE_AMOUNT, AttributeModifier.Operation.ADD_MULTIPLIED_BASE),
					Attributes.ATTACK_DAMAGE, new AttributeModifier(MagiaLucisMod.id("amber_wreath_attack_damage"),
							ATTACK_DAMAGE_INCREASE_AMOUNT, AttributeModifier.Operation.ADD_VALUE));
			return attribute;
		}
		return ImmutableMultimap.of();
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
	                            @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
		String key = "item.magialucis." + (this.day ? "druid_wreath" : "dryad_wreath") + ".tooltip";

		tooltip.add(Component.translatable(key + ".0"));
		tooltip.add(Component.translatable(key + ".1",
				NumberFormats.pct(MOVEMENT_SPEED_INCREASE_AMOUNT, ChatFormatting.YELLOW),
				NumberFormats.dec(ATTACK_DAMAGE_INCREASE_AMOUNT, ChatFormatting.YELLOW)));
		tooltip.add(Component.translatable(key + ".2"));

		super.appendHoverText(stack, context, tooltip, flag);
	}
}
