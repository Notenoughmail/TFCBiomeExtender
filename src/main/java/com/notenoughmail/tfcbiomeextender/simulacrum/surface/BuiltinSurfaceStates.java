package com.notenoughmail.tfcbiomeextender.simulacrum.surface;

import com.mojang.serialization.Codec;
import net.dries007.tfc.world.surface.SurfaceState;
import net.dries007.tfc.world.surface.SurfaceStates;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum BuiltinSurfaceStates implements StringRepresentable {
    ROCK(SurfaceStates.RAW),
    COBBLE(SurfaceStates.COBBLE),
    GRAVEL(SurfaceStates.GRAVEL),
    GRASS(SurfaceStates.GRASS),
    DIRT(SurfaceStates.DIRT),
    MUD(SurfaceStates.MUD),
    SAND_OR_GRAVEL(SurfaceStates.SAND_OR_GRAVEL),
    SANDSTONE_OR_GRAVEL(SurfaceStates.SANDSTONE_OR_GRAVEL),
    RIVER_SAND(SurfaceStates.RIVER_SAND),
    SHORE_SAND(SurfaceStates.SHORE_SAND),
    SHORE_SANDSTONE(SurfaceStates.SHORE_SANDSTONE),
    SHORE_MUD(SurfaceStates.SHORE_MUD),
    RARE_SHORE_SAND(SurfaceStates.RARE_SHORE_SAND),
    RARE_SHORE_SANDSTONE(SurfaceStates.RARE_SHORE_SANDSTONE),
    WATER(SurfaceStates.WATER);

    public static final Codec<BuiltinSurfaceStates> CODEC = StringRepresentable.fromEnum(BuiltinSurfaceStates::values);

    public final SurfaceState surfaceState;
    private final String name;

    BuiltinSurfaceStates(SurfaceState surfaceState) {
        this.surfaceState = surfaceState;
        name = name().toLowerCase(Locale.ROOT);
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
