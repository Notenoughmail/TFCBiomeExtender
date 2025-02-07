package com.notenoughmail.tfcbiomeextender.simulacrum.noise;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.notenoughmail.tfcbiomeextender.Registration;
import net.dries007.tfc.world.noise.Noise2D;
import net.minecraft.util.ExtraCodecs;

import java.util.Optional;
import java.util.function.Function;

public interface NoiseOperand {

    Codec<NoiseOperand> CODEC = ExtraCodecs.lazyInitializedCodec(() -> Registration.NOISE_OPERAND_REGISTRY.get().getCodec())
            .dispatch(NoiseOperand::codec, Function.identity());

    Noise2D modify(Noise2D noise, long seed);

    Codec<? extends NoiseOperand> codec();

    record Octaves(int octaves) implements NoiseOperand {

        public static final Codec<Octaves> CODEC = RecordCodecBuilder.create(i -> i.group(Codec.INT.fieldOf("octaves").forGetter(Octaves::octaves)).apply(i, Octaves::new));

        @Override
        public Noise2D modify(Noise2D noise, long seed) {
            return noise.octaves(octaves);
        }

        @Override
        public Codec<Octaves> codec() {
            return CODEC;
        }
    }

    record Terraced(int levels) implements NoiseOperand {

        public static final Codec<Terraced> CODEC = RecordCodecBuilder.create(i -> i.group(ExtraCodecs.NON_NEGATIVE_INT.fieldOf("levels").forGetter(Terraced::levels)).apply(i, Terraced::new));

        @Override
        public Noise2D modify(Noise2D noise, long seed) {
            return noise.terraces(levels);
        }

        @Override
        public Codec<Terraced> codec() {
            return CODEC;
        }
    }

    record Spread(double scaleFactor) implements NoiseOperand {

        public static final Codec<Spread> CODEC = RecordCodecBuilder.create(i -> i.group(Codec.DOUBLE.fieldOf("scale_factor").forGetter(Spread::scaleFactor)).apply(i, Spread::new));

        @Override
        public Noise2D modify(Noise2D noise, long seed) {
            return noise.spread(scaleFactor);
        }

        @Override
        public Codec<Spread> codec() {
            return CODEC;
        }
    }

    record Scaled(Optional<Double> prevMin, Optional<Double> prevMax, double min, double max) implements NoiseOperand {

