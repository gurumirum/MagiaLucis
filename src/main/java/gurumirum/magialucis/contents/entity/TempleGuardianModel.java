package gurumirum.magialucis.contents.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

import static gurumirum.magialucis.MagiaLucisMod.id;

public class TempleGuardianModel<T extends TempleGuardian> extends EntityModel<T> {
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(id("temple_guardian"), "main");

	public static final int TEX_WIDTH = 64;
	public static final int TEX_HEIGHT = 32;

	private final ModelPart legs;
	private final ModelPart torso1;
	private final ModelPart torso2;
	private final ModelPart head;

	private float partialTick;
	private float headRotX;
	private float headRotY;
	private float headRotZ;

	public TempleGuardianModel(ModelPart root) {
		this.legs = root.getChild("legs");
		this.torso1 = root.getChild("torso1");
		this.torso2 = root.getChild("torso2");
		this.head = root.getChild("head");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition p = mesh.getRoot();

		PartDefinition legs = p.addOrReplaceChild("legs", CubeListBuilder.create().texOffs(0, 20).addBox(-2.0F, -3.5F, -1.0F, 1.0F, 9.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(12, 24).addBox(-3.0F, -3.5F, -1.0F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(6, 20).mirror().addBox(1.0F, -3.5F, -1.0F, 1.0F, 9.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(12, 17).mirror().addBox(2.0F, -3.5F, -1.0F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 15.5F, 0.0F));

		PartDefinition torso1 = p.addOrReplaceChild("torso1", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, -1.0F, -1.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 10.0F, 0.0F));

		PartDefinition torso2 = p.addOrReplaceChild("torso2", CubeListBuilder.create().texOffs(0, 12).addBox(-3.0F, -1.0F, -1.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 7.0F, 0.0F));

		PartDefinition head = p.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(24, 0).addBox(-5.0F, -5.0F, 0.0F, 10.0F, 10.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(mesh, TEX_WIDTH, TEX_HEIGHT);
	}

	@Override
	public void prepareMobModel(@NotNull T entity, float limbSwing, float limbSwingAmount, float partialTick) {
		super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
		this.partialTick = partialTick; // nice fucking framework
	}

	@Override
	public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		double deg2rad = Math.PI / 180.0;

		float headRotation = (float)(Mth.rotLerp(this.partialTick, entity.clientSideHeadRotationO, entity.clientSideHeadRotation) * deg2rad);
		float bobbing = ageInTicks / 20;

		// wtf why do i need to reapply model offset
		this.legs.y = 15.5f + (float)(Math.sin(bobbing) / 2);
		this.torso1.y = 10.0f + (float)(Math.sin(bobbing + Math.PI / 2) / 2);
		this.torso2.y = 7.0f + (float)(Math.sin(bobbing + Math.PI) / 2);

		this.headRotY = netHeadYaw * (float)deg2rad;
		this.headRotX = headPitch * (float)deg2rad;
		this.headRotZ = headRotation;
	}

	@Override
	public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
		this.legs.render(poseStack, buffer, packedLight, packedOverlay, color);
		this.torso1.render(poseStack, buffer, packedLight, packedOverlay, color);
		this.torso2.render(poseStack, buffer, packedLight, packedOverlay, color);

		poseStack.pushPose();
		poseStack.mulPose(new Quaternionf().rotateYXZ(this.headRotY, this.headRotX, this.headRotZ));
		this.head.render(poseStack, buffer, packedLight, packedOverlay, color);
		poseStack.popPose();
	}
}
