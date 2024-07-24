package com.koralix.oneforall.input.hotkey;

import com.koralix.oneforall.input.ButtonEvent;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public abstract class HotKey<T extends Collection<ButtonEvent>> {
    private final Runnable action;
    private byte flags;

    private final T expected;

    private static final byte PRESS                     = 1;
    private static final byte RELEASE                   = 1 << 1;
    private static final byte IN_GAME                   = 1 << 2;
    private static final byte IN_GUI                    = 1 << 3;
    private static final byte ALLOW_EMPTY               = 1 << 4;
    private static final byte ALLOW_EXTRA               = 1 << 5;
    private static final byte EXCLUSIVE                 = 1 << 6;
    private static final byte CANCEL_FURTHER_PROCESSING = (byte) (1 << 7);

    protected HotKey(Runnable action, byte flags, T expected) {
        this.action = action;
        this.flags = flags;
        this.expected = expected;
    }

    @Contract("!null -> new")
    public static @NotNull UnorderedHotKey unordered(Runnable action) {
        return new UnorderedHotKey(action, (byte) (PRESS | IN_GAME));
    }

    @Contract("!null -> new")
    public static @NotNull OrderedHotKey ordered(Runnable action) {
        return new OrderedHotKey(action, (byte) (PRESS | IN_GAME));
    }

    public final boolean on(ButtonEvent event, List<ButtonEvent> actual, boolean[] cancelPtr) {
        if (!test(cancelPtr, actual)) return false;
        action.run();
        cancelPtr[0] = true;
        return cancelFurtherProcessing();
    }

    private boolean test(boolean[] cancelPtr, List<ButtonEvent> actual) {
        return !(exclusive() && cancelPtr[0]) && testKeys(expected, actual) && isValid();
    }

    protected abstract boolean testKeys(T expected, List<ButtonEvent> actual);

    public final HotKey<T> add(ButtonEvent event) {
        expected.add(event);
        return this;
    }

    public final void reset() {
        expected.clear();
    }

    private boolean isValid() {
        boolean inGame = inGame() && MinecraftClient.getInstance().player == null;
        boolean inGui = inGui() && MinecraftClient.getInstance().currentScreen == null;
        return !(inGame || inGui);
    }

    public final boolean press() {
        return (flags & PRESS) != 0;
    }

    public final HotKey<T> press(boolean press) {
        if (press) {
            flags |= PRESS;
        } else {
            flags &= ~PRESS;
        }
        return this;
    }

    public final boolean release() {
        return (flags & RELEASE) != 0;
    }

    public final HotKey<T> release(boolean release) {
        if (release) {
            flags |= RELEASE;
        } else {
            flags &= ~RELEASE;
        }
        return this;
    }

    public final boolean inGame() {
        return (flags & IN_GAME) != 0;
    }

    public final HotKey<T> inGame(boolean inGame) {
        if (inGame) {
            flags |= IN_GAME;
        } else {
            flags &= ~IN_GAME;
        }
        return this;
    }

    public final boolean inGui() {
        return (flags & IN_GUI) != 0;
    }

    public final HotKey<T> inGui(boolean inGui) {
        if (inGui) {
            flags |= IN_GUI;
        } else {
            flags &= ~IN_GUI;
        }
        return this;
    }

    public final boolean allowEmpty() {
        return (flags & ALLOW_EMPTY) != 0;
    }

    public final HotKey<T> allowEmpty(boolean allowEmpty) {
        if (allowEmpty) {
            flags |= ALLOW_EMPTY;
        } else {
            flags &= ~ALLOW_EMPTY;
        }
        return this;
    }

    public final boolean allowExtra() {
        return (flags & ALLOW_EXTRA) != 0;
    }

    public final HotKey<T> allowExtra(boolean allowExtra) {
        if (allowExtra) {
            flags |= ALLOW_EXTRA;
        } else {
            flags &= ~ALLOW_EXTRA;
        }
        return this;
    }

    public final boolean exclusive() {
        return (flags & EXCLUSIVE) != 0;
    }

    public final HotKey<T> exclusive(boolean exclusive) {
        if (exclusive) {
            flags |= EXCLUSIVE;
        } else {
            flags &= ~EXCLUSIVE;
        }
        return this;
    }

    public final boolean cancelFurtherProcessing() {
        return (flags & CANCEL_FURTHER_PROCESSING) != 0;
    }

    public final HotKey<T> cancelFurtherProcessing(boolean cancelFurtherProcessing) {
        if (cancelFurtherProcessing) {
            flags |= CANCEL_FURTHER_PROCESSING;
        } else {
            flags &= ~CANCEL_FURTHER_PROCESSING;
        }
        return this;
    }
}
