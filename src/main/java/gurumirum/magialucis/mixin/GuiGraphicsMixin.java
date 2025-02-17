package gurumirum.magialucis.mixin;

import gurumirum.magialucis.api.augment.Augment;
import gurumirum.magialucis.client.Textures;
import gurumirum.magialucis.contents.ModDataComponents;
import gurumirum.magialucis.contents.data.ItemAugment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin {
	@Inject(method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V",
			at = @At("RETURN"))
	public void magialucis$renderItemDecorations(Font font, ItemStack stack, int x, int y,
	                                             @Nullable String text, CallbackInfo info) {
		if (stack.isEmpty()) return;

		ItemAugment itemAugment = stack.get(ModDataComponents.AUGMENTS);
		if (itemAugment != null && !itemAugment.isEmpty()) {
			int index = (int)Long.remainderUnsigned(
					Long.divideUnsigned(System.currentTimeMillis(), 1000),
					itemAugment.size());
			Augment augment = itemAugment.list().get(index).value();

			ResourceLocation texture = augment.texture(stack);
			if (texture == null) return;

			@SuppressWarnings("DataFlowIssue")
			GuiGraphics self = (GuiGraphics)(Object)this;
			self.pose().pushPose();
			self.pose().translate(0, 0, 200);

			self.blit(x, y, 0, 8, 8, Minecraft.getInstance()
					.getTextureAtlas(Textures.AUGMENT_ATLAS).apply(texture));
			self.pose().popPose();
		}
	}
}
