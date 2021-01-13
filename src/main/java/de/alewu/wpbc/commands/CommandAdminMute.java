package de.alewu.wpbc.commands;

import static de.alewu.wpbc.util.StaticMethodCollection.tc;

import de.alewu.wpbc.ml.MlGeneral;
import de.alewu.wpbc.ml.cmds.MlPunishments;
import de.alewu.wpbc.util.Constants;
import de.alewu.wpbc.util.PunishmentCommunicationHelper;
import de.alewu.wpc.helpers.TimeUnit;
import de.alewu.wpc.helpers.Timespan;
import de.alewu.wpc.repository.entity.Punishment;
import de.alewu.wpc.repository.entity.PunishmentTemplate;
import de.alewu.wpc.repository.entity.User;
import de.alewu.wpc.repository.enums.PunishmentType;
import java.util.StringJoiner;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class CommandAdminMute extends PunishmentCommand {

    public CommandAdminMute() {
        super("adminmute", "wp.adminmute", "am");
    }

    @Override
    void sendHelp(CommandSender pp, MlPunishments ml, int page) {
        pp.sendMessage(tc(Constants.PREFIX + ml.adminMuteWrongUsage()));
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
            if (args.length >= 3) {
                String target = args[0];
                Timespan duration;
                try {
                    duration = new Timespan(args[1]);
                    if (duration.getValue() <= 0) {
                        throw new IllegalArgumentException("Timespan <= 0");
                    }
                } catch (Exception e) {
                    pp.sendMessage(tc(Constants.PREFIX + String.format(ml.banTemplatesInvalidTimespan(), args[1])));
                    return;
                }
                StringJoiner reason = new StringJoiner(" ");
                for (int i = 2; i < args.length; i++) {
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
                if (punishmentCache.getActiveMute(targetUser.getId()) != null) {
                    pp.sendMessage(tc(Constants.PREFIX + String.format(ml.muteAlreadyMuted(), targetUser.getUsername())));
                    return;
                }
                Punishment punishment = Punishment.forAdminMute(targetUser.getId(),
                    pp.getUniqueId(), reason.toString(), duration);
                punishmentCache.save(punishment);
                Timespan timespan = new Timespan(punishment.getPunishmentDuration());
                ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(targetUser.getId());
                if (targetPlayer != null) {
                    PunishmentCommunicationHelper.help(targetPlayer, MlPunishments.get(targetUser.getLanguage().toString()))
                        .mutePlayer(reason.toString().replaceAll("_", " "), punishment.getPunishmentDuration());
                }
                ProxyServer.getInstance().getPlayers().forEach(x -> {
                    final boolean[] receiveTeamNotifications = {false};
                    userCache.findById(x.getUniqueId()).ifPresent(y -> receiveTeamNotifications[0] = y.getTeamNotifications());
                    if (x.hasPermission("wp.punishments.broadcast") && (x.getUniqueId().equals(pp.getUniqueId()) || receiveTeamNotifications[0])) {
                        boolean permanent = timespan.getTimeUnit() == TimeUnit.PERMANENT;
                        x.sendMessage(tc(String.format(ml.muteBroadcast(), Constants.PREFIX,
                            pp.getName(), targetUser.getUsername(), reason.toString().replaceAll("_", " "), permanent ? ml.durationPermanent() : timespan.toString(false))));
                    }
                });
            } else {
                sendHelp(pp, ml, 1);
            }
        }
    }
}
