package com.marzfisch.treebranch.behaviour;

import org.dreambot.api.Client;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.script.frameworks.treebranch.Branch;

/** Children are evaluated in insertion order; the first valid leaf runs. */
public final class GrandExchangeBranch extends Branch {
    public GrandExchangeBranch() {
        addLeaves(new IdleAtGrandExchange(), new WalkToGrandExchange());
    }

    @Override
    public boolean isValid() {
        return Client.isLoggedIn() && Players.getLocal() != null;
    }
}
