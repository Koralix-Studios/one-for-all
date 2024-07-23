package com.koralix.oneforall.input;

import com.koralix.oneforall.automaton.Automaton;
import com.koralix.oneforall.input.event.InputEvent;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;

public class HotKey {
    private List<InputEvent> inputEvents;
    private ActivateOn activateOn;
    private Context context;
    private boolean allowEmpty;
    private boolean allowExtra;
    private boolean ordered;
    private boolean exclusive;
    private boolean cancelFurtherProcessing;
    private Runnable action;

    public HotKey(
            List<InputEvent> inputEvents,
            ActivateOn activateOn,
            Context context,
            boolean allowEmpty,
            boolean allowExtra,
            boolean ordered,
            boolean exclusive,
            boolean cancelFurtherProcessing,
            Runnable action
    ) {
        this.inputEvents = new ArrayList<>(inputEvents);
        this.activateOn = activateOn;
        this.context = context;
        this.allowEmpty = allowEmpty;
        this.allowExtra = allowExtra;
        this.ordered = ordered;
        this.exclusive = exclusive;
        this.cancelFurtherProcessing = cancelFurtherProcessing;
        this.action = action;
    }

    public void register(Automaton<InputEvent, HotKey> automaton) {
        int q = automaton.state();
        automaton.start(q);
        for (InputEvent inputEvent : inputEvents) {
            int qn = automaton.state();
            automaton.transition(inputEvent, q, qn);
            q = qn;
        }
        automaton.accept(q);
        automaton.subscribe(q, this::execute);
        automaton.attach(q, this);
    }

    public boolean execute(HotKey hotKey) {
        if (!context.isValid()) {
            return false;
        }
        if (action != null) {
            action.run();
            return cancelFurtherProcessing;
        }
        return false;
    }

    public enum ActivateOn {
        PRESS,
        RELEASE,
        BOTH;

        public boolean isPress() {
            return this == PRESS || this == BOTH;
        }

        public boolean isRelease() {
            return this == RELEASE || this == BOTH;
        }
    }

    public enum Context {
        GAME,
        GUI,
        BOTH;

        public boolean isGame() {
            return this == GAME || this == BOTH;
        }

        public boolean isGui() {
            return this == GUI || this == BOTH;
        }

        public boolean isValid() {
            MinecraftClient client = MinecraftClient.getInstance();
            return switch (this) {
                case GAME -> client.currentScreen == null;
                case GUI -> client.currentScreen != null;
                case BOTH -> true;
            };
        }
    }
}
