package gurumirum.gemthing.impl;

import gurumirum.gemthing.impl.LuxNetEvent.ConnectionUpdated;

public interface LuxNodeInterface {
	void updateLink(LuxNet luxNet, LuxNet.LinkCollector linkCollector);

	default void connectionUpdated(ConnectionUpdated connectionUpdated) {}
}
