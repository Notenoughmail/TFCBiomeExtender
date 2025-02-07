package com.notenoughmail.tfcbiomeextender.simulacrum.surface;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.notenoughmail.tfcbiomeextender.Registration;
import net.dries007.tfc.world.surface.SurfaceBuilderContext;
import net.dries007.tfc.world.surface.builder.NormalSurfaceBuilder;
import net.dries007.tfc.world.surface.builder.SurfaceBuilderFactory;
import net.minecraft.util.ExtraCodecs;

import java.util.function.Function;

public interface DatapackableSurfaceBuilderFactory extends SurfaceBuilderFactory.Invariant {

    Codec<DatapackableSurfaceBuilderFactory> CODEC = ExtraCodecs.lazyInitializedCodec(() -> Registration.SBF_REGISTRY.get().getCodec())
            .dispatch(DatapackableSurfaceBuilderFactory::codec, Function.identity());

    Codec<? extends DatapackableSurfaceBuilderFactory> codec();

    record Normal(
            boolean rocky,
            DatapackableSurfaceState topState,
            DatapackableSurfaceState midState,
            DatapackableSurfaceState underState,
            DatapackableSurfaceState underWaterState,
            DatapackableSurfaceState thinUnderWaterState
    ) implements DatapackableSurfaceBuilderFactory {

        private static final DatapackableSurfaceState
                DEFAULT_TOP = new DatapackableSurfaceState.Builtin(BuiltinSurfaceStates.GRASS),
                DEFAULT_MID = new DatapackableSurfaceState.Builtin(BuiltinSurfaceStates.DIRT),
                DEFAULT_UNDER = new DatapackableSurfaceState.Builtin(BuiltinSurfaceStates.SANDSTONE_OR_GRAVEL),
                DEFAULT_UNDER_WATER = new DatapackableSurfaceState.Builtin(BuiltinSurfaceStates.SAND_OR_GRAVEL);

        public static final Codec<Normal> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.BOOL.optionalFieldOf("rocky", false).forGetter(Normal::rocky),
                DatapackableSurfaceState.CODEC.optionalFieldOf("top_state", DEFAULT_TOP).forGetter(Normal::topState),
                DatapackableSurfaceState.CODEC.optionalFieldOf("mid_state", DEFAULT_MID).forGetter(Normal::midState),
                DatapackableSurfaceState.CODEC.optionalFieldOf("under_state", DEFAULT_UNDER).forGetter(Normal::underState),
                DatapackableSurfaceState.CODEC.optionalFieldOf("under_water_state", DEFAULT_UNDER_WATER).forGetter(Normal::underWaterState),
                DatapackableSurfaceState.CODEC.optionalFieldOf("thin_under_water_state", DEFAULT_UNDER).forGetter(Normal::thinUnderWaterState)
        ).apply(instance, Normal::new));

        @Override
        public Codec<Normal> codec() {
            return CODEC;
        }

        @Override
        public void buildSurface(SurfaceBuilderContext context, int startY, int endY) {
            (rocky ? NormalSurfaceBuilder.ROCKY : NormalSurfaceBuilder.INSTANCE)
                    .buildSurface(
                            context, startY, endY,
                            topState,
                            midState,
                            underState,
                            underWaterState,
                            thinUnderWaterState
                    );
        }
    }


}
