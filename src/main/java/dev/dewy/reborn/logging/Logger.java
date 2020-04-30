package dev.dewy.reborn.logging;

import org.jline.reader.LineReader;
import org.jline.utils.InfoCmp;

import static dev.dewy.reborn.REborn.reader;

/**
 * A simple logging mechanism
 */
public class Logger implements ILogger {

    private final String name;
    private boolean seeDebug = false; //whether to display debug msgs

    public Logger(String name) {
        this.name = name;
    }

    @Override
    public void viewDebugs(boolean view) {
        seeDebug = view;
    }

    @Override
    public void log(String msg) {
        this.println("[" + name + " / INFO] " + msg);

    }

    @Override
    public void logWarning(String msg) {
        this.println("[" + name + " / WARN] " + msg);
    }

    @Override
    public void logError(String msg) {
        this.println("[" + name + " / ERROR] " + msg);
    }

    @Override
    public void logDebug(String msg) {
        if (seeDebug) this.println("[" + name + " / DEBUG] " + msg);
    }

    private void println(String msg) {
        reader.getTerminal().puts(InfoCmp.Capability.carriage_return);
        reader.getTerminal().writer().println(msg);
        try {
            reader.callWidget(LineReader.REDRAW_LINE);
            reader.callWidget(LineReader.REDISPLAY);
        } catch (Exception ignored) {
        }
        reader.getTerminal().writer().flush();
    }

}
