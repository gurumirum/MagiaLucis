package gurumirum.magialucis.contents.block.lux.lightloom;

import gurumirum.magialucis.client.render.RenderEffects;
import gurumirum.magialucis.contents.ModBlocks;
import gurumirum.magialucis.contents.block.ModBlockStateProps;
import gurumirum.magialucis.contents.block.Ticker;
import gurumirum.magialucis.contents.block.lux.LuxNodeBlockEntity;
import gurumirum.magialucis.impl.luxnet.LuxNet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class LightLoomBlockEntity extends LuxNodeBlockEntity<LightLoomBehavior>
		implements Ticker.Client {
	private static final float VELOCITY_IDLE = 0.5f;
	private static final float VELOCITY_WORKING = 54;
	private static final float ACCELERATION = 0.5f;

	private final LightLoomType type;

	private float clientSideAngle;
	private float clientSideAngleO;
	private float clientSideAVelocity;
	private boolean clientSideActive;

	public LightLoomBlockEntity(LightLoomType type, BlockPos pos, BlockState blockState) {
		super(type.blockEntityType(), pos, blockState);
		this.type = type;
	}

	public LightLoomType type() {
		return this.type;
	}

	public @NotNull Vector3d luxInput(@NotNull Vector3d dest) {
		return nodeBehavior().luxInput.min(dest);
	}

	public float clientSideAngle(float partialTick) {
		return Mth.rotLerp(partialTick, this.clientSideAngleO, this.clientSideAngle);
	}

	public boolean clientSideActive() {
		return this.clientSideActive;
	}

	@Override
	public void onLoad() {
		super.onLoad();
		if (this.level != null && this.level.isClientSide) {
			RenderEffects.light.register(new LightLoomBlockLightEffectProvider(this));
		}
	}

	@Override
	protected @NotNull LightLoomBehavior createNodeBehavior() {
		return new LightLoomBehavior(this.type);
	}

	@Override
	public void updateClient(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state) {
		boolean working = false;

		Direction facing = state.getValue(HORIZONTAL_FACING);
		BlockPos artisanryTablePos = pos.below().relative(facing);
		BlockState artisanryTableState = level.getBlockState(artisanryTablePos);

		if (artisanryTableState.is(ModBlocks.ARTISANRY_TABLE.block()) &&
				artisanryTableState.getValue(ModBlockStateProps.LEFT) &&
				artisanryTableState.getValue(HORIZONTAL_FACING) == facing.getClockWise()) {
			working = artisanryTableState.getValue(ModBlockStateProps.WORKING);
		}

		float targetVelocity = working ? VELOCITY_WORKING : VELOCITY_IDLE;
		float diff = targetVelocity - this.clientSideAVelocity;
		this.clientSideAVelocity += Math.signum(diff) * Math.min(Math.abs(diff), ACCELERATION);

		this.clientSideAngleO = this.clientSideAngle;
		this.clientSideAngle = Mth.wrapDegrees(this.clientSideAngle + this.clientSideAVelocity);
		this.clientSideActive = working;
	}

	@Override
	public void updateLink(LuxNet luxNet, LuxNet.LinkCollector linkCollector) {}
}
