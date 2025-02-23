package gurumirum.magialucis.contents.block.lux.charger;

import gurumirum.magialucis.api.MagiaLucisApi;
import gurumirum.magialucis.api.capability.LuxStat;
import gurumirum.magialucis.api.luxnet.behavior.LuxNodeType;
import gurumirum.magialucis.contents.Gem;
import gurumirum.magialucis.contents.ModBlockEntities;
import gurumirum.magialucis.contents.block.lux.ambercore.AmberCoreBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Objects;

public enum ChargerTier {
	PRIMITIVE(AmberCoreBlock.STAT),
	LUMINOUS(Gem.BRIGHTSTONE),
	LUSTROUS(Gem.PURIFIED_QUARTZ);

	private final LuxStat stat;
	private final LuxNodeType.Simple<ChargerBehavior> chargerBehaviorType;
	private final LuxNodeType.Simple<ChargerBehavior> remoteChargerBehaviorType;

	ChargerTier(@NotNull LuxStat stat) {
		this.stat = Objects.requireNonNull(stat);

		String name = name().toLowerCase(Locale.ROOT);
		this.chargerBehaviorType = new LuxNodeType.Simple<ChargerBehavior>(
				MagiaLucisApi.id(name + "_charger"),
				ChargerBehavior.class,
				t -> new ChargerBehavior(this, false));
		this.remoteChargerBehaviorType = new LuxNodeType.Simple<ChargerBehavior>(
				MagiaLucisApi.id(name + "_remote_charger"),
				ChargerBehavior.class,
				t -> new ChargerBehavior(this, true));
	}

	public LuxStat stat() {
		return this.stat;
	}

	public LuxNodeType.Simple<ChargerBehavior> chargerBehaviorType(boolean remote) {
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
