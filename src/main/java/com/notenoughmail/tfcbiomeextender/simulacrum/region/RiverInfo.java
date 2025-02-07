package com.notenoughmail.tfcbiomeextender.simulacrum.region;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.notenoughmail.tfcbiomeextender.simulacrum.biome.DatapackableBiomeExtension;
import net.dries007.tfc.world.Codecs;
import net.minecraft.util.ExtraCodecs;

public record RiverInfo(
        float length,
        int depth,
        float feather,
        float lakeRainfallImpact,
        DatapackableBiomeExtension biome
) {

    public static final Codec<RiverInfo> CODEC = RecordCodecBuilder.create(i -> i.group(
            ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("length", 2.7F).forGetter(RiverInfo::length),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("depth", 17).forGetter(RiverInfo::depth),
            ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("feather", 0.8F).forGetter(RiverInfo::feather),
            Codecs.UNIT_FLOAT.optionalFieldOf("lake_rainfall_impact", 0.09F).forGetter(RiverInfo::lakeRainfallImpact),
            DatapackableBiomeExtension.CODEC.fieldOf("biome_definition").forGetter(RiverInfo::biome)
    ).apply(i, RiverInfo::new));
}
