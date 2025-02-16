package gurumirum.magialucis.api.luxnet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Map;

public interface LinkCollectionBase {
	@NotNull @UnmodifiableView Map<? extends LuxNode, LinkInfo> links();
}
