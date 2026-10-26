package com.marzfisch.treebranch.paint;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/** Stateless layout: repeated repaints never move or enlarge the panel. */
public final class ScriptPaint {
    private static final Font FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
    private static final Color BACKGROUND = new Color(35, 35, 38, 210);
    private static final int X = 8;
    private static final int Y = 30;
    private static final int PADDING = 8;
    private volatile long startNanos = System.nanoTime();

    public void reset() {
        startNanos = System.nanoTime();
    }

    public String getRuntime() {
        long seconds = Math.max(0, (System.nanoTime() - startNanos) / 1_000_000_000L);
        return String.format("%02d:%02d:%02d", seconds / 3600, seconds / 60 % 60, seconds % 60);
    }

    public void paint(Graphics graphics, String[] lines) {
        if (!(graphics instanceof Graphics2D) || lines == null || lines.length == 0) {
            return;
        }
        // DreamBot owns the incoming graphics; only mutate and dispose a copy.
        Graphics2D canvas = (Graphics2D) graphics.create();
        try {
            canvas.setFont(FONT);
            canvas.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            FontMetrics metrics = canvas.getFontMetrics();
            int width = 0;
            for (String line : lines) {
                width = Math.max(width, metrics.stringWidth(line == null ? "" : line));
            }
            int height = lines.length * metrics.getHeight() + 2 * PADDING;
            canvas.setColor(BACKGROUND);
            canvas.fillRoundRect(X, Y, width + 2 * PADDING, height, 8, 8);
            canvas.setColor(Color.WHITE);
            int baseline = Y + PADDING + metrics.getAscent();
            for (String line : lines) {
                canvas.drawString(line == null ? "" : line, X + PADDING, baseline);
                baseline += metrics.getHeight();
            }
        } finally {
            canvas.dispose();
        }
    }
}
