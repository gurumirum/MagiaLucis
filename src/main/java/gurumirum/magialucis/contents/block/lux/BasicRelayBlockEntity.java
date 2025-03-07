package gurumirum.magialucis.contents.block.lux;

import gurumirum.magialucis.api.capability.LinkSource;
import gurumirum.magialucis.api.luxnet.InWorldLinkState;
import gurumirum.magialucis.api.luxnet.LinkDestinationSelector;
import gurumirum.magialucis.api.luxnet.LuxNet;
import gurumirum.magialucis.api.luxnet.LuxNetLinkCollector;
import gurumirum.magialucis.api.luxnet.behavior.LuxNodeBehavior;
import gurumirum.magialucis.impl.luxnet.ServerLuxNet;
import gurumirum.magialucis.api.Orientation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class BasicRelayBlockEntity<B extends LuxNodeBehavior> extends LuxNodeBlockEntity<B> implements LinkSource {
	public static final int DEFAULT_MAX_LINKS = 3;

	private final List<@Nullable Orientation> links = new ArrayList<>();

	private @Nullable List<@Nullable Orientation> linksView;

	public BasicRelayBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
		super(type, pos, blockState);
	}

	public @NotNull @UnmodifiableView List<@Nullable Orientation> getLinks() {
		return this.linksView == null ? this.linksView = Collections.unmodifiableList(this.links) : this.linksView;
	}

	@Override
	public void updateLink(LuxNet luxNet, LuxNetLinkCollector linkCollector) {
		if (this.links.isEmpty()) return;
		for (int i = 0; i < this.links.size(); i++) {
			Orientation o = this.links.get(i);
			if (o != null) {
				linkCollector.linkToInWorldNode(this, o,
						linkOrigin(), linkDistance(), i, linkDestinationSelector(), 1, true);
			}
		}
	}

	@Override
	public @Nullable LinkDestinationSelector linkDestinationSelector() {
		return null;
	}

	@Override
	public @NotNull Vec3 linkOrigin() {
		return Vec3.atCenterOf(getBlockPos());
	}

	@Override
	public int maxLinks() {
		return DEFAULT_MAX_LINKS;
	}

	@Override
	public @Nullable Orientation getLink(int index) {
		return index < 0 || index >= maxLinks() || index >= this.links.size() ? null : this.links.get(index);
	}

	@Override
	public @Nullable InWorldLinkState getLinkState(int index) {
		return getLink(index) == null ? null : super.getLinkState(index);
	}

	@Override
	public void setLink(int index, @Nullable Orientation orientation) {
		if (index < 0 || index >= maxLinks()) return;
		if (Objects.equals(getLink(index), orientation)) return;
		while (index >= this.links.size()) this.links.add(null);
		this.links.set(index, orientation);

		ServerLuxNet luxNet = getLuxNet();
		if (luxNet != null) luxNet.queueLinkUpdate(luxNodeId());

		setChanged();
		syncToClient();
	}

	@Override
	protected void save(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider, SaveLoadContext context) {
		super.save(tag, lookupProvider, context);

		if (!this.links.isEmpty()) {
			ListTag list = new ListTag();
			for (int i = 0, size = Math.min(maxLinks(), this.links.size()); i < size; i++) {
				Orientation o = this.links.get(i);
				if (o != null) {
					CompoundTag tag2 = new CompoundTag();
					tag2.putInt("index", i);
					tag2.putLong("orientation", o.packageToLong());
					list.add(tag2);
				}
			}
			tag.put("links", list);
		}
	}

	@Override
	protected void load(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider, SaveLoadContext context) {
		super.load(tag, lookupProvider, context);

		this.links.clear();
		if (tag.contains("links", CompoundTag.TAG_LIST)) {
			ListTag list = tag.getList("links", Tag.TAG_COMPOUND);
			for (int i = 0; i < list.size(); i++) {
				CompoundTag tag2 = list.getCompound(i);
				int index = tag2.getInt("index");
				if (index >= 0 && index < maxLinks()) setLink(index, Orientation.fromLong(tag2.getLong("orientation")));
			}
		}
	}
}
