package gurumirum.magialucis.client.render.beam;

import gurumirum.magialucis.api.item.BeamSource;
import gurumirum.magialucis.contents.item.wand.AncientLightWandItem;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.UUID;

public class PlayerBeamEffect implements BeamEffect {
	private static final Object2ObjectOpenHashMap<UUID, Vector3f> playerBeamStarts = new Object2ObjectOpenHashMap<>();

	public static void setBeamStart(Player player, Vector3f beamStart) {
		playerBeamStarts.computeIfAbsent(player.getUUID(), u -> new Vector3f()).set(beamStart);
	}

	public static Vector3f getBeamStart(Player player, Vector3f dest) {
		Vector3f v = playerBeamStarts.get(player.getUUID());
		if (v != null) return dest.set(v);
		else return dest.set(Float.NaN);
	}

	public static void clearBeamStarts() {
		playerBeamStarts.values().forEach(v -> v.set(Float.NaN));
	}

	private final Player player;
	private final ItemStack stack;
	private final BeamSource beamSource;

	public PlayerBeamEffect(Player player, ItemStack stack, BeamSource beamSource) {
		this.player = player;
		this.stack = stack;
		this.beamSource = beamSource;
	}

	@Override
	public @NotNull CoordinateSystem beamStart(@NotNull Vector3f dest, float partialTicks) {
		if (getBeamStart(this.player, dest).isFinite()) {
			return isFirstPersonPerspective() ? CoordinateSystem.VIEW : CoordinateSystem.MODEL;
		}
		// approximate origin
		Vec3 pos = this.player.getPosition(partialTicks);
		dest.set(pos.x, pos.y + this.player.getBbHeight() / 2 + 0.25, pos.z);
		return CoordinateSystem.WORLD;
	}

	@Override
	public @Nullable CoordinateSystem beamEnd(@NotNull Vector3f beamStart, @NotNull Vector3f dest, float partialTicks) {
		Vec3 start = this.player.getEyePosition(partialTicks);
		Vec3 viewVector = this.player.getViewVector(partialTicks);
		Vec3 end = start.add(viewVector.scale(AncientLightWandItem.DISTANCE));
		BlockHitResult hitResult = BeamSource.trace(this.player, start, end);

		if (hitResult.getType() == HitResult.Type.BLOCK) {
			end = hitResult.getLocation();
			double angle = end.toVector3f().sub(beamStart).angle(end.subtract(start).toVector3f());
			if (angle > Math.PI / 3) return null;
		}

		dest.set(end.x, end.y, end.z);
		return CoordinateSystem.WORLD;
	}

	@Override
	public float diameter(float partialTicks) {
		return this.beamSource.beamDiameter(this.player, this.stack, isFirstPersonPerspective(), partialTicks);
	}

	@Override
	public float rotation(float partialTicks) {
		return this.beamSource.beamRotation(this.player, this.stack, isFirstPersonPerspective(), partialTicks);
	}

	@Override
	public int color(float partialTicks) {
		return this.beamSource.beamColor(this.player, this.stack, isFirstPersonPerspective(), partialTicks);
	}

	public boolean isFirstPersonPerspective() {
		Minecraft mc = Minecraft.getInstance();
		return mc.options.getCameraType().isFirstPerson() && this.player == mc.cameraEntity;
	}
}
