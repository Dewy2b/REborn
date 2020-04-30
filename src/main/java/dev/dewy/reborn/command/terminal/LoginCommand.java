package dev.dewy.reborn.command.terminal;

import dev.dewy.reborn.REborn;
import com.sasha.simplecmdsys.SimpleCommand;

/**
 * Log into a Mojang account in REborn
 */
public class LoginCommand extends SimpleCommand {
    public LoginCommand() {
        super("login");
    }

    @Override
    public void onCommand() {
        if (this.getArguments() == null || this.getArguments().length != 2) {
            REborn.LOGGER.logError("Requires two arguments!");
            return;
        }
        String email = this.getArguments()[0];
        String pass = this.getArguments()[1];
        REborn.INSTANCE.MAIN_CONFIG.email = email;
        REborn.INSTANCE.MAIN_CONFIG.email = pass;
        REborn.LOGGER.log("Credentials updated! Please type \"relaunch\" to try logging in again.");
    }
}
