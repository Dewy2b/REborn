package com.sasha.reminecraft.command.terminal;

import com.sasha.reminecraft.ReMinecraft;
import com.sasha.simplecmdsys.SimpleCommand;

/**
 * Log into a Mojang account in RE:Minecraft
 */
public class LoginCommand extends SimpleCommand {
    public LoginCommand() {
        super("login");
    }

    @Override
    public void onCommand() {
        if (this.getArguments() == null || this.getArguments().length != 2) {
            ReMinecraft.LOGGER.logError("Requires two arguments!");
            return;
        }
        String email = this.getArguments()[0];
        String pass = this.getArguments()[1];
        ReMinecraft.INSTANCE.MAIN_CONFIG.email = email;
        ReMinecraft.INSTANCE.MAIN_CONFIG.email = pass;
        ReMinecraft.LOGGER.log("Credentials updated! Please type \"relaunch\" to try logging in again.");
    }
}
