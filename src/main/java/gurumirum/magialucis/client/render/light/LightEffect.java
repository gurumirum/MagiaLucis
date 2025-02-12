package gurumirum.magialucis.client.render.light;

import net.minecraft.util.FastColor;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

public sealed interface LightEffect {
	float radius();
	Vec3 start();
	int color();

	static int getLightColor(Vector3d luxFlow) {
		return getLightColor(luxFlow.x, luxFlow.y, luxFlow.z);
	}

	static int getLightColor(double red, double green, double blue) {
		double u = (red + green + blue) / 3;
		if (u <= 0) return 0;

		double rv = Math.max(0, red) / u;
		double gv = Math.max(0, green) / u;
		double bv = Math.max(0, blue) / u;

		double mv = Math.max(Math.max(rv, gv), bv);

		rv /= mv;
		gv /= mv;
		bv /= mv;

		int r = (int)(rv * 255);
		int g = (int)(gv * 255);
		int b = (int)(bv * 255);

		return FastColor.ARGB32.color(r, g, b);
	}

	static float sphereRadius(double luxFlowSum) {
		return (float)(1 / 4.0 * (Math.log10(luxFlowSum)));
	}

	static float rayRadius(double luxFlowSum) {
		return (float)(1 / 10.0 * (Math.log10(Math.max(15, luxFlowSum))));
	}

	record SpotEffect(float radius, Vec3 start, int color) implements LightEffect {}

	record LineEffect(float radius, Vec3 start, Vec3 end, int color, boolean fallOff) implements LightEffect {}
}
