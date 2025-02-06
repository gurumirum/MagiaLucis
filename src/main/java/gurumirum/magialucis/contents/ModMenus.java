package gurumirum.magialucis.contents;

import gurumirum.magialucis.contents.block.artisanrytable.ArtisanryTableMenu;
import gurumirum.magialucis.contents.item.wandbelt.WandBeltMenu;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;

import static gurumirum.magialucis.contents.Contents.MENUS;

public final class ModMenus {
	private ModMenus() {}

	public static final DeferredHolder<MenuType<?>, MenuType<WandBeltMenu>> WANG_BELT = MENUS.register("wand_belt",
			() -> new MenuType<>(WandBeltMenu::new, FeatureFlagSet.of()));

	public static final DeferredHolder<MenuType<?>, MenuType<ArtisanryTableMenu>> ARTISANRY_TABLE = MENUS.register("artisanry_table",
			() -> new MenuType<>(ArtisanryTableMenu::new, FeatureFlagSet.of()));

	public static void init() {}
}
