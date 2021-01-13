package de.alewu.wpbc.commands;

import static de.alewu.wpbc.util.StaticMethodCollection.tc;

import de.alewu.coreapi.db.caching.CacheRegistry;
import de.alewu.coreapi.exception.CachingException;
import de.alewu.wpbc.ml.MlGeneral;
import de.alewu.wpbc.util.Constants;
import de.alewu.wpc.repository.cache.UserCache;
import de.alewu.wpc.repository.entity.User;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandNotify extends Command {

    private final UserCache userCache;

    public CommandNotify() {
        super("notify");
        userCache = CacheRegistry.getCache(UserCache.class).orElseThrow(() -> new CachingException("userCache not registered"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ProxiedPlayer pp = (ProxiedPlayer) sender;
        User user = userCache.findById(pp.getUniqueId()).orElse(null);
        if (user == null) {
            pp.sendMessage(tc(Constants.PREFIX + String.format(MlGeneral.get("de").noCacheObjectFound(), User.class.getSimpleName())));
            return;
        }
        if (pp.hasPermission("wp.notify")) {
            if (user.getTeamNotifications()) {
                user.setTeamNotifications(false);
                pp.sendMessage(tc(Constants.PREFIX + "§cTeaminterne Benachrichtigungen wurden deaktiviert."));
            } else {
                user.setTeamNotifications(true);
                pp.sendMessage(tc(Constants.PREFIX + "§aTeaminterne Benachrichtigungen wurden reaktiviert."));
            }
            userCache.save(user);
        }
    }
}
