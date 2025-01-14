package gurumirum.magialucis.contents.block.sunlight.core;

import gurumirum.magialucis.capability.GemStats;
import gurumirum.magialucis.capability.LuxNetLinkDestination;
import gurumirum.magialucis.capability.LuxStat;
import gurumirum.magialucis.client.render.light.BlockLightEffectProvider;
import gurumirum.magialucis.client.render.light.LightEffectRender;
import gurumirum.magialucis.contents.ModBlockEntities;
import gurumirum.magialucis.contents.block.Ticker;
import gurumirum.magialucis.contents.block.lux.LuxNodeBlockEntity;
import gurumirum.magialucis.contents.block.sunlight.SunlightLogic;
import gurumirum.magialucis.contents.block.sunlight.focus.SunlightFocusBlockEntity;
import gurumirum.magialucis.impl.field.Field;
import gurumirum.magialucis.impl.field.Fields;
import gurumirum.magialucis.impl.luxnet.*;
import gurumirum.magialucis.utils.ServerTickQueue;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import org.joml.Vector3d;

import java.util.List;

import static gurumirum.magialucis.contents.block.ModBlockStateProps.OVERSATURATED;
import static gurumirum.magialucis.contents.block.ModBlockStateProps.SKY_VISIBILITY;

public class SunlightCoreBlockEntity extends LuxNodeBlockEntity implements LuxSourceNodeInterface, Ticker.Server {
	public static final LuxStat STAT = LuxStat.simple(
			GemStats.CITRINE.color(),
			0, // don't make cores just ignore foci
			GemStats.CITRINE.rMaxTransfer(),
			GemStats.CITRINE.gMaxTransfer(),
			0); // regular sunlight cores cannot receive blue light

	public static final double LINK_DISTANCE = 7;

	private static final int CYCLE = 50;

	private double power;

	public SunlightCoreBlockEntity(BlockPos pos, BlockState blockState) {
		super(ModBlockEntities.SUNLIGHT_CORE.get(), pos, blockState);
	}

	@Override
	public void onLoad() {
		super.onLoad();
		if (this.level != null && this.level.isClientSide) {
			LightEffectRender.register(new BlockLightEffectProvider<>(this));
		}
	}

	@Override
	protected void register() {
		super.register();
		registerField(Fields.SUNLIGHT_CORE);
	}

	@Override
	protected void unregister(boolean destroyed) {
		super.unregister(destroyed);
		unregisterField(Fields.SUNLIGHT_CORE);
	}

	@Override
	protected void setFieldPower(@NotNull Field field, double power) {
		if (field == Fields.SUNLIGHT_CORE) {
			this.power = power;
			ServerTickQueue.tryEnqueue(this.level, this::updateOversaturatedProperty);
		}
	}

	private void updateOversaturatedProperty() {
		if (!updateProperty(OVERSATURATED, getBlockState().getValue(SKY_VISIBILITY) > 0 && this.power < 1))
			syncToClient();
	}

	@Override
	public void updateServer(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state) {
		if (level.getGameTime() % CYCLE != 0) return;

		int skyVisibility = SunlightLogic.calculateSkyVisibility(level, pos, state);
		if (updateProperty(SKY_VISIBILITY, skyVisibility)) {
			if (skyVisibility == 0) unregisterField(Fields.SUNLIGHT_CORE);
			else registerField(Fields.SUNLIGHT_CORE);
		}
	}

	@Override
	public void generateLux(Vector3d dest) {
		int skyVisibility = getBlockState().getValue(SKY_VISIBILITY);
		SunlightLogic.getColor(this.level, getBlockPos(),
				SunlightLogic.DEFAULT_BASE_INTENSITY * (skyVisibility / 16.0) * power, dest);
	}

	@Override
	public @Nullable LuxStat calculateNodeStat(LuxNet luxNet) {
		return STAT;
	}

	@Override
	public void updateLink(LuxNet luxNet, LuxNet.LinkCollector linkCollector) {
		LuxUtils.linkToInWorldNode(this, linkCollector, (float)(Math.PI / 2), 0, LINK_DISTANCE,
				0, null);
	}

	@Override
	public void syncLinkStatus(@NotNull @UnmodifiableView Int2ObjectMap<InWorldLinkState> linkIndexToState) {}

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
