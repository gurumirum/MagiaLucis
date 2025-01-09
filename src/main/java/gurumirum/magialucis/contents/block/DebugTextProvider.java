package gurumirum.magialucis.contents.block;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface DebugTextProvider {
	@OnlyIn(Dist.CLIENT)
	void addDebugText(@NotNull List<String> list);
}
