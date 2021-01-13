package de.alewu.wpbc.commands;

import static de.alewu.wpbc.util.StaticMethodCollection.sendToServer;
import static de.alewu.wpbc.util.StaticMethodCollection.tc;

import de.alewu.coreapi.db.caching.CacheRegistry;
import de.alewu.coreapi.exception.CachingException;
import de.alewu.wpbc.WinterProjectBungeeCore;
import de.alewu.wpbc.files.Files;
import de.alewu.wpbc.ml.MlGeneral;
import de.alewu.wpbc.util.Constants;
import de.alewu.wpbc.util.StaticMethodCollection;
import de.alewu.wpc.repository.cache.UserCache;
import de.alewu.wpc.repository.entity.User;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandNether extends Command {

    private final UserCache userCache;

    public CommandNether() {
        super("nether");
        userCache = CacheRegistry.getCache(UserCache.class).orElseThrow(() -> new CachingException("userCache not registered"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer pp = (ProxiedPlayer) sender;
            if (pp.getServer().getInfo().getName().equalsIgnoreCase(Files.GLOBAL_CONFIG.getQueueServer())) {
                return;
            }
            User user = userCache.findById(pp.getUniqueId())
                .orElse(null);
            if (user == null) {
                sender.sendMessage(tc(Constants.PREFIX + String.format(MlGeneral.get("de").noCacheObjectFound(), User.class.getSimpleName())));
                return;
            }
            MlGeneral ml = MlGeneral.get(user.getLanguage().toString());
            if (args.length == 0) {
                if (!pp.getServer().getInfo().getName().equals(Files.GLOBAL_CONFIG.getFarmServer1())
                    && !pp.getServer().getInfo().getName().equals(Files.GLOBAL_CONFIG.getFarmServer2())) {
                    String farmServer = Files.GLOBAL_CONFIG.getRandomFarmServer();
                    sendToServer(sender, pp, ml, true, farmServer);
                } else {
                    sendToServer(sender, pp, ml, true, pp.getServer().getInfo().getName());
                }
            } else {
                try {
                    int server = Integer.parseInt(args[0]);
                    if (server == 1) {
                        String farmServer = Files.GLOBAL_CONFIG.getFarmServer1();
                        sendToServer(sender, pp, ml, false, farmServer);
                    } else if (server == 2) {
                        String farmServer = Files.GLOBAL_CONFIG.getFarmServer2();
                        sendToServer(sender, pp, ml, false, farmServer);
                    } else {
                        pp.sendMessage(tc(Constants.PREFIX + "§c/nether [1/2]"));
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage(tc(Constants.PREFIX + String.format(ml.invalidValue(), args[0])));
                }
            }
        }
    }

    private void sendToServer(CommandSender sender, ProxiedPlayer pp, MlGeneral ml, boolean random, String farmServer) {
        String displayNameServer = Files.GLOBAL_CONFIG.getDisplayName(farmServer);
        ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(farmServer);
        if (serverInfo == null) {
            sender.sendMessage(tc(Constants.PREFIX + String.format(ml.notOnline(), displayNameServer)));
            return;
        }
        boolean changedServer = false;
        if (random) {
            if (!pp.getServer().getInfo().getName().equals(Files.GLOBAL_CONFIG.getFarmServer1())
                && !pp.getServer().getInfo().getName().equals(Files.GLOBAL_CONFIG.getFarmServer2())) {
                pp.connect(serverInfo);
                sender.sendMessage(tc(Constants.PREFIX + String.format(ml.connecting(), displayNameServer)));
                changedServer = true;
            }
        } else {
            if (!pp.getServer().getInfo().getName().equals(farmServer)) {
                pp.connect(serverInfo);
                sender.sendMessage(tc(Constants.PREFIX + String.format(ml.connecting(), displayNameServer)));
                changedServer = true;
            }
        }
        if (changedServer) {
            ProxyServer.getInstance().getScheduler().schedule(WinterProjectBungeeCore.INSTANCE, () ->
                StaticMethodCollection.sendToServer(serverInfo, "TeleportToNetherSpawn", ((ProxiedPlayer) sender).getUniqueId().toString()), 1, TimeUnit.SECONDS);
        } else {
            StaticMethodCollection.sendToServer(serverInfo, "TeleportToNetherSpawn", ((ProxiedPlayer) sender).getUniqueId().toString());
            pp.sendMessage(tc(Constants.PREFIX + ml.netherFarm()));
        }
    }
}
