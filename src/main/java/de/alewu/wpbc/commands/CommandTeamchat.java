package de.alewu.wpbc.commands;

import static de.alewu.wpbc.util.StaticMethodCollection.tc;

import de.alewu.coreapi.db.caching.CacheRegistry;
import de.alewu.coreapi.exception.CachingException;
import de.alewu.wpbc.ml.MlGeneral;
import de.alewu.wpbc.util.Constants;
import de.alewu.wpc.repository.cache.GroupCache;
import de.alewu.wpc.repository.cache.UserCache;
import de.alewu.wpc.repository.entity.Group;
import de.alewu.wpc.repository.entity.User;
import java.util.StringJoiner;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandTeamchat extends Command {

    public static final String PERMISSION = "wp.teamchat";
    private final UserCache userCache;
    private final GroupCache groupCache;

    public CommandTeamchat() {
        super("teamchat", PERMISSION, "tc");
        userCache = CacheRegistry.getCache(UserCache.class).orElseThrow(() -> new CachingException("userCache not registered"));
        groupCache = CacheRegistry.getCache(GroupCache.class).orElseThrow(() -> new CachingException("groupCache not registered"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer pp = (ProxiedPlayer) sender;
            if (args.length == 0) {
                pp.sendMessage(tc(Constants.PREFIX + "§6/tc <Text>"));
                return;
            }
            User user = userCache.findById(pp.getUniqueId())
                .orElse(null);
            if (user == null) {
                pp.sendMessage(tc(Constants.PREFIX + String.format(MlGeneral.get("de").noCacheObjectFound(), User.class.getSimpleName())));
                return;
            }
            MlGeneral ml = MlGeneral.get(user.getLanguage().toString());
            Group g = groupCache.findById(user.getGroupId()).orElse(null);
            if (g == null) {
                pp.sendMessage(tc(Constants.PREFIX + String.format(ml.noCacheObjectFound(), Group.class.getSimpleName())));
                return;
            }
            StringJoiner msg = new StringJoiner(" ");
            for (String txt : args) {
                msg.add(txt);
            }
            ProxyServer.getInstance().getPlayers().forEach(all -> {
                final boolean[] receiveTeamNotifications = {false};
                userCache.findById(all.getUniqueId()).ifPresent(y -> receiveTeamNotifications[0] = y.getTeamNotifications());
                if (all.hasPermission(PERMISSION) && (all.getUniqueId().equals(pp.getUniqueId()) || receiveTeamNotifications[0])) {
                    all.sendMessage(tc(Constants.PREFIX + "§8[§c§lTC§8] §7" + g.getChatPrefix() + pp.getName() + "§8: §7"
                        + ChatColor.translateAlternateColorCodes('&', msg.toString())));
                }
            });
            if (!user.getTeamNotifications()) {
                pp.sendMessage(tc(Constants.PREFIX + "§4§l!! §7Teaminterne Benachrichtigungen sind noch deaktiviert!"));
            }
        }
    }
}
