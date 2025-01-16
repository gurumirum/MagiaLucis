package gurumirum.magialucis.datagen;

import gurumirum.magialucis.MagiaLucisMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static gurumirum.magialucis.contents.ModDamageTypes.LESSER_ICE_PROJECTILE;

public class DamageTypeTagGen extends DamageTypeTagsProvider {
	public DamageTypeTagGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, MagiaLucisMod.MODID, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.@NotNull Provider provider) {
		tag(LESSER_ICE_PROJECTILE, DamageTypeTags.IS_PROJECTILE, DamageTypeTags.PANIC_CAUSES, Tags.DamageTypes.IS_PHYSICAL);
	}

	@SafeVarargs
	private void tag(ResourceKey<DamageType> type, TagKey<DamageType>... tags) {
		for (TagKey<DamageType> key : tags) {
			tag(key).add(type);
		}
	}
}
