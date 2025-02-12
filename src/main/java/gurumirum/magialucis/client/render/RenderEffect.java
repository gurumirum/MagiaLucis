package gurumirum.magialucis.client.render;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public interface RenderEffect {
	boolean isEffectValid();

	void onLevelUnload(LevelAccessor level);

	abstract class LevelBound implements RenderEffect {
		private boolean unloaded;

		protected abstract @Nullable Level getLevel();

		@Override
		public boolean isEffectValid() {
			return !this.unloaded;
		}

		@Override
		public void onLevelUnload(LevelAccessor level) {
			if (level == getLevel()) this.unloaded = true;
		}
	}

	abstract class BlockEntityBound<BE extends BlockEntity> extends LevelBound {
		protected final BE blockEntity;

		public BlockEntityBound(BE blockEntity) {
			this.blockEntity = blockEntity;
		}

		protected @Nullable Level getLevel() {
			return this.blockEntity.getLevel();
		}

		@Override
		public boolean isEffectValid() {
			return super.isEffectValid() && !this.blockEntity.isRemoved();
		}
	}
}
