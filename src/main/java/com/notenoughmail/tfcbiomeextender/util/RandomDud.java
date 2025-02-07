package com.notenoughmail.tfcbiomeextender.util;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;

public class RandomDud implements RandomSource {
    @Override
    public RandomSource fork() {
        return this;
    }

    @Override
    public PositionalRandomFactory forkPositional() {
        throw new AssertionError("#forgkPositional should not be called on a RandomDud");
    }

    @Override
    public void setSeed(long seed) {
    }

    @Override
    public int nextInt() {
        return 0;
    }

    @Override
    public int nextInt(int bound) {
        return 0;
    }

    @Override
    public long nextLong() {
        return 0;
    }

    @Override
    public boolean nextBoolean() {
        return false;
    }

    @Override
    public float nextFloat() {
        return 0;
    }

    @Override
    public double nextDouble() {
        return 0;
    }

    @Override
    public double nextGaussian() {
        return 0;
    }
}
