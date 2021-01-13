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
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class CommandMute extends PunishmentCommand {

    public CommandMute() {
        super("mute", "wp.mute");
    }

    @Override
    void sendHelp(CommandSender pp, MlPunishments ml, int page) {
        pp.sendMessage(tc(Constants.PREFIX + ml.muteWrongUsage()));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer pp = (ProxiedPlayer) sender;
            if (args.length == 0) {
                sendHelp(pp, MlPunishments.get("de"), 1);
                sendTemplates(pp, PunishmentType.MUTE, null);
                return;
            }
            User user = userCache.findByUsername(args[0]).orElse(null);
            if (user == null) {
                pp.sendMessage(tc(Constants.PREFIX + String.format(MlGeneral.get("de").noCacheObjectFound(), User.class.getSimpleName())));
                return;
            }
            MlGeneral mlGeneral = MlGeneral.get(user.getLanguage().toString());
            MlPunishments ml = MlPunishments.get(user.getLanguage().toString());
            if (args.length == 2) {
                String target = args[0];
                String templateName = args[1];
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
                PunishmentTemplate template = punishmentTemplateCache.find(templateName, PunishmentType.MUTE).orElse(null);
                if (template == null) {
                    pp.sendMessage(tc(Constants.PREFIX + String.format(ml.punishmentUnknownTemplate(), templateName)));
                    return;
                }
                Punishment punishment = Punishment.forMute(targetUser.getId(),
                    pp.getUniqueId(), template, punishmentCache.findAllOfPunished(targetUser.getId()));
                punishmentCache.save(punishment);
                Timespan timespan = new Timespan(punishment.getPunishmentDuration());
                ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(targetUser.getId());
                if (targetPlayer != null) {
                    PunishmentCommunicationHelper.help(targetPlayer, MlPunishments.get(targetUser.getLanguage().toString()))
                        .mutePlayer(template.getTemplateReason().replaceAll("_", " "), punishment.getPunishmentDuration());
                }
                ProxyServer.getInstance().getPlayers().forEach(x -> {
                    final boolean[] receiveTeamNotifications = {false};
                    userCache.findById(x.getUniqueId()).ifPresent(y -> receiveTeamNotifications[0] = y.getTeamNotifications());
                    if (x.hasPermission("wp.punishments.broadcast") && (x.getUniqueId().equals(pp.getUniqueId()) || receiveTeamNotifications[0])) {
                        boolean permanent = timespan.getTimeUnit() == TimeUnit.PERMANENT;
                        x.sendMessage(tc(String.format(ml.muteBroadcast(), Constants.PREFIX,
                            pp.getName(), targetUser.getUsername(), template.getTemplateReason().replaceAll("_", " "), permanent ? ml.durationPermanent() : timespan.toString(false))));
                    }
                });
            } else {
                sendHelp(pp, ml, 1);
                if (args.length == 1) {
                    String target = args[0];
                    User targetUser = userCache.findByUsername(target).orElse(null);
                    if (targetUser == null) {
                        pp.sendMessage(tc(Constants.PREFIX + String.format(mlGeneral.noCacheObjectFound(), target + " " + User.class.getSimpleName())));
                        return;
                    }
                    sendTemplates(pp, PunishmentType.MUTE, targetUser.getId());
                }
            }
        }
    }
}
