package gurumirum.magialucis.contents.block.lux.relay;

import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.api.capability.DirectLinkDestination;
import gurumirum.magialucis.api.capability.LinkDestination;
import gurumirum.magialucis.capability.ModCapabilities;
import gurumirum.magialucis.client.render.RenderEffects;
import gurumirum.magialucis.client.render.light.BlockLightEffectProvider;
import gurumirum.magialucis.contents.ModBlockEntities;
import gurumirum.magialucis.contents.ModDataComponents;
import gurumirum.magialucis.contents.block.GemContainerBlock;
import gurumirum.magialucis.contents.block.lux.BasicRelayBlockEntity;
import gurumirum.magialucis.contents.data.GemItemData;
import gurumirum.magialucis.contents.data.GemStatLogic;
import gurumirum.magialucis.api.luxnet.LinkContext;
import gurumirum.magialucis.api.luxnet.LinkDestinationSelector;
import gurumirum.magialucis.api.luxnet.ServerSideLinkContext;
import gurumirum.magialucis.impl.luxnet.behavior.DynamicLuxNodeBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.Objects;

public class RelayBlockEntity extends BasicRelayBlockEntity<DynamicLuxNodeBehavior>
		implements DirectLinkDestination, LinkDestinationSelector, GemContainerBlock.GemContainer {
	private ItemStack stack = ItemStack.EMPTY;

	public RelayBlockEntity(BlockPos pos, BlockState blockState) {
		super(ModBlockEntities.RELAY.get(), pos, blockState);
	}

	@Override
	public @NotNull ItemStack stack() {
		return this.stack;
	}

	@Override
	public void setStack(@NotNull ItemStack stack) {
		this.stack = stack;
		if (luxNodeId() != NO_ID) {
			nodeBehavior().setStats(GemStatLogic.getOrDefault(stack));
		}
		setChanged();
		syncToClient();
	}

	@Override
	public void onLoad() {
		super.onLoad();
		if (this.level != null && this.level.isClientSide) {
			RenderEffects.light.register(new BlockLightEffectProvider<>(this));
			RenderEffects.prism.register(new RelayBlockPrismEffect(this));
		}
	}

	@Override
	protected @NotNull DynamicLuxNodeBehavior createNodeBehavior() {
		return new DynamicLuxNodeBehavior(GemStatLogic.get(this.stack));
	}

	@Override
	public @Nullable LinkDestinationSelector linkDestinationSelector() {
		return this;
	}

	@Override
	public @Nullable LinkDestination chooseLinkDestination(@NotNull Level level,
	                                                       @Nullable ServerSideLinkContext context,
	                                                       @NotNull BlockHitResult hitResult) {
		return testLinkDirection(hitResult.getLocation()) ?
				level.getCapability(ModCapabilities.LINK_DESTINATION, hitResult.getBlockPos(), hitResult.getDirection()) :
				null;
	}

	@SuppressWarnings("SuspiciousNameCombination")
	private boolean testLinkDirection(Vec3 connectLocation) {
		Direction dir = getBlockState().getValue(BlockStateProperties.FACING);

		BlockPos pos = getBlockPos();
		Vector3f v = new Vector3f().set(
				pos.getX() + 0.5 - connectLocation.x,
				pos.getY() + 0.5 - connectLocation.y,
				pos.getZ() + 0.5 - connectLocation.z
		);

		Direction.Axis axis = dir.getAxis();
		return (axis != Direction.Axis.X && testAngle(v.y, v.z, dir.getStepY(), dir.getStepZ())) ||
				(axis != Direction.Axis.Y && testAngle(v.x, v.z, dir.getStepX(), dir.getStepZ())) ||
				(axis != Direction.Axis.Z && testAngle(v.x, v.y, dir.getStepX(), dir.getStepY()));
	}

	private static boolean testAngle(float v1x, float v1y, float v2x, float v2y) {
		final float angleCos = (float)(Math.cos(Math.PI / 4) + 0.1);

		if (v1x == 0 && v1y == 0) return true;

		float dot = new Vector2f(v1x, v1y).normalize().dot(new Vector2f(v2x, v2y));
		return dot <= angleCos;
	}

	@Override
	public @NotNull LinkTestResult linkWithSource(@NotNull LinkContext context) {
		if (context.side() != null && context.side() == getBlockState()
				.getValue(BlockStateProperties.FACING)
				.getOpposite()) {
			return LinkTestResult.reject();
		}
		return LinkTestResult.linkable(luxNodeId());
	}

	@Override
	public @NotNull Vec3 linkOrigin() {
		Vec3i n = getBlockState().getValue(BlockStateProperties.FACING).getNormal();
		return Vec3.atLowerCornerWithOffset(getBlockPos(),
				0.5 + n.getX() * 0.01,
				0.5 + n.getY() * 0.01,
				0.5 + n.getZ() * 0.01);
	}

	@Override
	public @NotNull LinkTestResult directLinkWithSource(@NotNull LinkContext context) {
		if (context.side() != null && context.side() != getBlockState()
				.getValue(BlockStateProperties.FACING)
				.getOpposite()) {
			return LinkTestResult.reject();
		}
		return LinkTestResult.linkable(luxNodeId());
	}

	@Override
	protected void save(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider, SaveLoadContext context) {
		super.save(tag, lookupProvider, context);

		if (!this.stack.isEmpty()) {
			tag.put("item", this.stack.save(lookupProvider));
		}
	}

	@Override
	protected void load(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider, SaveLoadContext context) {
		super.load(tag, lookupProvider, context);

		if (tag.contains("item")) {
			this.stack = ItemStack.parse(lookupProvider, Objects.requireNonNull(tag.get("item"))).orElseGet(() -> {
				MagiaLucisMod.LOGGER.error("Cannot parse item of a relay");
				return ItemStack.EMPTY;
			});
		} else {
			this.stack = ItemStack.EMPTY;
		}
	}

	@Override
	protected void applyImplicitComponents(BlockEntity.@NotNull DataComponentInput componentInput) {
		super.applyImplicitComponents(componentInput);
		GemItemData gemItemData = componentInput.get(ModDataComponents.GEM_ITEM.get());
		this.stack = gemItemData != null ? gemItemData.stack().copy() : ItemStack.EMPTY;
	}

	@Override
	protected void collectImplicitComponents(DataComponentMap.@NotNull Builder components) {
		super.collectImplicitComponents(components);
		if (!this.stack.isEmpty())
			components.set(ModDataComponents.GEM_ITEM.get(), new GemItemData(this.stack.copy()));
	}

	@Override
	public void removeComponentsFromTag(CompoundTag tag) {
		super.removeComponentsFromTag(tag);
		tag.remove("item");
	}
}
