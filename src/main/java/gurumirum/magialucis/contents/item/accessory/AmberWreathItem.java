package gurumirum.magialucis.contents.item.accessory;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.contents.ModDataComponents;
import gurumirum.magialucis.contents.ModMobEffects;
import gurumirum.magialucis.utils.NumberFormats;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class AmberWreathItem extends LuxContainerCurioItem {
	public static final int COST_PER_UPDATE = 1;

	public static final FoodProperties APPLE_FOOD_PROPERTIES = new FoodProperties.Builder()
			.nutrition(4)
			.saturationModifier(1.2f)
			.effect(() -> new MobEffectInstance(ModMobEffects.NATURES_BLESSING, 20 * 60 * 8, 10), 1)
			.build();

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

		boolean active = AccessoryLogic.updateCharge(slotContext, stack,
				6, COST_PER_UPDATE,
				entity.hasEffect(ModMobEffects.NATURES_BLESSING) ||
						((this.day ? level.isDay() : level.isNight()) &&
								level.getBrightness(LightLayer.SKY, entity.blockPosition()) > 12)
		);

		boolean stackActive = stack.getOrDefault(ModDataComponents.ACTIVE, false);
		if (stackActive != active) {
			stack.set(ModDataComponents.ACTIVE, active);
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
