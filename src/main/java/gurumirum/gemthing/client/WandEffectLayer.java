package gurumirum.gemthing.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import gurumirum.gemthing.contents.Contents;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.NotNull;

public class WandEffectLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
	public WandEffectLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer) {
		super(renderer);
	}

	@Override
	public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight,
	                   @NotNull AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTick,
	                   float ageInTicks, float netHeadYaw, float headPitch) {
		if (!player.isUsingItem() || !player.getUseItem().is(Contents.Items.WAND.asItem())) return;

		HumanoidArm arm = player.getUsedItemHand() == InteractionHand.MAIN_HAND ?
				player.getMainArm() : player.getMainArm().getOpposite();

		poseStack.pushPose();
		this.getParentModel().translateToHand(arm, poseStack);
		poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
		poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
		boolean leftArm = arm == HumanoidArm.LEFT;
		poseStack.translate((float)(leftArm ? -1 : 1) / 16.0F, 0.125F, -0.625F);

		BeamRender.applyItemTransform(poseStack, arm, false);

		poseStack.last().pose()
				.transformProject(18 / 16f, 18 / 16f, .5f, BeamRender.getOrCreatePlayerBeamStart(player));

		BeamRender.drawWandEffect(poseStack, player, partialTick, false);

		poseStack.popPose();
	}
}
