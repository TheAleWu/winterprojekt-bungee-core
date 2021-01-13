package de.alewu.wpbc.commands;

import static de.alewu.wpbc.util.StaticMethodCollection.tc;

import de.alewu.coreapi.db.caching.CacheRegistry;
import de.alewu.coreapi.exception.CachingException;
import de.alewu.wpbc.files.Files;
import de.alewu.wpbc.ml.MlGeneral;
import de.alewu.wpbc.ml.cmds.MlCommandGlobalConfig;
import de.alewu.wpbc.util.Constants;
import de.alewu.wpc.repository.cache.UserCache;
import de.alewu.wpc.repository.entity.User;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandGlobalConfig extends Command {

    private final UserCache userCache;

    public CommandGlobalConfig() {
        super("globalconfig", "wp.globalconfig", "gc");
        this.userCache = CacheRegistry.getCache(UserCache.class)
            .orElseThrow(() -> new CachingException("userCache not registered"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            User user = userCache.findById(((ProxiedPlayer) sender).getUniqueId())
                .orElse(null);
            if (user == null) {
                sender.sendMessage(tc(Constants.PREFIX + String.format(MlGeneral.get("de").noCacheObjectFound(), User.class.getSimpleName())));
                return;
            }
            execute(sender, args, user.getLanguage().toString());
        } else {
            execute(sender, args, "de");
        }
    }

    private void execute(CommandSender sender, String[] args, String lang) {
        MlGeneral mlGeneral = MlGeneral.get(lang);
        MlCommandGlobalConfig mlCommandGlobalConfig = MlCommandGlobalConfig.get(lang);
        if (args.length == 0) {
            wrongUsage(sender, mlGeneral);
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("help")) {
                sendHelp(sender, lang);
            } else if (args[0].equalsIgnoreCase("cps")) {
                Map<String, String> entries = Files.GLOBAL_CONFIG.getChatPrefixedServers();
                if (entries.isEmpty()) {
                    sender.sendMessage(tc(Constants.PREFIX + mlCommandGlobalConfig.cpsNoEntries()));
                    return;
                }
                for (Entry<String, String> entry : entries.entrySet()) {
                    sender.sendMessage(tc(Constants.PREFIX + "§8- §7" + entry.getKey() + " §8[§6" + entry.getValue() + "§8]"));
                }
            } else if (args[0].equalsIgnoreCase("queue")) {
                sender.sendMessage(tc(Constants.PREFIX + "§8» §6" + Files.GLOBAL_CONFIG.getQueueServer()));
            } else if (args[0].equalsIgnoreCase("first")) {
                sender.sendMessage(tc(Constants.PREFIX + "§8» §6" + Files.GLOBAL_CONFIG.getFirstServer()));
            } else if (args[0].equalsIgnoreCase("farm1")) {
                sender.sendMessage(tc(Constants.PREFIX + "§8» §6" + Files.GLOBAL_CONFIG.getFarmServer1()));
            } else if (args[0].equalsIgnoreCase("farm2")) {
                sender.sendMessage(tc(Constants.PREFIX + "§8» §6" + Files.GLOBAL_CONFIG.getFarmServer2()));
            } else if (args[0].equalsIgnoreCase("build")) {
                sender.sendMessage(tc(Constants.PREFIX + "§8» §6" + Files.GLOBAL_CONFIG.getBuildServer()));
            } else if (args[0].equalsIgnoreCase("mpmsg")) {
                sender.sendMessage(tc(Constants.PREFIX + "§8» §6" + Files.GLOBAL_CONFIG.getMaintenancePingMessage()));
            } else if (args[0].equalsIgnoreCase("mbypass")) {
                for (UUID uuid : Files.GLOBAL_CONFIG.getMaintenanceBypassed()) {
                    User user = userCache.findById(uuid).orElse(null);
                    sender.sendMessage(tc(Constants.PREFIX + "§8» §6" + (user != null ? user.getUsername() : "? (" + uuid + ")")));
                }
            } else {
                wrongUsage(sender, mlGeneral);
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("cpsrem")) {
                if (Files.GLOBAL_CONFIG.getDisplayName(args[1]) == null) {
                    sender.sendMessage(tc(Constants.PREFIX + String.format(mlCommandGlobalConfig.cpsRemNotExists(), args[1])));
                    return;
                }
                Files.GLOBAL_CONFIG.removeChatPrefixedServer(args[1]);
                sender.sendMessage(tc(Constants.PREFIX + String.format(mlCommandGlobalConfig.cpsRemSuccess(), args[1])));
            } else if (args[0].equalsIgnoreCase("queue")) {
                String server = args[1];
                if (ProxyServer.getInstance().getServerInfo(server) == null) {
                    sender.sendMessage(tc(Constants.PREFIX + String.format(mlCommandGlobalConfig.serverUnknown(), server)));
                    return;
                }
                Files.GLOBAL_CONFIG.setQueueServer(server);
                sender.sendMessage(tc(Constants.PREFIX + String.format(mlCommandGlobalConfig.queueSetSuccess(), server)));
            } else if (args[0].equalsIgnoreCase("first")) {
                String server = args[1];
                if (ProxyServer.getInstance().getServerInfo(server) == null) {
                    sender.sendMessage(tc(Constants.PREFIX + String.format(mlCommandGlobalConfig.serverUnknown(), server)));
                    return;
                }
                Files.GLOBAL_CONFIG.setFirstServer(server);
                sender.sendMessage(tc(Constants.PREFIX + String.format(mlCommandGlobalConfig.helpFirstSet(), server)));
            } else if (args[0].equalsIgnoreCase("farm1")) {
                String server = args[1];
                if (ProxyServer.getInstance().getServerInfo(server) == null) {
                    sender.sendMessage(tc(Constants.PREFIX + String.format(mlCommandGlobalConfig.serverUnknown(), server)));
                    return;
                }
                Files.GLOBAL_CONFIG.setFarmServer1(server);
                sender.sendMessage(tc(Constants.PREFIX + String.format(mlCommandGlobalConfig.helpFarmSet(), server)));
            } else if (args[0].equalsIgnoreCase("farm2")) {
                String server = args[1];
                if (ProxyServer.getInstance().getServerInfo(server) == null) {
                    sender.sendMessage(tc(Constants.PREFIX + String.format(mlCommandGlobalConfig.serverUnknown(), server)));
                    return;
                }
                Files.GLOBAL_CONFIG.setFarmServer2(server);
                sender.sendMessage(tc(Constants.PREFIX + String.format(mlCommandGlobalConfig.helpFarmSet(), server)));
            } else if (args[0].equalsIgnoreCase("build")) {
                String server = args[1];
                if (ProxyServer.getInstance().getServerInfo(server) == null) {
                    sender.sendMessage(tc(Constants.PREFIX + String.format(mlCommandGlobalConfig.serverUnknown(), server)));
                    return;
                }
                Files.GLOBAL_CONFIG.setBuildServer(server);
                sender.sendMessage(tc(Constants.PREFIX + String.format(mlCommandGlobalConfig.helpBuildSet(), server)));
            } else if (args[0].equalsIgnoreCase("mpmsg")) {
                String message = args[1];
                Files.GLOBAL_CONFIG.setMaintenancePingMessage(ChatColor.translateAlternateColorCodes('&', message));
                sender.sendMessage(tc(Constants.PREFIX + String.format(mlCommandGlobalConfig.helpMaintenancePingMessageSet(), message)));
            } else if (args[0].equalsIgnoreCase("mbypass")) {
                String username = args[1];
                User user = userCache.findByUsername(username).orElse(null);
                if (user == null) {
                    sender.sendMessage(tc(Constants.PREFIX + String.format(MlGeneral.get("de").neverPlayed(), username)));
                    return;
                }
                UUID uuid = user.getId();
                List<UUID> bypassed = Files.GLOBAL_CONFIG.getMaintenanceBypassed();
                if (!bypassed.contains(uuid)) {
                    bypassed.add(uuid);
                    sender.sendMessage(tc(Constants.PREFIX + String.format(mlCommandGlobalConfig.helpMaintenanceBypassedSetAdded(), user.getUsername())));
                } else {
                    bypassed.remove(uuid);
                    sender.sendMessage(tc(Constants.PREFIX + String.format(mlCommandGlobalConfig.helpMaintenanceBypassedSetRemoved(), user.getUsername())));
                }
                Files.GLOBAL_CONFIG.setMaintenanceBypassed(bypassed);
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("cpsadd")) {
                Files.GLOBAL_CONFIG.setChatPrefixedServer(args[1], args[2]);
                sender.sendMessage(tc(Constants.PREFIX + String.format(mlCommandGlobalConfig.cpsAddUpdated(), args[1], args[2])));
            }
        }
    }

    private void wrongUsage(CommandSender sender, MlGeneral mlGeneral) {
        sender.sendMessage(tc(Constants.PREFIX + String.format(mlGeneral.wrongUsage(), "/gc help")));
    }

    private void sendHelp(CommandSender sender, String lang) {
        MlCommandGlobalConfig mlCommandGlobalConfig = MlCommandGlobalConfig.get(lang);
        sender.sendMessage(tc(Constants.PREFIX + "§8--- " + mlCommandGlobalConfig.helpHeader() + " §8---"));
        sender.sendMessage(tc(Constants.PREFIX + "§6/gc help"));
        sender.sendMessage(tc(Constants.PREFIX + mlCommandGlobalConfig.helpHelp()));
        sender.sendMessage(tc(Constants.PREFIX + "§6/gc cps"));
        sender.sendMessage(tc(Constants.PREFIX + mlCommandGlobalConfig.helpCps()));
        sender.sendMessage(tc(Constants.PREFIX + "§6/gc cpsadd <server> <displayName>"));
        sender.sendMessage(tc(Constants.PREFIX + mlCommandGlobalConfig.helpCpsAdd()));
        sender.sendMessage(tc(Constants.PREFIX + "§6/gc cpsrem <server>"));
        sender.sendMessage(tc(Constants.PREFIX + mlCommandGlobalConfig.helpCpsRem()));
        sender.sendMessage(tc(Constants.PREFIX + "§6/gc queue"));
        sender.sendMessage(tc(Constants.PREFIX + mlCommandGlobalConfig.helpQueue()));
        sender.sendMessage(tc(Constants.PREFIX + "§6/gc queue <server>"));
        sender.sendMessage(tc(Constants.PREFIX + mlCommandGlobalConfig.helpQueueSet()));
        sender.sendMessage(tc(Constants.PREFIX + "§6/gc farm1"));
        sender.sendMessage(tc(Constants.PREFIX + mlCommandGlobalConfig.helpFarm()));
        sender.sendMessage(tc(Constants.PREFIX + "§6/gc farm1 <server>"));
        sender.sendMessage(tc(Constants.PREFIX + mlCommandGlobalConfig.helpFarmSet()));
        sender.sendMessage(tc(Constants.PREFIX + "§6/gc farm2"));
        sender.sendMessage(tc(Constants.PREFIX + mlCommandGlobalConfig.helpFarm()));
        sender.sendMessage(tc(Constants.PREFIX + "§6/gc farm2 <server>"));
        sender.sendMessage(tc(Constants.PREFIX + mlCommandGlobalConfig.helpFarmSet()));
        sender.sendMessage(tc(Constants.PREFIX + "§6/gc build"));
        sender.sendMessage(tc(Constants.PREFIX + mlCommandGlobalConfig.helpBuild()));
        sender.sendMessage(tc(Constants.PREFIX + "§6/gc build <server>"));
        sender.sendMessage(tc(Constants.PREFIX + mlCommandGlobalConfig.helpBuildSet()));
        sender.sendMessage(tc(Constants.PREFIX + "§6/gc mpmsg"));
        sender.sendMessage(tc(Constants.PREFIX + mlCommandGlobalConfig.helpMaintenancePingMessageGet()));
        sender.sendMessage(tc(Constants.PREFIX + "§6/gc mpmsg <message>"));
        sender.sendMessage(tc(Constants.PREFIX + mlCommandGlobalConfig.helpMaintenancePingMessageSet()));
        sender.sendMessage(tc(Constants.PREFIX + "§6/gc mbypass"));
        sender.sendMessage(tc(Constants.PREFIX + mlCommandGlobalConfig.helpMaintenanceBypassedGet()));
        sender.sendMessage(tc(Constants.PREFIX + "§6/gc mbypass <username>"));
        sender.sendMessage(tc(Constants.PREFIX + mlCommandGlobalConfig.helpMaintenanceBypassedSet()));
        sender.sendMessage(tc(Constants.PREFIX + "§8--- " + mlCommandGlobalConfig.helpHeader() + " §8---"));
    }
}
