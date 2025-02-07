package com.notenoughmail.tfcbiomeextender.simulacrum.region;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.world.Codecs;
import net.minecraft.util.ExtraCodecs;

public record OceanClimateBias(float targetTemperature, float rainfallAddition, float maxRainfall, float temperatureBiasStrength, float rainfallBiasStrength) {

    public static final Codec<OceanClimateBias> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.FLOAT.optionalFieldOf("temperature_target", 5F).forGetter(OceanClimateBias::targetTemperature),
            Codec.FLOAT.optionalFieldOf("rainfall_addition", 350F).forGetter(OceanClimateBias::rainfallAddition),
            ExtraCodecs.POSITIVE_FLOAT.fieldOf("maximum_rainfall").forGetter(OceanClimateBias::maxRainfall),
            Codecs.UNIT_FLOAT.optionalFieldOf("temperature_bias_strength", 0.23F).forGetter(OceanClimateBias::temperatureBiasStrength),
            Codecs.UNIT_FLOAT.optionalFieldOf("rainfall_bias_strength", 0.23F).forGetter(OceanClimateBias::rainfallBiasStrength)
    ).apply(i, OceanClimateBias::new));
}
