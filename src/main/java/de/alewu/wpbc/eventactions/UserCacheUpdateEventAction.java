package de.alewu.wpbc.eventactions;

import static de.alewu.wpbc.util.StaticMethodCollection.tc;

import de.alewu.coreapi.db.caching.BroadcastType;
import de.alewu.coreapi.db.caching.CacheRegistry;
import de.alewu.coreapi.db.caching.CacheUpdateEventAction;
import de.alewu.coreapi.exception.CachingException;
import de.alewu.coreapi.ml.Iso2CountryCode;
import de.alewu.wpbc.ml.MlGeneral;
import de.alewu.wpbc.util.Constants;
import de.alewu.wpc.repository.cache.UserCache;
import de.alewu.wpc.repository.entity.User;
import de.alewu.wpc.repository.enums.UserStatus;
import java.util.UUID;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class UserCacheUpdateEventAction extends CacheUpdateEventAction<User, UUID> {

    private final UserCache userCache;

    public UserCacheUpdateEventAction() {
        this.userCache = CacheRegistry.getCache(UserCache.class)
            .orElseThrow(() -> new CachingException("userCache not registered"));
    }

    @Override
    public void perform(BroadcastType broadcastType, User newUser, User oldUser) {
        if (oldUser.getUserStatus() != newUser.getUserStatus() && newUser.getUserStatus() == UserStatus.LIVE) {
            if (newUser.getTwitchUsername() == null || newUser.getTwitchUsername().equals("null")) {
                ProxiedPlayer pp = ProxyServer.getInstance().getPlayer(newUser.getId());
                if (pp != null && pp.hasPermission("wp.twitch")) {
                    MlGeneral ml = MlGeneral.get(newUser.getLanguage().toString());
                    pp.sendMessage(tc(Constants.PREFIX + ml.pleaseSetTwitchUsername()));
                }
                return;
            }
            ProxyServer.getInstance().getPlayers().forEach(all -> {
                User user = userCache.findById(all.getUniqueId())
                    .orElseGet(() -> {
                        User u = new User();
                        u.setLanguage(new Iso2CountryCode("de"));
                        return u;
                    });
                MlGeneral mlGeneral = MlGeneral.get(user.getLanguage().toString());
                all.sendMessage(tc(Constants.PREFIX + String.format(mlGeneral.nowLive(), newUser.getUsername(), newUser.getTwitchUsername())));
            });
        }
    }
}
