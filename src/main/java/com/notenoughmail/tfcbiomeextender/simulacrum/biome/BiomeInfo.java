package com.notenoughmail.tfcbiomeextender.simulacrum.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import java.util.Optional;

public record BiomeInfo(
        Optional<ResourceKey<Biome>> shore,
        Optional<ResourceKey<Biome>> lake,
        boolean isOcean,
        boolean isMountains,
        boolean isLow,
        boolean hasShore,
        boolean hasLake,
        boolean hasRivers,
        boolean isShore,
        boolean isSalty,
        boolean isSpawnable,
        boolean hasSandyRiverShores

) {

    public static final Codec<BiomeInfo> CODEC = RecordCodecBuilder.create(i -> i.group(
            ResourceKey.codec(Registries.BIOME).optionalFieldOf("shore_biome").forGetter(BiomeInfo::shore),
            ResourceKey.codec(Registries.BIOME).optionalFieldOf("lake_biome").forGetter(BiomeInfo::lake),
            Codec.BOOL.optionalFieldOf("is_ocean", false).forGetter(BiomeInfo::isOcean),
            Codec.BOOL.optionalFieldOf("is_mountain", false).forGetter(BiomeInfo::isMountains),
            Codec.BOOL.optionalFieldOf("is_low", false).forGetter(BiomeInfo::isLow),
            Codec.BOOL.optionalFieldOf("has_shore", true).forGetter(BiomeInfo::hasShore),
            Codec.BOOL.optionalFieldOf("has_lake", true).forGetter(BiomeInfo::hasLake),
            Codec.BOOL.optionalFieldOf("has_rivers", true).forGetter(BiomeInfo::hasRivers),
            Codec.BOOL.optionalFieldOf("is_shore", false).forGetter(BiomeInfo::isShore),
            Codec.BOOL.optionalFieldOf("is_salty", false).forGetter(BiomeInfo::isSalty), // If salt water should be used over regular water in the WATER builtin surface state
            Codec.BOOL.optionalFieldOf("is_spawnable", true).forGetter(BiomeInfo::isSpawnable),
            Codec.BOOL.optionalFieldOf("has_sandy_river_shores", true).forGetter(BiomeInfo::hasSandyRiverShores) // If the river surface builder should use sandy shores, sorta
    ).apply(i, BiomeInfo::new));

    public boolean canSpawnIn() {
        return !isOcean && isSpawnable;
    }
}
