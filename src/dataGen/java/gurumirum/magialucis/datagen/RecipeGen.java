package gurumirum.magialucis.datagen;

import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.datagen.recipe.*;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class RecipeGen extends RecipeProvider {
	private final Set<ResourceLocation> mismatchingNamespaceRecipes = new ObjectOpenHashSet<>();

	public RecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	protected @NotNull CompletableFuture<?> run(@NotNull CachedOutput output, HolderLookup.@NotNull Provider registries) {
		this.mismatchingNamespaceRecipes.clear();

		CompletableFuture<?> run = super.run(output, registries);

		if (!this.mismatchingNamespaceRecipes.isEmpty()) {
			throw new IllegalStateException(this.mismatchingNamespaceRecipes.size() +
					" recipes with mismatching namespace have been registered:\n  " +
					this.mismatchingNamespaceRecipes.stream()
							.map(Object::toString)
							.collect(Collectors.joining("\n  ")));
		}

		return run;
	}

	@Override
	protected void buildRecipes(@NotNull RecipeOutput out, HolderLookup.@NotNull Provider provider) {
		out = new RecipeOutputWrapper(out);

		MaterialRecipes.add(out);
		CraftingRecipes.add(out);
		WandRecipes.add(out);
		ArtisanryRecipes.add(out);
		BuildingBlockRecipes.add(out);
		AncientLightRecipes.add(out);
		LightBasinRecipes.add(out);
	}

	private final class RecipeOutputWrapper implements RecipeOutput {
		private final RecipeOutput delegate;

		private RecipeOutputWrapper(RecipeOutput delegate) {
			this.delegate = delegate;
		}

		@Override
		public Advancement.@NotNull Builder advancement() {
			return this.delegate.advancement();
		}

		@Override
		public void accept(@NotNull ResourceLocation id, @NotNull Recipe<?> recipe,
		                   @Nullable AdvancementHolder advancement, ICondition @NotNull ... conditions) {
			if (!id.getNamespace().equals(MagiaLucisMod.MODID)) {
				RecipeGen.this.mismatchingNamespaceRecipes.add(id);
			}
			this.delegate.accept(id, recipe, advancement, conditions);
		}
	}
}
