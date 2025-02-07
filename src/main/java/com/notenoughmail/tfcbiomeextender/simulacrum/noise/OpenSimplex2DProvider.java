package com.notenoughmail.tfcbiomeextender.simulacrum.noise;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;

import java.util.List;
import java.util.Optional;

public record OpenSimplex2DProvider(Optional<Long> seedOffset, List<NoiseOperand> operands) {

    public static final Codec<OpenSimplex2DProvider> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.LONG.optionalFieldOf("seed_offset").forGetter(OpenSimplex2DProvider::seedOffset),
            NoiseOperand.CODEC.listOf().fieldOf("operands").forGetter(OpenSimplex2DProvider::operands)
    ).apply(i, OpenSimplex2DProvider::new));

    public OpenSimplex2D create(long seed) {
        return apply(new OpenSimplex2D(seed + seedOffset.orElse(0L)), seed);
    }

    public OpenSimplex2D create(int seed) {
        int seed0 = seed;
        if (seedOffset.isPresent()) seed0 += seedOffset.get();
        return apply(new OpenSimplex2D(seed0), seed);
    }

    private OpenSimplex2D apply(Noise2D noise, long seed) {
        for (NoiseOperand operand : operands) {
            noise = operand.modify(noise, seed);
        }

        final Noise2D finalNoise = noise;
        return new OpenSimplex2D(0) {
            @Override
            public double noise(double x, double z) {
                return finalNoise.noise(x, z);
            }
        };
    }
}
