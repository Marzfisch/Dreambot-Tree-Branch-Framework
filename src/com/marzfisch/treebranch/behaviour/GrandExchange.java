package com.marzfisch.treebranch.behaviour;

import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.wrappers.interactive.Player;

/** One shared definition keeps the idle and walking conditions complementary. */
final class GrandExchange {
    private static final Area AREA = BankLocation.GRAND_EXCHANGE.getArea(10);

    private GrandExchange() {
    }

    static boolean contains(Player player) {
        return player != null && AREA.contains(player);
    }
}
