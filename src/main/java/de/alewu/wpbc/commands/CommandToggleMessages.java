package de.alewu.wpbc.commands;

import static de.alewu.wpbc.util.StaticMethodCollection.tc;

import de.alewu.coreapi.db.caching.CacheRegistry;
import de.alewu.coreapi.exception.CachingException;
import de.alewu.coreapi.ml.Iso2CountryCode;
import de.alewu.wpbc.ml.MlGeneral;
import de.alewu.wpbc.ml.cmds.MlCommandMessage;
import de.alewu.wpbc.util.Constants;
import de.alewu.wpc.repository.cache.UserCache;
import de.alewu.wpc.repository.entity.User;
import java.util.StringJoiner;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandToggleMessages extends Command {

    private final UserCache userCache;

    public CommandToggleMessages() {
        super("togglemessages", null, "togglemsg");
        userCache = CacheRegistry.getCache(UserCache.class).orElseThrow(() -> new CachingException("userCache not registered"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer pp = (ProxiedPlayer) sender;
            User user = userCache.findById(pp.getUniqueId())
                .orElse(null);
            if (user == null) {
                pp.sendMessage(tc(Constants.PREFIX + String.format(MlGeneral.get("de").noCacheObjectFound(), User.class.getSimpleName())));
                return;
            }
            MlCommandMessage ml = MlCommandMessage.get(user.getLanguage().toString());
            if (user.isReceivingPrivateMessages()) {
                user.setReceivingPrivateMessages(false);
                userCache.save(user);
                pp.sendMessage(tc(Constants.PREFIX + ml.toggleReceiveOff()));
            } else {
                user.setReceivingPrivateMessages(true);
                userCache.save(user);
                pp.sendMessage(tc(Constants.PREFIX + ml.toggleReceiveOn()));
            }
        }
    }
}
