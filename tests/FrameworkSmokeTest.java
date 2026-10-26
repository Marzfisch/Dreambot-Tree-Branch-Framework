import com.marzfisch.treebranch.Main;
import com.marzfisch.treebranch.paint.ScriptPaint;
import org.dreambot.api.script.frameworks.treebranch.Branch;
import org.dreambot.api.script.frameworks.treebranch.Leaf;
import org.dreambot.api.script.frameworks.treebranch.TreeScript;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

/** Offline regression checks; no game client or account is started. */
public final class FrameworkSmokeTest {
    private static void check(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }
    public static void main(String[] args) {
        Main script = new Main();
        script.onStart();
        check(script.getRoot().getChildren().size() == 1, "one branch after startup");
        script.onStart("quickstart");
        check(script.getRoot().getChildren().size() == 1, "no duplicate branch on QuickStart");
        Branch example = (Branch) script.getRoot().getChildren().get(0);
        check(example.getChildren().size() == 2, "two example leaves");
        check(example.getChildren().get(0).getTree() == script, "native parent wiring");
        TreeScript tree = new TreeScript() { };
        Branch branch = new Branch() { public boolean isValid() { return true; } };
        int[] calls = {0, 0};
        branch.addLeaves(new Leaf() {
            public boolean isValid() { return true; }
            public int onLoop() { calls[0]++; return 600; }
        }, new Leaf() {
            public boolean isValid() { return true; }
            public int onLoop() { calls[1]++; return 1200; }
        });
        tree.addBranches(branch);
        check(tree.onLoop() == 600 && calls[0] == 1 && calls[1] == 0, "first valid leaf wins");
        ScriptPaint paint = new ScriptPaint();
        BufferedImage image = new BufferedImage(600, 240, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.MAGENTA);
        Font font = g.getFont();
        String[] lines = {"Example", null, "Runtime: 00:00:00"};
        paint.paint(g, lines);
        check(g.getColor().equals(Color.MAGENTA) && g.getFont().equals(font), "caller graphics preserved");
        int[] first = image.getRGB(0, 0, 600, 240, null, 0, 600);
        g.setComposite(AlphaComposite.Clear); g.fillRect(0, 0, 600, 240);
        g.setComposite(AlphaComposite.SrcOver);
        paint.paint(g, lines);
        check(Arrays.equals(first, image.getRGB(0, 0, 600, 240, null, 0, 600)), "stable repeated paint");
        paint.paint(g, null); paint.paint(g, new String[0]); paint.paint(null, lines);
        paint.reset(); check(paint.getRuntime().equals("00:00:00"), "runtime reset");
        g.dispose();
        System.out.println("PASS: startup, QuickStart, native tree ownership and priority, paint stability, graphics isolation, runtime reset");
    }
}
