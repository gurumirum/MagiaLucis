package gurumirum.magialucis.client.render.light;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.IOException;

public class LightCylinderShaderInstance extends ShaderInstance {
	private final Uniform lightStart;
	private final Uniform lightEnd;
	private final Uniform lightRadius;

	private final Vector3f vec3 = new Vector3f();
	private final Vector4f vec4 = new Vector4f();

	public LightCylinderShaderInstance(ResourceProvider resourceProvider, ResourceLocation shaderLocation, VertexFormat fmt) throws IOException {
		super(resourceProvider, shaderLocation, fmt);

		this.lightStart = getUniform("LightStart");
		this.lightEnd = getUniform("LightEnd");
		this.lightRadius = getUniform("LightRadius");
	}

	@Override
	public void setDefaultUniforms(VertexFormat.@NotNull Mode mode, @NotNull Matrix4f projectionMatrix,
	                               @NotNull Matrix4f frustrumMatrix, @NotNull Window window) {
		super.setDefaultUniforms(mode, projectionMatrix, frustrumMatrix, window);

		if (this.lightStart != null) this.lightStart.set(this.vec4.set(LightEffectRender.lightStart(this.vec3), 0));
		if (this.lightEnd != null) this.lightEnd.set(this.vec4.set(LightEffectRender.lightEnd(this.vec3), 0));
		if (this.lightRadius != null) this.lightRadius.set(LightEffectRender.lightRadius());
	}
}
