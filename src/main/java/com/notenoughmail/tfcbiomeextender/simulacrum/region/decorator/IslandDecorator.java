package com.notenoughmail.tfcbiomeextender.simulacrum.region.decorator;

import com.notenoughmail.tfcbiomeextender.simulacrum.region.DatapackableRegionGenerator;
import com.notenoughmail.tfcbiomeextender.simulacrum.region.IslandInfo;
import net.dries007.tfc.world.region.Region;
import net.minecraft.util.RandomSource;

// Customization: attempts, chain length
public enum IslandDecorator implements RegionDecorator {
    INSTANCE;

    @Override
    public void apply(DatapackableRegionGenerator.Ctx ctx) {
        final Region region = ctx.region;
        final RandomSource random = ctx.random;
        final IslandInfo islandInfo = ctx.generator().islandInfo;

        for (int attempt = 0, placed = 0 ; attempt < islandInfo.chainAttempts() && placed < islandInfo.maxNumberOfChains() ; attempt++) {
            int x = region.minX() + random.nextInt(region.sizeX());
            int z = region.minZ() + random.nextInt(region.sizeZ());

            Region.Point point = region.maybeAt(x, z);
            if (point != null && !point.land() && !point.shore() && point.distanceToEdge > 2) {
                for (int island = 0 ; island < islandInfo.maxIslandsInChain() ; island++) {
                    point.setLand();
                    point.setIsland();

                    x += random.nextInt(4) - random.nextInt(4);
                    z += random.nextInt(4) - random.nextInt(4);

                    point = region.maybeAt(x, z);
                    if (point == null || (point.land() && !point.island()) || point.distanceToEdge <= 2) {
                        break;
                    }
                }
                placed++;
            }
        }
    }
}
