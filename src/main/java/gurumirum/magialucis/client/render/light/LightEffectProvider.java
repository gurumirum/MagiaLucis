package gurumirum.magialucis.client.render.light;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public interface LightEffectProvider {
	@OnlyIn(Dist.CLIENT)
	void getLightEffects(float partialTicks, @NotNull Collector collector);

	@OnlyIn(Dist.CLIENT)
	boolean isLightEffectProviderValid();

	@OnlyIn(Dist.CLIENT)
	void onLevelUnload(LevelAccessor level);

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
