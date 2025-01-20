package gurumirum.magialucis.contents.block.sunlight.core;

import gurumirum.magialucis.capability.LuxNetLinkDestination;
import gurumirum.magialucis.client.render.light.BlockLightEffectProvider;
import gurumirum.magialucis.client.render.light.LightEffectRender;
import gurumirum.magialucis.contents.block.Ticker;
import gurumirum.magialucis.contents.block.lux.LuxNodeBlockEntity;
import gurumirum.magialucis.contents.block.sunlight.focus.SunlightFocusBlockEntity;
import gurumirum.magialucis.impl.field.Field;
import gurumirum.magialucis.impl.luxnet.LinkContext;
import gurumirum.magialucis.impl.luxnet.LuxNet;
import gurumirum.magialucis.impl.luxnet.LuxUtils;
import gurumirum.magialucis.utils.ServerTickQueue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

import java.util.List;

import static gurumirum.magialucis.contents.block.ModBlockStateProps.OVERSATURATED;

public abstract class BaseSunlightCoreBlockEntity extends LuxNodeBlockEntity implements Ticker.Client {
	public static final double LINK_DISTANCE = 7;

	private double power;

	private float clientSideRotation;
	private float clientSideRotationO;

	public BaseSunlightCoreBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
		super(type, pos, blockState);
	}

	protected abstract @Nullable Field field();

	public float getClientSideRotation(float partialTicks) {
		return Mth.rotLerp(partialTicks, this.clientSideRotationO, this.clientSideRotation);
	}

	@Override
	public void onLoad() {
		super.onLoad();
		if (this.level != null && this.level.isClientSide) {
			LightEffectRender.register(new BlockLightEffectProvider<>(this, 1.5f));
		}
	}

	@Override
	protected void register() {
		super.register();
		Field f = field();
		if (f != null) registerField(f);
	}

	@Override
	protected void unregister(boolean destroyed) {
		super.unregister(destroyed);
		Field f = field();
		if (f != null) unregisterField(f);
	}

	@Override
	protected void setFieldPower(@NotNull Field field, double power) {
		if (field == field()) {
			this.power = power;
			ServerTickQueue.tryEnqueue(this.level, this::updateOversaturatedProperty);
		}
	}

	private void updateOversaturatedProperty() {
		if (!updateProperty(OVERSATURATED, this.power < 1))
			syncToClient();
	}

	@Override
	public void updateClient(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state) {
		this.clientSideRotationO = this.clientSideRotation;
		this.clientSideRotation += Mth.lerp(luxInputPercentage(), minRotation(), maxRotation());
		this.clientSideRotation = Mth.wrapDegrees(this.clientSideRotation);
	}

	protected float minRotation() {
		return 0.5f;
	}

	protected float maxRotation() {
		return 30;
	}

	protected float luxInputPercentage() {
		var max = rMaxTransfer() + gMaxTransfer() + bMaxTransfer();
		if (!(max > 0)) return 0;
		var lux = LuxUtils.sum(luxFlow(new Vector3d()));
		return Math.min(1, (float)(lux / max));
	}

	@Override
	public void updateLink(LuxNet luxNet, LuxNet.LinkCollector linkCollector) {
		float xRot = 0, yRot = 0;

		switch (getBlockState().getValue(BlockStateProperties.FACING)) {
			case DOWN -> xRot = Mth.HALF_PI;
			case UP -> xRot = -Mth.HALF_PI;
			case NORTH -> yRot = Mth.PI;
			case SOUTH -> {}
			case WEST -> yRot = Mth.HALF_PI;
			case EAST -> yRot = -Mth.HALF_PI;
		}

		LuxUtils.linkToInWorldNode(this, linkCollector, xRot, yRot, LINK_DISTANCE,
				0, null);
	}

	@Override
	public @NotNull LuxNetLinkDestination.LinkTestResult linkWithSource(@NotNull LinkContext context) {
		if (!(context.sourceInterface() instanceof SunlightFocusBlockEntity)) return LinkTestResult.reject();
		return LinkTestResult.linkable(luxNodeId());
	}

	@Override
	protected void save(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider, SaveLoadContext context) {
		super.save(tag, lookupProvider, context);
		if (context.isSync()) {
			tag.putDouble("power", this.power);
		}
	}

	@Override
	protected void load(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider, SaveLoadContext context) {
		super.load(tag, lookupProvider, context);
		if (context.isSync()) {
			this.power = tag.getDouble("power");
		}
	}

	@Override
	public void addDebugText(@NotNull List<String> list) {
		super.addDebugText(list);
		list.add("Power: " + this.power);
	}
}
