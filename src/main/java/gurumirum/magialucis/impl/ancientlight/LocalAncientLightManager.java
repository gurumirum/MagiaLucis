package gurumirum.magialucis.impl.ancientlight;

import gurumirum.magialucis.contents.recipe.ancientlight.AncientLightRecipe;
import gurumirum.magialucis.net.msgs.SetBeamCraftingInfoMsg;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class LocalAncientLightManager extends AncientLightManager {
	public @Nullable AncientLightRecord record() {
		return this.record;
	}

	public @NotNull AncientLightRecord getOrCreateRecord() {
		if (this.record == null) this.record = new AncientLightRecord();
		return this.record;
	}

	@Override
	public void setFocus(@NotNull Player player, @Nullable BlockPos blockPos) {
		if (Minecraft.getInstance().isLocalPlayer(player.getUUID())) {
			if (Objects.equals(getFocus(player), blockPos)) return;
			PacketDistributor.sendToServer(new SetBeamCraftingInfoMsg(blockPos));
		}
		super.setFocus(player, blockPos);
	}

	@Override
	protected void updateProgressAt(@NotNull Level level, @NotNull Player p, @NotNull BlockPos focusPos,
	                                @NotNull BlockState focusBlockState, @NotNull AncientLightRecipe recipe) {
		if (this.record == null) this.record = new AncientLightRecord();

		// keep counting on, it should be corrected by sync anyway
		int progress = this.record.getProgress(focusPos) + getProgress(p);
		int totalProgress = this.record.getTotalProgress(focusPos);

		this.record.setProgress(focusPos, progress, totalProgress <= 0 ?
				recipe.getProcessTicks(level, focusPos, focusBlockState) : totalProgress);
	}
}
