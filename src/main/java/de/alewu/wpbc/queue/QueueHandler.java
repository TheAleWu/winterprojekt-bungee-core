package de.alewu.wpbc.queue;

import static de.alewu.wpbc.util.StaticMethodCollection.sendToServer;
import static de.alewu.wpbc.util.StaticMethodCollection.tc;

import de.alewu.coreapi.db.caching.CacheRegistry;
import de.alewu.coreapi.exception.CachingException;
import de.alewu.wpbc.WinterProjectBungeeCore;
import de.alewu.wpbc.files.Files;
import de.alewu.wpbc.ml.MlGeneral;
import de.alewu.wpbc.util.Constants;
import de.alewu.wpc.repository.cache.GameKeyCache;
import de.alewu.wpc.repository.cache.UserCache;
import de.alewu.wpc.repository.entity.GameKey;
import de.alewu.wpc.repository.entity.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;

public class QueueHandler {

    public static final List<UUID> INFORMED_ABOUT_STATUS = new ArrayList<>();
    public static final List<UUID> ATTEMPTING_TO_CONNECT = new ArrayList<>();
    private static final Map<UUID, Integer> PREVIOUS_SERVER_PING_THRESHOLD = new HashMap<>();
    private static final Map<UUID, ScheduledTask> WAITING_IN_QUEUE = new HashMap<>();
    private static final int MAX_PREVIOUS_SERVER_PING = 5;
    private static UserCache userCache;
    private static GameKeyCache gameKeyCache;

    private QueueHandler() {
        // Util class
    }

    public static void init() {
        userCache = CacheRegistry.getCache(UserCache.class)
            .orElseThrow(() -> new CachingException("userCache not registered"));
        gameKeyCache = CacheRegistry.getCache(GameKeyCache.class)
            .orElseThrow(() -> new CachingException("gameKeyCache not registered"));
    }

