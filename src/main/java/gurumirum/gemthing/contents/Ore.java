package gurumirum.gemthing.contents;

import com.mojang.datafixers.util.Pair;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.item.BlockItem;
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
	SILVER(OreType.STONE, OreType.DEEPSLATE),

	AMBER(OreType.STONE),
	CITRINE(OreType.STONE),
	AQUAMARINE(OreType.STONE),

	RUBY(OreType.STONE, OreType.DEEPSLATE),
	SAPPHIRE(OreType.STONE, OreType.DEEPSLATE),
	TOPAZ(OreType.STONE, OreType.DEEPSLATE);

	private final Map<OreType, Pair<DeferredBlock<DropExperienceBlock>, DeferredItem<BlockItem>>> ores = new EnumMap<>(OreType.class);

	Ore(OreType... oreTypes) {
		this(ConstantInt.of(0), oreTypes);
	}
	Ore(@NotNull IntProvider experience, OreType... oreTypes) {
		if (oreTypes.length == 0) throw new IllegalArgumentException("Write ore types you dingus");

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
}
