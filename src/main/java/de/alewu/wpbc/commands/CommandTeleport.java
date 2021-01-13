package de.alewu.wpbc.commands;

import static de.alewu.wpbc.util.StaticMethodCollection.sendToServer;
import static de.alewu.wpbc.util.StaticMethodCollection.tc;

import de.alewu.wpbc.WinterProjectBungeeCore;
import de.alewu.wpbc.util.Constants;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandTeleport extends Command {

    public CommandTeleport() {
        super("teleport", "wp.teleport", "tp");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer pp = (ProxiedPlayer) sender;
            if (args.length == 0) {
                pp.sendMessage(tc(Constants.PREFIX + "§c/tp <Spieler> || /tp <Spieler> <Zielspieler>"));
            } else if (args.length == 1) {
                ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
                if (target == null) {
                    pp.sendMessage(tc(Constants.PREFIX + "§6" + args[0] + " §cist derzeit nicht online."));
                    return;
                }
                if (!pp.getServer().getInfo().getName().equals(target.getServer().getInfo().getName())) {
                    pp.connect(target.getServer().getInfo());
                    ProxyServer.getInstance().getScheduler().schedule(WinterProjectBungeeCore.INSTANCE, () -> sendToServer(pp.getServer().getInfo(), "TeleportPlayerToPlayer",
                        pp.getUniqueId().toString(), target.getUniqueId().toString()), 1, TimeUnit.SECONDS);
                } else {
                    sendToServer(target.getServer().getInfo(), "TeleportPlayerToPlayer", pp.getUniqueId().toString(), target.getUniqueId().toString());
                }
                pp.sendMessage(tc(Constants.PREFIX + "§aDu wirst zu §6" + target.getName() + " §ateleportiert."));
            } else if (args.length == 2) {
                ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
                ProxiedPlayer targetTo = ProxyServer.getInstance().getPlayer(args[1]);
                if (target == null) {
                    pp.sendMessage(tc(Constants.PREFIX + "§6" + args[0] + " §cist derzeit nicht online."));
                    return;
                }
                if (targetTo == null) {
                    pp.sendMessage(tc(Constants.PREFIX + "§6" + args[1] + " §cist derzeit nicht online."));
                    return;
                }
                if (!target.getServer().getInfo().getName().equals(targetTo.getServer().getInfo().getName())) {
                    target.connect(targetTo.getServer().getInfo());
                    ProxyServer.getInstance().getScheduler().schedule(WinterProjectBungeeCore.INSTANCE, () -> sendToServer(target.getServer().getInfo(), "TeleportPlayerToPlayer",
                        target.getUniqueId().toString(), targetTo.getUniqueId().toString()), 1, TimeUnit.SECONDS);
                } else {
                    sendToServer(target.getServer().getInfo(), "TeleportPlayerToPlayer", target.getUniqueId().toString(), targetTo.getUniqueId().toString());
                }
                target.sendMessage(tc(Constants.PREFIX + "§aDu wirst zu §6" + targetTo.getName() + " §ateleportiert."));
                pp.sendMessage(tc(Constants.PREFIX + "§aDu hast §6" + target.getName() + " §azu §6" + targetTo.getName() + " §ateleportiert."));
            }
        }
    }

}
