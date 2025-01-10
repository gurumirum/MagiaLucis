package gurumirum.magialucis.contents.block;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

public class PillarOrnamentBlock extends Block {
	private final boolean top;
	private final OrnamentType type;

	public PillarOrnamentBlock(Properties properties, boolean top, OrnamentType type) {
		super(properties);
		this.top = top;
		this.type = type;
	}

	public boolean isTopOrnament() {
		return top;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public @Nullable BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
		Direction clickedFace = context.getClickedFace();

		if (context.isSecondaryUseActive()) return defaultBlockState().setValue(FACING, clickedFace);

		BlockState s1 = context.getLevel().getBlockState(context.getClickedPos().relative(clickedFace));
		BlockState s2 = context.getLevel().getBlockState(context.getClickedPos().relative(clickedFace.getOpposite()));

		Direction facing;

		if (isConnectedPillarBlock(s1, clickedFace)) {
			facing = this.top ? clickedFace.getOpposite() : clickedFace;
		} else if (isConnectedPillarBlock(s2, clickedFace.getOpposite())) {
			facing = this.top ? clickedFace : clickedFace.getOpposite();
		} else {
			facing = clickedFace;
		}

		return defaultBlockState().setValue(FACING, facing);
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context,
	                            @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
		tooltip.add(this.type.description());
	}

	private static boolean isConnectedPillarBlock(BlockState state, Direction direction) {
		if (state.getBlock() instanceof PillarOrnamentBlock pillarOrnamentBlock) {
			return state.getValue(FACING) == (pillarOrnamentBlock.isTopOrnament() ? direction : direction.getOpposite());
		} else {
			return state.getBlock() instanceof RotatedPillarBlock &&
					state.hasProperty(BlockStateProperties.AXIS) &&
					state.getValue(BlockStateProperties.AXIS) == direction.getAxis();
		}
	}

	public enum OrnamentType {
		DORIC,
		IONIC,
		CORINTHIAN,
		IONIC_CORINTHIAN;

		private final Component description = Component.translatable(
						"item.magialucis.lapis_manalis_pillar_ornament.tooltip." + name().toLowerCase(Locale.ROOT))
				.withStyle(ChatFormatting.GRAY);

		public Component description() {
			return this.description;
		}
	}
}
