package gurumirum.magialucis.contents.block.lux.splitter;

import com.google.common.math.IntMath;
import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.capability.LinkSource;
import gurumirum.magialucis.client.render.RenderEffects;
import gurumirum.magialucis.client.render.light.BlockLightEffectProvider;
import gurumirum.magialucis.contents.ModBlockEntities;
import gurumirum.magialucis.contents.ModDataComponents;
import gurumirum.magialucis.contents.block.GemContainerBlock;
import gurumirum.magialucis.contents.block.RelativeDirection;
import gurumirum.magialucis.contents.block.lux.LuxNodeBlockEntity;
import gurumirum.magialucis.contents.block.lux.relay.GemItemData;
import gurumirum.magialucis.impl.GemStatLogic;
import gurumirum.magialucis.impl.luxnet.LinkContext;
import gurumirum.magialucis.impl.luxnet.LuxNet;
import gurumirum.magialucis.impl.luxnet.LuxUtils;
import gurumirum.magialucis.impl.luxnet.behavior.DynamicLuxNodeBehavior;
import gurumirum.magialucis.utils.Orientation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SplitterBlockEntity extends LuxNodeBlockEntity<DynamicLuxNodeBehavior>
		implements GemContainerBlock.GemContainer {
	public static final byte APERTURE_LEVELS = 4;

	private final byte[] apertureLevels = new byte[5];
	private ItemStack stack = ItemStack.EMPTY;

	public SplitterBlockEntity(BlockPos pos, BlockState blockState) {
		super(ModBlockEntities.SPLITTER.get(), pos, blockState);
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

	public byte apertureLevel(@NotNull RelativeDirection dir) {
		return dir == RelativeDirection.BACK ? 0 : this.apertureLevels[toIndex(dir)];
	}

	public void cycleApertureLevel(@NotNull RelativeDirection dir) {
		if (dir == RelativeDirection.BACK) return;
		int i = toIndex(dir);
		this.apertureLevels[i] = (byte)Math.floorMod(this.apertureLevels[i] - 1, APERTURE_LEVELS);
		setChanged();
		syncToClient();
	}

	@Override
	public void onLoad() {
		super.onLoad();
		if (this.level != null && this.level.isClientSide) {
			RenderEffects.cutout.register(new SplitterCutoutEffect(this));
			RenderEffects.light.register(new BlockLightEffectProvider<>(this));
		}
	}

	@Override
	protected @NotNull DynamicLuxNodeBehavior createNodeBehavior() {
		return new DynamicLuxNodeBehavior(GemStatLogic.get(this.stack));
	}

	@Override
	public void updateLink(LuxNet luxNet, LuxNet.LinkCollector linkCollector) {
		Direction facing = getBlockState().getValue(BlockStateProperties.FACING);
		int i = 0;

		for (RelativeDirection dir : RelativeDirection.values()) {
			if (dir == RelativeDirection.BACK) continue;
			byte l = apertureLevel(dir);
			if (l == 0) continue;

			int weight = IntMath.pow(3, l - 1);
			Direction side = RelativeDirection.getSide(facing, dir);

			if (!LuxUtils.directLinkToInWorldNode(this, linkCollector,
					getBlockPos().relative(side), side.getOpposite(), i, null,
					weight, false)) {

				LuxUtils.linkToInWorldNode(this, linkCollector, Orientation.of(side),
						Vec3.atCenterOf(getBlockPos()), LinkSource.DEFAULT_LINK_DISTANCE, i, null,
						weight, true);
			}

			i++;
		}
	}

	@Override
	public @NotNull LinkTestResult linkWithSource(@NotNull LinkContext context) {
		if (context.side() != null && context.side() != getBlockState()
				.getValue(BlockStateProperties.FACING)
				.getOpposite()) {
			return LinkTestResult.reject();
		}
		return LinkTestResult.linkable(luxNodeId());
	}

	@Override
	protected void save(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider lookupProvider, SaveLoadContext context) {
		super.save(tag, lookupProvider, context);

		if (!this.stack.isEmpty()) {
			tag.put("item", this.stack.save(lookupProvider));
		}

		int apertureLevels = 0;
		for (int i = 0; i < this.apertureLevels.length; i++) {
			apertureLevels += this.apertureLevels[i] * IntMath.pow(APERTURE_LEVELS, i);
		}
		tag.putInt("apertureLevels", apertureLevels);
	}

	@Override
	protected void load(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider lookupProvider, SaveLoadContext context) {
		super.load(tag, lookupProvider, context);

		if (tag.contains("item")) {
			this.stack = ItemStack.parse(lookupProvider, Objects.requireNonNull(tag.get("item"))).orElseGet(() -> {
				MagiaLucisMod.LOGGER.error("Cannot parse item of a relay");
				return ItemStack.EMPTY;
			});
		} else {
			this.stack = ItemStack.EMPTY;
		}

		int apertureLevels = tag.getInt("apertureLevels");
		for (int i = 0; i < this.apertureLevels.length; i++) {
			this.apertureLevels[i] = (byte)((apertureLevels / IntMath.pow(APERTURE_LEVELS, i)) % APERTURE_LEVELS);
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

	private static int toIndex(RelativeDirection direction) {
		return switch (direction) {
			case FRONT -> 0;
			case BACK -> -1;
			case U -> 1;
			case D -> 2;
			case L -> 3;
			case R -> 4;
		};
	}
}
