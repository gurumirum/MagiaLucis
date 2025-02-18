package gurumirum.magialucis.contents.item.wand;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import gurumirum.magialucis.client.render.beam.BeamRender;
import gurumirum.magialucis.client.ModArmPose;
import gurumirum.magialucis.client.render.WandEffect;
import gurumirum.magialucis.client.render.beam.PlayerBeamEffect;
import gurumirum.magialucis.api.item.BeamSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class WandItemExtension implements IClientItemExtensions {
	@Override
	public HumanoidModel.@Nullable ArmPose getArmPose(@NotNull LivingEntity entity,
	                                                  @NotNull InteractionHand hand,
	                                                  @NotNull ItemStack stack) {
		return entity.isUsingItem() && entity.getUsedItemHand() == hand ? ModArmPose.WAND.getValue() : null;
	}

	private static final Matrix4f _mat = new Matrix4f();

	@Override
	public boolean applyForgeHandTransform(@NotNull PoseStack poseStack, @NotNull LocalPlayer player,
	                                       @NotNull HumanoidArm arm, @NotNull ItemStack itemInHand,
	                                       float partialTick, float equipProcess, float swingProcess) {
		InteractionHand hand = arm == player.getMainArm() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;

		BeamSource beamSource = BeamSource.from(itemInHand);
		boolean canProduceBeam = beamSource != null && beamSource.canProduceBeam(player, itemInHand, hand);
		WandEffect wandEffect = WandEffect.from(itemInHand, player, hand);
		if (!canProduceBeam && wandEffect == null) return false;

		ItemInHandRenderer itemInHandRenderer = Minecraft.getInstance()
				.getEntityRenderDispatcher().getItemInHandRenderer();

		itemInHandRenderer.applyItemArmTransform(poseStack, arm, equipProcess);
		itemInHandRenderer.applyItemArmAttackTransform(poseStack, arm, swingProcess);

		poseStack.pushPose();

		BeamRender.applyItemTransform(poseStack, itemInHand, player, arm, true);

		if (canProduceBeam) {
			PlayerBeamEffect.setBeamStart(player, _mat.identity()
					.mul(RenderSystem.getProjectionMatrix())
					.mul(RenderSystem.getModelViewMatrix())
					.mul(poseStack.last().pose())
					.transformProject(18 / 16f, 18 / 16f, .5f, new Vector3f()));
		}

		if (wandEffect != null) {
			wandEffect.render(poseStack, player, itemInHand, partialTick, true);
		}

		poseStack.popPose();
		return true;
	}
}
