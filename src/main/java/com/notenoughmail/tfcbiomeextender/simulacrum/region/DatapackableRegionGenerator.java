package com.notenoughmail.tfcbiomeextender.simulacrum.region;

import com.notenoughmail.tfcbiomeextender.simulacrum.DatapackableRegionBiomeSource;
import com.notenoughmail.tfcbiomeextender.simulacrum.region.decorator.*;
import com.notenoughmail.tfcbiomeextender.util.RandomDud;
import net.dries007.tfc.world.layer.TFCLayers;
import net.dries007.tfc.world.noise.Cellular2D;
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.region.*;
import net.dries007.tfc.world.settings.Settings;
import net.minecraft.util.RandomSource;

import java.util.Optional;
import java.util.function.BiConsumer;

public class DatapackableRegionGenerator extends RegionGenerator {

    private static final RandomSource DUD = new RandomDud();

    private final long seed;

    public final Cellular2D dataCellNoise;
    public final Noise2D dataContinentNoise;
    public final Noise2D dataTemperatureNoise;
    public final Noise2D dataRainfallNoise;

    public final double continentThreshold;

    public final IslandInfo islandInfo;
    public final OceanClimateBias oceanClimateBias;
    public final Optional<RiverInfo> riverInfo;
    public final DatapackableRegionBiomeSource biomes;

    public DatapackableRegionGenerator(Settings settings, RandomSource random, RegionProvider provider, DatapackableRegionBiomeSource biomes) {
        super(settings, DUD);

        seed = random.nextLong();

        dataCellNoise = new Cellular2D(random.nextLong() + provider.cellNoiseSeedOffset().orElse(0L)).spread(1F / Units.CELL_WIDTH_IN_GRID);

        dataContinentNoise = provider.createContinentNoise(dataCellNoise, random.nextLong());

        dataTemperatureNoise = provider.createNoise(false, settings.temperatureScale(), settings.temperatureConstant(), random.nextInt());
        dataRainfallNoise = provider.createNoise(true, settings.rainfallScale(), settings.rainfallConstant(), random.nextInt());

        biomeArea.set(TFCLayers.createUniformLayer(random, 2).get());
        rockArea.set(TFCLayers.createUniformLayer(random, 3).get());

        continentThreshold = provider.continentNoise().threshold();
        islandInfo = provider.islandInfo();
        oceanClimateBias = provider.oceanClimateBias();
        riverInfo = provider.riverInfo();

        this.biomes = biomes;
    }

    @Override
    public long seed() {
        return seed;
    }

    @Override
    protected Region createRegion(Cellular2D.Cell regionCell, BiConsumer<RegionGenerator.Task, Region> viewer) {
        return new Ctx(viewer, regionCell, seed).run().region;
    }

    @Override
    public Cellular2D.Cell sampleCell(int gridX, int gridZ) {
        return dataCellNoise.cell(gridX, gridZ);
    }

    public enum Task {
        ADD_CONTINENTS(ContinentDecorator.INSTANCE),
        SHRINK_TO_CELL(ShrinkToCell.INSTANCE),
        ANNOTATE_DISTANCE_TO_CELL_EDGE(AnnotateDistanceToCellEdge.INSTANCE),
        ADD_ISLANDS(IslandDecorator.INSTANCE),
        ANNOTATE_DISTANCE_TO_OCEAN(AnnotateDistanceToOcean.INSTANCE),
        ANNOTATE_BASE_LAND_HEIGHT(AnnotateBaseLandHeight.INSTANCE),
        ADD_MOUNTAINS(AddMountains.INSTANCE),
        ANNOTATE_BIOME_ALTITUDE(AnnotateBiomeAltitude.INSTANCE),
        ANNOTATE_CLIMATE(ClimateDecorator.INSTANCE),
        CHOOSE_BIOMES(BiomeDecorator.INSTANCE),
        CHOOSE_ROCKS(ChooseRocks.INSTANCE),
        ADD_RIVERS_AND_LAKES(RiversAndLakesDecorator.INSTANCE);

        private static final Task[] VALUES = values();

        private final RegionDecorator task;

        Task(RegionDecorator task) {
            this.task = task;
        }

        Task(RegionTask task) {
            this.task = task::apply;
        }
    }

    public class Ctx extends RegionGenerator.Context {

        Ctx(BiConsumer<RegionGenerator.Task, Region> viewer, Cellular2D.Cell regionCell, long seed) {
            super(
                    viewer,
                    regionCell,
                    seed
            );
        }

        public Ctx run() {
            for (Task task : Task.VALUES) {
                task.task.apply(this);
            }
            return this;
        }

        @Override
        public DatapackableRegionGenerator generator() {
            return DatapackableRegionGenerator.this;
        }
    }
}
