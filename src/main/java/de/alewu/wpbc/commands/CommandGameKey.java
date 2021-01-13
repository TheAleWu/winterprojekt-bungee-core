package de.alewu.wpbc.commands;

import static de.alewu.wpbc.util.StaticMethodCollection.tc;

import de.alewu.coreapi.db.caching.CacheRegistry;
import de.alewu.coreapi.exception.CachingException;
import de.alewu.wpbc.ml.MlGeneral;
import de.alewu.wpbc.ml.cmds.MlCommandGameKey;
import de.alewu.wpbc.util.Constants;
import de.alewu.wpc.helpers.TimeZoneHelper;
import de.alewu.wpc.repository.cache.GameKeyCache;
import de.alewu.wpc.repository.cache.UserCache;
import de.alewu.wpc.repository.entity.GameKey;
import de.alewu.wpc.repository.entity.User;
import java.time.OffsetDateTime;
import java.util.Optional;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandGameKey extends Command {

    private final UserCache userCache;
    private final GameKeyCache gameKeyCache;

    public CommandGameKey() {
        super("gamekey");
        userCache = CacheRegistry.getCache(UserCache.class).orElseThrow(() -> new CachingException("userCache not registered"));
        gameKeyCache = CacheRegistry.getCache(GameKeyCache.class).orElseThrow(() -> new CachingException("gameKeyCache not registered"));
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
            MlCommandGameKey ml = MlCommandGameKey.get(user.getLanguage().toString());
            if (pp.hasPermission("wp.bypass-gamekey")) {
                pp.sendMessage(tc(Constants.PREFIX + ml.doNotNeedOne()));
                return;
            }
            if (args.length == 1) {
                if (gameKeyCache.findByUser(pp.getUniqueId()).isPresent()) {
                    pp.sendMessage(tc(Constants.PREFIX + ml.alreadyUsedOne()));
                    return;
                }
                String key = args[0];
                Optional<GameKey> opt = gameKeyCache.findByToken(key);
                if (!opt.isPresent()) {
                    pp.sendMessage(tc(Constants.PREFIX + String.format(ml.noKeyFound(), key)));
                } else {
                    GameKey gk = opt.get();
                    if (gk.getUsedBy() != null) {
                        pp.sendMessage(tc(Constants.PREFIX + ml.alreadyInUse()));
                        return;
                    }
                    gk.setUsedBy(pp.getUniqueId());
                    gk.setUsedAt(TimeZoneHelper.epochSecond());
                    gameKeyCache.save(gk);
                    pp.sendMessage(tc(Constants.PREFIX + String.format(ml.successfullyUsed(), key)));
                    pp.sendMessage(tc(Constants.PREFIX + ml.reconnectToPlay()));
                }
            } else {
                pp.sendMessage(tc(Constants.PREFIX + ml.wrongUsage()));
            }
        }
    }
}
