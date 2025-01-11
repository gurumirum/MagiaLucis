package gurumirum.magialucis.contents.block.lux;

import gurumirum.magialucis.capability.LinkSource;
import gurumirum.magialucis.impl.luxnet.InWorldLinkState;
import gurumirum.magialucis.impl.luxnet.LinkDestinationSelector;
import gurumirum.magialucis.impl.luxnet.LuxNet;
import gurumirum.magialucis.impl.luxnet.LuxUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class BasicRelayBlockEntity extends LuxNodeBlockEntity implements LinkSource {
	public static final int DEFAULT_MAX_LINKS = 3;

	private final List<@Nullable Orientation> links = new ArrayList<>();
	private final Int2ObjectMap<InWorldLinkState> linkIndexToState = new Int2ObjectOpenHashMap<>();

	private @Nullable List<@Nullable Orientation> linksView;

	public BasicRelayBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
		super(type, pos, blockState);
	}

	public @NotNull @UnmodifiableView List<@Nullable Orientation> getLinks() {
		return this.linksView == null ? this.linksView = Collections.unmodifiableList(this.links) : this.linksView;
	}

	@Override
	public void updateLink(LuxNet luxNet, LuxNet.LinkCollector linkCollector) {
		if (this.links.isEmpty()) return;
		for (int i = 0; i < this.links.size(); i++) {
			Orientation o = this.links.get(i);
			if (o != null) {
				LuxUtils.linkToInWorldNode(this, linkCollector, o.xRot(), o.yRot(), linkDistance(),
						luxNodeId(), i, linkDestinationSelector());
			}
		}
	}

	protected @Nullable LinkDestinationSelector linkDestinationSelector(){
		return null;
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
		return getLink(index) == null ? null : this.linkIndexToState.get(index);
	}

	@Override
	public void setLink(int index, @Nullable Orientation orientation) {
		if (index < 0 || index >= maxLinks()) return;
		if (Objects.equals(getLink(index), orientation)) return;
		while (index >= this.links.size()) this.links.add(null);
		this.links.set(index, orientation);

		LuxNet luxNet = getLuxNet();
		if (luxNet != null) luxNet.queueLinkUpdate(luxNodeId());

		setChanged();
		syncToClient();
	}

	@Override
	public void syncLinkStatus(@NotNull @UnmodifiableView Int2ObjectMap<InWorldLinkState> linkIndexToState) {
		if (!this.linkIndexToState.equals(linkIndexToState)) {
			this.linkIndexToState.clear();
			this.linkIndexToState.putAll(linkIndexToState);
			syncToClient();
		}
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

		if (context.isSync()) {
			ListTag list = new ListTag();
			for (var e : this.linkIndexToState.int2ObjectEntrySet()) {
				CompoundTag tag2 = e.getValue().save();
				tag2.putInt("index", e.getIntKey());
				list.add(tag2);
			}
			tag.put("linkIndexToState", list);
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

		if (context.isSync()) {
			ListTag list = tag.getList("linkIndexToState", Tag.TAG_COMPOUND);
			this.linkIndexToState.clear();
			for (int i = 0; i < list.size(); i++) {
				CompoundTag tag2 = list.getCompound(i);
				this.linkIndexToState.put(tag2.getInt("index"), new InWorldLinkState(tag2));
			}
		}
	}
}
