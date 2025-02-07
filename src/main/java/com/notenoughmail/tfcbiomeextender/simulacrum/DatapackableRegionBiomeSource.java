package com.notenoughmail.tfcbiomeextender.simulacrum;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.world.biome.BiomeExtension;
import net.dries007.tfc.world.biome.BiomeSourceExtension;
import net.dries007.tfc.world.region.RegionPartition;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;

import java.util.stream.Stream;

public class DatapackableRegionBiomeSource extends BiomeSource implements BiomeSourceExtension {

    public static final Codec<DatapackableRegionBiomeSource> CODEC = RecordCodecBuilder.create(i -> i.group(

    ).apply(i, DatapackableRegionBiomeSource::new));



    protected DatapackableRegionBiomeSource() {

    }

    public boolean hasLake(int biome) {

    }

    public int getLake(int biome) {

    }

    @Override
    public BiomeExtension getBiomeExtensionNoRiver(int quartX, int quartZ) {
        return null;
    }

    @Override
    public Holder<Biome> getBiomeFromExtension(BiomeExtension extension) {
        return null;
    }

    @Override
    public RegionPartition.Point getPartition(int blockX, int blockZ) {
        return null;
    }

    @Override
    protected Codec<? extends BiomeSource> codec() {
        return null;
    }

    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        return Stream.empty();
    }

    @Override
    public Holder<Biome> getNoiseBiome(int x, int y, int z, Climate.Sampler sampler) {
        return null;
    }
}
