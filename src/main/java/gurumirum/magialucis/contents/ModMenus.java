package gurumirum.magialucis.contents;

import gurumirum.magialucis.contents.item.wandbelt.WandBeltMenu;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class ModMenus {
	private ModMenus(){}

	public static final DeferredHolder<MenuType<?>, MenuType<WandBeltMenu>> WANG_BELT_MENU = Contents.MENUS.register("wand_belt",
			() -> new MenuType<>(WandBeltMenu::new, FeatureFlagSet.of()));

	public static void init(){}
}
