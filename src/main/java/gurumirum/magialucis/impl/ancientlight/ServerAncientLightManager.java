package gurumirum.magialucis.impl.ancientlight;

import gurumirum.magialucis.net.msgs.SyncAncientLightProgressMsg;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ServerAncientLightManager extends AncientLightManager {
	protected @Nullable AncientLightRecord lastSyncedRecord;

	public @Nullable AncientLightRecord lastSyncedRecord() {
		return this.lastSyncedRecord;
	}

	@Override
	protected void updateProgressAt(@NotNull Level level, Player p, BlockPos focusPos,
	                                AncientLightRecipe recipe, boolean decayTick) {
		if (this.record == null) this.record = new AncientLightRecord();

		int progress = this.record.getProgress(focusPos) + UPDATE_INTERVAL;
		if (progress >= recipe.processTicks()) {
			this.record.removeProgress(focusPos);
			removeFocus(p);
			level.destroyBlock(focusPos, false);

			for (ItemStack s : recipe.output()) {
				ItemEntity itemEntity = new ItemEntity(level,
						focusPos.getX() + .5, focusPos.getY() + .5, focusPos.getZ() + .5,
						s.copy());
				level.addFreshEntity(itemEntity);
			}
		} else {
			// sus logic to counteract decay (im lazy)
			this.record.setProgress(focusPos,
					decayTick ? progress + DECAY_AMOUNT_PER_SEC : progress,
					recipe.processTicks());
		}
	}

	@Override
	protected void applyDecay(@NotNull Level level) {
		super.applyDecay(level);

		if (this.record != null && level instanceof ServerLevel serverLevel) {
			for (ServerPlayer p : serverLevel.players()) {
				SyncAncientLightProgressMsg msg = this.record.createUpdatePacket(p.getChunkTrackingView(), this.lastSyncedRecord);
				if (msg != null) PacketDistributor.sendToPlayer(p, msg);
			}

			if (this.lastSyncedRecord == null) this.lastSyncedRecord = new AncientLightRecord();

			this.lastSyncedRecord.copyFrom(this.record);
		}
	}
}
