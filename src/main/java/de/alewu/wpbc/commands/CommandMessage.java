package de.alewu.wpbc.commands;

import static de.alewu.wpbc.util.StaticMethodCollection.tc;

import de.alewu.coreapi.db.caching.CacheRegistry;
import de.alewu.coreapi.exception.CachingException;
import de.alewu.wpbc.ml.MlGeneral;
import de.alewu.wpbc.ml.cmds.MlCommandMessage;
import de.alewu.wpbc.ml.cmds.MlCommandSocialSpy;
import de.alewu.wpbc.ml.cmds.MlPunishments;
import de.alewu.wpbc.util.Constants;
import de.alewu.wpbc.util.MessageManager;
import de.alewu.wpbc.util.PlayerCapabilities;
import de.alewu.wpbc.util.PunishmentCommunicationHelper;
import de.alewu.wpbc.util.SocialSpyHelper;
import de.alewu.wpc.helpers.TimeZoneHelper;
import de.alewu.wpc.repository.cache.GroupCache;
import de.alewu.wpc.repository.cache.PunishmentCache;
import de.alewu.wpc.repository.cache.UserCache;
import de.alewu.wpc.repository.entity.Group;
import de.alewu.wpc.repository.entity.Punishment;
import de.alewu.wpc.repository.entity.User;
import java.util.StringJoiner;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandMessage extends Command {

    private final UserCache userCache;
    private final GroupCache groupCache;
    private final PunishmentCache punishmentCache;

    public CommandMessage() {
        super("message", null, "msg");
        userCache = CacheRegistry.getCache(UserCache.class).orElseThrow(() -> new CachingException("userCache not registered"));
        groupCache = CacheRegistry.getCache(GroupCache.class).orElseThrow(() -> new CachingException("groupCache not registered"));
        punishmentCache = CacheRegistry.getCache(PunishmentCache.class).orElseThrow(() -> new CachingException("punishmentCache not registered"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer pp = (ProxiedPlayer) sender;
            User user = userCache.findById(pp.getUniqueId()).orElse(null);
            if (user == null) {
                pp.sendMessage(tc(Constants.PREFIX + String.format(MlGeneral.get("de").noCacheObjectFound(), User.class.getSimpleName())));
                return;
            }
            MlCommandMessage ml = MlCommandMessage.get(user.getLanguage().toString());
            if (!user.isReceivingPrivateMessages()) {
                pp.sendMessage(tc(Constants.PREFIX + ml.turnOnPrivateMessages()));
                return;
            }
            Group g = groupCache.findById(user.getGroupId()).orElse(null);
            if (g == null) {
                pp.sendMessage(tc(Constants.PREFIX + String.format(MlGeneral.get(user.getLanguage().toString()).noCacheObjectFound(), Group.class.getSimpleName())));
                return;
            }
            Punishment activeMute = punishmentCache.getActiveMute(pp.getUniqueId());
            if (activeMute != null) {
                long remainingDuration = activeMute.getPunishmentDuration() != -1 ? activeMute.getPunishmentEndTimestamp() - TimeZoneHelper.epochSecond() : -1;
                PunishmentCommunicationHelper.help(pp, MlPunishments.get(user.getLanguage().toString())).mutePlayer(activeMute.getReason().replaceAll("_", " "), remainingDuration);
                return;
            }
            if (args.length < 2) {
                pp.sendMessage(tc(Constants.PREFIX + ml.wrongUsage()));
                return;
            }
            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
            if (target == null) {
                pp.sendMessage(tc(Constants.PREFIX + String.format(ml.playerNotOnline(), args[0])));
                return;
            }
            if ("SPECTATOR".equals(PlayerCapabilities.getCapabilities(target.getUniqueId()).getGameMode())) {
                pp.sendMessage(tc(Constants.PREFIX + String.format(ml.playerNotOnline(), args[0])));
                return;
            }
            StringJoiner sj = new StringJoiner(" ");
            for (int i = 1; i < args.length; i++) {
                sj.add(args[i]);
            }
            if (target.getUniqueId().equals(pp.getUniqueId())) {
                pp.sendMessage(tc(Constants.PREFIX + ml.messageSelf()));
                Group defaultGroup = groupCache.getDefaultGroup().orElseGet(() -> {
                    Group dg = new Group();
                    dg.setChatPrefix("§7");
                    return dg;
                });
                pp.sendMessage(tc(Constants.PREFIX + String.format(ml.formatTo(),
                    g.getChatPrefix() + user.getUsername(), defaultGroup.getChatPrefix() + "Bob", sj.toString())));
                return;
            }
            User targetUser = userCache.findById(target.getUniqueId()).orElse(null);
            if (targetUser == null) {
                pp.sendMessage(tc(Constants.PREFIX + String.format(ml.playerNotMessageable(), target.getName())));
                return;
            }
            if (!targetUser.isReceivingPrivateMessages()) {
                pp.sendMessage(tc(Constants.PREFIX + String.format(ml.playerToggledMessages(), target.getName())));
                return;
            }
            Group targetGroup = groupCache.findById(targetUser.getGroupId()).orElse(null);
            if (targetGroup == null) {
                pp.sendMessage(tc(Constants.PREFIX + String.format(ml.playerNotMessageable(), target.getName())));
                return;
            }
            pp.sendMessage(tc(Constants.PREFIX + String.format(ml.formatTo(),
                g.getChatPrefix() + user.getUsername(), targetGroup.getChatPrefix() + targetUser.getUsername(), sj.toString())));
            target.sendMessage(tc(Constants.PREFIX + String.format(ml.formatFrom(),
                targetGroup.getChatPrefix() + targetUser.getUsername(), g.getChatPrefix() + user.getUsername(), sj.toString())));
            if (punishmentCache.getActiveMute(target.getUniqueId()) != null) {
                pp.sendMessage(tc(Constants.PREFIX + String.format(ml.playerMuted(), target.getName())));
            }
            MessageManager.updateLastMessaged(pp.getUniqueId(), target.getUniqueId());
            MessageManager.updateLastMessaged(target.getUniqueId(), pp.getUniqueId());

            ProxyServer.getInstance().getPlayers().forEach(x -> {
                if (SocialSpyHelper.canSpy(pp, target, x)) {
                    User u = userCache.findById(x.getUniqueId()).orElse(null);
                    String lang = (u != null ? u.getLanguage().toString() : "de");
                    MlCommandSocialSpy mlCommandSocialSpy = MlCommandSocialSpy.get(lang);
                    x.sendMessage(tc(Constants.PREFIX + String.format(mlCommandSocialSpy.format(),
                        g.getChatPrefix() + pp.getName(), targetGroup.getChatPrefix() + target.getName(), sj.toString())));
                }
            });
        }
    }
}
