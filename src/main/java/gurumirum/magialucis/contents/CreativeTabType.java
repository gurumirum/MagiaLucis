package gurumirum.magialucis.contents;

import gurumirum.magialucis.api.MagiaLucisApi;
import gurumirum.magialucis.contents.data.GemItemData;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Locale;

public enum CreativeTabType {
	MAIN {
		@Override
		protected ItemStack icon() {
			return new ItemStack(Wands.ANCIENT_LIGHT);
		}

		@Override
		protected void generate(ItemDisplayParameters p, CreativeModeTab.Output o) {
			for (var i : Wands.values()) {
				o.accept(i);
				if (i.luxContainerStat() != null) {
					ItemStack stack = new ItemStack(i);
					stack.set(ModDataComponents.LUX_CHARGE, i.luxContainerStat().maxCharge());
					o.accept(stack);
				}
			}

			for (var i : Accessories.values()) {
				o.accept(i);
				if (i.luxContainerStat() != null) {
					ItemStack stack = new ItemStack(i);
					stack.set(ModDataComponents.LUX_CHARGE, i.luxContainerStat().maxCharge());
					o.accept(stack);
				}
			}

			for (var i : ModItems.values()) {
				if (i.getCreativeTab() == this) o.accept(i);
			}

			for (var b : ModBlocks.values()) {
				if (b.getCreativeTab() == this) b.addItem(o);
			}
		}
	},
	RESOURCES {
		@Override
		protected ItemStack icon() {
			return new ItemStack(GemItems.BRIGHTSTONE);
		}

		@Override
		protected void generate(ItemDisplayParameters p, CreativeModeTab.Output o) {
			for (var ore : Ore.values()) ore.allOreItems().forEach(o::accept);

			for (var i : ModItems.values()) {
				if (i.getCreativeTab() == this) o.accept(i);
			}

			for (var b : ModBlocks.values()) {
				if (b.getCreativeTab() == this) b.addItem(o);
			}

			for (var g : Gem.values()) g.forEachItem(o::accept);
		}
	},
	MECHANISMS {
		@Override
		protected ItemStack icon() {
			return new ItemStack(ModBlocks.SUNLIGHT_CORE);
		}

		@Override
		protected void generate(ItemDisplayParameters p, CreativeModeTab.Output o) {
			for (var b : ModBlocks.values()) {
				if (b.getCreativeTab() == this) b.addItem(o);
			}
		}
	},
	TRANSMITTERS {
		@Override
		protected ItemStack icon() {
			ItemStack stack = new ItemStack(ModBlocks.RELAY);
			stack.set(ModDataComponents.GEM_ITEM, new GemItemData(new ItemStack(Items.DIAMOND)));
			return stack;
		}

		@Override
		protected void generate(ItemDisplayParameters p, CreativeModeTab.Output o) {
			for (var b : ModBlocks.values()) {
				if (b.getCreativeTab() == this) b.addItem(o);
			}
		}
	},
	BUILDING_BLOCKS {
		@Override
		protected ItemStack icon() {
			return new ItemStack(ModBuildingBlocks.LAPIS_MANALIS);
		}

		@Override
		protected void generate(ItemDisplayParameters p, CreativeModeTab.Output o) {
			for (var i : ModBuildingBlocks.values()) o.accept(i);
		}
	};

	private final DeferredHolder<CreativeModeTab, CreativeModeTab> holder;

	CreativeTabType() {
		String name = name().toLowerCase(Locale.ROOT);
		this.holder = Contents.CREATIVE_MODE_TABS.register(name, () -> CreativeModeTab.builder()
				.title(Component.translatable("itemGroup." + MagiaLucisApi.MODID + "." + name))
				.icon(this::icon)
				.withTabsBefore(getTabsBefore())
				.displayItems(this::generate)
				.build());
	}

	protected abstract ItemStack icon();
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
