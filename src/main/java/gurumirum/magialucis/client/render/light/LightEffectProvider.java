package gurumirum.magialucis.client.render.light;

import gurumirum.magialucis.client.render.RenderEffect;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public interface LightEffectProvider extends RenderEffect {
	void getLightEffects(float partialTicks, @NotNull Collector collector);

	@FunctionalInterface
	interface Collector {
		void addEffect(@NotNull LightEffect lightEffect);

		default void addCircularEffect(float radius, Vec3 point, int color) {
			addEffect(new LightEffect.SpotEffect(radius, point, color));
		}

		default void addCylindricalEffect(float radius, Vec3 start, Vec3 end, int color, boolean fallOff) {
			addEffect(new LightEffect.LineEffect(radius, start, end, color, fallOff));
		}
	}
}
