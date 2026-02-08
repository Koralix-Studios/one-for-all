package com.koralix.oneforall.session;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public class Session {
    private final ClientConnection connection;
    private final Map<SessionComponentType<?>, SessionComponent<?>> components = new Reference2ObjectOpenHashMap<>();
    private final Map<Class<? extends Packet<?>>, Set<Function<? extends Packet<?>, Boolean>>> handlers = new Reference2ObjectOpenHashMap<>();

    public Session(ClientConnection connection) {
        this.connection = connection;
    }

    public @NotNull ClientConnection connection() {
        return this.connection;
    }

    public boolean has(SessionComponentType<?> type) {
        return this.components.containsKey(type);
    }

    @SuppressWarnings("unchecked")
    public <T extends SessionComponent<T>> @Nullable T get(SessionComponentType<T> type) {
        return (T) this.components.get(type);
    }

    public <T extends SessionComponent<T>> void set(SessionComponentType<T> type, T component) {
        this.components.put(type, component);
    }

    public <T extends SessionComponent<T>> void set(T component) {
        this.set(component.type(), component);
    }

    public <T extends SessionComponent<T>> void remove(SessionComponentType<T> type) {
        this.components.remove(type);
    }

    public <P extends Packet<?>> void on(Class<P> packet, Function<P, Boolean> consumer) {
        this.handlers.computeIfAbsent(packet, k -> new ReferenceOpenHashSet<>()).add(consumer);
    }

    public <P extends Packet<?>> void on(Class<P> packet, Consumer<P> consumer) {
        this.on(packet, p -> {
            consumer.accept(p);
            return false;
        });
    }

    @SuppressWarnings("unchecked")
    public <P extends Packet<?>> boolean handle(@NotNull P packet) {
        Set<Function<? extends Packet<?>, Boolean>> consumers = this.handlers.get(packet.getClass());
        if (consumers == null) return false;
        boolean cancel = false;
        for (Function<? extends Packet<?>, Boolean> consumer : consumers) {
            cancel |= ((Function<P, Boolean>) consumer).apply(packet);
        }
        return cancel;
    }
}
