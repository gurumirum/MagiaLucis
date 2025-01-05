package gurumirum.gemthing.contents.block.lux.remotecharger;

import gurumirum.gemthing.GemthingMod;
import gurumirum.gemthing.capability.LuxAcceptor;
import gurumirum.gemthing.capability.LuxStat;
import gurumirum.gemthing.capability.ModCapabilities;
import gurumirum.gemthing.contents.ModBlockEntities;
import gurumirum.gemthing.contents.block.Ticker;
import gurumirum.gemthing.contents.block.lux.LuxNodeBlockEntity;
import gurumirum.gemthing.impl.LuxConsumerNodeInterface;
import gurumirum.gemthing.impl.LuxNet;
import gurumirum.gemthing.impl.LuxNode;
import gurumirum.gemthing.impl.RGB332;
import gurumirum.gemthing.utils.LuxUtils;
import gurumirum.gemthing.utils.TagUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

import java.text.DecimalFormat;

public class RemoteChargerBlockEntity extends LuxNodeBlockEntity implements Ticker, LuxConsumerNodeInterface {
	private static final int CYCLE = 10;

	public static RemoteChargerBlockEntity basic(BlockPos pos, BlockState blockState) {
		return new RemoteChargerBlockEntity(ModBlockEntities.REMOTE_CHARGER.get(), pos, blockState, RemoteChargerBlock.BASIC_STAT);
	}

	public static RemoteChargerBlockEntity advanced(BlockPos pos, BlockState blockState) {
		return new RemoteChargerBlockEntity(ModBlockEntities.REMOTE_CHARGER_2.get(), pos, blockState, RemoteChargerBlock.ADVANCED_STAT);
	}

	private final LuxStat stat;

	private final Vector3d charge = new Vector3d();

	private final Vector3d maxChargeRate = new Vector3d();
	private final Vector3d maxChargeStorage = new Vector3d();

	private final Vector3d acceptedCache = new Vector3d();
	private final Vector3d chargeLeftCache = new Vector3d();

	public RemoteChargerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState, LuxStat stat) {
		super(type, pos, blockState);
		this.stat = stat;
		this.maxChargeRate.set(
				stat.maxLuxThreshold() * RGB332.rBrightness(stat.color()),
				stat.maxLuxThreshold() * RGB332.gBrightness(stat.color()),
				stat.maxLuxThreshold() * RGB332.bBrightness(stat.color()));
		LuxUtils.snapComponents(this.maxChargeRate, 0);
		this.maxChargeStorage.set(this.maxChargeRate).mul(100);
	}

	@Override
	protected void initializeLuxNodeData(@NotNull LuxNet luxNet, @NotNull LuxNode node) {
		node.setStats(this.stat);
	}

	@Override
	public void updateServer(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state) {
		if (level.getGameTime() % CYCLE != 0) return;
		if (this.charge.x <= 0 && this.charge.y <= 0 && this.charge.z <= 0) return;

		Vector3d charge = this.chargeLeftCache;
		charge.set(this.maxChargeRate).min(this.charge);
		this.charge.sub(charge);

		for (Player p : level.getEntities(EntityTypeTest.forClass(Player.class), new AABB(pos).inflate(10), e -> true)) {
			Inventory inv = p.getInventory();
			for (int i = 0; i < 9; i++) {
				if (chargeItem(charge, inv.getItem(i))) {
					if (charge.x <= 0 && charge.y <= 0 && charge.z <= 0) return;
				}
			}
		}

		setChanged();
	}

	private boolean chargeItem(Vector3d charge, ItemStack stack) {
		LuxAcceptor luxAcceptor = stack.getCapability(ModCapabilities.LUX_ACCEPTOR);
		boolean success = false;
		if (luxAcceptor != null) {
			luxAcceptor.accept(
					Math.min(charge.x, this.maxChargeRate.x),
					Math.min(charge.y, this.maxChargeRate.y),
					Math.min(charge.z, this.maxChargeRate.z),
					false,
					this.acceptedCache.zero());
			if (LuxUtils.isValid(this.acceptedCache)) {
				if (!(this.acceptedCache.x <= 0) || !(this.acceptedCache.y <= 0) || !(this.acceptedCache.z <= 0)) {
					LuxUtils.snapComponents(this.acceptedCache, 0);
					GemthingMod.LOGGER.warn("Charged {}, max charge rate: {}, charges left: {}",
							this.acceptedCache.toString(new DecimalFormat("0")),
							this.maxChargeRate.toString(new DecimalFormat("0")),
							charge.toString(new DecimalFormat("0")));
					charge.sub(this.acceptedCache.min(this.maxChargeRate));
					success = true;
				}
			} else {
				GemthingMod.LOGGER.warn("Lux acceptor capability of item stack {} returned an invalid result!", stack);
			}
		}
		return success;
	}

	@Override
	public void consumeLux(Vector3d lux) {
		LuxUtils.transfer(lux, this.charge, this.maxChargeStorage);
		setChanged();
	}

	@Override
	public void updateLink(LuxNet luxNet, LuxNet.LinkCollector linkCollector) {}

	@Override
	protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider) {
		super.saveAdditional(tag, lookupProvider);
		TagUtils.writeVector3d(tag, "charge", this.charge);
	}

	@Override
	protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider) {
		super.loadAdditional(tag, lookupProvider);
		TagUtils.readVector3d(tag, "charge", this.charge);
	}
}
