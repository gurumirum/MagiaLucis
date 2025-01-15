package gurumirum.magialucis.impl.ancientlight;

import gurumirum.magialucis.contents.Wands;
import gurumirum.magialucis.contents.item.wand.AncientLightWandItem;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

public abstract class AncientLightManager {
	protected static final int UPDATE_INTERVAL = 2;
	protected static final int PROGRESS_DECAY_UPDATE_INTERVAL = UPDATE_INTERVAL * 10;
	protected static final int DECAY_AMOUNT_PER_SEC = 5;

	protected final Map<UUID, BlockPos> focus = new Object2ObjectOpenHashMap<>();
	protected @Nullable AncientLightRecord record;

	public void update(@NotNull Level level) {
		long gameTime = level.getGameTime();
		boolean updateTick = gameTime % UPDATE_INTERVAL == 0;
		boolean decayUpdateTick = gameTime % PROGRESS_DECAY_UPDATE_INTERVAL == 0;

		if (updateTick) updateProgress(level, decayUpdateTick);
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

	protected void updateProgress(@NotNull Level level, boolean decayTick) {
		for (Player p : level.players()) {
			if (p.isDeadOrDying() || !p.isUsingItem() || !p.getUseItem().is(Wands.ANCIENT_LIGHT.asItem())) {
				removeFocus(p);
				continue;
			}

			BlockPos focusPos = getFocus(p);
			if (focusPos == null || !level.isLoaded(focusPos)) continue;

			BlockState state = level.getBlockState(focusPos);
			AncientLightRecipe recipe = AncientLightCrafting.getRecipes().get(state);
			if (recipe == null) continue;

			updateProgressAt(level, p, focusPos, recipe, decayTick);
		}

		this.focus.entrySet().removeIf(e -> {
			UUID uuid = e.getKey();
			BlockPos focus = e.getValue();
			Player p = level.getPlayerByUUID(uuid);
			return p == null ||
					p.distanceToSqr(focus.getX() + 0.5, focus.getY() + 0.5, focus.getZ() + 0.5) >=
							AncientLightWandItem.DISTANCE * 1.5;
		});

		if (decayTick) applyDecay(level);
	}

	protected abstract void updateProgressAt(@NotNull Level level, Player p, BlockPos focusPos,
	                                         AncientLightRecipe recipe, boolean decayTick);

	protected void applyDecay(@NotNull Level level) {
		if (this.record != null) this.record.applyDecay(DECAY_AMOUNT_PER_SEC);
	}
}
