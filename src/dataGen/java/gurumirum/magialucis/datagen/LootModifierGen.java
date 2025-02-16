package gurumirum.magialucis.datagen;

import gurumirum.magialucis.api.MagiaLucisApi;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.AddTableLootModifier;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import static gurumirum.magialucis.api.MagiaLucisApi.id;
import static net.minecraft.core.registries.Registries.LOOT_TABLE;

public class LootModifierGen extends GlobalLootModifierProvider {
	public static final ResourceKey<LootTable> SILVER_IN_CHEST = ResourceKey.create(LOOT_TABLE, id("silver_in_chest"));
	public static final ResourceKey<LootTable> AMBER_IN_CHEST = ResourceKey.create(LOOT_TABLE, id("amber_in_chest"));
	public static final ResourceKey<LootTable> CITRINE_IN_CHEST = ResourceKey.create(LOOT_TABLE, id("citrine_in_chest"));
	public static final ResourceKey<LootTable> IOLITE_IN_CHEST = ResourceKey.create(LOOT_TABLE, id("iolite_in_chest"));
	public static final ResourceKey<LootTable> AQUAMARINE_IN_CHEST = ResourceKey.create(LOOT_TABLE, id("aquamarine_in_chest"));
	public static final ResourceKey<LootTable> PEARL_IN_CHEST = ResourceKey.create(LOOT_TABLE, id("pearl_in_chest"));
	public static final ResourceKey<LootTable> RUBY_IN_CHEST = ResourceKey.create(LOOT_TABLE, id("ruby_in_chest"));
	public static final ResourceKey<LootTable> SAPPHIRE_IN_CHEST = ResourceKey.create(LOOT_TABLE, id("sapphire_in_chest"));

	public LootModifierGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries, MagiaLucisApi.MODID);
	}

	@Override
	protected void start() {
		addTableLootModifier(SILVER_IN_CHEST,
				BuiltInLootTables.VILLAGE_WEAPONSMITH);
		addTableLootModifier(AMBER_IN_CHEST,
				BuiltInLootTables.JUNGLE_TEMPLE);
		addTableLootModifier(CITRINE_IN_CHEST,
				BuiltInLootTables.DESERT_PYRAMID);
		addTableLootModifier(IOLITE_IN_CHEST,
				BuiltInLootTables.SHIPWRECK_TREASURE);
		addTableLootModifier(AQUAMARINE_IN_CHEST,
				BuiltInLootTables.UNDERWATER_RUIN_BIG,
				BuiltInLootTables.UNDERWATER_RUIN_SMALL,
				BuiltInLootTables.SHIPWRECK_TREASURE);
		addTableLootModifier(PEARL_IN_CHEST,
				BuiltInLootTables.BURIED_TREASURE);
		addTableLootModifier(RUBY_IN_CHEST,
				BuiltInLootTables.ABANDONED_MINESHAFT);
		addTableLootModifier(SAPPHIRE_IN_CHEST,
				BuiltInLootTables.ABANDONED_MINESHAFT);
	}

	@SafeVarargs
	private void addTableLootModifier(ResourceKey<LootTable> lootTable, ResourceKey<LootTable>... targetTables) {
		LootItemCondition condition = switch (targetTables.length) {
			case 0 -> throw new IllegalArgumentException();
			case 1 -> LootTableIdCondition.builder(targetTables[0].location()).build();
			default -> AnyOfCondition.anyOf(Arrays.stream(targetTables).map(
					key -> LootTableIdCondition.builder(key.location())
			).toArray(LootItemCondition.Builder[]::new)).build();
		};

		add(lootTable.location().getPath(), new AddTableLootModifier(new LootItemCondition[]{condition}, lootTable));
	}
}
