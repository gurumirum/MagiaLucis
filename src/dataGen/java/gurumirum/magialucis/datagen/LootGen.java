package gurumirum.magialucis.datagen;

import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.contents.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static net.minecraft.world.level.storage.loot.LootPool.lootPool;
import static net.minecraft.world.level.storage.loot.LootTable.lootTable;

public class LootGen extends LootTableProvider {
	public LootGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, Set.of(), List.of(
				new LootTableProvider.SubProviderEntry(BlockLoot::new, LootContextParamSets.BLOCK),
				new LootTableProvider.SubProviderEntry(EntityLoot::new, LootContextParamSets.ENTITY)
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
			if (modBlock.block().getLootTable() == BuiltInLootTables.EMPTY) return;
			switch (modBlock) {
				case RELAY -> add(ModBlocks.RELAY.block(), b -> lootTable().withPool(
						applyExplosionCondition(b, lootPool()
								.setRolls(ConstantValue.exactly(1))
								.add(LootItem.lootTableItem(b)
										.apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
												.include(ModDataComponents.RELAY_ITEM.get())
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
}
