package dev.dewy.reborn.command.terminal;

import com.sasha.simplecmdsys.SimpleCommand;
import dev.dewy.reborn.REborn;

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
