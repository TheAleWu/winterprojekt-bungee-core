package de.alewu.wpbc;

import static de.alewu.wpbc.util.StaticMethodCollection.sendToServers;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.alewu.coreapi.CoreAPI;
import de.alewu.coreapi.db.caching.Cache;
import de.alewu.coreapi.db.caching.CacheRegistry;
import de.alewu.coreapi.db.caching.CacheUpdateBroadcaster;
import de.alewu.coreapi.exception.CachingException;
import de.alewu.wpbc.cache.RefreshDatabaseAction;
import de.alewu.wpbc.cache.UpdateToDatabaseAction;
import de.alewu.wpbc.commands.CommandAdminBan;
import de.alewu.wpbc.commands.CommandAdminKick;
import de.alewu.wpbc.commands.CommandAdminMute;
import de.alewu.wpbc.commands.CommandBan;
import de.alewu.wpbc.commands.CommandBanTemplate;
import de.alewu.wpbc.commands.CommandBans;
import de.alewu.wpbc.commands.CommandCreateKey;
import de.alewu.wpbc.commands.CommandDeathTop;
import de.alewu.wpbc.commands.CommandDeaths;
import de.alewu.wpbc.commands.CommandDsgvo;
import de.alewu.wpbc.commands.CommandFarmwelt;
import de.alewu.wpbc.commands.CommandGameKey;
import de.alewu.wpbc.commands.CommandGlobalConfig;
import de.alewu.wpbc.commands.CommandGroup;
import de.alewu.wpbc.commands.CommandHelp;
import de.alewu.wpbc.commands.CommandKick;
import de.alewu.wpbc.commands.CommandLanguage;
import de.alewu.wpbc.commands.CommandList;
import de.alewu.wpbc.commands.CommandMaintenance;
import de.alewu.wpbc.commands.CommandMessage;
import de.alewu.wpbc.commands.CommandMute;
import de.alewu.wpbc.commands.CommandNether;
import de.alewu.wpbc.commands.CommandNotify;
import de.alewu.wpbc.commands.CommandOnTime;
import de.alewu.wpbc.commands.CommandPing;
import de.alewu.wpbc.commands.CommandRang;
import de.alewu.wpbc.commands.CommandReply;
import de.alewu.wpbc.commands.CommandShowKeys;
import de.alewu.wpbc.commands.CommandSocialSpy;
import de.alewu.wpbc.commands.CommandSpectate;
import de.alewu.wpbc.commands.CommandTeamchat;
import de.alewu.wpbc.commands.CommandTeleport;
import de.alewu.wpbc.commands.CommandToggleMessages;
import de.alewu.wpbc.commands.CommandToggleRank;
import de.alewu.wpbc.commands.CommandTpa;
import de.alewu.wpbc.commands.CommandTpaccept;
import de.alewu.wpbc.commands.CommandTpdeny;
import de.alewu.wpbc.commands.CommandTwitch;
import de.alewu.wpbc.commands.CommandUnban;
import de.alewu.wpbc.commands.CommandUnmute;
import de.alewu.wpbc.eventactions.UserCacheUpdateEventAction;
import de.alewu.wpbc.listener.ListenerChat;
import de.alewu.wpbc.listener.ListenerPlayerDisconnect;
import de.alewu.wpbc.listener.ListenerPluginMessage;
import de.alewu.wpbc.listener.ListenerPostLogin;
import de.alewu.wpbc.listener.ListenerProxyPing;
import de.alewu.wpbc.listener.ListenerServerConnect;
import de.alewu.wpbc.listener.ListenerServerSwitch;
import de.alewu.wpbc.ml.MlGeneral;
import de.alewu.wpbc.ml.MlPlayerAction;
import de.alewu.wpbc.ml.cmds.MlCommandCreateKey;
import de.alewu.wpbc.ml.cmds.MlCommandGameKey;
import de.alewu.wpbc.ml.cmds.MlCommandGlobalConfig;
import de.alewu.wpbc.ml.cmds.MlCommandGroup;
import de.alewu.wpbc.ml.cmds.MlCommandHelp;
import de.alewu.wpbc.ml.cmds.MlCommandMaintenance;
import de.alewu.wpbc.ml.cmds.MlCommandMessage;
import de.alewu.wpbc.ml.cmds.MlCommandShowKeys;
import de.alewu.wpbc.ml.cmds.MlCommandSocialSpy;
import de.alewu.wpbc.ml.cmds.MlCommandSpectate;
import de.alewu.wpbc.ml.cmds.MlCommandToggleRank;
import de.alewu.wpbc.ml.cmds.MlCommandTpa;
import de.alewu.wpbc.ml.cmds.MlCommandTwitch;
import de.alewu.wpbc.ml.cmds.MlPunishments;
import de.alewu.wpbc.queue.QueueHandler;
import de.alewu.wpc.WinterProjectCore;
import de.alewu.wpc.repository.cache.UserCache;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

