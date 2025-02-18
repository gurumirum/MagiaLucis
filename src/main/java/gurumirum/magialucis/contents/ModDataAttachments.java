package gurumirum.magialucis.contents;

import gurumirum.magialucis.contents.data.EnderPortalStorage;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class ModDataAttachments {
	private ModDataAttachments() {}

	public static final DeferredHolder<AttachmentType<?>, AttachmentType<EnderPortalStorage>> ENDER_PORTAL_STORAGE =
			Contents.ATTACHMENTS.register("ender_portal_storage", () ->
					AttachmentType.builder(EnderPortalStorage::new)
							.serialize(new EnderPortalStorage.Serializer())
							.copyOnDeath()
							.build());

	public static void init() {}
}
