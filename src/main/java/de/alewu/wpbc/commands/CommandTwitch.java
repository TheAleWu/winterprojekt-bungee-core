package de.alewu.wpbc.commands;

import static de.alewu.wpbc.util.StaticMethodCollection.tc;

import de.alewu.coreapi.db.caching.CacheRegistry;
import de.alewu.coreapi.exception.CachingException;
import de.alewu.wpbc.ml.MlGeneral;
import de.alewu.wpbc.ml.cmds.MlCommandTwitch;
import de.alewu.wpbc.util.Constants;
import de.alewu.wpc.repository.cache.UserCache;
import de.alewu.wpc.repository.entity.User;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandTwitch extends Command {

    private final UserCache userCache;

    public CommandTwitch() {
        super("twitch", "wp.twitch");
        this.userCache = CacheRegistry.getCache(UserCache.class)
            .orElseThrow(() -> new CachingException("userCache not registered"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer pp = (ProxiedPlayer) sender;
            User user = userCache.findById(pp.getUniqueId()).orElse(null);
            if (user == null) {
                sender.sendMessage(tc(Constants.PREFIX + String.format(MlGeneral.get("de").noCacheObjectFound(), User.class.getSimpleName())));
                return;
            }
            MlCommandTwitch ml = MlCommandTwitch.get(user.getLanguage().toString());
            if (args.length != 1) {
                pp.sendMessage(tc(Constants.PREFIX + ml.wrongUsage()));
                return;
            }
            String twitchUsername = args[0];
            user.setTwitchUsername(twitchUsername);
            userCache.save(user);
            pp.sendMessage(tc(Constants.PREFIX + String.format(ml.setUsername(), twitchUsername)));
        }
    }
}
