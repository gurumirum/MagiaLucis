package gurumirum.magialucis.datagen.builder;

import gurumirum.magialucis.contents.recipe.ancientlight.SimpleAncientLightRecipe;
import gurumirum.magialucis.utils.BlockProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class AncientLightRecipeBuilder extends ModRecipeBuilder<SimpleAncientLightRecipe> {
	private final LinkedHashSet<Holder<Block>> blocks = new LinkedHashSet<>();
	private final List<ItemStack> results = new ArrayList<>();
	private int processTicks;

	public AncientLightRecipeBuilder block(BlockProvider block) {
		return block(block.block());
	}

	public AncientLightRecipeBuilder block(Block block) {
		this.blocks.add(BuiltInRegistries.BLOCK.getHolderOrThrow(
				BuiltInRegistries.BLOCK.getResourceKey(block).orElseThrow()));
		return this;
	}

	public AncientLightRecipeBuilder result(ItemLike item) {
		return result(new ItemStack(item));
	}

	public AncientLightRecipeBuilder result(ItemLike item, int count) {
		return result(new ItemStack(item, count));
	}

	public AncientLightRecipeBuilder result(ItemStack stack) {
		this.results.add(stack);
		return this;
	}

	public AncientLightRecipeBuilder processTicks(int processTicks) {
		this.processTicks = processTicks;
		return this;
	}

	@Override
	protected SimpleAncientLightRecipe createRecipeInstance() {
		return new SimpleAncientLightRecipe(this.blocks, this.results, this.processTicks);
	}

	@Override
	protected @Nullable ResourceLocation defaultRecipeId() {
		return this.results.size() == 1 ?
				getId(this.results.getFirst().getItem()).withPrefix(defaultRecipePrefix()) :
				null;
	}

	@Override
	protected @NotNull String defaultRecipePrefix() {
		return "ancient_light/";
	}

	@Override
	protected void ensureValid(ResourceLocation id) {
		if (this.blocks.isEmpty()) {
			throw new IllegalStateException("Ancient light recipe " + id + " has no blocks");
		}
		if (this.results.isEmpty()) {
			throw new IllegalStateException("Ancient light recipe " + id + " has no result");
		}
		if (this.processTicks <= 0) {
			throw new IllegalStateException("Transfusion recipe " + id + " has no process ticks");
		}
	}
}
