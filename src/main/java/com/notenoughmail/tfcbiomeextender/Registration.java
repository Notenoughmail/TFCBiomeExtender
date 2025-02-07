package com.notenoughmail.tfcbiomeextender;

import com.mojang.serialization.Codec;
import com.notenoughmail.tfcbiomeextender.simulacrum.noise.NoiseOperand;
import com.notenoughmail.tfcbiomeextender.simulacrum.surface.DatapackableSurfaceBuilderFactory;
import com.notenoughmail.tfcbiomeextender.simulacrum.surface.DatapackableSurfaceState;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Supplier;

public class Registration {

    static void init(IEventBus bus) {
        SBF_REG.register(bus);
        SURFACE_STATE_REG.register(bus);
        NOISE_OPERAND_REG.register(bus);
    }

    public static final ResourceKey<Registry<Codec<? extends DatapackableSurfaceBuilderFactory>>> SBF_KEY = regKey("surface_builder_factory");
    public static final ResourceKey<Registry<Codec<? extends DatapackableSurfaceState>>> SURFACE_STATE_KEY = regKey("surface_state");
    public static final ResourceKey<Registry<Codec<? extends NoiseOperand>>> NOISE_OPERAND_KEY = regKey("noise_operand");

    private static final DeferredRegister<Codec<? extends DatapackableSurfaceBuilderFactory>> SBF_REG = defReg(SBF_KEY);
    private static final DeferredRegister<Codec<? extends DatapackableSurfaceState>> SURFACE_STATE_REG = defReg(SURFACE_STATE_KEY);
    private static final DeferredRegister<Codec<? extends NoiseOperand>> NOISE_OPERAND_REG = defReg(NOISE_OPERAND_KEY);

    public static final Supplier<IForgeRegistry<Codec<? extends DatapackableSurfaceBuilderFactory>>> SBF_REGISTRY = makeReg(SBF_REG);
    public static final Supplier<IForgeRegistry<Codec<? extends DatapackableSurfaceState>>> SURFACE_STATE_REGISTRY = makeReg(SURFACE_STATE_REG);
    public static final Supplier<IForgeRegistry<Codec<? extends NoiseOperand>>> NOISE_OPERAND_REGISTRY = makeReg(NOISE_OPERAND_REG);

    static {
        SBF_REG.register("normal", () -> DatapackableSurfaceBuilderFactory.Normal.CODEC);

        SURFACE_STATE_REG.register("noise_boundary", () -> DatapackableSurfaceState.NoiseBoundary.CODEC);
        SURFACE_STATE_REG.register("builtin", () -> DatapackableSurfaceState.Builtin.CODEC);
        SURFACE_STATE_REG.register("block", () -> DatapackableSurfaceState.SingleBlock.CODEC);

        NOISE_OPERAND_REG.register("octaves", () -> NoiseOperand.Octaves.CODEC);
        NOISE_OPERAND_REG.register("terraced", () -> NoiseOperand.Terraced.CODEC);
        NOISE_OPERAND_REG.register("spread", () -> NoiseOperand.Spread.CODEC);
        NOISE_OPERAND_REG.register("scaled", () -> NoiseOperand.Scaled.CODEC);
        NOISE_OPERAND_REG.register("affine", () -> NoiseOperand.Affine.CODEC);
        NOISE_OPERAND_REG.register("warped", () -> NoiseOperand.Warped.CODEC);
        NOISE_OPERAND_REG.register("clamped", () -> NoiseOperand.Clamped.CODEC);
        NOISE_OPERAND_REG.register("sum", () -> NoiseOperand.Sum.CODEC);
        NOISE_OPERAND_REG.register("add", () -> NoiseOperand.Add.CODEC);
        NOISE_OPERAND_REG.register("product", () -> NoiseOperand.Product.CODEC);
        NOISE_OPERAND_REG.register("multiply", () -> NoiseOperand.Multiply.CODEC);
        NOISE_OPERAND_REG.register("absolute", () -> NoiseOperand.Absolute.CODEC);
        NOISE_OPERAND_REG.register("ridged", () -> NoiseOperand.Ridged.CODEC);
        NOISE_OPERAND_REG.register("inverse", () -> NoiseOperand.Inverse.CODEC);
        NOISE_OPERAND_REG.register("swap", () -> NoiseOperand.Swap.CODEC);
    }

    private static <T> ResourceKey<Registry<T>> regKey(String path) {
        return ResourceKey.createRegistryKey(TFCBiomeExtender.id(path));
    }

    private static <T> DeferredRegister<T> defReg(ResourceKey<Registry<T>> reg) {
        return DeferredRegister.create(reg, TFCBiomeExtender.ID);
    }

    private static <T> Supplier<IForgeRegistry<T>> makeReg(DeferredRegister<T> reg) {
        return reg.makeRegistry(RegistryBuilder::new);
    }
}
