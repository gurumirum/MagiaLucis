package gurumirum.gemthing.impl;

import gurumirum.gemthing.impl.LuxNetEvent.ConnectionUpdated;

public interface LuxNodeInterface {
	void updateLink();

	default void connectionUpdated(ConnectionUpdated connectionUpdated) {}
}
