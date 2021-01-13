package de.alewu.wpbc.listener;

import static de.alewu.wpbc.util.StaticMethodCollection.tc;

import de.alewu.coreapi.db.caching.CacheRegistry;
import de.alewu.coreapi.exception.CachingException;
import de.alewu.wpbc.WinterProjectBungeeCore;
import de.alewu.wpbc.files.Files;
import de.alewu.wpbc.ml.MlGeneral;
import de.alewu.wpbc.queue.QueueHandler;
import de.alewu.wpbc.util.Constants;
import de.alewu.wpbc.util.PlayerCapabilities;
import de.alewu.wpc.repository.cache.StashedItemCache;
import de.alewu.wpc.repository.cache.UserCache;
import de.alewu.wpc.repository.entity.StashedItem;
import de.alewu.wpc.repository.entity.User;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ListenerServerConnect implements Listener {

    private final UserCache userCache;
    private final StashedItemCache stashedItemCache;

    public ListenerServerConnect() {
        userCache = CacheRegistry.getCache(UserCache.class).orElseThrow(() -> new CachingException("userCache not registered"));
        stashedItemCache = CacheRegistry.getCache(StashedItemCache.class).orElseThrow(() -> new CachingException("stashedItemCache not registered"));
    }

    @EventHandler
    public void on(ServerConnectedEvent e) {
        ProxiedPlayer pp = e.getPlayer();
        User user = userCache.findById(pp.getUniqueId()).orElse(null);
        ProxyServer.getInstance().getScheduler().schedule(WinterProjectBungeeCore.INSTANCE,
            () -> QueueHandler.handle(pp, e.getServer().getInfo()), 500, TimeUnit.MILLISECONDS);
        if (e.getServer().getInfo().getName().equals(Files.GLOBAL_CONFIG.getQueueServer())) {
            List<StashedItem> stashedItems = stashedItemCache.getStash(pp.getUniqueId());
            if (!stashedItems.isEmpty()) {
                if (user != null) {
                    MlGeneral ml = MlGeneral.get(user.getLanguage().toString());
                    pp.sendMessage(tc(Constants.PREFIX + String.format(ml.stashedItemsAvailable(), stashedItems.size())));
                    pp.sendMessage(tc(Constants.PREFIX + ml.claimStashedItemsByUsingCmd()));
                }
            }

            PlayerCapabilities.send(pp);
            if (user != null && !user.getTeamNotifications()) {
                pp.sendMessage(tc(Constants.PREFIX + "§4§l!! §7Teaminterne Benachrichtigungen sind noch deaktiviert!"));
            }
        }
    }

}
