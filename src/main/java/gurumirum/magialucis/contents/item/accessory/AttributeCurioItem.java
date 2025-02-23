package gurumirum.magialucis.contents.item.accessory;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import gurumirum.magialucis.api.capability.LuxContainerStat;
import gurumirum.magialucis.contents.ModDataComponents;
import gurumirum.magialucis.impl.LuxStatTooltip;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;
import java.util.Map;

public abstract class AttributeCurioItem extends LuxContainerCurioItem {
	public final int chargeTicks;
	public final long luxCostPerCharge;

	private final Map<ResourceLocation, Multimap<Holder<Attribute>, AttributeModifier>> attributes = new Object2ObjectOpenHashMap<>();

	public AttributeCurioItem(Properties properties, int chargeTicks, long luxCostPerCharge) {
		super(properties);
		this.chargeTicks = chargeTicks;
		this.luxCostPerCharge = luxCostPerCharge;
	}

	@Override
	public void curioTick(SlotContext slotContext, ItemStack stack) {
		LivingEntity entity = slotContext.entity();
		if (entity.level().isClientSide) return;

		AccessoryLogic.updateCharge(slotContext, stack,
				this.chargeTicks, this.luxCostPerCharge,
				shouldConsumeCharge(slotContext, stack));
	}

	protected boolean shouldConsumeCharge(SlotContext slotContext, ItemStack stack) {
		return true;
	}

	@Override
	public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
		int charge = stack.getOrDefault(ModDataComponents.CHARGE, 0);
		if (charge <= 0) return ImmutableMultimap.of();

		return this.attributes.computeIfAbsent(id, this::createActiveAttributeModifier);
	}

	protected abstract @NotNull Multimap<Holder<Attribute>, AttributeModifier> createActiveAttributeModifier(ResourceLocation slotId);

	@Override
	protected void appendLuxContainerDescription(@NotNull ItemStack stack, @NotNull TooltipContext context,
	                                             @NotNull List<Component> tooltip, @NotNull TooltipFlag flag,
	                                             @NotNull LuxContainerStat luxContainerStat) {
		super.appendLuxContainerDescription(stack, context, tooltip, flag, luxContainerStat);
		double luxConsumption = ((double)this.luxCostPerCharge / this.chargeTicks) * 20;
		if (luxConsumption >= 0) {
			if (luxConsumption < 1) tooltip.add(LuxStatTooltip.subzeroLuxConsumptionPerSec());
			else tooltip.add(LuxStatTooltip.luxConsumptionPerSec(
					(long)Math.floor(luxConsumption), luxContainerStat.maxCharge()));
		}
	}

	public static abstract class Unique extends AttributeCurioItem {
		private @Nullable Multimap<Holder<Attribute>, AttributeModifier> attribute;

		public Unique(Properties properties, int chargeTicks, long luxCostPerCharge) {
			super(properties, chargeTicks, luxCostPerCharge);
		}

		@Override
		protected final @NotNull Multimap<Holder<Attribute>, AttributeModifier> createActiveAttributeModifier(ResourceLocation slotId) {
			if (this.attribute == null) {
				this.attribute = createActiveAttributeModifier();
			}
			return this.attribute;
		}

		protected abstract @NotNull Multimap<Holder<Attribute>, AttributeModifier> createActiveAttributeModifier();
	}
}
