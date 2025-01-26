package gurumirum.magialucis.contents.item.accessory;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.utils.NumberFormats;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SpeedBoostCurioItem extends AttributeCurioItem.Unique {
	private static final ResourceLocation SPEED_BOOST_ID = MagiaLucisMod.id("speed_boost");

	public final double speedBoostAmount;

	private @Nullable Multimap<Holder<Attribute>, AttributeModifier> attribute;

	public SpeedBoostCurioItem(Properties properties, double speedBoostAmount,
	                           int chargeTicks, long luxCostPerCharge) {
		super(properties, chargeTicks, luxCostPerCharge);
		this.speedBoostAmount = speedBoostAmount;
	}

	@Override
	protected @NotNull Multimap<Holder<Attribute>, AttributeModifier> createActiveAttributeModifier() {
		if (this.attribute == null) {
			this.attribute = ImmutableMultimap.of(
					Attributes.MOVEMENT_SPEED, new AttributeModifier(SPEED_BOOST_ID, this.speedBoostAmount,
							AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
		}
		return this.attribute;
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
		tooltip.add(Component.translatable("item.magialucis.speed_ring.tooltip.0",
				NumberFormats.pct(this.speedBoostAmount, ChatFormatting.YELLOW)));
		super.appendHoverText(stack, context, tooltip, flag);
	}
}
