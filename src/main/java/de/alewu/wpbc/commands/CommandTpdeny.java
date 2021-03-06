package de.alewu.wpbc.commands;

import static de.alewu.wpbc.util.StaticMethodCollection.tc;

import de.alewu.coreapi.db.caching.CacheRegistry;
import de.alewu.coreapi.exception.CachingException;
import de.alewu.wpbc.ml.MlGeneral;
import de.alewu.wpbc.ml.cmds.MlCommandTpa;
import de.alewu.wpbc.util.Constants;
import de.alewu.wpbc.util.TpaManager;
import de.alewu.wpc.repository.cache.UserCache;
import de.alewu.wpc.repository.entity.User;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandTpdeny extends Command {

    private final UserCache userCache;

    public CommandTpdeny() {
        super("tpdeny");
        this.userCache = CacheRegistry.getCache(UserCache.class).orElseThrow(() -> new CachingException("userCache not registered"));
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
            MlCommandTpa ml = MlCommandTpa.get(user.getLanguage().toString());
            if (args.length == 1) {
                ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
                if (target == null) {
                    pp.sendMessage(tc(Constants.PREFIX + String.format(ml.notOnline(), args[0])));
                    return;
                }
                TpaManager.denyRequest(target.getUniqueId(), pp.getUniqueId(), false);
            } else {
                pp.sendMessage(tc(Constants.PREFIX + ml.tpdenyWrongUsage()));
            }
        }
    }

}
