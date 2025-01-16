package gurumirum.magialucis.contents.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

import java.util.UUID;

public class LesserIceProjectileRenderer extends EntityRenderer<LesserIceProjectile> {
	private static final float SCALE = 0.15f;

	private final BlockRenderDispatcher blockRenderDispatcher;

	public LesserIceProjectileRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.blockRenderDispatcher = context.getBlockRenderDispatcher();
	}

	@Override
	public void render(@NotNull LesserIceProjectile entity, float entityYaw, float partialTick,
	                   @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
		poseStack.pushPose();
		poseStack.scale(SCALE, SCALE, SCALE);

		UUID uuid = entity.getUUID();

		int xr = (int)(uuid.getMostSignificantBits() >> 32);
		int yr = (int)(uuid.getMostSignificantBits());
		int zr = (int)(uuid.getLeastSignificantBits());

		poseStack.mulPose(new Quaternionf()
				.integrate(entity.tickCount + partialTick, toAngle(xr), toAngle(yr), toAngle(zr)));
		poseStack.translate(-0.5, -0.5, -0.5);

		//noinspection DataFlowIssue
		this.blockRenderDispatcher.renderSingleBlock(Blocks.ICE.defaultBlockState(), poseStack, bufferSource,
				packedLight, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);

		poseStack.popPose();
	}

	@Override
	public @NotNull ResourceLocation getTextureLocation(@NotNull LesserIceProjectile entity) {
		return InventoryMenu.BLOCK_ATLAS; // unused
	}

	private static float toAngle(int thing) {
		final int mask = ((1 << 16) - 1);
		return (toAngle0((thing >> 16) & mask) + toAngle0(thing & mask)) / 2;
	}

	private static float toAngle0(int thing) {
		final float maxValue = ((1 << 16) - 1);
		return (thing / maxValue - 0.5f) * 2 * (Mth.TWO_PI / 25);
	}
}
