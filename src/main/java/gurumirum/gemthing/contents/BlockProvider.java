package gurumirum.gemthing.contents;

import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public interface BlockProvider {
	@NotNull Block block();
}
