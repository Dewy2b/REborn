package dev.dewy.reborn.command.terminal;

import dev.dewy.reborn.REborn;
import com.sasha.simplecmdsys.SimpleCommand;

/**
 * Quit REborn
 */
public class ExitCommand extends SimpleCommand {
    public ExitCommand() {
        super("stop");
    }

    @Override
    public void onCommand() {
        REborn.INSTANCE.stop();
    }
}
