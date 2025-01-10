package gurumirum.magialucis.contents;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import static gurumirum.magialucis.MagiaLucisMod.id;

public final class ModBlockTags {
	private ModBlockTags() {}

	// well uh, this is the correct pluralization probably
	public static final TagKey<Block> LAPIDES_MANALIS = BlockTags.create(id("lapides_manalis"));
}
