package com.notenoughmail.tfcbiomeextender.simulacrum.surface;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.notenoughmail.tfcbiomeextender.Registration;
import com.notenoughmail.tfcbiomeextender.simulacrum.noise.NoiseThreshold;
import net.dries007.tfc.world.surface.SoilSurfaceState;
import net.dries007.tfc.world.surface.SurfaceBuilderContext;
import net.dries007.tfc.world.surface.SurfaceState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;
import java.util.function.Function;

public interface DatapackableSurfaceState extends SurfaceState {

    Codec<DatapackableSurfaceState> CODEC = ExtraCodecs.lazyInitializedCodec(() -> Registration.SURFACE_STATE_REGISTRY.get().getCodec())
            .dispatch(DatapackableSurfaceState::codec, Function.identity());

    Codec<? extends DatapackableSurfaceState> codec();

    record NoiseBoundary(DatapackableSurfaceState from, DatapackableSurfaceState to, Optional<NoiseThreshold> threshold) implements DatapackableSurfaceState {

        public static final Codec<NoiseBoundary> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                DatapackableSurfaceState.CODEC.fieldOf("from").forGetter(NoiseBoundary::from),
                DatapackableSurfaceState.CODEC.fieldOf("to").forGetter(NoiseBoundary::to),
                NoiseThreshold.codec(0D).optionalFieldOf("threshold").forGetter(NoiseBoundary::threshold)
        ).apply(instance, NoiseBoundary::new));

        @Override
        public Codec<NoiseBoundary> codec() {
            return CODEC;
        }

        @Override
        public BlockState getState(SurfaceBuilderContext context) {
            final BlockPos pos = context.pos();
            if (threshold.isPresent()) {
                final double noise = threshold.get().noise().create(context.getSeed()).noise(pos.getX(), pos.getZ());
                return (noise > threshold.get().threshold() ? from : to).getState(context);
            }

            final double noise = SoilSurfaceState.PATCH_NOISE.noise(pos.getX(), pos.getZ());
            return (noise > 0 ? from : to).getState(context);
        }
    }

    record Builtin(BuiltinSurfaceStates bss) implements DatapackableSurfaceState {

        public static final Codec<Builtin> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BuiltinSurfaceStates.CODEC.fieldOf("name").forGetter(Builtin::bss)
        ).apply(instance, Builtin::new));

        @Override
        public Codec<? extends DatapackableSurfaceState> codec() {
            return CODEC;
        }

        @Override
        public BlockState getState(SurfaceBuilderContext context) {
            return bss.surfaceState.getState(context);
        }
    }

    record SingleBlock(Block block) implements DatapackableSurfaceState {

        public static final Codec<SingleBlock> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").forGetter(SingleBlock::block)
        ).apply(instance, SingleBlock::new));

        @Override
        public Codec<? extends DatapackableSurfaceState> codec() {
            return CODEC;
        }

        @Override
        public BlockState getState(SurfaceBuilderContext context) {
            return block.defaultBlockState();
        }
    }


}
