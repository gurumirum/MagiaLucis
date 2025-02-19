package gurumirum.magialucis.impl.ancientlight;

import gurumirum.magialucis.contents.Augments;
import gurumirum.magialucis.contents.ModRecipes;
import gurumirum.magialucis.contents.Wands;
import gurumirum.magialucis.contents.data.AugmentLogic;
import gurumirum.magialucis.contents.item.wand.AncientLightWandItem;
import gurumirum.magialucis.contents.recipe.ancientlight.AncientLightRecipe;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.resource.ContextAwareReloadListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public abstract class AncientLightManager extends ContextAwareReloadListener {
	protected static final int UPDATE_INTERVAL = 2;
	protected static final int PROGRESS_DECAY_UPDATE_INTERVAL = UPDATE_INTERVAL * 10;

	protected static final int PROGRESS_PER_UPDATE = 2;
	protected static final int PROGRESS_PER_UPDATE_ACCELERATED = 3;
	protected static final int DECAY_AMOUNT_PER_SEC = 5;

	protected final Map<UUID, BlockPos> focus = new Object2ObjectOpenHashMap<>();
	protected @Nullable AncientLightRecord record;

	private @Nullable Map<Block, List<AncientLightRecipe>> recipes;

	public AncientLightManager() {
		NeoForge.EVENT_BUS.addListener((AddReloadListenerEvent event) -> event.addListener(this));
	}

	public void update(@NotNull Level level) {
		if (level.getGameTime() % UPDATE_INTERVAL == 0) updateProgress(level);
	}

	public @Nullable BlockPos getFocus(@NotNull Player player) {
		return this.focus.get(player.getUUID());
	}

	public void removeFocus(@NotNull Player player) {
		setFocus(player, null);
	}

	public void setFocus(@NotNull Player player, @Nullable BlockPos blockPos) {
		if (blockPos != null) this.focus.put(player.getUUID(), blockPos);
		else this.focus.remove(player.getUUID());
	}

	protected void updateProgress(@NotNull Level level) {
		for (Player p : level.players()) {
			if (p.isDeadOrDying() || !p.isUsingItem() || !p.getUseItem().is(Wands.ANCIENT_LIGHT.asItem())) {
				removeFocus(p);
				continue;
			}

			BlockPos focusPos = getFocus(p);
			if (focusPos == null || !level.isLoaded(focusPos)) continue;

			BlockState state = level.getBlockState(focusPos);
			var recipes = getRecipes(level.getRecipeManager()).get(state.getBlock());

			if (recipes == null) continue;

			for (AncientLightRecipe r : recipes) {
				if (!r.isValid(level, focusPos, state)) continue;
				updateProgressAt(level, p, focusPos, state, r);
				break;
			}
		}

		this.focus.entrySet().removeIf(e -> {
			UUID uuid = e.getKey();
			BlockPos focus = e.getValue();
			Player p = level.getPlayerByUUID(uuid);
			return p == null ||
					p.distanceToSqr(focus.getX() + 0.5, focus.getY() + 0.5, focus.getZ() + 0.5) >=
							AncientLightWandItem.DISTANCE * 1.5;
		});
	}

	protected abstract void updateProgressAt(@NotNull Level level, @NotNull Player p, @NotNull BlockPos focusPos,
	                                         @NotNull BlockState focusBlockState, @NotNull AncientLightRecipe recipe);

	@Override
	public @NotNull CompletableFuture<Void> reload(
			@NotNull PreparationBarrier preparationBarrier, @NotNull ResourceManager resourceManager,
			@NotNull ProfilerFiller preparationsProfiler, @NotNull ProfilerFiller reloadProfiler,
			@NotNull Executor backgroundExecutor, @NotNull Executor gameExecutor) {
		return CompletableFuture.runAsync(() -> this.recipes = null, gameExecutor);
	}

	private @NotNull Map<Block, List<AncientLightRecipe>> getRecipes(RecipeManager recipeManager) {
		if (this.recipes == null) {
			this.recipes = new Object2ObjectOpenHashMap<>();
			recipeManager.getAllRecipesFor(ModRecipes.ANCIENT_LIGHT_TYPE.get()).stream()
					.map(RecipeHolder::value)
					.forEach(r -> {
						for (Holder<Block> h : r.blocks()) {
							this.recipes.computeIfAbsent(h.value(), b -> new ArrayList<>()).add(r);
						}
					});
		}
		return this.recipes;
	}

	protected static int getProgress(@NotNull Player player) {
		if (player.isUsingItem()) {
			ItemStack stack = player.getUseItem();
			if (stack.is(Wands.ANCIENT_LIGHT.asItem()) &&
					AugmentLogic.getAugments(stack).has(Augments.ACCELERATION_1.augment())) {
				return PROGRESS_PER_UPDATE_ACCELERATED;
			}
		}
		return PROGRESS_PER_UPDATE;
	}
}
