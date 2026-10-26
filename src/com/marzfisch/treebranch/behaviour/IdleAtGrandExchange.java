package com.marzfisch.treebranch.behaviour;

import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.script.frameworks.treebranch.Leaf;

/** Replace this idle action with the work your script should perform on arrival. */
public final class IdleAtGrandExchange extends Leaf {
    @Override
    public boolean isValid() {
        return GrandExchange.contains(Players.getLocal());
    }

    @Override
    public int onLoop() {
        // No per-loop logging: the paint already shows which leaf is active.
        return 600;
    }
}
