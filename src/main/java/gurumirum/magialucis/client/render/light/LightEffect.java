package gurumirum.magialucis.client.render.light;

import net.minecraft.world.phys.Vec3;

public sealed interface LightEffect {
	float radius();
	Vec3 start();
	int color();

	record SpotEffect(float radius, Vec3 start, int color) implements LightEffect {}

	record LineEffect(float radius, Vec3 start, Vec3 end, int color, boolean fallOff) implements LightEffect {}
}
