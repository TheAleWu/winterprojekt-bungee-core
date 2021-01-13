package de.alewu.wpbc.commands;

import static de.alewu.wpbc.util.StaticMethodCollection.tc;

import de.alewu.coreapi.db.caching.CacheRegistry;
import de.alewu.coreapi.exception.CachingException;
import de.alewu.wpbc.files.Files;
import de.alewu.wpbc.util.PlayerCapabilities;
import de.alewu.wpbc.util.PlayerCapabilities.Capabilities;
import de.alewu.wpc.repository.cache.GroupCache;
import de.alewu.wpc.repository.cache.UserCache;
import de.alewu.wpc.repository.entity.Group;
import de.alewu.wpc.repository.entity.User;
import java.util.StringJoiner;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandList extends Command {

    private final UserCache userCache;
    private final GroupCache groupCache;

    public CommandList() {
        super("list", null, "glist");
        this.userCache = CacheRegistry.getCache(UserCache.class)
            .orElseThrow(() -> new CachingException("userCache not registered"));
        this.groupCache = CacheRegistry.getCache(GroupCache.class)
            .orElseThrow(() -> new CachingException("groupCache not registered"));
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (commandSender instanceof ProxiedPlayer) {
            ProxiedPlayer pp = (ProxiedPlayer) commandSender;
            ServerInfo build = ProxyServer.getInstance().getServerInfo(Files.GLOBAL_CONFIG.getBuildServer());
            ServerInfo farm1 = ProxyServer.getInstance().getServerInfo(Files.GLOBAL_CONFIG.getFarmServer1());
            ServerInfo farm2 = ProxyServer.getInstance().getServerInfo(Files.GLOBAL_CONFIG.getFarmServer2());

            pp.sendMessage(tc("§8======================================"));
            int buildPlayers = 0;
            StringJoiner buildSj = new StringJoiner("§8, ");
            for (ProxiedPlayer online : build.getPlayers()) {
                Capabilities capabilities = PlayerCapabilities.getCapabilities(online.getUniqueId());
                if (capabilities != null && "SPECTATOR".equalsIgnoreCase(capabilities.getGameMode())) {
                    continue;
                }
                User user = userCache.findById(online.getUniqueId()).orElse(null);
                if (user != null) {
                    if (user.getSpectatedUser() == null) {
                        buildPlayers++;
                        Group g;
                        if (user.isRankToggled()) {
                            g = groupCache.getDefaultGroup().orElse(null);
                        } else {
                            g = groupCache.findById(user.getGroupId()).orElse(null);
                        }
                        if (g != null) {
                            buildSj.add(g.getChatPrefix() + online.getName());
                        } else {
                            buildSj.add("§7" + online.getName());
                        }
                    }
                } else {
                    buildSj.add("§7" + online.getName());
                }
            }
            pp.sendMessage(tc("§6" + Files.GLOBAL_CONFIG.getDisplayName(build.getName()) + " §7(" + buildPlayers + ")§6:"));
            pp.sendMessage(tc(buildSj.toString()));
            pp.sendMessage(tc("§8======================================"));
            int farm1Players = 0;
            StringJoiner farm1Sj = new StringJoiner("§8, ");
            for (ProxiedPlayer online : farm1.getPlayers()) {
                Capabilities capabilities = PlayerCapabilities.getCapabilities(online.getUniqueId());
                if (capabilities != null && "SPECTATOR".equalsIgnoreCase(capabilities.getGameMode())) {
                    continue;
                }
                User user = userCache.findById(online.getUniqueId()).orElse(null);
                if (user != null) {
                    if (user.getSpectatedUser() == null) {
                        farm1Players++;
                        Group g = groupCache.findById(user.getGroupId()).orElse(null);
                        if (g != null) {
                            farm1Sj.add(g.getChatPrefix() + online.getName());
                        } else {
                            farm1Sj.add("§7" + online.getName());
                        }
                    }
                } else {
                    farm1Sj.add("§7" + online.getName());
                }
            }
            pp.sendMessage(tc("§6" + Files.GLOBAL_CONFIG.getDisplayName(farm1.getName()) + " §7(" + farm1Players + ")§6:"));
            pp.sendMessage(tc(farm1Sj.toString()));
            pp.sendMessage(tc("§8======================================"));
            int farm2Players = 0;
            StringJoiner farm2Sj = new StringJoiner("§8, ");
            for (ProxiedPlayer online : farm2.getPlayers()) {
                Capabilities capabilities = PlayerCapabilities.getCapabilities(online.getUniqueId());
                if (capabilities != null && "SPECTATOR".equalsIgnoreCase(capabilities.getGameMode())) {
                    continue;
                }
                User user = userCache.findById(online.getUniqueId()).orElse(null);
                if (user != null) {
                    if (user.getSpectatedUser() == null) {
                        String suffix = "";
                        farm2Players++;
                        Group g = groupCache.findById(user.getGroupId()).orElse(null);
                        if (g != null) {
                            farm2Sj.add(g.getChatPrefix() + online.getName() + suffix);
                        } else {
                            farm2Sj.add("§7" + online.getName() + suffix);
                        }
                    }
                } else {
                    farm2Sj.add("§7" + online.getName());
                }
            }
            pp.sendMessage(tc("§6" + Files.GLOBAL_CONFIG.getDisplayName(farm2.getName()) + " §7(" + farm2Players + ")§6:"));
            pp.sendMessage(tc(farm2Sj.toString()));
            pp.sendMessage(tc("§8======================================"));
            pp.sendMessage(tc("§2∑ Online: §7" + (buildPlayers + farm1Players + farm2Players)));
        }
    }

}
