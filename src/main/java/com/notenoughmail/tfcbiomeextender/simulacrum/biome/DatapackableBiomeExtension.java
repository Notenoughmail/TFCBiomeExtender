package com.notenoughmail.tfcbiomeextender.simulacrum.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.notenoughmail.tfcbiomeextender.TFCBiomeExtender;
import com.notenoughmail.tfcbiomeextender.simulacrum.noise.OpenSimplex2DProvider;
import com.notenoughmail.tfcbiomeextender.simulacrum.surface.DatapackableSurfaceBuilderFactory;
import net.dries007.tfc.world.BiomeNoiseSampler;
import net.dries007.tfc.world.TFCChunkGenerator;
import net.dries007.tfc.world.biome.AquiferLookahead;
import net.dries007.tfc.world.biome.BiomeBlendType;
import net.dries007.tfc.world.biome.BiomeExtension;
import net.dries007.tfc.world.biome.BiomeNoise;
import net.dries007.tfc.world.river.RiverBlendType;
import net.dries007.tfc.world.surface.builder.SurfaceBuilderFactory;
import net.dries007.tfc.world.surface.builder.VolcanoesSurfaceBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.function.LongFunction;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class DatapackableBiomeExtension extends BiomeExtension {

    // TODO: carving, see BiomeBuilder#carving
    public static final Codec<DatapackableBiomeExtension> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceKey.codec(Registries.BIOME).fieldOf("biome").forGetter(c -> c.biomeKey),
            VolcanicInfo.CODEC.optionalFieldOf("volcano_info").forGetter(c -> c.volcano),
            TFCBiomeExtender.enumCodec(BiomeBlendType.values()).optionalFieldOf("biome_blend_type", BiomeBlendType.LAND).forGetter(BiomeExtension::biomeBlendType),
            TFCBiomeExtender.enumCodec(RiverBlendType.ALL).optionalFieldOf("river_blend_type", RiverBlendType.NONE).forGetter(BiomeExtension::riverBlendType),
            DatapackableSurfaceBuilderFactory.CODEC.fieldOf("surface_factory").forGetter(be -> be.surfaceBuilderFactory),
            OpenSimplex2DProvider.CODEC.fieldOf("heightmap").forGetter(be -> be.heightmap),
            AquiferInfo.CODEC.optionalFieldOf("aquifers").forGetter(be -> be.aquiferInfo),
            BiomeInfo.CODEC.fieldOf("additional_info").forGetter(be -> be.info)
    ).apply(instance, DatapackableBiomeExtension::new));

    private final ResourceKey<Biome> biomeKey;
    private final Optional<VolcanicInfo> volcano;
    private final DatapackableSurfaceBuilderFactory surfaceBuilderFactory;
    private final OpenSimplex2DProvider heightmap;
    private final Optional<AquiferInfo> aquiferInfo;
    private final BiomeInfo info;
    private int index;
    private boolean lockedIndex;

    public DatapackableBiomeExtension(
            ResourceKey<Biome> biomeKey,
            Optional<VolcanicInfo> volcano,
            BiomeBlendType biomeBlendType,
            RiverBlendType riverBlendType,
            DatapackableSurfaceBuilderFactory surfaceBuilderFactory,
            OpenSimplex2DProvider heightmap,
            Optional<AquiferInfo> aquiferInfo,
            BiomeInfo info
    ) {
        super(
                biomeKey,
                createBiomeNoiseSampler(
                        heightmap,
                        volcano
                ),
                createSurfaceBuilderFactory(
                        surfaceBuilderFactory,
                        volcano
                ),
                createAquiferLookAhead(
                        aquiferInfo
                ),
                biomeBlendType,
                riverBlendType,
                info.isSalty(),
                volcano.isPresent(),
                0,
                0,
                info.canSpawnIn(),
                info.hasRivers(),
                info.isShore(),
                info.hasSandyRiverShores()
        );
        this.biomeKey = biomeKey;
        this.volcano = volcano;
        this.surfaceBuilderFactory = surfaceBuilderFactory;
        this.heightmap = heightmap;
        this.aquiferInfo = aquiferInfo;
        this.info = info;
        index = -1;
        lockedIndex = false;
    }

    public void setIndex(int index) {
        if (!lockedIndex) {
            this.index = index;
            lockedIndex = true;
        } else {
            throw new IllegalStateException("Index has already been set!");
        }
    }

    public int getIndex() {
        if (!lockedIndex) throw new IllegalStateException("Cannot call #getIndex before the index has been set!");
        return index;
    }

    private static SurfaceBuilderFactory createSurfaceBuilderFactory(DatapackableSurfaceBuilderFactory base, Optional<VolcanicInfo> volcano) {
        return volcano.isPresent() ? VolcanoesSurfaceBuilder.create(base) : base; // TODO: Customizable volcano surface builder, probably as fields in VolcanoInfo
    }

    private static LongFunction<BiomeNoiseSampler> createBiomeNoiseSampler(OpenSimplex2DProvider heightmap, Optional<VolcanicInfo> volcano) {
        return seed ->
                BiomeNoiseSampler.fromHeightNoise(
                        volcano.map(info ->
                                BiomeNoise.addVolcanoes(
                                        seed,
                                        heightmap.create(seed),
                                        info.frequency(),
                                        info.baseHeight(),
                                        info.scaleHeight()
                                )
                        ).orElse(heightmap.create(seed))
                );
    }

    private static AquiferLookahead createAquiferLookAhead(Optional<AquiferInfo> info) {
        return (sampler, x, z) -> {
            sampler.setColumn(x, z);
            final double height = sampler.height();

            if (info.isPresent()) {
                final double val = info.get().value();
                return switch (info.get().mode().orElse(AquiferInfoMode.RELATIVE_TO_SURFACE)) {
                    case ABSOLUTE -> val;
                    case RELATIVE_TO_SURFACE -> height + val;
                    case RELATIVE_TO_SEA_LEVEL -> TFCChunkGenerator.SEA_LEVEL_Y + val;
                };
            }

            return height;
        };
    }

    @Override
    public int getVolcanoRarity() {
        return volcano.map(VolcanicInfo::frequency).orElse(1);
    }

    @Override
    public int getVolcanoBasaltHeight() {
        return volcano.map(VolcanicInfo::basaltHeight).orElse(50) + TFCChunkGenerator.SEA_LEVEL_Y;
    }

    @Override
    public List<HolderSet<PlacedFeature>> getFlattenedFeatures(Biome biome) {
        return super.getFlattenedFeatures(biome);
    }

    @Override
    public Set<PlacedFeature> getFlattenedFeatureSet(Biome biome) {
        return super.getFlattenedFeatureSet(biome);
    }

    public record VolcanicInfo(int frequency, int baseHeight, int scaleHeight, int basaltHeight) { // basaltHeight is height above sea level that volcanic surfaces will be built
        public static final Codec<VolcanicInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ExtraCodecs.POSITIVE_INT.optionalFieldOf("frequency", 1).forGetter(VolcanicInfo::frequency),
                Codec.INT.optionalFieldOf("base_height", 0).forGetter(VolcanicInfo::baseHeight),
                ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("scale_height", 1).forGetter(VolcanicInfo::scaleHeight),
                ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("basalt_height", 50).forGetter(VolcanicInfo::basaltHeight)
        ).apply(instance, VolcanicInfo::new));
    }

    public record AquiferInfo(double value, Optional<AquiferInfoMode> mode) {
        public static final Codec<AquiferInfo> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.DOUBLE.fieldOf("offset").forGetter(AquiferInfo::value),
                AquiferInfoMode.CODEC.optionalFieldOf("mode").forGetter(AquiferInfo::mode)
        ).apply(i, AquiferInfo::new));
    }

    public enum AquiferInfoMode implements StringRepresentable {
        ABSOLUTE,
        RELATIVE_TO_SURFACE,
        RELATIVE_TO_SEA_LEVEL;

        public static final Codec<AquiferInfoMode> CODEC = StringRepresentable.fromEnum(AquiferInfoMode::values);

        private final String name = name().toLowerCase(Locale.ROOT);

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
