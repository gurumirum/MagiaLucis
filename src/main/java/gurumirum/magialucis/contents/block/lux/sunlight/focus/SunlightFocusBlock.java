package gurumirum.magialucis.contents.block.lux.sunlight.focus;

import gurumirum.magialucis.capability.LuxStat;
import gurumirum.magialucis.contents.block.Ticker;
import gurumirum.magialucis.contents.block.lux.sunlight.SunlightLogic;
import gurumirum.magialucis.impl.LuxStatTooltip;
import gurumirum.magialucis.impl.RGB332;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static gurumirum.magialucis.contents.block.ModBlockStates.SKY_VISIBILITY;

public class SunlightFocusBlock extends Block implements EntityBlock {
	public static final LuxStat STAT = LuxStat.simple(RGB332.WHITE,
			0,
			// use max throughput of foci for the stat
			SunlightLogic.DEFAULT_BASE_INTENSITY,
			SunlightLogic.DEFAULT_BASE_INTENSITY,
			SunlightLogic.DEFAULT_BASE_INTENSITY);

	private static final VoxelShape SHAPE = Shapes.or(
			box(0, 0, 0, 16, 4, 16),
			box(2, 4, 2, 14, 14, 14));

	public SunlightFocusBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(SKY_VISIBILITY, 15));
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new SunlightFocusBlockEntity(pos, state);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(SKY_VISIBILITY);
	}

	@Override
	protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level,
	                                       @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return SHAPE;
	}

	@Override
	protected @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
		return RenderShape.INVISIBLE;
	}

	@Override
	protected boolean useShapeForLightOcclusion(@NotNull BlockState state) {
		return true;
	}

	@Override
	public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level,
	                                                                        @NotNull BlockState state,
	                                                                        @NotNull BlockEntityType<T> blockEntityType) {
		return Ticker.server(level);
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context,
	                            @NotNull List<Component> tooltip, @NotNull TooltipFlag tooltipFlag) {
		tooltip.add(Component.translatable("block.magialucis.sunlight_focus.tooltip.0"));
		tooltip.add(Component.translatable("item.magialucis.tooltip.link_source"));
		tooltip.add(Component.translatable("block.magialucis.sunlight_focus.tooltip.1"));
		tooltip.add(Component.translatable("block.magialucis.sunlight_focus.tooltip.2"));

		LuxStatTooltip.formatStat(STAT, tooltip, LuxStatTooltip.Type.SOURCE);
	}
}
