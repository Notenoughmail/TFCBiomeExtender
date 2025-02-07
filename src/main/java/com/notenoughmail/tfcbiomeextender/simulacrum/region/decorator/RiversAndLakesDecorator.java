package com.notenoughmail.tfcbiomeextender.simulacrum.region.decorator;

import com.notenoughmail.tfcbiomeextender.simulacrum.region.DatapackableRegionGenerator;
import com.notenoughmail.tfcbiomeextender.simulacrum.region.RiverInfo;
import net.dries007.tfc.world.region.AddRiversAndLakes;
import net.dries007.tfc.world.region.Region;
import net.dries007.tfc.world.region.RiverEdge;
import net.dries007.tfc.world.river.River;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Customization: use lake getters, river disabling
public enum RiversAndLakesDecorator implements RegionDecorator {
    INSTANCE;

    @Override
    public void apply(DatapackableRegionGenerator.Ctx ctx) {
        final Region region = ctx.region;
        if (ctx.generator().riverInfo.isPresent()) {
            final RandomSource random = ctx.random;

            final AddRiversAndLakes.RegionRiverGenerator riverGen = new AddRiversAndLakes.RegionRiverGenerator(region);

            createInitialDrains(ctx, region, riverGen);

            final List<RiverEdge> rivers = riverGen.build(e -> new RiverEdge(e, random));

            region.setRivers(rivers);
            if (!rivers.isEmpty()) {
                annotateRiver(ctx, region, random, rivers);
            }
        } else {
            // This is handled by #createInitialDrains when generating rivers
            for (int dx = 0 ; dx < region.sizeX() ; dx++) {
                for (int dz = 0 ; dz < region.sizeZ() ; dz++) {
                    final int index = dx + region.sizeX() * dz;
                    final Region.Point point = region.data()[index];
                    if (point != null) {
                        point.rainfall = Mth.clamp(point.rainfall, 0F, ctx.generator().oceanClimateBias.maxRainfall());
                    }
                }
            }
        }
    }

    private void createInitialDrains(DatapackableRegionGenerator.Ctx ctx, Region region, AddRiversAndLakes.RegionRiverGenerator riverGen) {
        final RiverInfo riverInfo = ctx.generator().riverInfo.get();
        for (int dx = 0 ; dx < region.sizeX() ; dx++) {
            for (int dz = 0 ; dz < region.sizeZ() ; dz++) {
                final int index = region.sizeX() * dz;
                final Region.Point point = region.data()[index];
                if (point != null) {
                    point.rainfall = Mth.clamp(point.rainfall, 0F, ctx.generator().oceanClimateBias.maxRainfall());
                    if (point.shore()) {
                        float bestAngle = findBestStartingAngle(region, ctx.random, index);
                        if (!Float.isNaN(bestAngle)) {
                            final XoroshiroRandomSource rng = new XoroshiroRandomSource(ctx.random.nextLong());
                            riverGen.add(new River.Builder(rng, region.minX() + dx + 0.5F, region.minZ() + dz + 0.5F, bestAngle, riverInfo.length(), riverInfo.depth(), riverInfo.feather()));
                            point.setRiver();
                        }
                    }
                }
            }
        }
    }

    private float findBestStartingAngle(Region region, RandomSource random, int index) {
        float bestDistanceMetric = Float.MIN_VALUE;
        int bestDistanceCount = 0;
        float bestAngle = Float.NaN;

        for (int dirX = -1 ; dirX <= 1 ; dirX++) {
            for (int dirZ = -1 ; dirZ <= 1 ; dirZ++) {
                if (dirX == 0 && dirZ == 0) continue;

                final int dirIndex = region.offset(index, 4 * dirX, 4 * dirZ);
                if (dirIndex != -1) {
                    final Region.Point dirPoint = region.data()[dirIndex];
                    if (dirPoint != null && dirPoint.land()) {
                        final float dirDistanceMetric = dirPoint.distanceToOcean - Math.abs(dirX) - Math.abs(dirZ);
                        if (dirDistanceMetric > bestDistanceMetric || (dirDistanceMetric == bestDistanceMetric && random.nextInt(1 + bestDistanceCount) == 0)) {
                            if (dirDistanceMetric > bestDistanceMetric) {
                                bestDistanceMetric = dirDistanceMetric;
                                bestDistanceCount = 0;
                            }
                            bestDistanceCount += 1;
                            bestAngle = (float) Math.atan2(dirZ, dirX);
                        }
                    }
                }
            }
        }

        if (!Float.isNaN(bestAngle)) {
            bestAngle += random.nextFloat() * 0.2F - 0.1F;
        }

        return bestAngle;
    }

    private void annotateRiver(DatapackableRegionGenerator.Ctx ctx, Region region, RandomSource random, List<RiverEdge> rivers) {
        final Map<River.Vertex, RiverEdge> sourceVertexToEdge = new HashMap<>();
        for (RiverEdge edge : rivers) {
            sourceVertexToEdge.put(edge.source(), edge);
        }

        for (RiverEdge edge : rivers) {
            edge.linkToDrain(sourceVertexToEdge.get(edge.drain()));
        }

        for (RiverEdge edge : rivers) {
            if (!edge.sourceEdge()) {
                int width = RiverEdge.MIN_WIDTH;
                while (edge != null) {
                    edge.width = Math.max(edge.width, width);
                    edge = edge.drainEdge();
                    width = Math.min(width + 2, RiverEdge.MAX_WIDTH);
                }
            }
        }

        for (RiverEdge edge : rivers) {
            if (!edge.sourceEdge() && random.nextInt(3) == 0) {
                placeLakeNear(ctx, region, edge, 1, 1);
                placeLakeNear(ctx, region, edge, -1, 1);
                placeLakeNear(ctx, region, edge, 1, -1);
                placeLakeNear(ctx, region, edge, -1, -1);
            }
        }
    }

    private void placeLakeNear(DatapackableRegionGenerator.Ctx ctx, Region region, RiverEdge edge, int offsetX, int offsetZ) {
        final int gridX = (int) (edge.source().x() + 0.3F * offsetX);
        final int gridZ = (int) (edge.source().y() + 0.3F * offsetZ);

        final Region.Point point = region.maybeAt(gridX, gridZ);
        if (point != null && point.land() && point.distanceToOcean >= 2 && point.distanceToEdge >= 2 && ctx.generator().biomes.hasLake(point.biome)) {
            point.biome = ctx.generator().biomes.getLake(point.biome);
            point.rainfall += ctx.generator().riverInfo.get().lakeRainfallImpact() * (ctx.generator().oceanClimateBias.maxRainfall() - point.rainfall);
        }
    }
}
