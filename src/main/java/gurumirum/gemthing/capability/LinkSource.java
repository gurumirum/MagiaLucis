package gurumirum.gemthing.capability;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public interface LinkSource {
	void link(@NotNull Vec3 target);
}
