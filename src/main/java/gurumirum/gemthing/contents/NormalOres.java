package gurumirum.gemthing.contents;

import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Objects;

public enum NormalOres {
	SILVER,

	AMBER(true, false),
	CITRINE(true, false),
	AQUAMARINE(true, false),

	RUBY(true, false),
	SAPPHIRE(true, false),
	TOPAZ(true, false);

	@Nullable
	private final DeferredBlock<Block> ore;
	@Nullable
	private final DeferredBlock<Block> deepslateOre;
	@Nullable
	private final DeferredItem<BlockItem> oreItem;
	@Nullable
	private final DeferredItem<BlockItem> deepslateOreItem;

	NormalOres() {
		this(ConstantInt.of(0));
	}
	NormalOres(@NotNull IntProvider experience) {
		this(experience, true, true);
	}
	NormalOres(boolean normal, boolean deepslate) {
		this(ConstantInt.of(0), normal, deepslate);
	}
	NormalOres(@NotNull IntProvider experience, boolean normal, boolean deepslate) {
		String oreId = oreId();
		String deepslateId = deepslateOreId();

		if (normal) {
			this.ore = Contents.BLOCKS.register(oreId, () ->
					new DropExperienceBlock(experience, BlockBehaviour.Properties.ofFullCopy(Blocks.GOLD_ORE)));
			this.oreItem = Contents.ITEMS.register(oreId, () ->
					new BlockItem(this.ore.get(), new Item.Properties()));
		} else {
			this.ore = null;
			this.oreItem = null;
		}

		if (deepslate) {
			this.deepslateOre = Contents.BLOCKS.register(deepslateId, () ->
					new DropExperienceBlock(experience, BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_GOLD_ORE)));
			this.deepslateOreItem = Contents.ITEMS.register(deepslateId, () ->
					new BlockItem(this.deepslateOre.get(), new Item.Properties()));
		} else {
			this.deepslateOre = null;
			this.deepslateOreItem = null;
		}
	}

	public @NotNull String oreBaseName() {
		return name().toLowerCase(Locale.ROOT);
	}

	public @NotNull String oreId() {
		return oreBaseName() + "_ore";
	}

	public @NotNull String deepslateOreId() {
		return "deepslate_" + oreBaseName() + "_ore";
	}

	public @NotNull Block ore() {
		return Objects.requireNonNull(this.ore).get();
	}
	public @NotNull Block deepslateOre() {
		return Objects.requireNonNull(this.deepslateOre).get();
	}
	public @NotNull BlockItem oreItem() {
		return Objects.requireNonNull(this.oreItem).get();
	}
	public @NotNull BlockItem deepslateOreItem() {
		return Objects.requireNonNull(this.deepslateOreItem).get();
	}

	public boolean hasOre() {
		return this.ore != null;
	}
	public boolean hasDeepslateOre() {
		return this.deepslateOre != null;
	}

	public static void init() {}
}
