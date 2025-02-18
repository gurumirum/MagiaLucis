package gurumirum.magialucis.api.augment;

import gurumirum.magialucis.api.MagiaLucisRegistries;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SimpleAugment implements Augment {
	protected final int descriptionCount;

	private @Nullable Component name;
	private @Nullable List<Component> description;
	private @Nullable ResourceLocation texture;

	public SimpleAugment(@NotNull Properties properties) {
		this.descriptionCount = properties.descriptions;
		this.name = properties.name;
		this.texture = properties.texture;
	}

	@Override
	public @NotNull Component name() {
		if (this.name == null) {
			if (!MagiaLucisRegistries.initialized()) return Component.empty();
			ResourceLocation key = MagiaLucisRegistries.augmentRegistry().getKey(this);
			this.name = createName(key);
		}
		return this.name;
	}

	protected @NotNull Component createName(@Nullable ResourceLocation key) {
		if (key == null) return Component.translatable("magialucis.augment.unregistered_sadface");
		return Component.translatable("magialucis.augment." + key.getNamespace() + "." + key.getPath().replace('/', '.'));
	}

	@Override
	public void appendHoverText(Item.@NotNull TooltipContext context, @Nullable Player player, @NotNull ItemStack stack,
	                            @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
		if (this.description == null) {
			if (!MagiaLucisRegistries.initialized()) return;
			ResourceLocation key = MagiaLucisRegistries.augmentRegistry().getKey(this);
			this.description = createDescription(key);
		}
		tooltip.addAll(this.description);
	}

	protected @NotNull List<Component> createDescription(@Nullable ResourceLocation key) {
		if (key == null) return List.of();
		return IntStream.range(0, this.descriptionCount).mapToObj(i ->
				Component.translatable("magialucis.augment.tooltip",
						Component.translatable("magialucis.augment." +
								key.getNamespace() + "." + key.getPath().replace('/', '.') +
								".description." + i
						)
				).withStyle(ChatFormatting.YELLOW)
		).collect(Collectors.toList());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public @NotNull ResourceLocation texture(@NotNull ItemStack stack) {
		if (this.texture == null) {
			if (!MagiaLucisRegistries.initialized()) return MissingTextureAtlasSprite.getLocation();
			this.texture = Objects.requireNonNullElseGet(
					MagiaLucisRegistries.augmentRegistry().getKey(this),
					MissingTextureAtlasSprite::getLocation);
		}
		return this.texture;
	}

	public static final class Properties {
		private @Nullable Component name;
		private int descriptions;
		private @Nullable ResourceLocation texture;

		public Properties name(Component name) {
			this.name = name;
			return this;
		}

		public Properties descriptions(int descriptions) {
			this.descriptions = descriptions;
			return this;
		}

		public Properties texture(ResourceLocation texture) {
			this.texture = texture;
			return this;
		}
	}
}
