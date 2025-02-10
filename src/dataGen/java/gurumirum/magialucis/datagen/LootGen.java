package gurumirum.magialucis.datagen;

import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.contents.*;
import gurumirum.magialucis.contents.block.ModBlockStates;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import static gurumirum.magialucis.datagen.LootModifierGen.*;
import static net.minecraft.world.level.storage.loot.LootPool.lootPool;
import static net.minecraft.world.level.storage.loot.LootTable.lootTable;
import static net.minecraft.world.level.storage.loot.providers.number.UniformGenerator.between;

public class LootGen extends LootTableProvider {
	public LootGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, Set.of(), List.of(
				new SubProviderEntry(BlockLoot::new, LootContextParamSets.BLOCK),
				new SubProviderEntry(EntityLoot::new, LootContextParamSets.ENTITY),
				new SubProviderEntry(ChestLoot::new, LootContextParamSets.CHEST)
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
			for (ModBlocks block : ModBlocks.values()) genModBlockModels(block);
			for (ModBuildingBlocks buildingBlock : ModBuildingBlocks.values()) dropSelf(buildingBlock.block());

			for (Ore o : Ore.values()) {
				o.allOreBlocks().forEach(b -> add(b, o.doubleDrop() ?
						createDoubleOreDrops(b, o.dropItem()) : createOreDrop(b, o.dropItem())));
			}
		}

		private void genModBlockModels(ModBlocks modBlock) {
			Block block = modBlock.block();
			if (block.getLootTable() == BuiltInLootTables.EMPTY) return;
			switch (modBlock) {
				case RELAY -> add(block, b -> lootTable().withPool(
						applyExplosionCondition(b, lootPool()
								.setRolls(ConstantValue.exactly(1))
								.add(LootItem.lootTableItem(b)
										.apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
												.include(ModDataComponents.GEM_ITEM.get())
										)
								)
						)
				));
				case ARTISANRY_TABLE -> add(block, b -> lootTable().withPool(
						applyExplosionCondition(block, lootPool()
								.setRolls(ConstantValue.exactly(1.0F))
								.add(LootItem.lootTableItem(block)
										.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
												.setProperties(StatePropertiesPredicate.Builder.properties()
														.hasProperty(ModBlockStates.LEFT, true))
										)
								)
						)
				));
				default -> {
					if (modBlock.blockItem() != null) dropSelf(block);
					else add(block, noDrop());
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

		private LootTable.Builder createDoubleOreDrops(Block block, ItemLike dropItem) {
			HolderLookup.RegistryLookup<Enchantment> registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
			return createSilkTouchDispatchTable(block, applyExplosionDecay(block, LootItem.lootTableItem(dropItem)
					.apply(SetItemCountFunction.setCount(ConstantValue.exactly(2)))
					.apply(ApplyBonusCount.addOreBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))));
		}
	}

	private static class EntityLoot extends EntityLootSubProvider {
		protected EntityLoot(HolderLookup.Provider registries) {
			super(FeatureFlags.DEFAULT_FLAGS, registries);
		}

		@Override
		public void generate() {
			add(ModEntities.TEMPLE_GUARDIAN.get(), lootTable().withPool(lootPool()
					.setRolls(ConstantValue.exactly(1))
					.add(LootItem.lootTableItem(ModItems.ANCIENT_CORE))));
		}

		@Override
		protected @NotNull Stream<EntityType<?>> getKnownEntityTypes() {
			return BuiltInRegistries.ENTITY_TYPE.stream().filter(b ->
					BuiltInRegistries.ENTITY_TYPE.getKey(b).getNamespace().equals(MagiaLucisMod.MODID));
		}
	}

	private static class ChestLoot implements LootTableSubProvider {
		protected ChestLoot(HolderLookup.Provider registries) {}

		@Override
		public void generate(@NotNull BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
			output.accept(SILVER_IN_CHEST, loot(ModItems.SILVER_INGOT, between(3, 8), 7, between(1, 4)));
			output.accept(AMBER_IN_CHEST, loot(GemItems.AMBER, between(2, 6), 10, between(2, 6)));
			output.accept(CITRINE_IN_CHEST, loot(GemItems.CITRINE, between(2, 4), 20, between(2, 4)));
			output.accept(IOLITE_IN_CHEST, loot(GemItems.IOLITE, between(3, 5), 30, between(2, 4)));
			output.accept(AQUAMARINE_IN_CHEST, loot(GemItems.AQUAMARINE, between(1, 3), 25, between(1, 3)));
			output.accept(PEARL_IN_CHEST, loot(GemItems.PEARL, between(1, 2), 100, null));
			output.accept(RUBY_IN_CHEST, loot(GemItems.RUBY, between(2, 4), 5, between(1, 2)));
			output.accept(SAPPHIRE_IN_CHEST, loot(GemItems.SAPPHIRE, between(2, 4), 5, between(1, 2)));
		}

		private static LootTable.Builder loot(@NotNull ItemLike item, @NotNull NumberProvider rolls,
		                                      int pct, @Nullable NumberProvider quantity) {
			if (pct <= 0) throw new IllegalArgumentException();
			LootPool.Builder lootPoolBuilder = lootPool()
					.setRolls(rolls);

			var lootItem = LootItem.lootTableItem(item);
			if (quantity != null) {
				lootItem.apply(SetItemCountFunction.setCount(quantity));
			}

			if (pct < 100) {
				lootPoolBuilder.add(lootItem.setWeight(pct));
				lootPoolBuilder.add(EmptyLootItem.emptyItem().setWeight(100 - pct));
			} else {
				lootPoolBuilder.add(lootItem);
			}

			return lootTable().withPool(lootPoolBuilder);
		}
	}
}