    public static void handle(ProxiedPlayer pp, ServerInfo connectedTo) {
        String queueServer = Files.GLOBAL_CONFIG.getQueueServer();
        User user = userCache.findById(pp.getUniqueId()).orElse(null);
        if (!connectedTo.getName().equals(queueServer) || user == null) {
            return;
        }
        GameKey gameKey = gameKeyCache.findByUser(pp.getUniqueId()).orElse(null);
        if (gameKey == null && !pp.hasPermission("wp.bypass-gamekey")) {
            sendToServer(connectedTo, "NoGameKeyEntered", pp.getUniqueId().toString());
            return;
        }
        if (WAITING_IN_QUEUE.containsKey(pp.getUniqueId())) {
            return;
        }
        String previousServer = user.getPreviousServer();
        if (previousServer == null || previousServer.equals("null") || previousServer.equals(Files.GLOBAL_CONFIG.getQueueServer())) {
            // First Join
            String firstServer = Files.GLOBAL_CONFIG.getFirstServer();
            ServerInfo si = ProxyServer.getInstance().getServerInfo(firstServer);
            si.ping((ping, throwable) -> {
                if (throwable == null) {
                    if (ATTEMPTING_TO_CONNECT.contains(pp.getUniqueId())) {
                        return;
                    }
                    ATTEMPTING_TO_CONNECT.add(pp.getUniqueId());
                    connect(pp, si);
                    sendToServer(si, "TeleportToSpawn", pp.getUniqueId().toString());
                } else {
                    if (WAITING_IN_QUEUE.containsKey(pp.getUniqueId())) {
                        return;
                    }
                    final long[] serverCount = {
                        ProxyServer.getInstance().getServers().values().stream()
                            .filter(x -> !x.getName().equals(Files.GLOBAL_CONFIG.getQueueServer())).count()
                    };
                    final int[] currentServerCount = {0};
                    ProxyServer.getInstance().getServers().values().stream().filter(x -> !x.getName().equals(Files.GLOBAL_CONFIG.getQueueServer()))
                        .forEach(x -> x.ping((checkPing, checkThrowable) -> {
                            currentServerCount[0] = currentServerCount[0] + 1;
                            if (checkThrowable != null) {
                                if (currentServerCount[0] >= serverCount[0]) {
                                    sendToServer(connectedTo, "AllServersDown", pp.getUniqueId().toString());
                                    INFORMED_ABOUT_STATUS.add(pp.getUniqueId());
                                }
                            }
                        }));
                }
            });
        } else {
            // Has played before
            ServerInfo si = ProxyServer.getInstance().getServerInfo(previousServer);
            si.ping((ping, throwable) -> {
                if (throwable == null) {
                    if (ATTEMPTING_TO_CONNECT.contains(pp.getUniqueId())) {
                        return;
                    }
                    ATTEMPTING_TO_CONNECT.add(pp.getUniqueId());
                    connect(pp, si);
                } else {
                    if (WAITING_IN_QUEUE.containsKey(pp.getUniqueId())) {
                        return;
                    }
                    final long[] serverCount = {
                        ProxyServer.getInstance().getServers().values().stream()
                            .filter(x -> !x.getName().equals(Files.GLOBAL_CONFIG.getQueueServer())).count()
                    };
                    final int[] currentServerCount = {0};
                    final boolean[] gaveItem = {false};
                    ProxyServer.getInstance().getServers().values().stream().filter(x -> !x.getName().equals(Files.GLOBAL_CONFIG.getQueueServer()))
                        .forEach(x -> x.ping((checkPing, checkThrowable) -> {
                                currentServerCount[0] = currentServerCount[0] + 1;
                                if (checkThrowable == null) {
                                    PREVIOUS_SERVER_PING_THRESHOLD.merge(pp.getUniqueId(), 1, Integer::sum);
                                    if (gaveItem[0] || PREVIOUS_SERVER_PING_THRESHOLD.get(pp.getUniqueId()) > MAX_PREVIOUS_SERVER_PING) {
                                        return;
                                    }
                                    sendToServer(connectedTo, "GiveForceJoinItem", pp.getUniqueId().toString());
                                    gaveItem[0] = true;
                                    INFORMED_ABOUT_STATUS.add(pp.getUniqueId());
                                } else {
                                    if (currentServerCount[0] >= serverCount[0]) {
                                        sendToServer(connectedTo, "AllServersDown", pp.getUniqueId().toString());
                                        INFORMED_ABOUT_STATUS.add(pp.getUniqueId());
                                    }
                                }
                            }
                        ));
                }
            });
            ScheduledTask task = ProxyServer.getInstance().getScheduler().schedule(WinterProjectBungeeCore.INSTANCE, () -> {
                UUID uuid = pp.getUniqueId();
                if (!pp.isConnected()) {
                    ScheduledTask x = WAITING_IN_QUEUE.getOrDefault(uuid, null);
                    if (x != null) {
                        ProxyServer.getInstance().getScheduler().cancel(x);
                    }
                    WAITING_IN_QUEUE.remove(uuid);
                    return;
                }
                handle(pp, connectedTo);
            }, 5, 5, TimeUnit.SECONDS);
            WAITING_IN_QUEUE.put(pp.getUniqueId(), task);
        }
    }

    public static void onServerSwitch(ProxiedPlayer pp) {
        ScheduledTask task = WAITING_IN_QUEUE.getOrDefault(pp.getUniqueId(), null);
        if (task != null) {
            ProxyServer.getInstance().getScheduler().cancel(task);
        }
        WAITING_IN_QUEUE.remove(pp.getUniqueId());
        INFORMED_ABOUT_STATUS.remove(pp.getUniqueId());
        PREVIOUS_SERVER_PING_THRESHOLD.remove(pp.getUniqueId());
    }

    public static void connect(ProxiedPlayer pp, ServerInfo server) {
        onServerSwitch(pp);
        pp.connect(server);
        userCache.findById(pp.getUniqueId()).ifPresent(user -> {
            if (!server.getName().equals(Files.GLOBAL_CONFIG.getQueueServer())) {
                sendToServer(server, "LoadInventory", pp.getUniqueId().toString());
            }
            MlGeneral ml = MlGeneral.get(user.getLanguage().toString());
            String serverName = Files.GLOBAL_CONFIG.getDisplayName(server.getName());
            if (serverName == null) {
                serverName = server.getName();
            }
            pp.sendMessage(tc(Constants.PREFIX + String.format(ml.connecting(), serverName)));
        });
    }
}
