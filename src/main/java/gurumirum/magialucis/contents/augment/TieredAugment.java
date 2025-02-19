package gurumirum.magialucis.contents.augment;

import gurumirum.magialucis.api.augment.SimpleAugment;
import gurumirum.magialucis.contents.data.AugmentLogic;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TieredAugment extends SimpleAugment {
	private final TieredAugmentType type;
	private final int index;

	private @Nullable Component tierlessName;

	public TieredAugment(@NotNull Properties properties, @NotNull TieredAugmentType type, int index) {
		super(properties);
		this.type = type;
		this.index = index;
	}

	public @NotNull TieredAugmentType type() {
		return type;
	}

	public int index() {
		return index;
	}

	@Override
	public @NotNull ResourceLocation texture(@NotNull ItemStack stack) {
		return isTheOnlyTier(stack) ? this.type.getTierlessTexture() : this.type.getTieredTexture(this.index);
	}

	@Override
	public @Nullable Component getDescriptionName(Item.@NotNull TooltipContext context, @Nullable Player player,
	                                              @NotNull ItemStack stack, @NotNull TooltipFlag flag) {
		if (isTheOnlyTier(stack)) {
			if (this.tierlessName == null) {
				ResourceLocation baseName = this.type.baseName();
				this.tierlessName = AugmentLogic.augmentName(
						"magialucis.augment." + baseName.getNamespace() + "." +
								baseName.getPath().replace('/', '.'));
			}
			return this.tierlessName;
		}
		return super.getDescriptionName(context, player, stack, flag);
	}

	protected boolean isTheOnlyTier(@NotNull ItemStack stack) {
		return this.index == 0 && (this.type.tiers() < 2 || !AugmentLogic.getSpec(stack).contains(this.type.get(1)));
	}
}
