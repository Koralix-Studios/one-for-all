package com.koralix.oneforall.mixin.server.protocol;

import com.koralix.oneforall.lang.TranslationUnit;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(PacketByteBuf.class)
public class PacketByteBufMixin {
    @ModifyArg(
            method = "Lnet/minecraft/network/PacketByteBuf;writeText(Lnet/minecraft/text/Text;)Lnet/minecraft/network/PacketByteBuf;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/text/Text$Serializer;toJson(Lnet/minecraft/text/Text;)Ljava/lang/String;"
            ),
            index = 0
    )
    private Text writeTextInjector(Text text) {
        return TranslationUnit.adaptText(text);
    }
}
