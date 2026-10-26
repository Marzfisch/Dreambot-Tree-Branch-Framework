package com.marzfisch.treebranch;

import com.marzfisch.treebranch.behaviour.GrandExchangeBranch;
import com.marzfisch.treebranch.paint.ScriptPaint;
import org.dreambot.api.Client;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.frameworks.treebranch.TreeScript;

import java.awt.Graphics;

@ScriptManifest(category = Category.MISC, author = "Marzfisch",
        name = "TreeBranchExample", description = "Native DreamBot tree-branch example", version = 2.0)
public final class Main extends TreeScript {
    private final ScriptPaint paint = new ScriptPaint();
    private volatile String status = "Starting";

    @Override
    public void onStart() {
        // Both startup entry points may be called; rebuilding must never duplicate branches.
        clear();
        addBranches(new GrandExchangeBranch());
        setCurrentBranchName("");
        setCurrentLeafName("");
        paint.reset();
        status = "Ready";
    }

    @Override
    public void onStart(String... args) {
        onStart();
    }

    @Override
    public int onLoop() {
        // A local player can be absent during login, logout, or loading.
        setCurrentBranchName("");
        setCurrentLeafName("");
        if (!Client.isLoggedIn() || Players.getLocal() == null) {
            status = "Waiting for player";
            return 600;
        }
        status = "Running";
        // Native Branch's idle Gaussian can be negative: never return a stop signal.
        return Math.max(100, super.onLoop());
    }

    @Override
    public void onPaint(Graphics graphics) {
        paint.paint(graphics, new String[] {
                "TreeBranchExample v2.0",
                "Runtime: " + paint.getRuntime(),
                "Status: " + status,
                "Branch: " + getCurrentBranchName(),
                "Leaf: " + getCurrentLeafName()
        });
    }
}
