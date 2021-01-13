package de.alewu.wpbc.commands;

import static de.alewu.wpbc.util.StaticMethodCollection.sendToServer;
import static de.alewu.wpbc.util.StaticMethodCollection.tc;

import de.alewu.coreapi.db.caching.CacheRegistry;
import de.alewu.coreapi.exception.CachingException;
import de.alewu.wpbc.ml.MlGeneral;
import de.alewu.wpbc.ml.MlPlayerAction;
import de.alewu.wpbc.ml.cmds.MlCommandSpectate;
import de.alewu.wpbc.util.Constants;
import de.alewu.wpbc.util.SpectateUtil;
import de.alewu.wpc.repository.cache.GroupCache;
import de.alewu.wpc.repository.cache.UserCache;
import de.alewu.wpc.repository.entity.Group;
import de.alewu.wpc.repository.entity.User;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandSpectate extends Command {

    private final UserCache userCache;
    private final GroupCache groupCache;

    public CommandSpectate() {
        super("spectate", "wp.spectate", "spec");
        this.userCache = CacheRegistry.getCache(UserCache.class)
            .orElseThrow(() -> new CachingException("userCache not registered"));
        this.groupCache = CacheRegistry.getCache(GroupCache.class)
            .orElseThrow(() -> new CachingException("groupCache not registered"));
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
            MlCommandSpectate ml = MlCommandSpectate.get(user.getLanguage().toString());
            if (args.length > 1) {
                pp.sendMessage(tc(Constants.PREFIX + ml.wrongUsage()));
                return;
            }
            if (args.length == 0) {
                if (user.getSpectatedUser() != null) {
                    user.setSpectatedUser(null);
                    userCache.save(user);
                    SpectateUtil.backToPreviousLocation(pp, user);
                    pp.sendMessage(tc(Constants.PREFIX + ml.leftSpectatorMode()));

                    Group g = groupCache.findById(user.getGroupId()).orElse(groupCache.getDefaultGroup().orElseThrow(() -> new CachingException("No default group defined!")));
                    String playerNameWithPrefix = g.getChatPrefix() + pp.getName();
                    ProxyServer.getInstance().getPlayers().forEach(x -> {
                        User u = userCache.findById(x.getUniqueId()).orElse(null);
                        if (u != null) {
                            String quitMessage = Constants.PREFIX + String.format(MlPlayerAction.get(u.getLanguage().toString()).join(), playerNameWithPrefix);
                            BaseComponent[] joinMessageComponents = tc(quitMessage);
                            x.sendMessage(joinMessageComponents);
                        }
                    });
                } else {
                    user.setSpectatedUser(pp.getUniqueId());
                    user.setServerBeforeSpectating(pp.getServer().getInfo().getName());
                    userCache.save(user);
                    sendToServer(pp.getServer().getInfo(), "RequestCurrentSpecLocation", pp.getUniqueId().toString());

                    Group g = groupCache.findById(user.getGroupId()).orElse(groupCache.getDefaultGroup().orElseThrow(() -> new CachingException("No default group defined!")));
                    String playerNameWithPrefix = g.getChatPrefix() + pp.getName();
                    ProxyServer.getInstance().getPlayers().forEach(x -> {
                        User u = userCache.findById(x.getUniqueId()).orElse(null);
                        if (u != null) {
                            String quitMessage = Constants.PREFIX + String.format(MlPlayerAction.get(u.getLanguage().toString()).quit(), playerNameWithPrefix);
                            BaseComponent[] joinMessageComponents = tc(quitMessage);
                            x.sendMessage(joinMessageComponents);
                        }
                    });
                }
            } else {
                if (user.getSpectatedUser() != null) {
                    User targetUser = userCache.findById(user.getSpectatedUser()).orElse(null);
                    if (targetUser != null) {
                        pp.sendMessage(tc(Constants.PREFIX + String.format(ml.alreadySpectating(), targetUser.getUsername())));
                        return;
                    }
                }
                String target = args[0];
                User targetUser = userCache.findByUsername(target).orElse(null);
                if (targetUser == null) {
                    pp.sendMessage(tc(Constants.PREFIX + String.format(ml.notOnline(), target)));
                    return;
                }
                ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(targetUser.getId());
                if (targetPlayer == null) {
                    pp.sendMessage(tc(Constants.PREFIX + String.format(ml.notOnline(), target)));
                    return;
                }
                if (targetUser.getSpectatedUser() != null) {
                    pp.sendMessage(tc(Constants.PREFIX + String.format(ml.alreadySpectatingAnotherPlayer(), targetUser.getUsername())));
                    return;
                }
                if (!targetPlayer.getUniqueId().equals(pp.getUniqueId()) && targetPlayer.hasPermission("wp.spectate")) {
                    pp.sendMessage(tc(Constants.PREFIX + String.format(ml.cannotBeSpectated(), targetUser.getUsername())));
                    return;
                }
                user.setSpectatedUser(targetUser.getId());
                user.setServerBeforeSpectating(pp.getServer().getInfo().getName());
                userCache.save(user);
                sendToServer(pp.getServer().getInfo(), "RequestCurrentSpecLocation", pp.getUniqueId().toString());

                Group g = groupCache.findById(user.getGroupId()).orElse(groupCache.getDefaultGroup().orElseThrow(() -> new CachingException("No default group defined!")));
                String playerNameWithPrefix = g.getChatPrefix() + pp.getName();
                ProxyServer.getInstance().getPlayers().forEach(x -> {
                    User u = userCache.findById(x.getUniqueId()).orElse(null);
                    if (u != null) {
                        String quitMessage = Constants.PREFIX + String.format(MlPlayerAction.get(u.getLanguage().toString()).quit(), playerNameWithPrefix);
                        BaseComponent[] joinMessageComponents = tc(quitMessage);
                        x.sendMessage(joinMessageComponents);
                    }
                });
            }
        }
    }
}
