package gurumirum.magialucis.contents.block;

import gurumirum.magialucis.contents.ModBlockEntities;
import gurumirum.magialucis.impl.field.FieldManager;
import gurumirum.magialucis.impl.field.Fields;
import gurumirum.magialucis.impl.field.ServerFieldInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class AmberLanternBlockEntity extends BlockEntityBase {
	public AmberLanternBlockEntity(BlockPos pos, BlockState blockState) {
		super(ModBlockEntities.AMBER_LANTERN.get(), pos, blockState);
	}

	@Override public void onLoad() {
		super.onLoad();
		if (this.level != null && !this.level.isClientSide) {
			ServerFieldInstance inst = FieldManager.tryGetField(this.level, Fields.AMBER_LANTERN);
			if (inst != null) {
				inst.add(getBlockPos());
				this.level.scheduleTick(getBlockPos(), getBlockState().getBlock(), AmberLanternBlock.TICK_CYCLE);
			}
		}
	}
}
