package gurumirum.magialucis.client;

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

public class LightSphereShaderInstance extends ShaderInstance {
	private final Uniform lightPos;
	private final Uniform lightRadius;

	private final Vector3f vec3 = new Vector3f();
	private final Vector4f vec4 = new Vector4f();

	public LightSphereShaderInstance(ResourceProvider resourceProvider, ResourceLocation shaderLocation, VertexFormat fmt) throws IOException {
		super(resourceProvider, shaderLocation, fmt);

		this.lightPos = getUniform("LightPosition");
		this.lightRadius = getUniform("LightRadius");
	}

	@Override
	public void setDefaultUniforms(VertexFormat.@NotNull Mode mode, @NotNull Matrix4f projectionMatrix,
	                               @NotNull Matrix4f frustrumMatrix, @NotNull Window window) {
		super.setDefaultUniforms(mode, projectionMatrix, frustrumMatrix, window);

		if (this.lightPos != null) this.lightPos.set(this.vec4.set(LightEffect.lightStart(this.vec3), 0));
		if (this.lightRadius != null) this.lightRadius.set(LightEffect.lightRadius());
	}
}
