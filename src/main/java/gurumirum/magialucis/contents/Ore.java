package gurumirum.magialucis.contents;

import com.mojang.datafixers.util.Pair;
import gurumirum.magialucis.contents.profile.BlockProfile;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public enum Ore {
	SILVER(MiningLevel.STONE, UniformInt.of(0, 1), OreType.STONE, OreType.DEEPSLATE),

	AMBER(MiningLevel.WOOD, UniformInt.of(0, 2), OreType.STONE),
	CITRINE(MiningLevel.STONE, UniformInt.of(2, 5), OreType.STONE),
	CORDIERITE(MiningLevel.STONE, UniformInt.of(2, 5), OreType.STONE),
	AQUAMARINE(MiningLevel.STONE, UniformInt.of(2, 5), OreType.STONE),

	RUBY(MiningLevel.IRON, UniformInt.of(3, 7), OreType.STONE, OreType.DEEPSLATE),
	SAPPHIRE(MiningLevel.IRON, UniformInt.of(3, 7), OreType.STONE, OreType.DEEPSLATE),
	TOPAZ(MiningLevel.IRON, UniformInt.of(3, 7), OreType.STONE, OreType.DEEPSLATE);

	private final Map<OreType, Pair<DeferredBlock<DropExperienceBlock>, DeferredItem<BlockItem>>> ores = new EnumMap<>(OreType.class);
	private final MiningLevel miningLevel;

	Ore(@NotNull MiningLevel miningLevel, @NotNull IntProvider experience, OreType... oreTypes) {
		if (oreTypes.length == 0) throw new IllegalArgumentException("Write ore types you dingus");
		this.miningLevel = Objects.requireNonNull(miningLevel);

		for (OreType oreType : oreTypes) {
			this.ores.computeIfAbsent(oreType, o -> {
				BlockProfile<DropExperienceBlock, BlockItem> profile = BlockProfile.customBlock(
						p -> new DropExperienceBlock(experience, p),
						o.getBlockProperties());
				String oreId = o.getOreId(oreBaseName());
				DeferredBlock<DropExperienceBlock> deferredBlock = profile.create(oreId);
				DeferredItem<BlockItem> deferredItem = profile.createItem(deferredBlock);

				return new Pair<>(deferredBlock, deferredItem);
			});
		}
	}

	public @NotNull String oreBaseName() {
		return name().toLowerCase(Locale.ROOT);
	}

	public @NotNull String oreId() {
		return oreBaseName() + "_ore";
	}

	public @Nullable Block oreBlock(OreType type) {
		var pair = this.ores.get(type);
		return pair == null ? null : pair.getFirst().get();
	}

	public @NotNull Block expectOreBlock(OreType type) {
		assertExists(type);
		return Objects.requireNonNull(oreBlock(type));
	}

	public @Nullable BlockItem oreItem(OreType type) {
		var pair = this.ores.get(type);
		return pair == null ? null : pair.getSecond().get();
	}

	public @NotNull BlockItem expectOreItem(OreType type) {
		assertExists(type);
		return Objects.requireNonNull(oreItem(type));
	}

	public @NotNull MiningLevel miningLevel() {
		return this.miningLevel;
	}

	public @NotNull Item dropItem() {
		return this == SILVER ? ModItems.RAW_SILVER.asItem() : smeltItem();
	}

	public @NotNull Item smeltItem() {
		return (switch (this) {
			case SILVER -> ModItems.SILVER_INGOT;
			case AMBER -> GemItems.AMBER;
			case CITRINE -> GemItems.CITRINE;
			case CORDIERITE -> GemItems.IOLITE;
			case AQUAMARINE -> GemItems.AQUAMARINE;
			case RUBY -> GemItems.RUBY;
			case SAPPHIRE -> GemItems.SAPPHIRE;
			case TOPAZ -> GemItems.TOPAZ;
		}).asItem();
	}

	public boolean doubleDrop() {
		return switch (this) {
			case AMBER, CITRINE, CORDIERITE, AQUAMARINE -> true;
			default -> false;
		};
	}

	public boolean exists(OreType type) {
		return this.ores.containsKey(type);
	}

	public void assertExists(OreType type) {
		if (!exists(type)) throw new IllegalStateException("Ore " + this + " does not have ore type " + type);
	}

	public Stream<Block> allOreBlocks() {
		return this.ores.values().stream().map(Pair::getFirst).map(DeferredHolder::get);
	}

	public Stream<BlockItem> allOreItems() {
		return this.ores.values().stream().map(Pair::getSecond).map(DeferredHolder::get);
	}

	public Stream<Map.Entry<OreType, Pair<DeferredBlock<DropExperienceBlock>, DeferredItem<BlockItem>>>> entries() {
		return this.ores.entrySet().stream();
	}

	public static void init() {}

	public enum MiningLevel {
		WOOD,
		STONE,
		IRON,
		DIAMOND;
	}
}
