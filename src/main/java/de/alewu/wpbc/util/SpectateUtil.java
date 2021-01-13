package de.alewu.wpbc.util;

import static de.alewu.wpbc.util.StaticMethodCollection.sendToServer;
import static de.alewu.wpbc.util.StaticMethodCollection.tc;

import de.alewu.coreapi.db.caching.CacheRegistry;
import de.alewu.wpbc.ml.cmds.MlCommandSpectate;
import de.alewu.wpc.repository.cache.UserCache;
import de.alewu.wpc.repository.entity.User;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class SpectateUtil {

    public static void followToServer(ProxiedPlayer spectator, ProxiedPlayer spectated, User spectatorUser) {
        if (spectator.getServer().getInfo().getName().equals(spectated.getServer().getInfo().getName())) {
            return;
        }
        MlCommandSpectate ml = MlCommandSpectate.get(spectatorUser.getLanguage().toString());
        spectator.connect(spectated.getServer().getInfo());
        spectator.sendMessage(tc(Constants.PREFIX + String.format(ml.followedToServer(), spectated.getName(), spectated.getServer().getInfo().getName())));
    }

    public static void backToPreviousLocation(ProxiedPlayer player, User user) {
        if (user.getServerBeforeSpectating() != null && !user.getServerBeforeSpectating().equals("null") &&
            user.getLocationBeforeSpectating() != null && !user.getLocationBeforeSpectating().equals("null")) {
            ServerInfo info = ProxyServer.getInstance().getServerInfo(user.getServerBeforeSpectating());
            if (info != null) {
                if (player.getServer() != null && player.getServer().getInfo() != null && player.getServer().getInfo().getName() != null &&
                    player.getServer().getInfo().getName().equals(info.getName())) {
                    sendToServer(info, "TeleportPlayer", player.getUniqueId().toString(), user.getLocationBeforeSpectating());
                    sendToServer(info, "GameMode", player.getUniqueId().toString(), "SURVIVAL");
                    UserCache userCache = CacheRegistry.getCache(UserCache.class).orElse(null);
                    if (userCache != null) {
                        user.setSpectatedUser(null);
                        user.setLocationBeforeSpectating("null");
                        user.setServerBeforeSpectating("null");
                        userCache.save(user);
                    }
                    return;
                }
                player.connect(info);
            }
        }
    }

}
