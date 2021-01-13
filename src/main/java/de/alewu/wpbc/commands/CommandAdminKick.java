package de.alewu.wpbc.commands;

import static de.alewu.wpbc.util.StaticMethodCollection.tc;

import de.alewu.wpbc.ml.MlGeneral;
import de.alewu.wpbc.ml.cmds.MlPunishments;
import de.alewu.wpbc.util.Constants;
import de.alewu.wpbc.util.PunishmentCommunicationHelper;
import de.alewu.wpc.repository.entity.Punishment;
import de.alewu.wpc.repository.entity.User;
import java.util.StringJoiner;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class CommandAdminKick extends PunishmentCommand {

    public CommandAdminKick() {
        super("adminkick", "wp.adminkick", "ak");
    }

    @Override
    void sendHelp(CommandSender pp, MlPunishments ml, int page) {
        pp.sendMessage(tc(Constants.PREFIX + ml.adminKickWrongUsage()));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer pp = (ProxiedPlayer) sender;
            if (args.length == 0) {
                sendHelp(pp, MlPunishments.get("de"), 1);
                return;
            }
            User user = userCache.findByUsername(args[0]).orElse(null);
            if (user == null) {
                pp.sendMessage(tc(Constants.PREFIX + String.format(MlGeneral.get("de").noCacheObjectFound(), User.class.getSimpleName())));
                return;
            }
            MlGeneral mlGeneral = MlGeneral.get(user.getLanguage().toString());
            MlPunishments ml = MlPunishments.get(user.getLanguage().toString());
            if (args.length >= 2) {
                String target = args[0];
                StringJoiner reason = new StringJoiner(" ");
                for (int i = 1; i < args.length; i++) {
                    reason.add(args[i]);
                }
                User targetUser = userCache.findByUsername(target).orElse(null);
                if (targetUser == null) {
                    pp.sendMessage(tc(Constants.PREFIX + String.format(mlGeneral.noCacheObjectFound(), target + " " + User.class.getSimpleName())));
                    return;
                }
                if (!canPunish(user, targetUser)) {
                    pp.sendMessage(tc(Constants.PREFIX + ml.notAllowedToPunish()));
                    return;
                }
                ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(targetUser.getId());
                if (targetPlayer == null) {
                    pp.sendMessage(tc(Constants.PREFIX + String.format(mlGeneral.notOnline(), target)));
                    return;
                }
                Punishment punishment = Punishment.forAdminKick(targetPlayer.getUniqueId(), pp.getUniqueId(), reason.toString());
                punishmentCache.save(punishment);
                PunishmentCommunicationHelper.help(targetPlayer, MlPunishments.get(targetUser.getLanguage().toString())).kickPlayer(reason.toString().replaceAll("_", " "));
                ProxyServer.getInstance().getPlayers().forEach(x -> {
                    final boolean[] receiveTeamNotifications = {false};
                    userCache.findById(x.getUniqueId()).ifPresent(y -> receiveTeamNotifications[0] = y.getTeamNotifications());
                    if (x.hasPermission("wp.punishments.broadcast") && (x.getUniqueId().equals(pp.getUniqueId()) || receiveTeamNotifications[0])) {
                        x.sendMessage(tc(String.format(ml.kickBroadcast(), Constants.PREFIX,
                            pp.getName(), targetPlayer.getName(), reason.toString().replaceAll("_", " "))));
                    }
                });
            } else {
                sendHelp(pp, ml, 1);
            }
        }
    }
}
