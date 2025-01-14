package gurumirum.magialucis.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import gurumirum.magialucis.MagiaLucisMod;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector3f;

public final class RenderShapes {
	private RenderShapes() {}

	public static final ResourceLocation NO_FABULOUS_TEXTURE = MagiaLucisMod.id("textures/effect/no_fabulous.png");
	public static final ResourceLocation NO_FABULOUS_TEXTURE_JOKE = MagiaLucisMod.id("textures/effect/install_cs_source.png");

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

	private static final int SPHERE_Y_SLICE = 16;
	private static final int SPHERE_X_SLICE = 32;

	private static final Vector3f[][] sphereVectorCache = new Vector3f[SPHERE_Y_SLICE - 1][SPHERE_X_SLICE];

	static {
		Vector2d[] sinCosPhi = new Vector2d[SPHERE_Y_SLICE - 1];

		for (int i = 0; i < SPHERE_Y_SLICE - 1; i++) {
			int ySliceIndex = i + 1;
			double phi = Math.PI * ((double)ySliceIndex / SPHERE_Y_SLICE) + Math.PI / 2;
			double sinPhi = Math.sin(phi);
			double cosPhi = Math.cos(phi);
			sinCosPhi[i] = new Vector2d().set(sinPhi, cosPhi);
		}

		for (int j = 0; j < SPHERE_X_SLICE; j++) {
			double theta = 2 * Math.PI * ((double)j / SPHERE_X_SLICE);
			double sinTheta = Math.sin(theta);
			double cosTheta = Math.cos(theta);

			for (int i = 0; i < SPHERE_Y_SLICE - 1; i++) {
				Vector2d phi = sinCosPhi[i];
				double sinPhi = phi.x;
				double cosPhi = phi.y;

				sphereVectorCache[i][j] = new Vector3f().set(cosPhi * cosTheta, sinPhi, cosPhi * sinTheta);
			}
		}
	}

	public static void sphere(PoseStack poseStack, VertexConsumer vc, int color) {
		upperSphere(poseStack, vc, color);
		lowerSphere(poseStack, vc, color);
	}

	public static void cylinder(PoseStack poseStack, VertexConsumer vc, float startOffset, float endOffset, int color, boolean reverseCull) {
		int ySlice = (SPHERE_Y_SLICE - 1) / 2;

		for (int j = 0; j < SPHERE_X_SLICE; j++) {
			int nextSliceIndex = (j + 1) % SPHERE_X_SLICE;
			Vector3f v1 = sphereVectorCache[ySlice][j];
			Vector3f v2 = sphereVectorCache[ySlice][nextSliceIndex];

			tri(poseStack, vc,
					0, startOffset, 0,
					v1.x, v1.y + startOffset, v1.z,
					v2.x, v2.y + startOffset, v2.z,
					color, reverseCull);

			tri(poseStack, vc,
					v1.x, v1.y + startOffset, v1.z,
					v1.x, v1.y + endOffset, v1.z,
					v2.x, v2.y + endOffset, v2.z,
					color, reverseCull);

			tri(poseStack, vc,
					v1.x, v1.y + startOffset, v1.z,
					v2.x, v2.y + endOffset, v2.z,
					v2.x, v2.y + startOffset, v2.z,
					color, reverseCull);

			tri(poseStack, vc,
					v1.x, v1.y + endOffset, v1.z,
					0, endOffset, 0,
					v2.x, v2.y + endOffset, v2.z,
					color, reverseCull);
		}
	}

	public static void upperSphere(PoseStack poseStack, VertexConsumer vc, int color) {
		PoseStack.Pose pose = poseStack.last();
		for (int i = 0, end = (SPHERE_Y_SLICE - 1) / 2; i < end; i++) {
			if (i == 0) {
				for (int j = 0; j < SPHERE_X_SLICE; j++) {
					int nextSliceIndex = (j + 1) % SPHERE_X_SLICE;
					vc.addVertex(pose, sphereVectorCache[i][j]).setColor(color);
					vc.addVertex(pose, 0, 1, 0).setColor(color);
					vc.addVertex(pose, sphereVectorCache[i][nextSliceIndex]).setColor(color);
				}
			}

			for (int j = 0; j < SPHERE_X_SLICE; j++) {
				int nextSliceIndex = (j + 1) % SPHERE_X_SLICE;
				vc.addVertex(pose, sphereVectorCache[i + 1][j]).setColor(color);
				vc.addVertex(pose, sphereVectorCache[i][j]).setColor(color);
				vc.addVertex(pose, sphereVectorCache[i][nextSliceIndex]).setColor(color);

				vc.addVertex(pose, sphereVectorCache[i + 1][j]).setColor(color);
				vc.addVertex(pose, sphereVectorCache[i][nextSliceIndex]).setColor(color);
				vc.addVertex(pose, sphereVectorCache[i + 1][nextSliceIndex]).setColor(color);
			}
		}
	}

	public static void lowerSphere(PoseStack poseStack, VertexConsumer vc, int color) {
		PoseStack.Pose pose = poseStack.last();
		for (int i = (SPHERE_Y_SLICE - 1) / 2; i < SPHERE_Y_SLICE - 1; i++) {
			if (i == SPHERE_Y_SLICE - 2) {
				for (int j = 0; j < SPHERE_X_SLICE; j++) {
					int nextSliceIndex = (j + 1) % SPHERE_X_SLICE;
					vc.addVertex(pose, 0, -1, 0).setColor(color);
					vc.addVertex(pose, sphereVectorCache[i][j]).setColor(color);
					vc.addVertex(pose, sphereVectorCache[i][nextSliceIndex]).setColor(color);
				}
			} else {
				for (int j = 0; j < SPHERE_X_SLICE; j++) {
					int nextSliceIndex = (j + 1) % SPHERE_X_SLICE;
					vc.addVertex(pose, sphereVectorCache[i + 1][j]).setColor(color);
					vc.addVertex(pose, sphereVectorCache[i][j]).setColor(color);
					vc.addVertex(pose, sphereVectorCache[i][nextSliceIndex]).setColor(color);

					vc.addVertex(pose, sphereVectorCache[i + 1][j]).setColor(color);
					vc.addVertex(pose, sphereVectorCache[i][nextSliceIndex]).setColor(color);
					vc.addVertex(pose, sphereVectorCache[i + 1][nextSliceIndex]).setColor(color);
				}
			}
		}
	}

	private static void tri(PoseStack poseStack, VertexConsumer vc,
	                        float x1, float y1, float z1,
	                        float x2, float y2, float z2,
	                        float x3, float y3, float z3,
	                        int color, boolean reverseCull) {
		PoseStack.Pose pose = poseStack.last();
		if (!reverseCull) vc.addVertex(pose, x1, y1, z1).setColor(color);
		vc.addVertex(pose, x2, y2, z2).setColor(color);
		if (reverseCull) vc.addVertex(pose, x1, y1, z1).setColor(color);
		vc.addVertex(pose, x3, y3, z3).setColor(color);
	}
}
