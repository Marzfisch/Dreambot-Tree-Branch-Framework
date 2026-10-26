# DreamBot Tree Branch Framework

A small Java 11 example using DreamBot's native `TreeScript`, `Branch`, and `Leaf` API.

The example walks to the Grand Exchange, then idles inside a radius-10 area.
It does not trade or perform other activities. Replace the idle leaf with your own behavior.

## Build on Windows

1. Install a JDK (11 or newer). Set `JAVA_HOME` to its installation folder, or put its `bin` folder on PATH.
2. Launch DreamBot once so `DreamBot/BotData/repository2/dreambot-client.jar` and its dependencies exist.
3. Run from PowerShell:

```powershell
.\build.ps1
# Optional custom installation paths:
.\build.ps1 -DreamBotHome 'D:\DreamBot' -JdkHome 'C:\Java\jdk-11'
```

The build uses your installed DreamBot libraries, targets Java 11, and produces
`build/TreeBranchExample.jar`. It never bundles DreamBot or changes your installed scripts.
Copy the JAR to your DreamBot `Scripts` directory, refresh local scripts, and select
**TreeBranchExample**.

For IntelliJ, mark `src` as Sources Root, select Java 11 as the language level, and
add DreamBot's `BotData/repository2` directory as a library. Only package your module output.

## Structure and behavior

- `Main` extends native `TreeScript` and owns startup and paint state.
- `GrandExchangeBranch` registers `IdleAtGrandExchange` before `WalkToGrandExchange`.
- The first valid child runs; registration order establishes priority.
- Player availability is checked before tree evaluation and again before walking.
- Loop delays are positive; the outer loop also clamps the native framework's idle delay.
- Startup clears and rebuilds the tree, including when invoked through QuickStart.
- Paint uses an isolated graphics copy and monotonic, per-instance runtime.
- No game interaction occurs from the paint callback.

To add behavior, extend `org.dreambot.api.script.frameworks.treebranch.Leaf`, implement
`isValid()` and `onLoop()`, and register it through a branch's `addLeaves(...)`.
Keep specific conditions before broad fallback conditions. Return a delay in milliseconds.

## Migration from v1

The duplicated `org.dreambot.framework` classes and global `utilities.API` state have
been removed. Import the native classes from
`org.dreambot.api.script.frameworks.treebranch`; use `Main extends TreeScript`,
`addBranches(...)`, and `addLeaves(...)` (previously `addLeafs`).
Native leaves are not generic; `getTree()` provides the owning script.

Script classes now live under `com.marzfisch.treebranch`, keeping them out of
DreamBot's own namespace. The numbered example leaves have descriptive names.
The old configurable Swing paint helpers are replaced by a compact Java2D panel;
custom users should migrate their paint customization into `ScriptPaint`.

## Validation

The API signatures were checked against the locally installed DreamBot client JAR.
The build requires a JDK; a JRE alone cannot compile the example.
Before using a newly built JAR, smoke-test normal launch and QuickStart, login/loading,
walking to the Grand Exchange, arrival/idle behavior, and script restart.

## References

- [DreamBot scripting setup](https://dreambot.org/guides/scripting/creating-your-first-script/)
- [Native tree-branch API](https://dreambot.org/javadocs/org/dreambot/api/script/frameworks/treebranch/package-summary.html)
