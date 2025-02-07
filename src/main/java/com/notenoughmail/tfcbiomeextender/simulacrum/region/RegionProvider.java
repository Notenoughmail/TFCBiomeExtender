package com.notenoughmail.tfcbiomeextender.simulacrum.region;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.notenoughmail.tfcbiomeextender.simulacrum.noise.NoiseThreshold;
import com.notenoughmail.tfcbiomeextender.simulacrum.noise.OpenSimplex2DProvider;
import net.dries007.tfc.world.noise.Cellular2D;
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.region.Units;
import net.minecraft.util.Mth;

import java.util.Optional;

public record RegionProvider(
    Optional<Long> cellNoiseSeedOffset,
    NoiseThreshold continentNoise,
    OpenSimplex2DProvider temperatureNoise,
    OpenSimplex2DProvider rainfallNoise,
    boolean reverseAxes,
    float baseMinTemperature,
    float baseMaxTemperature,
    float baseMinRainfall,
    float baseMaxRainfall,
    Optional<RiverInfo> riverInfo,
    IslandInfo islandInfo,
    OceanClimateBias oceanClimateBias
) {

    public static final Codec<RegionProvider> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.LONG.optionalFieldOf("seed_offset").forGetter(RegionProvider::cellNoiseSeedOffset),
            NoiseThreshold.codec(4.4D).fieldOf("continent_noise").forGetter(RegionProvider::continentNoise),
            OpenSimplex2DProvider.CODEC.fieldOf("temperature_noise").forGetter(RegionProvider::temperatureNoise),
            OpenSimplex2DProvider.CODEC.fieldOf("rainfall_noise").forGetter(RegionProvider::rainfallNoise),
            Codec.BOOL.optionalFieldOf("swap_climate_axes", false).forGetter(RegionProvider::reverseAxes),
            Codec.FLOAT.fieldOf("temperature_scale_minimum_base").forGetter(RegionProvider::baseMinTemperature),
            Codec.FLOAT.fieldOf("temperature_scale_maximum_base").forGetter(RegionProvider::baseMaxRainfall),
            Codec.floatRange(0F, Float.MAX_VALUE).optionalFieldOf("rainfall_scale_minimum_base", 0F).forGetter(RegionProvider::baseMinRainfall),
            Codec.FLOAT.fieldOf("rainfall_scale_maximum_base").forGetter(RegionProvider::baseMaxRainfall),
            RiverInfo.CODEC.optionalFieldOf("rivers").forGetter(RegionProvider::riverInfo),
            IslandInfo.CODEC.fieldOf("islands").forGetter(RegionProvider::islandInfo),
            OceanClimateBias.CODEC.fieldOf("ocean_climate_bias").forGetter(RegionProvider::oceanClimateBias)
    ).apply(i, RegionProvider::new));

    private static double triangle(double freq, double value) {
        return Math.abs(4F * freq * value + 1F - 4F * Mth.floor(freq * value + 0.75)) -1F;
    }

    public Noise2D createNoise(boolean rain, float scale, float constant, int seed) {
        final float frequency = Units.GRID_WIDTH_IN_BLOCK / (2f * scale);
        final boolean xAxis = reverseAxes != rain;

        final Noise2D base = scale == 0F ?
                (x, z) -> constant :
                xAxis ?
                        (x, z) -> triangle(frequency, x) :
                        (x, z) -> triangle(z, frequency);

        return rain ?
                base.scaled(baseMinRainfall, baseMaxRainfall)
                        .lazyProduct(rainfallNoise.create(seed)) :
                base.scaled(baseMinTemperature, baseMaxTemperature)
                        .lazyProduct(temperatureNoise.create(seed));
    }

    // TODO: Figure out a way to insert the continentalness param
    public Noise2D createContinentNoise(Cellular2D cellNoise, long seed) {
        return cellNoise.then(c -> 1 - c.f1() / (0.37F + c.f2())) // Just what are these values?
                .lazyProduct(continentNoise.noise().create(seed));
    }
}
