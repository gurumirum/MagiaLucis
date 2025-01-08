package gurumirum.gemthing.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import gurumirum.gemthing.GemthingMod;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector2f;

public final class RenderShapes {
	private RenderShapes() {}

	public static final ResourceLocation NO_FABULOUS_TEXTURE = GemthingMod.id("textures/effect/no_fabulous.png");
	public static final ResourceLocation NO_FABULOUS_TEXTURE_JOKE = GemthingMod.id("textures/effect/install_cs_source.png");

	private static final double NO_FABULOUS_RADIUS = 0.7; // diameter of a circle equal in size with unit square
	private static final int NO_FABULOUS_SEGMENTS = 16;

	private static Vector2f[] noFabulousSegmentPos;

	public static void noFabulousWarning(PoseStack poseStack, VertexConsumer vc, boolean joke) {
		if (noFabulousSegmentPos == null) {
			noFabulousSegmentPos = new Vector2f[NO_FABULOUS_SEGMENTS];

			for (int i = 0; i < NO_FABULOUS_SEGMENTS; i++) {
				double angle = (Math.PI * 2) / NO_FABULOUS_SEGMENTS * i;
				noFabulousSegmentPos[i] = new Vector2f(
						(float)((Math.cos(angle) * NO_FABULOUS_RADIUS + .5)),
						(float)((Math.sin(angle) * NO_FABULOUS_RADIUS + .5)));
			}
		}

		PoseStack.Pose pose = poseStack.last();

		for (int i = 0; i < NO_FABULOUS_SEGMENTS; i++) {
			Vector2f v1 = noFabulousSegmentPos[i];
			Vector2f v2 = noFabulousSegmentPos[i == NO_FABULOUS_SEGMENTS - 1 ? 0 : i + 1];

			int textureSegment = joke ? NO_FABULOUS_SEGMENTS : NO_FABULOUS_SEGMENTS / 2;
			int texIndex = i % textureSegment;

			float u1 = 1 - (texIndex) / (float)textureSegment;
			float u2 = 1 - (texIndex + 1) / (float)textureSegment;

			vc.addVertex(pose, v1.x, 9 / 16f, v1.y).setUv(u1, 0).setColor(-1);
			vc.addVertex(pose, v2.x, 9 / 16f, v2.y).setUv(u2, 0).setColor(-1);
			vc.addVertex(pose, v2.x, 5 / 16f, v2.y).setUv(u2, 1).setColor(-1);
			vc.addVertex(pose, v1.x, 5 / 16f, v1.y).setUv(u1, 1).setColor(-1);
		}
	}

	public static void drawOctahedron(PoseStack poseStack, VertexConsumer vc, int color, boolean reverseCull) {
		int color2 = color & 0x80ffffff;

		drawOctahedronSide(poseStack, vc,
				0.5f, 1.0f, 0.5f,
				0.5f, 0.5f, 1.0f,
				1.0f, 0.5f, 0.5f,
				1, 1, 1,
				color2, reverseCull);

		drawOctahedronSide(poseStack, vc,
				0.5f, 1.0f, 0.5f,
				0.0f, 0.5f, 0.5f,
				0.5f, 0.5f, 1.0f,
				-1, 1, 1,
				color, reverseCull);

		drawOctahedronSide(poseStack, vc,
				0.5f, 1.0f, 0.5f,
				0.5f, 0.5f, 0.0f,
				0.0f, 0.5f, 0.5f,
				-1, 1, -1,
				color2, reverseCull);

		drawOctahedronSide(poseStack, vc,
				0.5f, 1.0f, 0.5f,
				1.0f, 0.5f, 0.5f,
				0.5f, 0.5f, 0.0f,
				1, 1, -1,
				color, reverseCull);

		drawOctahedronSide(poseStack, vc,
				0.5f, 0.0f, 0.5f,
				1.0f, 0.5f, 0.5f,
				0.5f, 0.5f, 1.0f,
				1, -1, 1,
				color, reverseCull);

		drawOctahedronSide(poseStack, vc,
				0.5f, 0.0f, 0.5f,
				0.5f, 0.5f, 1.0f,
				0.0f, 0.5f, 0.5f,
				-1, -1, 1,
				color2, reverseCull);

		drawOctahedronSide(poseStack, vc,
				0.5f, 0.0f, 0.5f,
				0.0f, 0.5f, 0.5f,
				0.5f, 0.5f, 0.0f,
				-1, -1, -1,
				color, reverseCull);

		drawOctahedronSide(poseStack, vc,
				0.5f, 0.0f, 0.5f,
				0.5f, 0.5f, 0.0f,
				1.0f, 0.5f, 0.5f,
				1, -1, -1,
				color2, reverseCull);
	}

	private static void drawOctahedronSide(PoseStack poseStack, VertexConsumer vc,
	                                       float x1, float y1, float z1,
	                                       float x2, float y2, float z2,
	                                       float x3, float y3, float z3,
	                                       float normalX, float normalY, float normalZ,
	                                       int color, boolean reverseCull) {
		PoseStack.Pose pose = poseStack.last();

		if (reverseCull) {
			vc.addVertex(pose, x1, y1, z1).setColor(color).setNormal(pose, -normalX, -normalY, -normalZ);
			vc.addVertex(pose, x3, y3, z3).setColor(color).setNormal(pose, -normalX, -normalY, -normalZ);
			vc.addVertex(pose, x2, y2, z2).setColor(color).setNormal(pose, -normalX, -normalY, -normalZ);
		} else {
			vc.addVertex(pose, x1, y1, z1).setColor(color).setNormal(pose, normalX, normalY, normalZ);
			vc.addVertex(pose, x2, y2, z2).setColor(color).setNormal(pose, normalX, normalY, normalZ);
			vc.addVertex(pose, x3, y3, z3).setColor(color).setNormal(pose, normalX, normalY, normalZ);
		}
	}

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
