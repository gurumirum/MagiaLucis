package gurumirum.magialucis.client.render.light;

import gurumirum.magialucis.impl.luxnet.LuxUtils;
import net.minecraft.util.FastColor;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

public sealed interface LightEffect {
	float radius();
	Vec3 start();
	int color();

	static int getLightColor(Vector3d luxFlow) {
		double u = LuxUtils.sum(luxFlow) / 3;
		if (u <= 0) return 0;

		double rv = Math.max(0, luxFlow.x) / u;
		double gv = Math.max(0, luxFlow.y) / u;
		double bv = Math.max(0, luxFlow.z) / u;

		double mv = Math.max(Math.max(rv, gv), bv);

		rv /= mv;
		gv /= mv;
		bv /= mv;

		int r = (int)(rv * 255);
		int g = (int)(gv * 255);
		int b = (int)(bv * 255);

		return FastColor.ARGB32.color(r, g, b);
	}

	record SpotEffect(float radius, Vec3 start, int color) implements LightEffect {}

	record LineEffect(float radius, Vec3 start, Vec3 end, int color, boolean fallOff) implements LightEffect {}
}
