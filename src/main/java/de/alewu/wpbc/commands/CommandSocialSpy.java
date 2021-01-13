package de.alewu.wpbc.commands;

import de.alewu.wpbc.util.SocialSpyHelper;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandSocialSpy extends Command {

    public CommandSocialSpy() {
        super("socialspy", "wp.socialspy");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ProxiedPlayer pp = (ProxiedPlayer) sender;
        if (SocialSpyHelper.isSpying(pp)) {
            SocialSpyHelper.disableSpy(pp);
        } else {
            SocialSpyHelper.enableSpy(pp);
        }
    }
}
