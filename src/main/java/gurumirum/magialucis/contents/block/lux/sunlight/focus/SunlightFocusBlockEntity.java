package gurumirum.magialucis.contents.block.lux.sunlight.focus;

import gurumirum.magialucis.capability.LinkDestination;
import gurumirum.magialucis.contents.ModBlockEntities;
import gurumirum.magialucis.contents.block.Ticker;
import gurumirum.magialucis.contents.block.lux.BasicRelayBlockEntity;
import gurumirum.magialucis.contents.block.lux.sunlight.SunlightLogic;
import gurumirum.magialucis.contents.block.lux.sunlight.core.BaseSunlightCoreBlockEntity;
import gurumirum.magialucis.impl.luxnet.LinkContext;
import gurumirum.magialucis.impl.luxnet.LinkDestinationSelector;
import gurumirum.magialucis.impl.luxnet.ServerSideLinkContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static gurumirum.magialucis.contents.block.ModBlockStateProps.SKY_VISIBILITY;

public class SunlightFocusBlockEntity extends BasicRelayBlockEntity<SunlightFocusBehavior> implements LinkDestinationSelector, Ticker.Server {
	public static final double LINK_DISTANCE = 10;

	private static final int CYCLE = 50;

	public SunlightFocusBlockEntity(BlockPos pos, BlockState blockState) {
		super(ModBlockEntities.SUNLIGHT_FOCUS.get(), pos, blockState);
	}

	@Override
	public int maxLinks() {
		return 1;
	}

	@Override
	public double linkDistance() {
		return LINK_DISTANCE;
	}

	@Override
	public void updateServer(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state) {
		if (level.getGameTime() % CYCLE != 0) return;

		int skyVisibility = SunlightLogic.calculateSkyVisibility(level, pos, state);
		if (updateProperty(SKY_VISIBILITY, skyVisibility)) {
			nodeBehavior().setSkyVisibility(skyVisibility);
		}
	}

	@Override
	public @Nullable LinkDestinationSelector linkDestinationSelector() {
		return this;
	}

	@Override
	protected @NotNull SunlightFocusBehavior createNodeBehavior() {
		return new SunlightFocusBehavior(getBlockPos(), getBlockState().getValue(SKY_VISIBILITY));
	}

	@Override
	public @NotNull LinkDestination.LinkTestResult linkWithSource(@NotNull LinkContext context) {
		return LinkTestResult.reject(); // cannot connect
	}

	@Override
	public @Nullable LinkDestination chooseLinkDestination(@NotNull Level level,
	                                                       @Nullable ServerSideLinkContext context,
	                                                       @NotNull BlockHitResult hitResult) {
		if (hitResult.getBlockPos().getY() < this.getBlockPos().getY()) return null;
		return level.getBlockEntity(hitResult.getBlockPos()) instanceof BaseSunlightCoreBlockEntity<?> sunlightCore ?
				sunlightCore : null;
	}
}
