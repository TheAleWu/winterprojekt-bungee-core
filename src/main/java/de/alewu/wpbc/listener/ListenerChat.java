package de.alewu.wpbc.listener;

import static de.alewu.wpbc.util.StaticMethodCollection.sendToServer;
import static de.alewu.wpbc.util.StaticMethodCollection.tc;

import de.alewu.coreapi.db.caching.CacheRegistry;
import de.alewu.coreapi.exception.CachingException;
import de.alewu.wpbc.files.Files;
import de.alewu.wpbc.ml.MlGeneral;
import de.alewu.wpbc.ml.MlPlayerAction;
import de.alewu.wpbc.ml.cmds.MlPunishments;
import de.alewu.wpbc.util.Constants;
import de.alewu.wpbc.util.PlayerCapabilities;
import de.alewu.wpbc.util.PunishmentCommunicationHelper;
import de.alewu.wpbc.util.SpamPrevention;
import de.alewu.wpc.helpers.TimeZoneHelper;
import de.alewu.wpc.repository.cache.GroupCache;
import de.alewu.wpc.repository.cache.PunishmentCache;
import de.alewu.wpc.repository.cache.UserCache;
import de.alewu.wpc.repository.entity.Group;
import de.alewu.wpc.repository.entity.Punishment;
import de.alewu.wpc.repository.entity.User;
import de.alewu.wpc.repository.enums.PunishmentType;
import java.util.StringJoiner;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ListenerChat implements Listener {

    private final UserCache userCache;
    private final GroupCache groupCache;
    private final PunishmentCache punishmentCache;

    public ListenerChat() {
        this.userCache = CacheRegistry.getCache(UserCache.class)
            .orElseThrow(() -> new CachingException("userCache not registered."));
        this.groupCache = CacheRegistry.getCache(GroupCache.class)
            .orElseThrow(() -> new CachingException("groupCache not registered."));
        this.punishmentCache = CacheRegistry.getCache(PunishmentCache.class)
            .orElseThrow(() -> new CachingException("punishmentCache not registered."));
    }

    @EventHandler
    public void on(ChatEvent e) {
        if (!(e.getSender() instanceof ProxiedPlayer) || e.getMessage().startsWith("/")) {
            return;
        }
        ProxiedPlayer pp = (ProxiedPlayer) e.getSender();
        if (pp.getServer().getInfo().getName().equals(Files.GLOBAL_CONFIG.getQueueServer())) {
            e.setCancelled(true);
            return;
        }
        User user = userCache.findById(pp.getUniqueId()).orElse(null);
        MlGeneral mlGeneral;
        if (user == null) {
            mlGeneral = MlGeneral.get("de");
            pp.sendMessage(tc(Constants.PREFIX + String.format(mlGeneral.noCacheObjectFound(), "User")));
            return;
        }
        mlGeneral = MlGeneral.get(user.getLanguage().toString());
        Punishment activeMute = punishmentCache.getActiveMute(pp.getUniqueId());
        if (activeMute != null) {
            MlPunishments mlPunishments = MlPunishments.get(user.getLanguage().toString());
            long remainingDuration = activeMute.getPunishmentDuration() != -1
                ? activeMute.getPunishmentEndTimestamp() - TimeZoneHelper.epochSecond()
                : -1;
            PunishmentCommunicationHelper.help(pp, mlPunishments).mutePlayer(activeMute.getReason().replaceAll("_", " "), remainingDuration);
            e.setCancelled(true);
            return;
        }
        MlPlayerAction mlPlayerAction = MlPlayerAction.get(user.getLanguage().toString());
        Group g = groupCache.findById(user.getGroupId()).orElse(groupCache.getDefaultGroup().orElse(null));
        if (user.isRankToggled()) {
            g = groupCache.getDefaultGroup().orElse(null);
        }
        if (g == null) {
            pp.sendMessage(tc(Constants.PREFIX + String.format(mlGeneral.noCacheObjectFound(), "Group")));
            return;
        }
        e.setCancelled(true);
        if (SpamPrevention.isSpamming(pp.getUniqueId(), e.getMessage())) {
            pp.sendMessage(tc(Constants.PREFIX + mlPlayerAction.stopSpamming()));
            return;
        }
        if ("SPECTATOR".equals(PlayerCapabilities.getCapabilities(pp.getUniqueId()).getGameMode())) {
            pp.sendMessage(tc(Constants.PREFIX + "§cDeine Identität soll geheim bleiben! ;)"));
            return;
        }
        SpamPrevention.track(pp.getUniqueId(), e.getMessage());
        String serverPrefix = Files.GLOBAL_CONFIG.getDisplayName(pp.getServer().getInfo().getName());
        String finalPrefix = serverPrefix != null ? "§8[§6" + serverPrefix + "§8] " : "";
        for (ProxiedPlayer online : ProxyServer.getInstance().getPlayers()) {
            online.sendMessage(tc(finalPrefix + g.getChatPrefix() + pp.getName() + " §8» §r" + buildMessage(online, e.getMessage())));
        }
    }

    private String buildMessage(ProxiedPlayer target, String originalMessage) {
        StringJoiner sj = new StringJoiner(" ");
        for (String messagePart : originalMessage.split(" ")) {
            if (messagePart.startsWith("@")) {
                String mentioned = messagePart.substring(1);
                ProxiedPlayer mentionedPlayer = ProxyServer.getInstance().getPlayers().stream()
                    .filter(x -> x.getName().equalsIgnoreCase(mentioned))
                    .findFirst()
                    .orElse(null);
                if (mentionedPlayer != null) {
                    if (target.getUniqueId().equals(mentionedPlayer.getUniqueId())) {
                        sj.add("§b@" + mentionedPlayer.getName() + "§r");
                        sendToServer(mentionedPlayer.getServer().getInfo(), "PingPlayer", mentionedPlayer.getUniqueId().toString());
                    } else {
                        sj.add("§7@" + mentionedPlayer.getName() + "§r");
                    }
                } else {
                    sj.add("@" + mentioned);
                }
            } else {
                sj.add(messagePart);
            }
        }
        return sj.toString();
    }

}
