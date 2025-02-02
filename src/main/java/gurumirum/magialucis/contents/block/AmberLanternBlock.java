package gurumirum.magialucis.contents.block;

import gurumirum.magialucis.contents.ModBlocks;
import gurumirum.magialucis.impl.field.FieldInstance;
import gurumirum.magialucis.impl.field.FieldManager;
import gurumirum.magialucis.impl.field.Fields;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AmberLanternBlock extends BaseLanternBlock.Stateless implements EntityBlock {
	public static final int RANGE = 10;
	public static final int TICK_CYCLE = 20;

	// cringe ass lookup table of each reachable block position offsets

	private static final int[] blockOffsets;
	private static final BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();

	static {
		int rangeSq = RANGE * RANGE;
		int min = -RANGE + 1;
		int max = RANGE - 1;
		IntList ints = new IntArrayList();

		for (int x = min; x <= max; x++) {
			for (int y = min; y <= max; y++) {
				for (int z = min; z <= max; z++) {
					double distSq = x * x + y * y + z * z;
					if (distSq >= rangeSq) continue;

					mpos.set(x, y, z);
					ints.add(pack());
				}
			}
		}

		blockOffsets = ints.toIntArray();
	}

	private static int pack() {
		return Byte.toUnsignedInt((byte)mpos.getX()) << 16 |
				Byte.toUnsignedInt((byte)mpos.getY()) << 8 |
				Byte.toUnsignedInt((byte)mpos.getZ());
	}

	private static void unpack(int pos) {
		mpos.set((byte)(pos >> 16), (byte)(pos >> 8), (byte)pos);
	}

	public AmberLanternBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected void tick(@NotNull BlockState state, @NotNull ServerLevel level,
	                    @NotNull BlockPos pos, @NotNull RandomSource random) {
		// keep on placing lights until it fails enough times or the count reaches hard limit
		int successes = 0;
		int fails = 0;
		for (int i = 0; i < 10 && fails < 5; i++) {
			int b = blockOffsets[random.nextInt(blockOffsets.length)];
			unpack(b);
			mpos.move(pos);

			if (!placeLight(level)) fails++;
			else successes++;
		}

		level.scheduleTick(pos, this, TICK_CYCLE * Math.max(1, 5 - successes));
	}

	private boolean placeLight(@NotNull ServerLevel level) {
		if (!level.isLoaded(mpos)) return false;

		BlockState s2 = level.getBlockState(mpos);
		boolean waterlogged = false;

		if (!s2.isAir()) {
			if (!s2.is(Blocks.WATER)) return false;

			FluidState fluidState = level.getFluidState(mpos);
			if (fluidState.isEmpty() || !fluidState.isSourceOfType(Fluids.WATER)) return false;
			else waterlogged = true;
		}

		if (level.getBrightness(LightLayer.BLOCK, mpos) > 10) return false;

		level.setBlock(mpos, ModBlocks.AMBER_LIGHT.block().defaultBlockState()
				.setValue(ModBlockStateProps.LANTERN, true)
				.setValue(BlockStateProperties.WATERLOGGED, waterlogged), 2);

		return true;
	}

	@Override
	protected void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
	                        @NotNull BlockState newState, boolean movedByPiston) {
		FieldInstance inst = FieldManager.tryGetField(level, Fields.AMBER_LANTERN, false);
		if (inst == null) return;
		inst.remove(pos);
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new AmberLanternBlockEntity(pos, state);
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context,
	                            @NotNull List<Component> tooltip, @NotNull TooltipFlag tooltipFlag) {
		tooltip.add(Component.translatable("block.magialucis.amber_lantern.tooltip.0"));
		tooltip.add(Component.translatable("block.magialucis.amber_lantern.tooltip.1"));
	}
}
