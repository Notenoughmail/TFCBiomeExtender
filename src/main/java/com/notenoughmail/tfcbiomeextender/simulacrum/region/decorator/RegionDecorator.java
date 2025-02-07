package com.notenoughmail.tfcbiomeextender.simulacrum.region.decorator;

import com.notenoughmail.tfcbiomeextender.simulacrum.region.DatapackableRegionGenerator;

@FunctionalInterface
public interface RegionDecorator {

    void apply(DatapackableRegionGenerator.Ctx ctx);
}
