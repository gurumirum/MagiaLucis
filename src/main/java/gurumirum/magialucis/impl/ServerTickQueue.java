package gurumirum.magialucis.impl;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ServerTickQueue {
	private ServerTickQueue() {}

	public static @Nullable Ticket tryEnqueue(@Nullable Level level, @NotNull Runnable thing) {
		if (level == null) return null;
		MinecraftServer server = level.getServer();
		return server != null ? enqueue(server, thing) : null;
	}

	public static Ticket enqueue(@NotNull MinecraftServer server, @NotNull Runnable thing) {
		Ticket t = new Ticket(thing);
		server.tell(new TickTask(0, () -> {
			if (t.thing != null) t.thing.run();
		}));
		return t;
	}

	public static final class Ticket {
		private @Nullable Runnable thing;

		public Ticket(@Nullable Runnable thing) {
			this.thing = thing;
		}

		public void drop() {
			this.thing = null;
		}
	}
}
