package gurumirum.magialucis.contents.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gurumirum.magialucis.capability.LuxStat;

public record GemStat(
		double minLuxThreshold,
		double rMaxTransfer,
		double gMaxTransfer,
		double bMaxTransfer
) implements LuxStat {
	public static final Codec<GemStat> CODEC = RecordCodecBuilder.create(b -> b.group(
			Codec.doubleRange(0, Double.POSITIVE_INFINITY).fieldOf("minLuxThreshold").forGetter(GemStat::minLuxThreshold),
			Codec.doubleRange(0, Double.POSITIVE_INFINITY).fieldOf("rMaxTransfer").forGetter(GemStat::rMaxTransfer),
			Codec.doubleRange(0, Double.POSITIVE_INFINITY).fieldOf("gMaxTransfer").forGetter(GemStat::gMaxTransfer),
			Codec.doubleRange(0, Double.POSITIVE_INFINITY).fieldOf("bMaxTransfer").forGetter(GemStat::bMaxTransfer)
	).apply(b, GemStat::new));

	public GemStat(LuxStat copyFrom) {
		this(copyFrom.minLuxThreshold(), copyFrom.rMaxTransfer(), copyFrom.gMaxTransfer(), copyFrom.bMaxTransfer());
	}
}
