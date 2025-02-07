package com.notenoughmail.tfcbiomeextender.simulacrum.region.decorator;

import com.notenoughmail.tfcbiomeextender.simulacrum.region.DatapackableRegionGenerator;
import com.notenoughmail.tfcbiomeextender.simulacrum.region.OceanClimateBias;
import net.dries007.tfc.world.region.Region;
import net.minecraft.util.Mth;

// Customization: use data-derived rain & temp noises; custom bias values
public enum ClimateDecorator implements RegionDecorator {
    INSTANCE;

    @Override
    public void apply(DatapackableRegionGenerator.Ctx ctx) {
        final Region region = ctx.region;
        final OceanClimateBias oceanClimateBias = ctx.generator().oceanClimateBias;

        for (int x = region.minX() ; x <= region.maxX() ; x++) {
            for (int z = region.minZ() ; z <= region.maxZ() ; z++) {
                final Region.Point point = region.maybeAt(x, z);
                if (point != null) {
                    point.temperature = (float) ctx.generator().dataTemperatureNoise.noise(x, z);
                    point.rainfall = (float) ctx.generator().dataRainfallNoise.noise(x, z);

                    // [0, 1], where higher = more inland
                    final float bias;
                    if (point.land()) {
                        assert point.distanceToOcean >= 0;

                        final float potentialBias = Mth.clampedMap(point.distanceToEdge, 2F, 6F, 0F, 1F);
                        final float oceanProximityBias = Mth.clampedMap(point.distanceToOcean, 2F, 6F, 0F, 1F);

                        bias = Math.max(potentialBias, oceanProximityBias);
                    } else {
                        bias = 0;
                    }

                    final float biasTargetTemperature = Mth.lerp(
                            bias,
                            oceanClimateBias.targetTemperature(),
                            point.temperature
                    );
                    final float biasTargetRainfall = Mth.lerp(
                            bias,
                            Math.min(point.rainfall + oceanClimateBias.rainfallAddition(), oceanClimateBias.maxRainfall()),
                            point.rainfall
                    );

                    point.temperature = Mth.lerp(
                            oceanClimateBias.temperatureBiasStrength(),
                            point.temperature,
                            biasTargetTemperature
                    );
                    point.rainfall = Mth.lerp(
                            oceanClimateBias.rainfallBiasStrength(),
                            point.rainfall,
                            biasTargetRainfall
                    );
                }
            }
        }
    }
}
