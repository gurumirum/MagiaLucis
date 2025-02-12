package gurumirum.magialucis.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import gurumirum.magialucis.MagiaLucisMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
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

		positionColorNormalTri(poseStack, vc,
				0.5f, 1.0f, 0.5f,
				0.5f, 0.5f, 1.0f,
				1.0f, 0.5f, 0.5f,
				1, 1, 1,
				color2, reverseCull);

		positionColorNormalTri(poseStack, vc,
				0.5f, 1.0f, 0.5f,
				0.0f, 0.5f, 0.5f,
				0.5f, 0.5f, 1.0f,
				-1, 1, 1,
				color, reverseCull);

		positionColorNormalTri(poseStack, vc,
				0.5f, 1.0f, 0.5f,
				0.5f, 0.5f, 0.0f,
				0.0f, 0.5f, 0.5f,
				-1, 1, -1,
				color2, reverseCull);

		positionColorNormalTri(poseStack, vc,
				0.5f, 1.0f, 0.5f,
				1.0f, 0.5f, 0.5f,
				0.5f, 0.5f, 0.0f,
				1, 1, -1,
				color, reverseCull);

		positionColorNormalTri(poseStack, vc,
				0.5f, 0.0f, 0.5f,
				1.0f, 0.5f, 0.5f,
				0.5f, 0.5f, 1.0f,
				1, -1, 1,
				color, reverseCull);

		positionColorNormalTri(poseStack, vc,
				0.5f, 0.0f, 0.5f,
				0.5f, 0.5f, 1.0f,
				0.0f, 0.5f, 0.5f,
				-1, -1, 1,
				color2, reverseCull);

		positionColorNormalTri(poseStack, vc,
				0.5f, 0.0f, 0.5f,
				0.0f, 0.5f, 0.5f,
				0.5f, 0.5f, 0.0f,
				-1, -1, -1,
				color, reverseCull);

		positionColorNormalTri(poseStack, vc,
				0.5f, 0.0f, 0.5f,
				0.5f, 0.5f, 0.0f,
				1.0f, 0.5f, 0.5f,
				1, -1, -1,
				color2, reverseCull);
	}

	public static void drawTruncatedCube(PoseStack poseStack, VertexConsumer vc, int color, boolean reverseCull) {
		// top
		positionColorNormalQuad(poseStack, vc,
				0.5f, 1.0f, 0.0f,
				0.0f, 1.0f, 0.5f,
				0.5f, 1.0f, 1.0f,
				1.0f, 1.0f, 0.5f,
				0, 1, 0,
				color, reverseCull);

		// bottom
		positionColorNormalQuad(poseStack, vc,
				0.5f, 0.0f, 0.0f,
				1.0f, 0.0f, 0.5f,
				0.5f, 0.0f, 1.0f,
				0.0f, 0.0f, 0.5f,
				0, -1, 0,
				color, reverseCull);

		// sides
		positionColorNormalQuad(poseStack, vc,
				1.0f, 0.5f, 0.0f,
				1.0f, 1.0f, 0.5f,
				1.0f, 0.5f, 1.0f,
				1.0f, 0.0f, 0.5f,
				1, 0, 0,
				color, reverseCull);

		positionColorNormalQuad(poseStack, vc,
				0.0f, 0.5f, 0.0f,
				0.0f, 0.0f, 0.5f,
				0.0f, 0.5f, 1.0f,
				0.0f, 1.0f, 0.5f,
				-1, 0, 0,
				color, reverseCull);

		positionColorNormalQuad(poseStack, vc,
				1.0f, 0.5f, 1.0f,
				0.5f, 1.0f, 1.0f,
				0.0f, 0.5f, 1.0f,
				0.5f, 0.0f, 1.0f,
				0, 0, 1,
				color, reverseCull);

		positionColorNormalQuad(poseStack, vc,
				1.0f, 0.5f, 0.0f,
				0.5f, 0.0f, 0.0f,
				0.0f, 0.5f, 0.0f,
				0.5f, 1.0f, 0.0f,
				0, 0, -1,
				color, reverseCull);

		// truncated edges

		// top
		positionColorNormalTri(poseStack, vc,
				0.5f, 1.0f, 1.0f,
				1.0f, 0.5f, 1.0f,
				1.0f, 1.0f, 0.5f,
				1, 1, 1,
				color, reverseCull);

		positionColorNormalTri(poseStack, vc,
				0.0f, 1.0f, 0.5f,
				0.0f, 0.5f, 1.0f,
				0.5f, 1.0f, 1.0f,
				-1, 1, 1,
				color, reverseCull);

		positionColorNormalTri(poseStack, vc,
				1.0f, 1.0f, 0.5f,
				1.0f, 0.5f, 0.0f,
				0.5f, 1.0f, 0.0f,
				1, 1, -1,
				color, reverseCull);

		positionColorNormalTri(poseStack, vc,
				0.5f, 1.0f, 0.0f,
				0.0f, 0.5f, 0.0f,
				0.0f, 1.0f, 0.5f,
				-1, 1, -1,
				color, reverseCull);

		// bottom
		positionColorNormalTri(poseStack, vc,
				1.0f, 0.5f, 1.0f,
				0.5f, 0.0f, 1.0f,
				1.0f, 0.0f, 0.5f,
				1, -1, 1,
				color, reverseCull);

		positionColorNormalTri(poseStack, vc,
				0.0f, 0.5f, 1.0f,
				0.0f, 0.0f, 0.5f,
				0.5f, 0.0f, 1.0f,
				-1, -1, 1,
				color, reverseCull);

		positionColorNormalTri(poseStack, vc,
				1.0f, 0.5f, 0.0f,
				1.0f, 0.0f, 0.5f,
				0.5f, 0.0f, 0.0f,
				1, -1, -1,
				color, reverseCull);

		positionColorNormalTri(poseStack, vc,
				0.0f, 0.5f, 0.0f,
				0.5f, 0.0f, 0.0f,
				0.0f, 0.0f, 0.5f,
				-1, -1, -1,
				color, reverseCull);
	}

	// epic method name
	public static void drawRhombicuboctahedronDome(PoseStack poseStack, VertexConsumer vc, int color, boolean reverseCull) {
		float a1 = 6 / 16f, a2 = 1 - a1;
		float b1 = 2 / 16f + 0.01f, b2 = 1 - b1;

		// orthogonal faces
		positionColorNormalQuad(poseStack, vc,
				a2, b2, a1,
				a1, b2, a1,
				a1, b2, a2,
				a2, b2, a2,
				0, 1, 0,
				color, reverseCull);

		positionColorNormalQuad(poseStack, vc,
				b1, a2, a1,
				b1, a1, a1,
				b1, a1, a2,
				b1, a2, a2,
				-1, 0, 0,
				color, reverseCull);

		positionColorNormalQuad(poseStack, vc,
				b2, a1, a1,
				b2, a2, a1,
				b2, a2, a2,
				b2, a1, a2,
				1, 0, 0,
				color, reverseCull);

		positionColorNormalQuad(poseStack, vc,
				a2, a1, b1,
				a1, a1, b1,
				a1, a2, b1,
				a2, a2, b1,
				0, 0, -1,
				color, reverseCull);

		positionColorNormalQuad(poseStack, vc,
				a1, a1, b2,
				a2, a1, b2,
				a2, a2, b2,
				a1, a2, b2,
				0, 0, 1,
				color, reverseCull);

		// diagonal faces ("edge" faces (idk the exact mathematical term (honestly idgaf (sorry math nerds))))

		positionColorNormalQuad(poseStack, vc,
				a1, a1, b1,
				b1, a1, a1,
				b1, a2, a1,
				a1, a2, b1,
				-1, 0, -1,
				color, reverseCull);

		positionColorNormalQuad(poseStack, vc,
				b2, a1, a1,
				a2, a1, b1,
				a2, a2, b1,
				b2, a2, a1,
				1, 0, -1,
				color, reverseCull);

		positionColorNormalQuad(poseStack, vc,
				b1, a1, a2,
				a1, a1, b2,
				a1, a2, b2,
				b1, a2, a2,
				-1, 0, 1,
				color, reverseCull);

		positionColorNormalQuad(poseStack, vc,
				a2, a1, b2,
				b2, a1, a2,
				b2, a2, a2,
				a2, a2, b2,
				1, 0, 1,
				color, reverseCull);

		// top edge faces
		positionColorNormalQuad(poseStack, vc,
				a1, b2, a1,
				b1, a2, a1,
				b1, a2, a2,
				a1, b2, a2,
				-1, 1, 0,
				color, reverseCull);

		positionColorNormalQuad(poseStack, vc,
				a2, b2, a2,
				b2, a2, a2,
				b2, a2, a1,
				a2, b2, a1,
				1, 1, 0,
				color, reverseCull);

		positionColorNormalQuad(poseStack, vc,
				a1, a2, b1,
				a1, b2, a1,
				a2, b2, a1,
				a2, a2, b1,
				0, 1, -1,
				color, reverseCull);

		positionColorNormalQuad(poseStack, vc,
				a2, a2, b2,
				a2, b2, a2,
				a1, b2, a2,
				a1, a2, b2,
				0, 1, 1,
				color, reverseCull);

		// tri faces ("vertex" faces (ok this sounds fackin ridiculous))
		positionColorNormalTri(poseStack, vc,
				a1, b2, a1,
				a1, a2, b1,
				b1, a2, a1,
				-1, 1, -1,
				color, reverseCull);

		positionColorNormalTri(poseStack, vc,
				a2, b2, a1,
				b2, a2, a1,
				a2, a2, b1,
				1, 1, -1,
				color, reverseCull);

		positionColorNormalTri(poseStack, vc,
				a1, b2, a2,
				b1, a2, a2,
				a1, a2, b2,
				-1, 1, 1,
				color, reverseCull);

		positionColorNormalTri(poseStack, vc,
				a2, b2, a2,
				a2, a2, b2,
				b2, a2, a2,
				1, 1, 1,
				color, reverseCull);
	}

	private static void positionColorNormalQuad(PoseStack poseStack, VertexConsumer vc,
	                                            float x1, float y1, float z1,
	                                            float x2, float y2, float z2,
	                                            float x3, float y3, float z3,
	                                            float x4, float y4, float z4,
	                                            float normalX, float normalY, float normalZ,
	                                            int color, boolean reverseCull) {
		positionColorNormalTri(poseStack, vc,
				x1, y1, z1,
				x2, y2, z2,
				x3, y3, z3,
				normalX, normalY, normalZ,
				color, reverseCull);

		positionColorNormalTri(poseStack, vc,
				x3, y3, z3,
				x4, y4, z4,
				x1, y1, z1,
				normalX, normalY, normalZ,
				color, reverseCull);
	}

	private static void positionColorNormalTri(PoseStack poseStack, VertexConsumer vc,
	                                           float x1, float y1, float z1,
	                                           float x2, float y2, float z2,
	                                           float x3, float y3, float z3,
	                                           float normalX, float normalY, float normalZ,
	                                           int color, boolean reverseCull) {
		PoseStack.Pose pose = poseStack.last();

		if (reverseCull) {
			Vector3f n = pose.transformNormal(-normalX, -normalY, -normalZ, new Vector3f());
			vc.addVertex(pose, x1, y1, z1).setColor(color).setNormal(n.x, n.y, n.z);
			vc.addVertex(pose, x3, y3, z3).setColor(color).setNormal(n.x, n.y, n.z);
			vc.addVertex(pose, x2, y2, z2).setColor(color).setNormal(n.x, n.y, n.z);
		} else {
			Vector3f n = pose.transformNormal(normalX, normalY, normalZ, new Vector3f());
			vc.addVertex(pose, x1, y1, z1).setColor(color).setNormal(n.x, n.y, n.z);
			vc.addVertex(pose, x2, y2, z2).setColor(color).setNormal(n.x, n.y, n.z);
			vc.addVertex(pose, x3, y3, z3).setColor(color).setNormal(n.x, n.y, n.z);
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

	public static void texturedTintedBox(PoseStack poseStack, VertexConsumer vc,
	                                     float x1, float y1, float z1,
	                                     float x2, float y2, float z2,
	                                     int c1) {
		texturedTintedBox(poseStack, vc,
				x1, y1, z1,
				x2, y2, z2,
				0, 1, 0, 1,
				c1);
	}

	// TODO uv's all fucked up and idc enough to fix it sorry
	public static void texturedTintedBox(PoseStack poseStack, VertexConsumer vc,
	                                     float x1, float y1, float z1,
	                                     float x2, float y2, float z2,
	                                     float u1, float u2, float v1, float v2,
	                                     int c1) {
		PoseStack.Pose pose = poseStack.last();

		vc.addVertex(pose, x1, y1, z1).setUv(u1, v2).setColor(c1);
		vc.addVertex(pose, x1, y2, z1).setUv(u1, v1).setColor(c1);
		vc.addVertex(pose, x2, y2, z1).setUv(u2, v1).setColor(c1);
		vc.addVertex(pose, x2, y1, z1).setUv(u2, v2).setColor(c1);

		vc.addVertex(pose, x1, y1, z2).setUv(u1, v2).setColor(c1);
		vc.addVertex(pose, x1, y1, z1).setUv(u1, v1).setColor(c1);
		vc.addVertex(pose, x2, y1, z1).setUv(u2, v1).setColor(c1);
		vc.addVertex(pose, x2, y1, z2).setUv(u2, v2).setColor(c1);

		vc.addVertex(pose, x1, y1, z1).setUv(u2, v1).setColor(c1);
		vc.addVertex(pose, x1, y1, z2).setUv(u2, v2).setColor(c1);
		vc.addVertex(pose, x1, y2, z2).setUv(u1, v2).setColor(c1);
		vc.addVertex(pose, x1, y2, z1).setUv(u1, v1).setColor(c1);

		vc.addVertex(pose, x2, y2, z2).setUv(u1, v2).setColor(c1);
		vc.addVertex(pose, x2, y2, z1).setUv(u1, v1).setColor(c1);
		vc.addVertex(pose, x1, y2, z1).setUv(u2, v1).setColor(c1);
		vc.addVertex(pose, x1, y2, z2).setUv(u2, v2).setColor(c1);

		vc.addVertex(pose, x2, y2, z1).setUv(u2, v1).setColor(c1);
		vc.addVertex(pose, x2, y2, z2).setUv(u2, v2).setColor(c1);
		vc.addVertex(pose, x2, y1, z2).setUv(u1, v2).setColor(c1);
		vc.addVertex(pose, x2, y1, z1).setUv(u1, v1).setColor(c1);

		vc.addVertex(pose, x2, y1, z2).setUv(u1, v2).setColor(c1);
		vc.addVertex(pose, x2, y2, z2).setUv(u1, v1).setColor(c1);
		vc.addVertex(pose, x1, y2, z2).setUv(u2, v1).setColor(c1);
		vc.addVertex(pose, x1, y1, z2).setUv(u2, v2).setColor(c1);
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

	public static void renderMatrix(@NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource,
	                                @NotNull ResourceLocation texture, float coreRotation, float scale,
	                                @Nullable Quaternionf rotation) {
		poseStack.pushPose();
		poseStack.translate(.5f, .5f, .5f);

		Quaternionf q = new Quaternionf();
		if (rotation != null) q.mul(rotation);
		q.rotateYXZ(-coreRotation, (float)(Math.PI / 4), (float)(Math.PI / 4));

		poseStack.mulPose(q);
		poseStack.scale(scale, scale, scale);
		poseStack.translate(-.5f, -.5f, -.5f);

		TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(texture);

		VertexConsumer vc = bufferSource.getBuffer(ModRenderTypes.positionTextureColor(InventoryMenu.BLOCK_ATLAS));
		texturedTintedBox(poseStack, vc,
				4 / 16f, 4 / 16f, 4 / 16f,
				12 / 16f, 12 / 16f, 12 / 16f,
				sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1(),
				-1);

		poseStack.popPose();
	}
}
