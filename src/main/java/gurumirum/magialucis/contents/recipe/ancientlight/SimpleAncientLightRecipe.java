package gurumirum.magialucis.contents.recipe.ancientlight;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gurumirum.magialucis.contents.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class SimpleAncientLightRecipe implements AncientLightRecipe {
	private final Set<Holder<Block>> blocks;
	private final List<ItemStack> results;
	private final int processTicks;

	public SimpleAncientLightRecipe(Set<Holder<Block>> blocks,
	                                List<ItemStack> results,
	                                int processTicks) {
		this.blocks = Collections.unmodifiableSet(blocks);
		this.results = Collections.unmodifiableList(results);
		this.processTicks = processTicks;
	}

	@Override
	public @NotNull @Unmodifiable Set<Holder<Block>> blocks() {
		return this.blocks;
	}

	@Override
	public int getProcessTicks(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state) {
		return this.processTicks;
	}

	@Override
	public @NotNull List<ItemStack> assemble(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state) {
		return this.results.stream().map(ItemStack::copy).toList();
	}

	@Override
	public @NotNull List<ItemStack> getResultItemView() {
		return this.results;
	}

	@Override
	public int getProcessTicksView() {
		return this.processTicks;
	}

	@Override
	public @NotNull RecipeSerializer<?> getSerializer() {
		return ModRecipes.ANCIENT_LIGHT_SERIALIZER.get();
	}

	public static class Serializer implements RecipeSerializer<SimpleAncientLightRecipe> {
		private static final MapCodec<SimpleAncientLightRecipe> CODEC = RecordCodecBuilder.mapCodec(b -> b.group(
				BuiltInRegistries.BLOCK.holderByNameCodec().listOf().fieldOf("blocks")
						.<Set<Holder<Block>>>xmap(LinkedHashSet::new, List::copyOf)
						.forGetter(r -> r.blocks),
				ItemStack.CODEC.listOf().fieldOf("results").forGetter(r -> r.results),
				Codec.INT.fieldOf("processTicks").forGetter(r -> r.processTicks)
		).apply(b, SimpleAncientLightRecipe::new));

		private static final StreamCodec<RegistryFriendlyByteBuf, SimpleAncientLightRecipe> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.holderRegistry(Registries.BLOCK).apply(ByteBufCodecs.collection(i -> new LinkedHashSet<>())),
				r -> r.blocks,
				ItemStack.LIST_STREAM_CODEC,
				r -> r.results,
				ByteBufCodecs.VAR_INT,
				r -> r.processTicks,
				SimpleAncientLightRecipe::new
		);

		@Override
		public @NotNull MapCodec<SimpleAncientLightRecipe> codec() {
			return CODEC;
		}

		@Override
		public @NotNull StreamCodec<RegistryFriendlyByteBuf, SimpleAncientLightRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
