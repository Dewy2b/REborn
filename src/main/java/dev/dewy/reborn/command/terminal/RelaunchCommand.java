package dev.dewy.reborn.command.terminal;

import dev.dewy.reborn.REborn;
import com.sasha.simplecmdsys.SimpleCommand;

/**
 * Quit REborn
 */
public class RelaunchCommand extends SimpleCommand {
    public RelaunchCommand() {
        super("relaunch");
    }

    @Override
    public void onCommand() {
        REborn.INSTANCE.reLaunch();
    }
}
