package com.koralix.oneforall.mixin.accessor;

import net.minecraft.nbt.NbtIo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.io.*;

@Mixin(NbtIo.class)
public interface NbtIOAccessor {
    @Invoker("compress")
    static DataOutputStream compress(OutputStream stream) throws IOException {
        throw new AssertionError();
    }

    @Invoker("decompress")
    static DataInputStream decompress(InputStream stream) throws IOException {
        throw new AssertionError();
    }
}
