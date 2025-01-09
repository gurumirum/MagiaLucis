package gurumirum.magialucis.contents.item.wand;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import gurumirum.magialucis.client.BeamRender;
import gurumirum.magialucis.client.ModArmPose;
import gurumirum.magialucis.client.WandEffect;
import gurumirum.magialucis.contents.item.BeamSource;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

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
		if (!player.isUsingItem() ||
				(player.getUsedItemHand() == InteractionHand.MAIN_HAND ?
						player.getMainArm() : player.getMainArm().getOpposite()) != arm) return false;

		BeamSource beamSource = BeamSource.from(itemInHand);
		boolean canProduceBeam = beamSource != null && beamSource.canProduceBeam(player, itemInHand);
		WandEffect wandEffect = WandEffect.from(itemInHand, player);
		if (!canProduceBeam && wandEffect == null) return false;

		applyItemArmTransform(poseStack, arm, equipProcess);

		poseStack.pushPose();

		BeamRender.applyItemTransform(poseStack, itemInHand, player, arm, true);

		if (canProduceBeam) {
			_mat.identity()
					.mul(RenderSystem.getProjectionMatrix())
					.mul(RenderSystem.getModelViewMatrix())
					.mul(poseStack.last().pose())
					.transformProject(18 / 16f, 18 / 16f, .5f, BeamRender.getOrCreatePlayerBeamStart(player));
		}

		if (wandEffect != null) wandEffect.render(poseStack, player, itemInHand, partialTick, false);

		poseStack.popPose();
		return true;
	}

	// ItemInHandRenderer#applyItemArmTransform
	private void applyItemArmTransform(PoseStack poseStack, HumanoidArm hand, float equipProcess) {
		poseStack.translate((hand == HumanoidArm.RIGHT ? 1 : -1) * 0.56f, -0.52f + equipProcess * -0.6f, -0.72f);
	}
}
