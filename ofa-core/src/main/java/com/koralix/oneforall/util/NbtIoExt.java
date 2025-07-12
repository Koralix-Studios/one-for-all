package com.koralix.oneforall.util;

import com.koralix.oneforall.mixin.accessor.NbtIOAccessor;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.util.FixedBufferInputStream;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public final class NbtIoExt {
    private NbtIoExt() {
        // Utility class, no instantiation
    }

    public static DataOutputStream compress(OutputStream output) throws IOException {
        return NbtIOAccessor.compress(output);
    }

    public static void writeCompressed(NbtElement nbt, OutputStream output) throws IOException {
        try (DataOutputStream dataOutput = compress(output)) {
            NbtIo.write(nbt, dataOutput);
        }
    }

    public static void writeCompressed(NbtElement nbt, Path path) throws IOException {
        try (OutputStream output = Files.newOutputStream(path)) {
            try (OutputStream buffered = new BufferedOutputStream(output)) {
                writeCompressed(nbt, buffered);
            }
        }
    }

    public static DataInputStream decompress(InputStream input) throws IOException {
        return NbtIOAccessor.decompress(input);
    }

    public static NbtElement readCompressed(InputStream input, NbtSizeTracker tracker) throws IOException {
        try (DataInputStream dataInput = decompress(input)) {
            return NbtIo.readElement(dataInput, tracker);
        }
    }

    public static NbtElement readCompressed(Path path, NbtSizeTracker tracker) throws IOException {
        try (InputStream input = Files.newInputStream(path)) {
            try (InputStream buffered = new FixedBufferInputStream(input)) {
                return readCompressed(buffered, tracker);
            }
        }
    }
}
