package gurumirum.gemthing.datagen;

import gurumirum.gemthing.GemthingMod;
import gurumirum.gemthing.contents.Contents;
import gurumirum.gemthing.contents.ModBlocks;
import gurumirum.gemthing.contents.Ore;
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
			dropSelf(ModBlocks.SILVER.block());
			dropSelf(ModBlocks.RAW_SILVER_BLOCK.block());

			dropSelf(ModBlocks.REMOTE_CHARGER.block());
			dropSelf(ModBlocks.REMOTE_CHARGER_2.block());

			add(ModBlocks.RELAY.block(), b -> LootTable.lootTable().withPool(
					applyExplosionCondition(b, LootPool.lootPool()
							.setRolls(ConstantValue.exactly(1))
							.add(LootItem.lootTableItem(b)
									.apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
											.include(Contents.RELAY_ITEM.get())
									)
							)
					)
			));

			dropSelf(ModBlocks.AMBER_CORE.block());
			dropSelf(ModBlocks.LUX_SOURCE.block());

			for (Ore o : Ore.values()) {
				o.allOreBlocks().forEach(b -> add(b, createOreDrop(b, o.dropItem())));
			}
		}

		@SuppressWarnings("ConstantValue") // it's not
		@Override
		protected @NotNull Iterable<Block> getKnownBlocks() {
			return () -> BuiltInRegistries.BLOCK.stream().filter(b -> {
				ResourceLocation key = BuiltInRegistries.BLOCK.getKey(b);
				return key != null && key.getNamespace().equals(GemthingMod.MODID);
			}).iterator();
		}
	}
}