public class WinterProjectBungeeCore extends Plugin {

    public static CoreAPI coreAPI = WinterProjectCore.getCoreAPI();
    public static WinterProjectBungeeCore INSTANCE;
    private PluginManager pm;

    @Override
    public void onEnable() {
        INSTANCE = this;
        WinterProjectCore.initialize(new RefreshDatabaseAction(), new UpdateToDatabaseAction());
        coreAPI = WinterProjectCore.getCoreAPI();
        registerTranslationClasses();

        pm = getProxy().getPluginManager();
        registerListener();
        registerCommands();

        ProxyServer.getInstance().registerChannel("winterproject:proxyinstruction");
        CacheRegistry.getCaches().forEach(Cache::refreshCache);
        CacheUpdateBroadcaster.setBroadcastFunction(json -> sendToServers(ProxyServer.getInstance().getServers().values(), "CacheUpdate", json));
        addCacheUpdateEventActions();
        startPlayerRefreshing();
        QueueHandler.init();
//        ServerInfo farmServer1 = ProxyServer.getInstance().getServerInfo(Files.GLOBAL_CONFIG.getFarmServer1());
//        if (farmServer1 != null) {
//            sendToServer(farmServer1, "RequestNextReset");
//        }
//        ServerInfo farmServer2 = ProxyServer.getInstance().getServerInfo(Files.GLOBAL_CONFIG.getFarmServer2());
//        if (farmServer2 != null) {
//            sendToServer(farmServer2, "RequestNextReset");
//        }

        ProxyServer.getInstance().getScheduler().schedule(this, () -> {
            try {
                CacheRegistry.pushToDatabase();
                coreAPI.database().cleanupMemory();
            } catch (Exception e) {
                ProxyServer.getInstance().getLogger().log(Level.SEVERE, "Error while doing database tasks", e);
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    private void registerTranslationClasses() {
        coreAPI.translations().registerTranslationClass(MlPlayerAction.class);
        coreAPI.translations().registerTranslationClass(MlGeneral.class);
        coreAPI.translations().registerTranslationClass(MlCommandGroup.class);
        coreAPI.translations().registerTranslationClass(MlCommandGlobalConfig.class);
        coreAPI.translations().registerTranslationClass(MlCommandTwitch.class);
        coreAPI.translations().registerTranslationClass(MlCommandSpectate.class);
        coreAPI.translations().registerTranslationClass(MlCommandGameKey.class);
        coreAPI.translations().registerTranslationClass(MlCommandCreateKey.class);
        coreAPI.translations().registerTranslationClass(MlCommandShowKeys.class);
        coreAPI.translations().registerTranslationClass(MlPunishments.class);
        coreAPI.translations().registerTranslationClass(MlCommandMessage.class);
        coreAPI.translations().registerTranslationClass(MlCommandToggleRank.class);
        coreAPI.translations().registerTranslationClass(MlCommandHelp.class);
        coreAPI.translations().registerTranslationClass(MlCommandTpa.class);
        coreAPI.translations().registerTranslationClass(MlCommandMaintenance.class);
        coreAPI.translations().registerTranslationClass(MlCommandSocialSpy.class);
    }

    private void registerListener() {
        pm.registerListener(this, new ListenerPluginMessage());
        pm.registerListener(this, new ListenerPostLogin());
        pm.registerListener(this, new ListenerPlayerDisconnect());
        pm.registerListener(this, new ListenerChat());
        pm.registerListener(this, new ListenerServerSwitch());
        pm.registerListener(this, new ListenerServerConnect());
        pm.registerListener(this, new ListenerProxyPing());
    }

    private void registerCommands() {
        pm.registerCommand(this, new CommandGroup());
        pm.registerCommand(this, new CommandGlobalConfig());
        pm.registerCommand(this, new CommandTwitch());
//        pm.registerCommand(this, new CommandSpectate());
        pm.registerCommand(this, new CommandGameKey());
        pm.registerCommand(this, new CommandCreateKey());
        pm.registerCommand(this, new CommandShowKeys());
        pm.registerCommand(this, new CommandBanTemplate());
        pm.registerCommand(this, new CommandBans());
        pm.registerCommand(this, new CommandKick());
        pm.registerCommand(this, new CommandMute());
        pm.registerCommand(this, new CommandBan());
        pm.registerCommand(this, new CommandUnmute());
        pm.registerCommand(this, new CommandUnban());
        pm.registerCommand(this, new CommandAdminKick());
        pm.registerCommand(this, new CommandAdminMute());
        pm.registerCommand(this, new CommandAdminBan());
        pm.registerCommand(this, new CommandFarmwelt());
        pm.registerCommand(this, new CommandLanguage());
        pm.registerCommand(this, new CommandTeamchat());
        pm.registerCommand(this, new CommandPing());
        pm.registerCommand(this, new CommandRang());
        pm.registerCommand(this, new CommandMessage());
        pm.registerCommand(this, new CommandToggleMessages());
        pm.registerCommand(this, new CommandToggleRank());
        pm.registerCommand(this, new CommandHelp());
        pm.registerCommand(this, new CommandTpa());
        pm.registerCommand(this, new CommandTpaccept());
        pm.registerCommand(this, new CommandTpdeny());
        pm.registerCommand(this, new CommandReply());
        pm.registerCommand(this, new CommandMaintenance());
        pm.registerCommand(this, new CommandNether());
        pm.registerCommand(this, new CommandSocialSpy());
        pm.registerCommand(this, new CommandDsgvo());
        pm.registerCommand(this, new CommandList());
        pm.registerCommand(this, new CommandOnTime());
        pm.registerCommand(this, new CommandNotify());
        pm.registerCommand(this, new CommandDeaths());
        pm.registerCommand(this, new CommandDeathTop());
        pm.registerCommand(this, new CommandTeleport());
    }

    private void addCacheUpdateEventActions() {
        UserCache userCache = CacheRegistry.getCache(UserCache.class)
            .orElseThrow(() -> new CachingException("userCache not registered"));
        userCache.addEventAction(new UserCacheUpdateEventAction());
    }

    @SuppressWarnings("UnstableApiUsage")
    private void startPlayerRefreshing() {
        ProxyServer.getInstance().getScheduler().schedule(this, () -> {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("RequestPlayers");
            Collection<ProxiedPlayer> proxiedPlayers = ProxyServer.getInstance().getPlayers();
            out.writeInt(proxiedPlayers.size());
            for (ProxiedPlayer online : proxiedPlayers) {
                out.writeUTF(online.getName());
            }
            ProxyServer.getInstance().getServers().values().forEach(si -> si.sendData("winterproject:proxyinstruction", out.toByteArray()));
        }, 0, 30, TimeUnit.SECONDS);
    }
}
