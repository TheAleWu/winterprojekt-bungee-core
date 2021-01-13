package de.alewu.wpbc.listener;

import static de.alewu.wpbc.util.StaticMethodCollection.sendToServer;

import de.alewu.coreapi.db.caching.CacheRegistry;
import de.alewu.coreapi.exception.CachingException;
import de.alewu.wpbc.files.Files;
import de.alewu.wpbc.queue.QueueHandler;
import de.alewu.wpbc.util.PlayerCapabilities;
import de.alewu.wpbc.util.SpectateUtil;
import de.alewu.wpc.repository.cache.UserCache;
import de.alewu.wpc.repository.entity.User;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ListenerServerSwitch implements Listener {

    public static final List<UUID> JOINED_ANOTHER_SERVER = new ArrayList<>();
    private final UserCache userCache;

    public ListenerServerSwitch() {
        this.userCache = CacheRegistry.getCache(UserCache.class)
            .orElseThrow(() -> new CachingException("userCache not registered"));
    }

    @EventHandler
    public void on(ServerSwitchEvent e) {
        ProxiedPlayer pp = e.getPlayer();
        sendToServer(pp.getServer().getInfo(), "ServerInfo", pp.getServer().getInfo().getName());
        User user = userCache.findById(pp.getUniqueId()).orElse(null);
        if (e.getFrom() != null && e.getFrom().getName().equals(Files.GLOBAL_CONFIG.getQueueServer())) {
            QueueHandler.onServerSwitch(pp);
//            QueueHandler.ATTEMPTING_TO_CONNECT.remove(pp.getUniqueId());
//            if (user != null && user.getLocationBeforeSpectating().equals("null") && !JOINED_ANOTHER_SERVER.contains(pp.getUniqueId()) &&
//                !Files.GLOBAL_CONFIG.getFarmServer1().equals(user.getPreviousServer()) && !Files.GLOBAL_CONFIG.getFarmServer2().equals(user.getPreviousServer())) {
//                sendToServer(pp.getServer().getInfo(), "JoinTeleport", pp.getUniqueId().toString(), user.getPreviousServerLocation());
//            }
//            JOINED_ANOTHER_SERVER.remove(pp.getUniqueId());
        }
//        if (user != null) {
//            if (pp.getServer() != null && pp.getServer().getInfo() != null &&
//                !pp.getServer().getInfo().getName().equals(Files.GLOBAL_CONFIG.getQueueServer())) {
//                sendToServer(pp.getServer().getInfo(), "LoadInventory", pp.getUniqueId().toString());
//            }
//            if (user.getSpectatedUser() == null) {
//                if (user.getServerBeforeSpectating() != null && !user.getServerBeforeSpectating().equals("null") &&
//                    user.getLocationBeforeSpectating() != null && !user.getLocationBeforeSpectating().equals("null")) {
//                    ServerInfo info = ProxyServer.getInstance().getServerInfo(user.getServerBeforeSpectating());
//                    sendToServer(info, "TeleportPlayer", pp.getUniqueId().toString(), user.getLocationBeforeSpectating());
//                    user.setLocationBeforeSpectating("null");
//                    user.setServerBeforeSpectating("null");
//                }
//                userCache.save(user);
//            }
//            User spectatedUser = userCache.findById(user.getSpectatedUser()).orElse(null);
//            if (spectatedUser != null) {
//                ProxiedPlayer spectatedPlayer = ProxyServer.getInstance().getPlayer(spectatedUser.getId());
//                if (spectatedPlayer != null) {
//                    if (!pp.getServer().getInfo().getName().equals(spectatedPlayer.getServer().getInfo().getName())) {
//                        SpectateUtil.followToServer(pp, spectatedPlayer, user);
//                    } else {
//                        sendToServer(pp.getServer().getInfo(), "TeleportSpectator", pp.getUniqueId().toString(), spectatedPlayer.getUniqueId().toString());
//                    }
//                }
//            }
//            user.setPreviousServer(pp.getServer().getInfo().getName());
//            userCache.save(user);
//        }
//        List<User> spectators = userCache.getCache().stream()
//            .filter(u -> u.getSpectatedUser() != null && u.getSpectatedUser().equals(pp.getUniqueId()))
//            .collect(Collectors.toList());
//        for (User spectator : spectators) {
//            ProxiedPlayer spectatorPlayer = ProxyServer.getInstance().getPlayer(spectator.getId());
//            if (spectatorPlayer == null) {
//                continue;
//            }
//            SpectateUtil.followToServer(spectatorPlayer, pp, spectator);
//        }
        PlayerCapabilities.send(pp);
        sendToServer(pp.getServer().getInfo(), "ServerInfo", pp.getServer().getInfo().getName());
    }

}
