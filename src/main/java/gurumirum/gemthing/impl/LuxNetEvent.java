package gurumirum.gemthing.impl;

import org.jetbrains.annotations.NotNull;

public sealed interface LuxNetEvent {
	void dispatch(@NotNull LuxNetEventDispatcher dispatcher);

	void call(@NotNull LuxNodeInterface iface);

	record ConnectionUpdated(
			int sourceNode,
			int prevDestinationNode,
			int newDestinationNode
	) implements LuxNetEvent {
		@Override
		public void dispatch(@NotNull LuxNetEventDispatcher dispatcher) {
			dispatcher.dispatchEvent(this.sourceNode, this);
			dispatcher.dispatchEvent(this.prevDestinationNode, this);
			dispatcher.dispatchEvent(this.newDestinationNode, this);
		}

		@Override
		public void call(@NotNull LuxNodeInterface iface) {
			iface.connectionUpdated(this);
		}
	}

	@FunctionalInterface
	interface LuxNetEventDispatcher {
		void dispatchEvent(int nodeId, @NotNull LuxNetEvent event);
	}
}
