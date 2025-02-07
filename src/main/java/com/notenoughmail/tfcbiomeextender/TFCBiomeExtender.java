package com.notenoughmail.tfcbiomeextender;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mod(TFCBiomeExtender.ID)
public class TFCBiomeExtender {

    public static final String ID = "tfcbiomeextender";

    public static <E extends Enum<E>> Codec<E> enumCodec(E[] values) {
        final Map<String, E> map = Arrays.stream(values).collect(Collectors.toMap(
                e -> e.name().toLowerCase(Locale.ROOT),
                Function.identity()
        ));
        return ExtraCodecs.orCompressed(
                ExtraCodecs.stringResolverCodec(
                        e -> e.name().toLowerCase(Locale.ROOT),
                        map::get
                ),
                ExtraCodecs.idResolverCodec(
                        Enum::ordinal,
                        i -> i >= 0 && i < values.length ? values[i] : null,
                        -1
                )
        );
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(ID, path);
    }

    public TFCBiomeExtender() {
        Registration.init(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
