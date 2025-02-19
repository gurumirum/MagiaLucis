package gurumirum.magialucis.api.augment;

import gurumirum.magialucis.api.MagiaLucisRegistries;
import gurumirum.magialucis.contents.data.AugmentLogic;
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
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SimpleAugment implements Augment {
	private @Nullable Component name;
	private @Nullable Function<@Nullable ResourceLocation, @NotNull List<@NotNull Component>> descriptionFunction;
	private @Nullable List<Component> description;
	private @Nullable ResourceLocation texture;

	public SimpleAugment(@NotNull Properties properties) {
		this.name = properties.name;
		this.descriptionFunction = properties.descriptions;
		this.texture = properties.texture;
	}

	@Override
	public @Nullable Component getDescriptionName(Item.@NotNull TooltipContext context, @Nullable Player player,
	                                              @NotNull ItemStack stack, @NotNull TooltipFlag flag) {
		if (this.name == null) {
			if (!MagiaLucisRegistries.initialized()) return null;
			ResourceLocation key = MagiaLucisRegistries.augmentRegistry().getKey(this);
			this.name = createName(key);
		}
		return this.name;
	}

	protected @NotNull Component createName(@Nullable ResourceLocation key) {
		return AugmentLogic.augmentName(key == null ?
				"magialucis.augment.unregistered_sadface" :
				"magialucis.augment." + key.getNamespace() + "." + key.getPath().replace('/', '.')
		);
	}

	@Override
	public void appendDescription(Item.@NotNull TooltipContext context, @Nullable Player player, @NotNull ItemStack stack,
	                              @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
		if (this.description == null) {
			if (!MagiaLucisRegistries.initialized()) return;
			ResourceLocation key = MagiaLucisRegistries.augmentRegistry().getKey(this);
			this.description = this.descriptionFunction != null ? this.descriptionFunction.apply(key) : List.of();
			this.descriptionFunction = null;
		}
		tooltip.addAll(this.description);
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

	public static @NotNull List<Component> simpleDescriptions(@Nullable ResourceLocation key, int descriptionCount) {
		if (key == null) return List.of();
		return IntStream.range(0, descriptionCount).mapToObj(i ->
				AugmentLogic.augmentDesc("magialucis.augment." +
						key.getNamespace() + "." + key.getPath().replace('/', '.') +
						".description." + i)
		).collect(Collectors.toList());
	}

	public static final class Properties {
		private @Nullable Component name;
		private @Nullable Function<@Nullable ResourceLocation, @NotNull List<@NotNull Component>> descriptions;
		private @Nullable ResourceLocation texture;

		public Properties name(@Nullable Component name) {
			this.name = name;
			return this;
		}

		public Properties descriptions(int descriptions) {
			return descriptions(key -> simpleDescriptions(key, descriptions));
		}

		public Properties descriptions(@Nullable Function<@Nullable ResourceLocation, @NotNull List<@NotNull Component>> descriptions) {
			this.descriptions = descriptions;
			return this;
		}

		public Properties texture(@Nullable ResourceLocation texture) {
			this.texture = texture;
			return this;
		}
	}
}
