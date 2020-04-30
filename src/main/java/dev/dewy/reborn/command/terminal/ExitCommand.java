package dev.dewy.reborn.command.terminal;

import com.sasha.simplecmdsys.SimpleCommand;
import dev.dewy.reborn.REborn;

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
