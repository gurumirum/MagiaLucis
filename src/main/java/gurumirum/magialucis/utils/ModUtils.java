package gurumirum.magialucis.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamEncoder;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntFunction;

public final class ModUtils {
	private ModUtils() {}

	public static void giveOrDrop(@Nullable Player player, @NotNull Level level,
	                              @NotNull ItemStack stack, @NotNull BlockPos pos) {
		giveOrDrop(player, level, stack,
				pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
				0, 0, 0);
	}

	public static void giveOrDrop(@Nullable Player player, @NotNull Level level, @NotNull ItemStack stack,
	                              double x, double y, double z,
	                              double dx, double dy, double dz) {
		if (player != null) {
			if (player.addItem(stack)) {
				level.playSound(null, player.getX(), player.getY(), player.getZ(),
						SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2f,
						((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7f + 1) * 2);
				return;
			}
		}

		drop(level, stack, x, y, z, dx, dy, dz);
	}

	public static void drop(@NotNull Level level, @NotNull BlockPos pos, @NotNull ItemStack stack) {
		drop(level, stack,
				pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
				0, 0, 0);
	}

	public static void drop(@NotNull Level level, @NotNull ItemStack stack,
	                        double x, double y, double z,
	                        double dx, double dy, double dz) {
		ItemEntity itemEntity = new ItemEntity(level, x, y, z, stack);
		itemEntity.setDeltaMovement(dx, dy, dz);
		level.addFreshEntity(itemEntity);
	}

	public static void addInventorySlots(@NotNull Inventory inventory, int inventoryX, int inventoryY,
	                                     @NotNull Consumer<Slot> addSlot) {
		addInventorySlots(inventory, inventoryX, inventoryY, addSlot, null);
	}

	public static void addInventorySlots(@NotNull Inventory inventory, int inventoryX, int inventoryY,
	                                     @NotNull Consumer<Slot> addSlot, @Nullable SlotFactory slotFactory) {
		if (slotFactory == null) slotFactory = SlotFactory.DEFAULT;

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlot.accept(slotFactory.createSlot(inventory, j + i * 9 + 9,
						inventoryX + 8 + j * 18, inventoryY + 8 + i * 18));
			}
		}

		for (int i = 0; i < 9; i++) {
			addSlot.accept(slotFactory.createSlot(inventory, i,
					inventoryX + 8 + i * 18, inventoryY + 66));
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> void writeCollection(
			RegistryFriendlyByteBuf buffer, Collection<T> collection,
			StreamEncoder<RegistryFriendlyByteBuf, T> elementWriter) {
		buffer.writeCollection(collection, (StreamEncoder<? super FriendlyByteBuf, T>)(Object)elementWriter);
	}

	@SuppressWarnings("unchecked")
	public static <T, C extends Collection<T>> C readCollection(
			RegistryFriendlyByteBuf buffer, IntFunction<C> collectionFactory,
			StreamDecoder<RegistryFriendlyByteBuf, T> elementReader) {
		return buffer.readCollection(collectionFactory, (StreamDecoder<? super FriendlyByteBuf, T>)(Object)elementReader);
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> readList(RegistryFriendlyByteBuf buffer, StreamDecoder<RegistryFriendlyByteBuf, T> elementReader) {
		return buffer.readList((StreamDecoder<? super FriendlyByteBuf, T>)(Object)elementReader);
	}

	@FunctionalInterface
	public interface SlotFactory {
		SlotFactory DEFAULT = Slot::new;

		@NotNull Slot createSlot(@NotNull Inventory inv, int i, int x, int y);
	}
}