        public static final Codec<Scaled> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.DOUBLE.optionalFieldOf("previous_minimum").forGetter(Scaled::prevMin),
                Codec.DOUBLE.optionalFieldOf("previous_maximum").forGetter(Scaled::prevMax),
                Codec.DOUBLE.fieldOf("minimum").forGetter(Scaled::min),
                Codec.DOUBLE.fieldOf("maximum").forGetter(Scaled::max)
        ).apply(i, Scaled::new));

        @Override
        public Noise2D modify(Noise2D noise, long seed) {
            return noise.scaled(prevMin.orElse(-1D), prevMax.orElse(1D), min, max);
        }

        @Override
        public Codec<Scaled> codec() {
            return CODEC;
        }
    }

    record Affine(double scale, double shift) implements NoiseOperand {

        public static final Codec<Affine> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.DOUBLE.fieldOf("scale").forGetter(Affine::scale),
                Codec.DOUBLE.fieldOf("shift").forGetter(Affine::shift)
        ).apply(i, Affine::new));

        @Override
        public Noise2D modify(Noise2D noise, long seed) {
            return noise.affine(scale, shift);
        }

        @Override
        public Codec<Affine> codec() {
            return CODEC;
        }
    }

    record Warped(OpenSimplex2DProvider noise) implements NoiseOperand {

        public static final Codec<Warped> CODEC = RecordCodecBuilder.create(i -> i.group(OpenSimplex2DProvider.CODEC.fieldOf("threshold").forGetter(Warped::noise)).apply(i, Warped::new));

        @Override
        public Noise2D modify(Noise2D noise, long seed) {
            return noise.warped(this.noise.create(seed));
        }

        @Override
        public Codec<Warped> codec() {
            return CODEC;
        }
    }

    record Clamped(double min, double max) implements NoiseOperand {

        public static final Codec<Clamped> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.DOUBLE.fieldOf("minimum").forGetter(Clamped::min),
                Codec.DOUBLE.fieldOf("maximum").forGetter(Clamped::max)
        ).apply(i, Clamped::new));

        @Override
        public Noise2D modify(Noise2D noise, long seed) {
            return noise.clamped(min, max);
        }

        @Override
        public Codec<Clamped> codec() {
            return CODEC;
        }
    }

    record Sum(OpenSimplex2DProvider noise) implements NoiseOperand {

        public static final Codec<Sum> CODEC = RecordCodecBuilder.create(i -> i.group(OpenSimplex2DProvider.CODEC.fieldOf("threshold").forGetter(Sum::noise)).apply(i, Sum::new));

        @Override
        public Noise2D modify(Noise2D noise, long seed) {
            return noise.add(this.noise.create(seed));
        }

        @Override
        public Codec<Sum> codec() {
            return CODEC;
        }
    }

    record Add(double value) implements NoiseOperand {

        public static final Codec<Add> CODEC = RecordCodecBuilder.create(i -> i.group(Codec.DOUBLE.fieldOf("value").forGetter(Add::value)).apply(i, Add::new));

        @Override
        public Noise2D modify(Noise2D noise, long seed) {
            return (x, y) -> noise.noise(x, y) + value;
        }

        @Override
        public Codec<Add> codec() {
            return CODEC;
        }
    }

    record Product(OpenSimplex2DProvider noise) implements  NoiseOperand {

        public static final Codec<Product> CODEC = RecordCodecBuilder.create(i -> i.group(OpenSimplex2DProvider.CODEC.fieldOf("threshold").forGetter(Product::noise)).apply(i, Product::new));

        @Override
        public Noise2D modify(Noise2D noise, long seed) {
            return noise.lazyProduct(this.noise.create(seed));
        }

        @Override
        public Codec<Product> codec() {
            return CODEC;
        }
    }

    record Multiply(double value) implements NoiseOperand {

        public static final Codec<Multiply> CODEC = RecordCodecBuilder.create(i -> i.group(Codec.DOUBLE.fieldOf("value").forGetter(Multiply::value)).apply(i, Multiply::new));

        @Override
        public Noise2D modify(Noise2D noise, long seed) {
            return (x, y) -> noise.noise(x, y) * value;
        }

        @Override
        public Codec<Multiply> codec() {
            return CODEC;
        }
    }

    enum Absolute implements NoiseOperand {
        INSTANCE;

        public static final Codec<Absolute> CODEC = Codec.unit(INSTANCE);

        @Override
        public Noise2D modify(Noise2D noise, long seed) {
            return noise.abs();
        }

        @Override
        public Codec<Absolute> codec() {
            return CODEC;
        }
    }

    enum Ridged implements NoiseOperand {
        INSTANCE;

        public static final Codec<Ridged> CODEC = Codec.unit(INSTANCE);

        @Override
        public Noise2D modify(Noise2D noise, long seed) {
            return noise.ridged();
        }

        @Override
        public Codec<Ridged> codec() {
            return CODEC;
        }
    }

    enum Inverse implements NoiseOperand {
        INSTANCE;

        public static final Codec<Inverse> CODEC = Codec.unit(INSTANCE);

        @Override
        public Noise2D modify(Noise2D noise, long seed) {
            return (x, y) -> -noise.noise(x, y);
        }

        @Override
        public Codec<Inverse> codec() {
            return CODEC;
        }
    }

    enum Swap implements NoiseOperand {
        INSTANCE;

        public static final Codec<Swap> CODEC = Codec.unit(INSTANCE);

        @SuppressWarnings("SuspiciousNameCombination")
        @Override
        public Noise2D modify(Noise2D noise, long seed) {
            return (x, y) -> noise.noise(y, x);
        }

        @Override
        public Codec<Swap> codec() {
            return CODEC;
        }
    }
}
