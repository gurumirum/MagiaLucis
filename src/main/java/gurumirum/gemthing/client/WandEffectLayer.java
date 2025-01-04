package gurumirum.gemthing.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import gurumirum.gemthing.contents.item.BeamSource;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class WandEffectLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
	public WandEffectLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer) {
		super(renderer);
	}

	@Override
	public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight,
	                   @NotNull AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTick,
	                   float ageInTicks, float netHeadYaw, float headPitch) {
		if (!player.isUsingItem()) return;

		ItemStack useItem = player.getUseItem();

		BeamSource beamSource = BeamSource.from(useItem);
		boolean canProduceBeam = beamSource != null && beamSource.canProduceBeam(player, useItem);
		WandEffect wandEffect = WandEffect.from(useItem, player);
		if (!canProduceBeam && wandEffect == null) return;

		HumanoidArm arm = player.getUsedItemHand() == InteractionHand.MAIN_HAND ?
				player.getMainArm() : player.getMainArm().getOpposite();

		poseStack.pushPose();
		this.getParentModel().translateToHand(arm, poseStack);
		poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
		poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
		boolean leftArm = arm == HumanoidArm.LEFT;
		poseStack.translate((float)(leftArm ? -1 : 1) / 16.0F, 0.125F, -0.625F);

		BeamRender.applyItemTransform(poseStack, useItem, player, arm, false);

		if (canProduceBeam) poseStack.last().pose()
				.transformProject(18 / 16f, 18 / 16f, .5f, BeamRender.getOrCreatePlayerBeamStart(player));

		if (wandEffect != null) wandEffect.render(poseStack, player, useItem, partialTick, false);

		poseStack.popPose();
	}
}
