package gurumirum.magialucis.contents.block.lux.sunlight.core;

import gurumirum.magialucis.api.Orientation;
import gurumirum.magialucis.api.capability.LinkDestination;
import gurumirum.magialucis.api.field.Field;
import gurumirum.magialucis.api.luxnet.LinkContext;
import gurumirum.magialucis.api.luxnet.LuxNet;
import gurumirum.magialucis.api.luxnet.LuxNetLinkCollector;
import gurumirum.magialucis.client.render.RenderEffects;
import gurumirum.magialucis.client.render.light.BlockLightEffectProvider;
import gurumirum.magialucis.contents.block.Ticker;
import gurumirum.magialucis.contents.block.lux.LuxNodeBlockEntity;
import gurumirum.magialucis.contents.block.lux.sunlight.focus.SunlightFocusBlockEntity;
import gurumirum.magialucis.impl.ServerTickQueue;
import gurumirum.magialucis.impl.field.FieldListener;
import gurumirum.magialucis.impl.field.FieldManager;
import gurumirum.magialucis.impl.field.ServerFieldInstance;
import gurumirum.magialucis.impl.luxnet.LuxUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

import java.util.List;

import static gurumirum.magialucis.contents.block.ModBlockStates.OVERSATURATED;

public abstract class BaseSunlightCoreBlockEntity<B extends BaseSunlightCoreNodeBehavior> extends LuxNodeBlockEntity<B>
		implements Ticker.Client {
	public static final double LINK_DISTANCE = 7;

	private FieldListener listener = FieldListener.invalid();
	private double clientSidePower;

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
			RenderEffects.light.register(new BlockLightEffectProvider<>(this).sphereSize(1.5f));
			RenderEffects.prism.register(new SunlightCoreBlockPrismEffect(this));
		}
	}

	@Override
	protected void onRegister(@NotNull ServerLevel serverLevel) {
		this.listener.invalidate();

		super.onRegister(serverLevel);

		Field field = field();
		if (field == null) return;

		ServerFieldInstance inst = FieldManager.get(serverLevel).getOrCreate(field);
		this.listener = inst.listener()
				.powerChanged(getBlockPos(), power -> {
					ServerTickQueue.tryEnqueue(this.level, this::updateOversaturatedProperty);
				});
	}

	@Override
	protected void onUnregister(@NotNull ServerLevel serverLevel, @NotNull UnregisterContext context) {
		super.onUnregister(serverLevel, context);
		this.listener.invalidate();
	}

	private void updateOversaturatedProperty() {
		if (!updateProperty(OVERSATURATED, nodeBehavior().fieldPower() < 1))
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
		double max = maxLuxInput();
		if (!(max > 0)) return 0;
		var lux = LuxUtils.sum(luxFlow(new Vector3d()));
		return Math.min(1, (float)(lux / max));
	}

	protected abstract double maxLuxInput();

	@Override
	public void updateLink(LuxNet luxNet, LuxNetLinkCollector linkCollector) {
		Orientation o = Orientation.of(getBlockState().getValue(BlockStateProperties.FACING));
		linkCollector.linkToInWorldNode(this, o,
				Vec3.atCenterOf(getBlockPos()), LINK_DISTANCE, 0, null, 1, true);
	}

	@Override
	public @NotNull LinkDestination.LinkTestResult linkWithSource(@NotNull LinkContext context) {
		if (!(context.sourceInterface() instanceof SunlightFocusBlockEntity)) return LinkTestResult.reject();
		if (context.side() != null && context.side() == getBlockState()
				.getValue(BlockStateProperties.FACING)
				.getOpposite()) {
			return LinkTestResult.reject();
		}
		return LinkTestResult.linkable(luxNodeId());
	}

	@Override
	protected void save(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider, SaveLoadContext context) {
		super.save(tag, lookupProvider, context);
		if (context.isSync()) {
			tag.putDouble("power", nodeBehavior().fieldPower());
		}
	}

	@Override
	protected void load(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider, SaveLoadContext context) {
		super.load(tag, lookupProvider, context);
		if (context.isSync()) {
			this.clientSidePower = tag.getDouble("power");
		}
	}

	@Override
	public void addDebugText(@NotNull List<String> list) {
		super.addDebugText(list);
		list.add("Power: " + this.clientSidePower);
	}
}
