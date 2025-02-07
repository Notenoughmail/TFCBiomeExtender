package com.notenoughmail.tfcbiomeextender.simulacrum.region.decorator;

import com.notenoughmail.tfcbiomeextender.simulacrum.region.DatapackableRegionGenerator;
import net.dries007.tfc.world.noise.Cellular2D;
import net.dries007.tfc.world.region.Region;
import net.dries007.tfc.world.region.Units;

// Customization: use data-derived continent noise & threshold
public enum ContinentDecorator implements RegionDecorator {
    INSTANCE;

    @Override
    public void apply(DatapackableRegionGenerator.Ctx ctx) {
        for (int dx = -Units.REGION_RADIUS_IN_GRID ; dx <= Units.REGION_RADIUS_IN_GRID ; dx++) {
            for (int dz = -Units.REGION_RADIUS_IN_GRID ; dz < Units.REGION_RADIUS_IN_GRID ; dz++) {
                final int gridX = ctx.region.minX() + Units.REGION_RADIUS_IN_GRID + dx;
                final int gridZ = ctx.region.minZ() + Units.REGION_RADIUS_IN_GRID + dz;
                final Cellular2D.Cell otherCell = ctx.generator().sampleCell(gridX, gridZ);

                if (otherCell.x() == ctx.regionCell.x() && otherCell.y() == ctx.regionCell.y()) {
                    final Region.Point point = ctx.region.atInit(gridX, gridZ);
                    final double continent = ctx.generator().dataContinentNoise.noise(gridX, gridZ);

                    if (continent > ctx.generator().continentThreshold) {
                        point.setLand();
                    }

                    if (gridX < ctx.minX) {
                        ctx.minX = gridX;
                    }
                    if (gridZ < ctx.minZ) {
                        ctx.minZ = gridZ;
                    }
                    if (gridX > ctx.maxX) {
                        ctx.maxX = gridX;
                    }
                    if (gridZ > ctx.maxZ) {
                        ctx.maxZ = gridZ;
                    }
                }
            }
        }
    }
}
