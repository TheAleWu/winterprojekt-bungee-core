package de.alewu.wpbc.util;

import static de.alewu.wpbc.util.StaticMethodCollection.tc;

import de.alewu.coreapi.db.caching.CacheRegistry;
import de.alewu.coreapi.exception.CachingException;
import de.alewu.wpbc.ml.cmds.MlCommandSocialSpy;
import de.alewu.wpc.repository.cache.UserCache;
import de.alewu.wpc.repository.entity.User;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class SocialSpyHelper {

    private static final List<UUID> SPYING = new ArrayList<>();

    public static void enableSpy(ProxiedPlayer pp) {
        if (!isSpying(pp)) {
            SPYING.add(pp.getUniqueId());

            if (pp.isConnected()) {

                UserCache userCache = CacheRegistry.getCache(UserCache.class).orElseThrow(() -> new CachingException("userCache not registered"));
                User user = userCache.findById(pp.getUniqueId()).orElse(null);
                String lang = (user != null ? user.getLanguage().toString() : "de");
                MlCommandSocialSpy ml = MlCommandSocialSpy.get(lang);

                pp.sendMessage(tc(Constants.PREFIX + ml.activated()));
                pp.sendMessage(tc(Constants.PREFIX + ml.deactivateOnQuit()));
            }
        }
    }

    public static boolean isSpying(ProxiedPlayer pp) {
        return SPYING.contains(pp.getUniqueId());
    }

    public static void disableSpy(ProxiedPlayer pp) {
        if (isSpying(pp)) {
            SPYING.remove(pp.getUniqueId());

            if (pp.isConnected()) {
                UserCache userCache = CacheRegistry.getCache(UserCache.class).orElseThrow(() -> new CachingException("userCache not registered"));
                User user = userCache.findById(pp.getUniqueId()).orElse(null);
                String lang = (user != null ? user.getLanguage().toString() : "de");
                MlCommandSocialSpy ml = MlCommandSocialSpy.get(lang);

                pp.sendMessage(tc(Constants.PREFIX + ml.deactivated()));
            }
        }
    }

    public static boolean canSpy(ProxiedPlayer sender, ProxiedPlayer receiver, ProxiedPlayer spying) {
        if (spying.hasPermission("wp.socialspy") && isSpying(spying)) {
            return !sender.hasPermission("wp.socialspy.protected") && !receiver.hasPermission("wp.socialspy.protected");
        }
        return false;
    }

}
