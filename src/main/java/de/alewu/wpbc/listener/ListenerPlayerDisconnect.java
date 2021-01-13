package de.alewu.wpbc.listener;

import static de.alewu.wpbc.util.StaticMethodCollection.tc;

import de.alewu.coreapi.db.caching.CacheRegistry;
import de.alewu.coreapi.exception.CachingException;
import de.alewu.wpbc.files.Files;
import de.alewu.wpbc.ml.MlPlayerAction;
import de.alewu.wpbc.ml.cmds.MlCommandSpectate;
import de.alewu.wpbc.queue.QueueHandler;
import de.alewu.wpbc.util.Constants;
import de.alewu.wpbc.util.SocialSpyHelper;
import de.alewu.wpbc.util.TpaManager;
import de.alewu.wpbc.util.TpaManager.TpaRequest;
import de.alewu.wpc.helpers.TimeZoneHelper;
import de.alewu.wpc.repository.cache.GroupCache;
import de.alewu.wpc.repository.cache.PunishmentCache;
import de.alewu.wpc.repository.cache.UserCache;
import de.alewu.wpc.repository.entity.Group;
import de.alewu.wpc.repository.entity.Punishment;
import de.alewu.wpc.repository.entity.User;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ListenerPlayerDisconnect implements Listener {

    private static final Logger LOG = Logger.getLogger(ListenerPostLogin.class.getName());
    private final UserCache userCache;
    private final GroupCache groupCache;
    private final PunishmentCache punishmentCache;

    public ListenerPlayerDisconnect() {
        userCache = CacheRegistry.getCache(UserCache.class)
            .orElseThrow(() -> new CachingException("userCache not registered"));
        groupCache = CacheRegistry.getCache(GroupCache.class)
            .orElseThrow(() -> new CachingException("groupCache not registered"));
        punishmentCache = CacheRegistry.getCache(PunishmentCache.class)
            .orElseThrow(() -> new CachingException("punishmentCache not registered"));
    }

    @EventHandler
    public void on(PlayerDisconnectEvent e) {
        ProxiedPlayer pp = e.getPlayer();
        Group defaultGroup = groupCache.getDefaultGroup()
            .orElse(null);
        if (defaultGroup == null) {
            LOG.severe("Default group is missing! Player disconnects silently as message cannot be built normally!");
            return;
        }

        User user = userCache.findById(pp.getUniqueId()).orElse(null);
        if (user == null) {
            LOG.severe("User object of player " + pp.getName() + " (" + pp.getUniqueId() + ") is missing. "
                + "Player disconnects silently as message cannot be built normally!");
            return;
        }
        user.setSpectatedUser(null);
        if (pp.getServer() != null && pp.getServer().getInfo() != null &&
            !pp.getServer().getInfo().getName().equals(Files.GLOBAL_CONFIG.getQueueServer())) {
            user.setPreviousServer(pp.getServer().getInfo().getName());
        }
        List<User> spectators = userCache.getCache().stream()
            .filter(u -> u.getSpectatedUser() != null && u.getSpectatedUser().equals(pp.getUniqueId()))
            .collect(Collectors.toList());
        for (User spectator : spectators) {
            MlCommandSpectate ml = MlCommandSpectate.get(spectator.getLanguage().toString());
            ProxiedPlayer spectatorPlayer = ProxyServer.getInstance().getPlayer(spectator.getId());
            if (spectatorPlayer != null) {
                spectatorPlayer.sendMessage(tc(Constants.PREFIX + ml.playerDisconnected()));
            }
            spectator.setSpectatedUser(null);
            userCache.save(spectator);
        }
        user.setLastOnline(TimeZoneHelper.epochSecond());
        userCache.save(user);

        Group g = groupCache.findById(user.getGroupId()).orElse(defaultGroup);
        String playerNameWithPrefix = g.getChatPrefix() + pp.getName();
        Punishment activeBan = punishmentCache.getActiveBan(pp.getUniqueId());
        if (activeBan == null && (!Files.GLOBAL_CONFIG.isMaintenance() || Files.GLOBAL_CONFIG.getMaintenanceBypassed().contains(pp.getUniqueId()))) {
            ProxyServer.getInstance().getPlayers().forEach(x -> {
                User u = userCache.findById(x.getUniqueId()).orElse(null);
                if (u != null) {
                    String quitMessage = Constants.PREFIX + String.format(MlPlayerAction.get(u.getLanguage().toString()).quit(), playerNameWithPrefix);
                    BaseComponent[] joinMessageComponents = tc(quitMessage);
                    x.sendMessage(joinMessageComponents);
                }
            });
        }

        QueueHandler.onServerSwitch(pp);
        QueueHandler.ATTEMPTING_TO_CONNECT.remove(pp.getUniqueId());

        List<TpaRequest> requests = TpaManager.getAllRequests(pp.getUniqueId());
        requests.forEach(TpaManager::denyRequestByRequestorQuit);

        List<TpaRequest> requested = TpaManager.getAllRequested(pp.getUniqueId());
        requested.forEach(x -> TpaManager.denyRequest(x.getRequestor(), x.getRequested(), false));

        SocialSpyHelper.disableSpy(pp);
    }

}
