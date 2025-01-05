package gurumirum.gemthing.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

public final class RenderShapes {
	private RenderShapes() {}

	public static void untexturedZGradientBox(PoseStack poseStack, VertexConsumer vc,
	                                          float x1, float y1, float z1,
	                                          float x2, float y2, float z2,
	                                          int c1, int c2) {
		PoseStack.Pose pose = poseStack.last();

		vc.addVertex(pose, x1, y1, z1).setColor(c1);
		vc.addVertex(pose, x1, y2, z1).setColor(c1);
		vc.addVertex(pose, x2, y2, z1).setColor(c1);
		vc.addVertex(pose, x2, y1, z1).setColor(c1);

		vc.addVertex(pose, x1, y1, z2).setColor(c2);
		vc.addVertex(pose, x1, y1, z1).setColor(c1);
		vc.addVertex(pose, x2, y1, z1).setColor(c1);
		vc.addVertex(pose, x2, y1, z2).setColor(c2);

		vc.addVertex(pose, x1, y1, z1).setColor(c1);
		vc.addVertex(pose, x1, y1, z2).setColor(c2);
		vc.addVertex(pose, x1, y2, z2).setColor(c2);
		vc.addVertex(pose, x1, y2, z1).setColor(c1);

		vc.addVertex(pose, x2, y2, z2).setColor(c2);
		vc.addVertex(pose, x2, y2, z1).setColor(c1);
		vc.addVertex(pose, x1, y2, z1).setColor(c1);
		vc.addVertex(pose, x1, y2, z2).setColor(c2);

		vc.addVertex(pose, x2, y2, z1).setColor(c1);
		vc.addVertex(pose, x2, y2, z2).setColor(c2);
		vc.addVertex(pose, x2, y1, z2).setColor(c2);
		vc.addVertex(pose, x2, y1, z1).setColor(c1);

		vc.addVertex(pose, x2, y1, z2).setColor(c2);
		vc.addVertex(pose, x2, y2, z2).setColor(c2);
		vc.addVertex(pose, x1, y2, z2).setColor(c2);
		vc.addVertex(pose, x1, y1, z2).setColor(c2);
	}

	// TODO uv's all fucked up and idc enough to fix it sorry
	public static void texturedTintedBox(PoseStack poseStack, VertexConsumer vc,
	                                     float x1, float y1, float z1,
	                                     float x2, float y2, float z2,
	                                     int c1) {
		PoseStack.Pose pose = poseStack.last();

		vc.addVertex(pose, x1, y1, z1).setUv(0, 1).setColor(c1);
		vc.addVertex(pose, x1, y2, z1).setUv(0, 0).setColor(c1);
		vc.addVertex(pose, x2, y2, z1).setUv(1, 0).setColor(c1);
		vc.addVertex(pose, x2, y1, z1).setUv(1, 1).setColor(c1);

		vc.addVertex(pose, x1, y1, z2).setUv(0, 1).setColor(c1);
		vc.addVertex(pose, x1, y1, z1).setUv(0, 0).setColor(c1);
		vc.addVertex(pose, x2, y1, z1).setUv(1, 0).setColor(c1);
		vc.addVertex(pose, x2, y1, z2).setUv(1, 1).setColor(c1);

		vc.addVertex(pose, x1, y1, z1).setUv(1, 0).setColor(c1);
		vc.addVertex(pose, x1, y1, z2).setUv(1, 1).setColor(c1);
		vc.addVertex(pose, x1, y2, z2).setUv(0, 1).setColor(c1);
		vc.addVertex(pose, x1, y2, z1).setUv(0, 0).setColor(c1);

		vc.addVertex(pose, x2, y2, z2).setUv(0, 1).setColor(c1);
		vc.addVertex(pose, x2, y2, z1).setUv(0, 0).setColor(c1);
		vc.addVertex(pose, x1, y2, z1).setUv(1, 0).setColor(c1);
		vc.addVertex(pose, x1, y2, z2).setUv(1, 1).setColor(c1);

		vc.addVertex(pose, x2, y2, z1).setUv(1, 0).setColor(c1);
		vc.addVertex(pose, x2, y2, z2).setUv(1, 1).setColor(c1);
		vc.addVertex(pose, x2, y1, z2).setUv(0, 1).setColor(c1);
		vc.addVertex(pose, x2, y1, z1).setUv(0, 0).setColor(c1);

		vc.addVertex(pose, x2, y1, z2).setUv(0, 1).setColor(c1);
		vc.addVertex(pose, x2, y2, z2).setUv(0, 0).setColor(c1);
		vc.addVertex(pose, x1, y2, z2).setUv(1, 0).setColor(c1);
		vc.addVertex(pose, x1, y1, z2).setUv(1, 1).setColor(c1);
	}
}
