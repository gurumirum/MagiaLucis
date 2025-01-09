package gurumirum.magialucis.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import gurumirum.magialucis.client.model.GemGolemModel;
import gurumirum.magialucis.contents.entity.GemGolemEntity;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class GemGolemRenderer extends MobRenderer<GemGolemEntity, GemGolemModel<GemGolemEntity>> {
    private static final ResourceLocation GOLEM_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/iron_golem/iron_golem.png");

    public GemGolemRenderer(EntityRendererProvider.Context p_174188_) {
        super(p_174188_, new GemGolemModel<>(p_174188_.bakeLayer(ModelLayers.IRON_GOLEM)), 0.7F);
    }

    /**
     * Returns the location of an entity's texture.
     */
    public ResourceLocation getTextureLocation(GemGolemEntity pEntity) {
        return GOLEM_LOCATION;
    }

    protected void setupRotations(GemGolemEntity pEntity, PoseStack pPoseStack, float pBob, float pYBodyRot, float pPartialTick, float pScale) {
        super.setupRotations(pEntity, pPoseStack, pBob, pYBodyRot, pPartialTick, pScale);
        if (!((double)pEntity.walkAnimation.speed() < 0.01)) {
            float f = 13.0F;
            float f1 = pEntity.walkAnimation.position(pPartialTick) + 6.0F;
            float f2 = (Math.abs(f1 % 13.0F - 6.5F) - 3.25F) / 3.25F;
            pPoseStack.mulPose(Axis.ZP.rotationDegrees(6.5F * f2));
        }
    }
}
