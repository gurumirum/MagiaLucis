package gurumirum.magialucis.datagen;

import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.contents.Contents;
import gurumirum.magialucis.contents.ModBlocks;
import gurumirum.magialucis.contents.Ore;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class LootGen extends LootTableProvider {
	public LootGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, Set.of(), List.of(
				new LootTableProvider.SubProviderEntry(BlockLoot::new, LootContextParamSets.BLOCK)
		), registries);
	}

	@Override
	protected void validate(@NotNull WritableRegistry<LootTable> writableregistry,
	                        @NotNull ValidationContext validationcontext,
	                        ProblemReporter.@NotNull Collector problemreporter$collector) {}

	private static class BlockLoot extends BlockLootSubProvider {
		protected BlockLoot(HolderLookup.Provider registries) {
			super(Set.of(), FeatureFlags.DEFAULT_FLAGS, registries);
		}

		@Override
		protected void generate() {
			for (ModBlocks modBlocks : ModBlocks.values()) genModBlockModels(modBlocks);

			for (Ore o : Ore.values()) {
				o.allOreBlocks().forEach(b -> add(b, createOreDrop(b, o.dropItem())));
			}
		}

		private void genModBlockModels(ModBlocks modBlock) {
			if (modBlock.block().getLootTable() == BuiltInLootTables.EMPTY) return;
			switch (modBlock) {
				case RELAY -> add(ModBlocks.RELAY.block(), b -> LootTable.lootTable().withPool(
						applyExplosionCondition(b, LootPool.lootPool()
								.setRolls(ConstantValue.exactly(1))
								.add(LootItem.lootTableItem(b)
										.apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
												.include(Contents.RELAY_ITEM.get())
										)
								)
						)
				));
				default -> {
					if (modBlock.blockItem() != null) dropSelf(modBlock.block());
					else add(modBlock.block(), noDrop());
				}
			}
		}

		@SuppressWarnings("ConstantValue") // it's not
		@Override
		protected @NotNull Iterable<Block> getKnownBlocks() {
			return () -> BuiltInRegistries.BLOCK.stream().filter(b -> {
				ResourceLocation key = BuiltInRegistries.BLOCK.getKey(b);
				return key != null && key.getNamespace().equals(MagiaLucisMod.MODID);
			}).iterator();
		}
	}
}
