package com.notenoughmail.tfcbiomeextender.simulacrum;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.world.ChunkGeneratorExtension;
import net.dries007.tfc.world.TFCChunkGenerator;
import net.dries007.tfc.world.biome.BiomeSourceExtension;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;
import net.dries007.tfc.world.settings.Settings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.UnaryOperator;

public class DatapackableChunkGeneratorExtension extends ChunkGenerator implements ChunkGeneratorExtension {

    public static final Codec<DatapackableChunkGeneratorExtension> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            DatapackableRegionBiomeSource.CODEC.fieldOf("biomes").forGetter(c -> c.biomes),
            NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter(c -> c.noiseSettings),
            Settings.CODEC.fieldOf("tfc_settings").forGetter(c -> c.tfcSettings)
    ).apply(instance, DatapackableChunkGeneratorExtension::new));

    private static DataResult<BiomeSourceExtension> guardBiomeSource(BiomeSource bs) {
        return bs instanceof BiomeSourceExtension d ? DataResult.success(d) : DataResult.error(() -> "Must be a " + BiomeSourceExtension.class.getSimpleName());
    }

    private final DatapackableRegionBiomeSource biomes;
    private final Holder<NoiseGeneratorSettings> noiseSettings;
    private Settings tfcSettings;

    private final NoiseBasedChunkGenerator mojangGen;

    public DatapackableChunkGeneratorExtension(DatapackableRegionBiomeSource biomes, Holder<NoiseGeneratorSettings> noiseSettings, Settings settings) {
        super(biomes.self());
        this.biomes = biomes;
        this.noiseSettings = noiseSettings;
        tfcSettings = settings;

        mojangGen = new NoiseBasedChunkGenerator(biomes.self(), noiseSettings);
    }

    @Override
    public Settings settings() {
        return tfcSettings;
    }

    @Override
    public void applySettings(UnaryOperator<Settings> settings) {
        tfcSettings = settings.apply(tfcSettings);
    }

    @Override
    public ChunkDataProvider chunkDataProvider() {
        return null;
    }

    @Override
    public Aquifer getOrCreateAquifer(ChunkAccess chunk) {
        return null;
    }

    @Override
    public void initRandomState(ChunkMap chunkMap, ServerLevel level) {

    }

    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public void applyCarvers(WorldGenRegion level, long seed, RandomState random, BiomeManager biomeManager, StructureManager structureManager, ChunkAccess chunk, GenerationStep.Carving step) {

    }

    @Override
    public void buildSurface(WorldGenRegion level, StructureManager structureManager, RandomState random, ChunkAccess chunk) {

    }

    @Override
    public void spawnOriginalMobs(WorldGenRegion level) {

    }

    @Override
    public int getGenDepth() {
        return noiseSettings.value().noiseSettings().height();
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor executor, Blender blender, RandomState random, StructureManager structureManager, ChunkAccess chunk) {
        return null;
    }

    @Override
    public int getSeaLevel() {
        return TFCChunkGenerator.SEA_LEVEL_Y;
    }

    @Override
    public int getMinY() {
        return noiseSettings.value().noiseSettings().minY();
    }

    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types type, LevelHeightAccessor level, RandomState random) {

    }

    @Override
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor height, RandomState random) {
    }

    @Override
    public void addDebugScreenInfo(List<String> info, RandomState random, BlockPos pos) {

    }

    private DatapackableChunkGeneratorExtension copy() {
        return new DatapackableChunkGeneratorExtension(biomes, noiseSettings, tfcSettings);
    }
}
