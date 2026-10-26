package com.marzfisch.treebranch.behaviour;

import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.frameworks.treebranch.Leaf;
import org.dreambot.api.wrappers.interactive.Player;

public final class WalkToGrandExchange extends Leaf {
    @Override
    public boolean isValid() {
        Player player = Players.getLocal();
        return player != null && !GrandExchange.contains(player);
    }

    @Override
    public int onLoop() {
        Player player = Players.getLocal();
        if (player == null || GrandExchange.contains(player)) {
            return 600;
        }
        if (Walking.shouldWalk(6)) {
            // A rejected path gets a longer retry delay instead of a tight loop.
            return Walking.walk(BankLocation.GRAND_EXCHANGE.getCenter()) ? 600 : 1200;
        }
        return 600;
    }
}
