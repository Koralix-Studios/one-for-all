package com.koralix.oneforall.mixin.lang;

import com.koralix.oneforall.lang.TranslationUnit;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TextCodecs.class)
public class TextCodecsMixin {
    @ModifyReturnValue(
            method = "createCodec",
            at = @At("RETURN")
    )
    private static @NotNull Codec<Text> wrap(Codec<Text> original) {
        return Codec.of(
                new Encoder<>() {
                    @Override
                    public <T1> DataResult<T1> encode(Text input, DynamicOps<T1> ops, T1 prefix) {
                        return original.encode(TranslationUnit.adapt(input), ops, prefix);
                    }
                },
                original
        );
    }
}
