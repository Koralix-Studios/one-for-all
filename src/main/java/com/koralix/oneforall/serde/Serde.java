package com.koralix.oneforall.serde;

import net.minecraft.network.PacketByteBuf;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class Serde {
    private static final Map<Class<?>, BiConsumer<PacketByteBuf, ?>> SERIALIZERS = new HashMap<>();
    private static final Map<Class<?>, Function<PacketByteBuf, ?>> DESERIALIZERS = new HashMap<>();

    static {
        register(Boolean.class, PacketByteBuf::writeBoolean, PacketByteBuf::readBoolean);
        register(Byte.class, (buf, o) -> buf.writeByte(o), PacketByteBuf::readByte);
        register(Short.class, (buf, o) -> buf.writeShort(o), PacketByteBuf::readShort);
        register(Integer.class, PacketByteBuf::writeInt, PacketByteBuf::readInt);
        register(Long.class, PacketByteBuf::writeLong, PacketByteBuf::readLong);
        register(Float.class, PacketByteBuf::writeFloat, PacketByteBuf::readFloat);
        register(Double.class, PacketByteBuf::writeDouble, PacketByteBuf::readDouble);
        register(String.class, PacketByteBuf::writeString, PacketByteBuf::readString);
    }

    private Serde() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    private static <T> void serializer(Class<T> clazz, BiConsumer<PacketByteBuf, T> serializer) {
        if (SERIALIZERS.put(clazz, serializer) == null) return;
        throw new IllegalArgumentException("Serializer for class " + clazz.getName() + " is already registered");
    }

    private static <T> void deserializer(Class<T> clazz, Function<PacketByteBuf, T> deserializer) {
        if (DESERIALIZERS.put(clazz, deserializer) == null) return;
        throw new IllegalArgumentException("Deserializer for class " + clazz.getName() + " is already registered");
    }

    public static <T> void register(
            Class<T> clazz,
            BiConsumer<PacketByteBuf, T> serializer,
            Function<PacketByteBuf, T> deserializer
    ) {
        serializer(clazz, serializer);
        deserializer(clazz, deserializer);
    }

    public static <T> void serialize(PacketByteBuf buf, T value) {
        @SuppressWarnings("unchecked")
        BiConsumer<PacketByteBuf, T> serializer = (BiConsumer<PacketByteBuf, T>) SERIALIZERS.get(value.getClass());
        if (serializer == null) throw new IllegalArgumentException("No serializer for class " + value.getClass().getName());
        serializer.accept(buf, value);
    }

    public static <T> T deserialize(PacketByteBuf buf, Class<T> clazz) {
        @SuppressWarnings("unchecked")
        Function<PacketByteBuf, T> deserializer = (Function<PacketByteBuf, T>) DESERIALIZERS.get(clazz);
        if (deserializer == null) throw new IllegalArgumentException("No deserializer for class " + clazz.getName());
        return deserializer.apply(buf);
    }
}
