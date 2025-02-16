package gurumirum.magialucis.contents.block.lux.lightloom;

import gurumirum.magialucis.api.MagiaLucisApi;
import gurumirum.magialucis.api.capability.LuxStat;
import gurumirum.magialucis.client.Textures;
import gurumirum.magialucis.contents.ModBlockEntities;
import gurumirum.magialucis.contents.ModBlocks;
import gurumirum.magialucis.contents.block.lux.sunlight.core.MoonlightCoreBlock;
import gurumirum.magialucis.contents.block.lux.sunlight.core.SunlightCoreBlock;
import gurumirum.magialucis.api.luxnet.behavior.LuxNodeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Locale;
import java.util.Objects;

public enum LightLoomType {
	CITRINE(SunlightCoreBlock.STAT),
	IOLITE(MoonlightCoreBlock.STAT);

	private final LuxStat luxStat;
	private final LuxNodeType<LightLoomBehavior> behaviorType;

	LightLoomType(LuxStat luxStat) {
		this.luxStat = Objects.requireNonNull(luxStat);

		String name = name().toLowerCase(Locale.ROOT);
		this.behaviorType = new LuxNodeType.Simple<>(
				MagiaLucisApi.id(name + "_lightloom"),
				LightLoomBehavior.class,
				() -> new LightLoomBehavior(this));
	}

	public LuxStat luxStat() {
		return this.luxStat;
	}

	public LuxNodeType<LightLoomBehavior> behaviorType() {
		return this.behaviorType;
	}

	public ResourceLocation matrixTexture() {
		return switch (this) {
			case CITRINE -> Textures.CITRINE_MATRIX;
			case IOLITE -> Textures.IOLITE_MATRIX;
		};
	}

	public Block block() {
		return blockProvider().block();
	}

	public BlockItem item() {
		return blockProvider().blockItem();
	}

	private ModBlocks blockProvider() {
		return switch (this) {
			case CITRINE -> ModBlocks.CITRINE_LIGHTLOOM;
			case IOLITE -> ModBlocks.IOLITE_LIGHTLOOM;
		};
	}

	public BlockEntityType<LightLoomBlockEntity> blockEntityType() {
		return switch (this) {
			case CITRINE -> ModBlockEntities.CITRINE_LIGHTLOOM.get();
			case IOLITE -> ModBlockEntities.IOLITE_LIGHTLOOM.get();
		};
	}
}
