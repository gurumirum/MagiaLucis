package gurumirum.magialucis.contents;

import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.capability.GemStats;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Locale;
import java.util.function.Supplier;

public enum CreativeTabType {
	MAIN(() -> new ItemStack(Wands.ANCIENT_LIGHT)) {
		@Override
		protected void generate(ItemDisplayParameters p, CreativeModeTab.Output o) {
			for (var i : Wands.values()) {
				o.accept(i);
				if (i.luxContainerStat() != null) {
					ItemStack stack = new ItemStack(i);
					stack.set(Contents.LUX_CHARGE, i.luxContainerStat().maxCharge());
					o.accept(stack);
				}
			}

			for (var i : ModItems.values()) {
				if (i.getCreativeTab() == this) o.accept(i);
			}

			for (var i : ModBlocks.values()) i.addItem(o);
		}
	},
	RESOURCES(() -> new ItemStack(GemItems.BRIGHTSTONE)) {
		@Override
		protected void generate(ItemDisplayParameters p, CreativeModeTab.Output o) {
			for (var ore : Ore.values()) ore.allOreItems().forEach(o::accept);

			for (var i : ModItems.values()) {
				if (i.getCreativeTab() == this) o.accept(i);
			}

			for (var g : GemStats.values()) g.forEachItem(o::accept);
		}
	},
	BUILDING_BLOCKS(() -> new ItemStack(ModBuildingBlocks.LAPIS_MANALIS)) {
		@Override
		protected void generate(ItemDisplayParameters p, CreativeModeTab.Output o) {
			for (var i : ModBuildingBlocks.values()) o.accept(i);
		}
	};

	private final DeferredHolder<CreativeModeTab, CreativeModeTab> holder;

	CreativeTabType(Supplier<ItemStack> icon) {
		String name = name().toLowerCase(Locale.ROOT);
		this.holder = Contents.CREATIVE_MODE_TABS.register(name, () -> CreativeModeTab.builder()
				.title(Component.translatable("itemGroup." + MagiaLucisMod.MODID + "." + name))
				.icon(icon)
				.withTabsBefore(getTabsBefore())
				.displayItems(this::generate)
				.build());
	}

	protected abstract void generate(ItemDisplayParameters p, CreativeModeTab.Output o);

	private ResourceLocation[] getTabsBefore() {
		ResourceLocation[] tabsBefore = new ResourceLocation[ordinal()];
		for (int i = 0; i < tabsBefore.length; i++) {
			tabsBefore[i] = values()[i].holder.getId();
		}
		return tabsBefore;
	}

	public static void init() {}
}
