package gurumirum.magialucis.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import gurumirum.magialucis.contents.item.BeamSource;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WandEffectLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
	public WandEffectLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer) {
		super(renderer);
	}

	@Override
	public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight,
	                   @NotNull AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTick,
	                   float ageInTicks, float netHeadYaw, float headPitch) {
		ItemStack stack = player.getMainHandItem();

		BeamSource beamSource = BeamSource.from(stack);
		boolean canProduceBeam = beamSource != null && beamSource.canProduceBeam(player, stack, InteractionHand.MAIN_HAND);
		WandEffect wandEffect = WandEffect.from(stack, player, InteractionHand.MAIN_HAND);
		if (canProduceBeam || wandEffect != null) {
			drawEffects(poseStack, player, partialTick, stack, InteractionHand.MAIN_HAND, canProduceBeam, wandEffect);
		}

		stack = player.getOffhandItem();

		beamSource = BeamSource.from(stack);
		canProduceBeam = !canProduceBeam && beamSource != null && beamSource.canProduceBeam(player, stack, InteractionHand.OFF_HAND);
		wandEffect = WandEffect.from(stack, player, InteractionHand.OFF_HAND);
		if (canProduceBeam || wandEffect != null) {
			drawEffects(poseStack, player, partialTick, stack, InteractionHand.OFF_HAND, canProduceBeam, wandEffect);
		}
	}

	private void drawEffects(@NotNull PoseStack poseStack, @NotNull AbstractClientPlayer player, float partialTick,
	                         ItemStack stack, InteractionHand hand, boolean canProduceBeam, @Nullable WandEffect wandEffect) {
		HumanoidArm arm = hand == InteractionHand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();

		poseStack.pushPose();
		this.getParentModel().translateToHand(arm, poseStack);
		poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
		poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
		boolean leftArm = arm == HumanoidArm.LEFT;
		poseStack.translate((float)(leftArm ? -1 : 1) / 16.0F, 0.125F, -0.625F);

		BeamRender.applyItemTransform(poseStack, stack, player, arm, false);

		if (canProduceBeam) poseStack.last().pose()
				.transformProject(18 / 16f, 18 / 16f, .5f, BeamRender.getOrCreatePlayerBeamStart(player));

		if (wandEffect != null) wandEffect.render(poseStack, player, stack, partialTick, false);

		poseStack.popPose();
	}
}
