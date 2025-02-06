package gurumirum.magialucis.contents.block.lux.lightloom;

import gurumirum.magialucis.client.render.RenderEffects;
import gurumirum.magialucis.contents.block.lux.LuxNodeBlockEntity;
import gurumirum.magialucis.impl.luxnet.LuxNet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

public class LightLoomBlockEntity extends LuxNodeBlockEntity<LightLoomBehavior> {
	private final LightLoomType type;

	public LightLoomBlockEntity(LightLoomType type, BlockPos pos, BlockState blockState) {
		super(type.blockEntityType(), pos, blockState);
		this.type = type;
	}

	public LightLoomType type() {
		return this.type;
	}

	public @NotNull Vector3d luxInput(@NotNull Vector3d dest) {
		return nodeBehavior().luxInput.min(dest);
	}

	@Override
	public void onLoad() {
		super.onLoad();
		if (this.level != null && this.level.isClientSide) {
			RenderEffects.light.register(new LightLoomBlockLightEffectProvider(this));
		}
	}

	@Override
	protected @NotNull LightLoomBehavior createNodeBehavior() {
		return new LightLoomBehavior(this.type);
	}

	@Override
	public void updateLink(LuxNet luxNet, LuxNet.LinkCollector linkCollector) {}
}
