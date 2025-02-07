package com.notenoughmail.tfcbiomeextender.simulacrum.region;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;

public record IslandInfo(int maxNumberOfChains, int maxIslandsInChain, int chainAttempts) {

    public static final Codec<IslandInfo> CODEC = RecordCodecBuilder.create(i -> i.group(
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("max_island_chain_count", 15).forGetter(IslandInfo::maxNumberOfChains),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("max_islands_in_chain", 12).forGetter(IslandInfo::maxIslandsInChain),
            ExtraCodecs.intRange(1, 500).optionalFieldOf("chain_attempts", 130).forGetter(IslandInfo::chainAttempts)
    ).apply(i, IslandInfo::new));
}
