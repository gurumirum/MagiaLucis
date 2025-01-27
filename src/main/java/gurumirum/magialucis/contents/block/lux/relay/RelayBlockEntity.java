package gurumirum.magialucis.contents.block.lux.relay;

import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.capability.DirectLinkDestination;
import gurumirum.magialucis.capability.LinkDestination;
import gurumirum.magialucis.capability.LuxStat;
import gurumirum.magialucis.capability.ModCapabilities;
import gurumirum.magialucis.client.render.RenderEffects;
import gurumirum.magialucis.client.render.light.BlockLightEffectProvider;
import gurumirum.magialucis.client.render.prism.RelayBlockPrismEffect;
import gurumirum.magialucis.contents.ModBlockEntities;
import gurumirum.magialucis.contents.ModDataComponents;
import gurumirum.magialucis.contents.block.lux.BasicRelayBlockEntity;
import gurumirum.magialucis.impl.luxnet.LinkContext;
import gurumirum.magialucis.impl.luxnet.LinkDestinationSelector;
import gurumirum.magialucis.impl.luxnet.ServerSideLinkContext;
import gurumirum.magialucis.impl.luxnet.behavior.DynamicLuxNodeBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
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

public class RelayBlockEntity extends BasicRelayBlockEntity<DynamicLuxNodeBehavior> implements DirectLinkDestination, LinkDestinationSelector {
	private ItemStack stack = ItemStack.EMPTY;

	public RelayBlockEntity(BlockPos pos, BlockState blockState) {
		super(ModBlockEntities.RELAY.get(), pos, blockState);
	}

	public ItemStack stack() {
		return this.stack;
	}

	public void setStack(ItemStack stack) {
		this.stack = stack;
		if (luxNodeId() != NO_ID) {
			nodeBehavior().setStats(Objects.requireNonNullElse(
					stack.getCapability(ModCapabilities.GEM_STAT), LuxStat.NULL));
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
		return new DynamicLuxNodeBehavior(this.stack.getCapability(ModCapabilities.GEM_STAT));
	}

	@Override
	public @Nullable LinkDestinationSelector linkDestinationSelector() {
		return this;
	}

	@Override
	public @Nullable LinkDestination chooseLinkDestination(@NotNull Level level,
	                                                       @Nullable ServerSideLinkContext context,
	                                                       @NotNull BlockHitResult hitResult) {
		Direction baseDirection = getBlockState().getValue(BlockStateProperties.FACING).getOpposite();

		BlockPos pos = getBlockPos();
		Vec3 loc = hitResult.getLocation();
		Vector3f v = new Vector3f().set(
						loc.x - pos.getX() - 0.5,
						loc.y - pos.getY() - 0.5,
						loc.z - pos.getZ() - 0.5)
				.rotate(baseDirection.getRotation().invert());

		final double angle = Math.cos(Math.PI / 4) + 0.01;

		Vector2f v2 = new Vector2f(v.x, v.y).normalize();
		if (v2.dot(new Vector2f(0, 1)) >= angle) {
			v2.set(v.z, v.y).normalize();
			if (v2.dot(new Vector2f(0, 1)) >= angle) {
				return null;
			}
		}

		return level.getCapability(ModCapabilities.LINK_DESTINATION, hitResult.getBlockPos(), hitResult.getDirection());
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
		RelayItemData relayItemData = componentInput.get(ModDataComponents.RELAY_ITEM.get());
		this.stack = relayItemData != null ? relayItemData.stack().copy() : ItemStack.EMPTY;
	}

	@Override
	protected void collectImplicitComponents(DataComponentMap.@NotNull Builder components) {
		super.collectImplicitComponents(components);
		if (!this.stack.isEmpty())
			components.set(ModDataComponents.RELAY_ITEM.get(), new RelayItemData(this.stack.copy()));
	}

	@Override
	public void removeComponentsFromTag(CompoundTag tag) {
		super.removeComponentsFromTag(tag);
		tag.remove("item");
	}
}
