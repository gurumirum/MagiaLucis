package gurumirum.gemthing.impl;

import gurumirum.gemthing.impl.LuxNetEvent.ConnectionUpdated;

public interface LuxNodeInterface {
	void updateProperties(LuxNet luxNet, LuxNode node);
	void updateLink(LuxNet luxNet, LuxNet.LinkCollector linkCollector);

	default void onBind(LuxNet luxNet, LuxNode node) {
		updateProperties(luxNet, node);
	}

	default void connectionUpdated(ConnectionUpdated connectionUpdated) {}
}
