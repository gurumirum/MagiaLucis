package gurumirum.magialucis.client;

import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import net.neoforged.neoforge.client.IArmPoseTransformer;
import org.jetbrains.annotations.NotNull;

public final class ModArmPose {
	private ModArmPose() {}

	public static final EnumProxy<HumanoidModel.ArmPose> WAND = new EnumProxy<>(
			HumanoidModel.ArmPose.class, false, (IArmPoseTransformer) ModArmPose::wandTransform);

	private static void wandTransform(@NotNull HumanoidModel<?> model, @NotNull LivingEntity entity, @NotNull HumanoidArm arm) {
		AnimationUtils.animateCrossbowHold(model.rightArm, model.leftArm, model.head, arm == HumanoidArm.RIGHT);
	}
}
