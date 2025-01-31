package gurumirum.magialucis.contents.block;

import gurumirum.magialucis.contents.ModBlockEntities;
import gurumirum.magialucis.impl.field.FieldInstance;
import gurumirum.magialucis.impl.field.FieldManager;
import gurumirum.magialucis.impl.field.Fields;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class AmberLampBlockEntity extends BlockEntityBase {
	public AmberLampBlockEntity(BlockPos pos, BlockState blockState) {
		super(ModBlockEntities.AMBER_LAMP.get(), pos, blockState);
	}

	@Override public void onLoad() {
		super.onLoad();
		if (this.level != null && !this.level.isClientSide) {
			FieldInstance inst = FieldManager.tryGetField(this.level, Fields.AMBER_LAMP);
			if (inst != null) {
				inst.add(getBlockPos());
				this.level.scheduleTick(getBlockPos(), getBlockState().getBlock(), AmberLampBlock.TICK_CYCLE);
			}
		}
	}
}
