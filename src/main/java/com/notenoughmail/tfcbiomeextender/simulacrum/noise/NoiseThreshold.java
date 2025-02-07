package com.notenoughmail.tfcbiomeextender.simulacrum.noise;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record NoiseThreshold(OpenSimplex2DProvider noise, double threshold) {

    public static Codec<NoiseThreshold> codec(double defaultThreshold) {
        return RecordCodecBuilder.create(i -> i.group(
                OpenSimplex2DProvider.CODEC.fieldOf("noise").forGetter(NoiseThreshold::noise),
                Codec.DOUBLE.optionalFieldOf("threshold", defaultThreshold).forGetter(NoiseThreshold::threshold)
        ).apply(i, NoiseThreshold::new));
    }
}
