package gurumirum.magialucis.contents;

import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.capability.GemStats;
import gurumirum.magialucis.capability.LuxStat;
import gurumirum.magialucis.contents.block.lux.ambercore.AmberCoreBlock;
import gurumirum.magialucis.contents.block.lux.charger.ChargerBlockEntity;
import gurumirum.magialucis.contents.block.lux.charger.ChargerBehavior;
import gurumirum.magialucis.contents.block.lux.charger.RemoteChargerBlockEntity;
import gurumirum.magialucis.impl.luxnet.behavior.LuxNodeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Objects;

public enum ChargerTier {
	PRIMITIVE(AmberCoreBlock.STAT),
	LUMINOUS(GemStats.BRIGHTSTONE),
	LUSTROUS(GemStats.PURIFIED_QUARTZ);

	private final LuxStat stat;
	private final LuxNodeType<ChargerBehavior> chargerBehaviorType;
	private final LuxNodeType<ChargerBehavior> remoteChargerBehaviorType;

	ChargerTier(@NotNull LuxStat stat) {
		this.stat = Objects.requireNonNull(stat);

		String name = name().toLowerCase(Locale.ROOT);
		this.chargerBehaviorType = new LuxNodeType.Simple<>(
				MagiaLucisMod.id(name + "_charger"),
				ChargerBehavior.class,
				() -> new ChargerBehavior(this, false));
		this.remoteChargerBehaviorType = new LuxNodeType.Simple<>(
				MagiaLucisMod.id(name + "_remote_charger"),
				ChargerBehavior.class,
				() -> new ChargerBehavior(this, true));
	}

	public LuxStat stat() {
		return this.stat;
	}

	public LuxNodeType<ChargerBehavior> chargerBehaviorType(boolean remote) {
		return remote ? this.remoteChargerBehaviorType : this.chargerBehaviorType;
	}

	public BlockEntityType<ChargerBlockEntity> chargerBlockEntityType() {
		return switch (this) {
			case PRIMITIVE -> ModBlockEntities.AMBER_CHARGER.get();
			case LUMINOUS -> ModBlockEntities.LUMINOUS_CHARGER.get();
			default -> throw new IllegalStateException(
					"Charger type " + this + " does not have charger variant");
		};
	}

	public BlockEntityType<RemoteChargerBlockEntity> remoteChargerBlockEntityType() {
		return switch (this) {
			case LUMINOUS -> ModBlockEntities.LUMINOUS_REMOTE_CHARGER.get();
			case LUSTROUS -> ModBlockEntities.LUSTROUS_REMOTE_CHARGER.get();
			default -> throw new IllegalStateException(
					"Charger type " + this + " does not have remote charger variant");
		};
	}
}
