package de.alewu.wpbc.listener;

import static de.alewu.wpbc.util.StaticMethodCollection.sendToServer;
import static de.alewu.wpbc.util.StaticMethodCollection.tc;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.alewu.coreapi.db.caching.BroadcastType;
import de.alewu.coreapi.db.caching.Cachable;
import de.alewu.coreapi.db.caching.Cache;
import de.alewu.coreapi.db.caching.CacheRegistry;
import de.alewu.coreapi.db.caching.CacheUpdateReceiver;
import de.alewu.coreapi.exception.CachingException;
import de.alewu.coreapi.json.JsonConvert;
import de.alewu.coreapi.ml.Iso2CountryCode;
import de.alewu.wpbc.WinterProjectBungeeCore;
import de.alewu.wpbc.files.Files;
import de.alewu.wpbc.ml.MlGeneral;
import de.alewu.wpbc.ml.MlPlayerAction;
import de.alewu.wpbc.ml.cmds.MlCommandSpectate;
import de.alewu.wpbc.queue.QueueHandler;
import de.alewu.wpbc.util.Constants;
import de.alewu.wpbc.util.DeathMessageResolver;
import de.alewu.wpbc.util.PlayerCapabilities;
import de.alewu.wpbc.util.SpectateUtil;
import de.alewu.wpc.repository.cache.GroupCache;
import de.alewu.wpc.repository.cache.UserCache;
import de.alewu.wpc.repository.cache.UserHomeCache;
import de.alewu.wpc.repository.entity.Group;
import de.alewu.wpc.repository.entity.User;
import de.alewu.wpc.repository.entity.UserHome;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ListenerPluginMessage implements Listener {

    private static final List<UUID> CONNECTING_TO_RANDOM_SERVER = new ArrayList<>();
    private final UserCache userCache;
    private final UserHomeCache userHomeCache;
    private final GroupCache groupCache;

    public ListenerPluginMessage() {
        userCache = CacheRegistry.getCache(UserCache.class)
            .orElseThrow(() -> new CachingException("userCache not registered"));
        userHomeCache = CacheRegistry.getCache(UserHomeCache.class)
            .orElseThrow(() -> new CachingException("userHomeCache not registered"));
        groupCache = CacheRegistry.getCache(GroupCache.class)
            .orElseThrow(() -> new CachingException("groupCache not registered"));
    }

    @EventHandler
    @SuppressWarnings("UnstableApiUsage")
    public void on(PluginMessageEvent e) {
        Connection receiver = e.getReceiver();
        if (!(receiver instanceof ProxiedPlayer)) {
            return;
        }
        if (!e.getTag().equalsIgnoreCase("winterproject:proxyinstruction")) {
            return;
        }
        ProxiedPlayer pp = (ProxiedPlayer) receiver;
        ByteArrayDataInput in = ByteStreams.newDataInput(e.getData());
        String instructionName = in.readUTF();
        if ("CacheUpdate".equals(instructionName)) {
            String json = in.readUTF();
            ProxyServer.getInstance().getScheduler().runAsync(WinterProjectBungeeCore.INSTANCE, () -> CacheUpdateReceiver.receive(json));

            // Send to all servers
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("CacheUpdate");
            out.writeUTF(json);
            ProxyServer.getInstance().getServers().values().forEach(si -> {
                if (!si.getName().equalsIgnoreCase(pp.getServer().getInfo().getName())) {
                    si.sendData("winterproject:proxyinstruction", out.toByteArray());
                }
            });
        } else if ("RequestPlayers".equals(instructionName)) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("RequestPlayers");
            Collection<ProxiedPlayer> proxiedPlayers = ProxyServer.getInstance().getPlayers();
            out.writeInt(proxiedPlayers.size());
            for (ProxiedPlayer online : proxiedPlayers) {
                out.writeUTF(online.getName());
            }
            pp.getServer().sendData("winterproject:proxyinstruction", out.toByteArray());
        } else if ("RequestCache".equals(instructionName)) {
            String cacheId = in.readUTF();
            Optional<Cache<? extends Cachable<?>, ?>> opt = CacheRegistry.getCache(cacheId);
            if (opt.isPresent()) {
                for (Cachable<?> cachable : opt.get().getCache()) {
                    if (cachable instanceof JsonConvert) {
                        ByteArrayDataOutput out = ByteStreams.newDataOutput();
                        out.writeUTF("CacheUpdate");
                        String json = ((JsonConvert) cachable).convertToJson();
                        JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
                        obj.addProperty("?cacheId", cacheId);
                        obj.addProperty("?type", BroadcastType.SAVE.toString());
                        out.writeUTF(obj.toString());
                        pp.getServer().sendData("winterproject:proxyinstruction", out.toByteArray());
                    }
                }
            }
        } else if ("SetPlayerSpecLocation".equals(instructionName)) {
            String uuidString = in.readUTF();
            UUID uuid = UUID.fromString(uuidString);
            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(uuid);
            User user = userCache.findById(uuid).orElse(null);
            if (target == null) {
                if (user != null) {
                    user.setSpectatedUser(null);
                    userCache.save(user);
                }
                return;
            }
            if (user == null) {
                target.sendMessage(tc(Constants.PREFIX + String.format(MlGeneral.get("de").noCacheObjectFound(), User.class.getSimpleName())));
                return;
            }
            ProxiedPlayer spectated = ProxyServer.getInstance().getPlayer(user.getSpectatedUser());
            if (spectated == null) {
                return;
            }
            MlCommandSpectate ml = MlCommandSpectate.get(user.getLanguage().toString());
            target.sendMessage(tc(Constants.PREFIX + String.format(ml.nowSpectating(), spectated.getName())));
            SpectateUtil.followToServer(pp, spectated, user);
        } else if ("ConnectToRandomServer".equals(instructionName)) {
            String uuidString = in.readUTF();
            UUID uuid = UUID.fromString(uuidString);
            if (CONNECTING_TO_RANDOM_SERVER.contains(uuid)) {
                return;
            }
            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(uuid);
            if (target != null) {
                CONNECTING_TO_RANDOM_SERVER.add(uuid);
                List<ServerInfo> servers = new ArrayList<>(ProxyServer.getInstance().getServers().values());
                servers.removeIf(si -> si.getName().equalsIgnoreCase(Files.GLOBAL_CONFIG.getQueueServer()));
                if (servers.isEmpty()) {
                    CONNECTING_TO_RANDOM_SERVER.remove(uuid);
                    return;
                }
                int index = 0;
                User user = userCache.findById(uuid).orElse(null);
                Iso2CountryCode lang = user != null ? user.getLanguage() : new Iso2CountryCode("de");
                MlGeneral ml = MlGeneral.get(lang.toString());
                tryNextServer(pp, ml, index, servers);
            }
        } else if ("SpawnCommand".equals(instructionName)) {
            String uuidString = in.readUTF();
            UUID uuid = UUID.fromString(uuidString);
            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(uuid);
            User user = userCache.findById(uuid).orElse(null);
            if (target != null && user != null) {
                ServerInfo info = ProxyServer.getInstance().getServerInfo(Files.GLOBAL_CONFIG.getBuildServer());
                Iso2CountryCode lang = user.getLanguage();
                MlGeneral ml = MlGeneral.get(lang.toString());
                if (!target.getServer().getInfo().getName().equalsIgnoreCase(Files.GLOBAL_CONFIG.getBuildServer())) {
                    target.connect(info);
                    ProxyServer.getInstance().getScheduler().schedule(WinterProjectBungeeCore.INSTANCE,
                        () -> sendToServer(info, "TeleportToSpawn", uuidString), 1, TimeUnit.SECONDS);
                    target.sendMessage(tc(Constants.PREFIX + String.format(ml.connecting(), Files.GLOBAL_CONFIG.getDisplayName(info.getName()))));
                } else {
                    sendToServer(info, "TeleportToSpawn", uuidString);
                    target.sendMessage(tc(Constants.PREFIX + String.format(ml.teleportToSpawn(), Files.GLOBAL_CONFIG.getDisplayName(info.getName()))));
                }
            }
        } else if ("SpawnCommandRequest".equals(instructionName)) {
            String uuidString = in.readUTF();
            UUID uuid = UUID.fromString(uuidString);
            ProxiedPlayer requestor = ProxyServer.getInstance().getPlayer(uuid);
            sendToServer(requestor.getServer().getInfo(), "SpawnCommandResponse", uuidString, "allow");
//            if (requestor.getServer().getInfo().getName().equals(Files.GLOBAL_CONFIG.getBuildServer())) {
//                sendToServer(requestor.getServer().getInfo(), "SpawnCommandResponse", uuidString, "deny");
//            } else {
//            }
        } else if ("PlayerDeath".equals(instructionName)) {
            String uuidString = in.readUTF();
            UUID uuid = UUID.fromString(uuidString);
            ProxiedPlayer died = ProxyServer.getInstance().getPlayer(uuid);
            if (died != null) {
                String deathCause = in.readUTF();
                String damagerType = in.readUTF();
                String damager = in.readUTF();
                if (damagerType.equals("Player")) {
                    String damagerName = "?";
                    UUID damagerUuid = UUID.fromString(damager);
                    User user = userCache.findById(damagerUuid).orElse(null);
                    if (user != null) {
                        Group g = groupCache.findById(user.getGroupId()).orElse(null);
                        damagerName = user.getUsername();
                        if (g != null) {
                            damagerName = g.getChatPrefix() + damagerName;
                        }
                    }
                    DeathMessageResolver dmr = new DeathMessageResolver(died, deathCause, damagerName);
                    ProxyServer.getInstance().getPlayers().forEach(dmr::byOther);
                } else {
                    DeathMessageResolver dmr = new DeathMessageResolver(died, deathCause, damager);
                    ProxyServer.getInstance().getPlayers().forEach(dmr::byOther);
                }
            }
        } else if ("PlayerDeathSolo".equals(instructionName)) {
            String uuidString = in.readUTF();
            UUID uuid = UUID.fromString(uuidString);
            ProxiedPlayer died = ProxyServer.getInstance().getPlayer(uuid);
            if (died != null) {
                String deathCause = in.readUTF();
                DeathMessageResolver dmr = new DeathMessageResolver(died, deathCause, null);
                ProxyServer.getInstance().getPlayers().forEach(dmr::solo);
            }
        } else if ("HomeCommand".equals(instructionName)) {
            String uuidString = in.readUTF();
            String homeName = in.readUTF();
            UUID uuid = UUID.fromString(uuidString);
            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(uuid);
            if (target != null) {
                User u = userCache.findById(target.getUniqueId()).orElse(null);
                String lang = u != null ? u.getLanguage().toString() : "de";
                MlGeneral ml = MlGeneral.get(lang);
                UserHome home = userHomeCache.getHome(target.getUniqueId(), homeName).orElse(null);
                if (home != null) {
                    if (!target.getServer().getInfo().getName().equalsIgnoreCase(home.getServer())) {
                        ServerInfo si = ProxyServer.getInstance().getServerInfo(home.getServer());
                        if (si != null) {
                            target.connect(si);
                            ProxyServer.getInstance().getScheduler().schedule(WinterProjectBungeeCore.INSTANCE,
                                () -> {
                                    sendToServer(si, "TeleportPlayer", target.getUniqueId().toString(), home.getLocation());
                                    sendToServer(si, "PlaySound", target.getUniqueId().toString(), "BLOCK_BEACON_ACTIVATE");
                                }, 1, TimeUnit.SECONDS);

                        } else {
                            target.sendMessage(tc(Constants.PREFIX + String.format(ml.homeNotFound(), homeName)));
                            sendToServer(target.getServer().getInfo(), "PlaySound", target.getUniqueId().toString(), "BLOCK_BEACON_DEACTIVATE");
                        }
                    } else {
                        sendToServer(target.getServer().getInfo(), "TeleportPlayer", target.getUniqueId().toString(), home.getLocation());
                        sendToServer(target.getServer().getInfo(), "PlaySound", target.getUniqueId().toString(), "BLOCK_BEACON_ACTIVATE");
                    }
                } else {
                    target.sendMessage(tc(Constants.PREFIX + String.format(ml.homeNotFound(), homeName)));
                    sendToServer(target.getServer().getInfo(), "PlaySound", target.getUniqueId().toString(), "BLOCK_BEACON_DEACTIVATE");
                }
            }
        } else if ("PlayerCapabilities".equals(instructionName)) {
            PlayerCapabilities.receive(
                UUID.fromString(in.readUTF()),
                Float.parseFloat(in.readUTF()),
                Float.parseFloat(in.readUTF()),
                in.readUTF(),
                Integer.parseInt(in.readUTF()),
                Float.parseFloat(in.readUTF()),
                Integer.parseInt(in.readUTF()),
                in.readUTF()
            );
        } else if ("SendMessage".equals(instructionName)) {
            String target = in.readUTF();
            UUID uuid = UUID.fromString(target);
            String msg = in.readUTF();
            ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(uuid);
            if (targetPlayer != null) {
                targetPlayer.sendMessage(tc(msg));
            }
        } else if ("PlaySound".equals(instructionName)) {
            String target = in.readUTF();
            UUID uuid = UUID.fromString(target);
            String sound = in.readUTF();
            ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(uuid);
            if (targetPlayer != null) {
                sendToServer(targetPlayer.getServer().getInfo(), "PlaySound", target, sound);
            }
        } else if ("SuspiciousFinding".equals(instructionName)) {
            String target = in.readUTF();
            String materialName = in.readUTF();
            String thresholdString = in.readUTF();
            String thresholdSecsString = in.readUTF();
            UUID uuid = UUID.fromString(target);

            String username = "? (" + uuid.toString() + ")";
            User targetUser = userCache.findById(uuid).orElse(null);
            if (targetUser != null) {
                username = targetUser.getUsername();
            }

            String finalUsername = username;
            ProxyServer.getInstance().getPlayers().forEach(x -> {
                final boolean[] receiveTeamNotifications = {false};
                userCache.findById(x.getUniqueId()).ifPresent(y -> receiveTeamNotifications[0] = y.getTeamNotifications());
                if (x.hasPermission("wp.suspicious-finding.receive-warning") && (x.getUniqueId().equals(pp.getUniqueId()) || receiveTeamNotifications[0])) {
                    String msg = "§7 Suspicious: §6" + finalUsername + " §8| §6" + materialName + " §8(§7T: > " + thresholdString + "/" + thresholdSecsString + " sec§8)";
                    x.sendMessage(tc("§4§l§m♦" + msg));
                }
            });
        } else if ("FakeJoin".equals(instructionName)) {
            String prefixedUsername = in.readUTF();
            ProxyServer.getInstance().getPlayers().forEach(x -> {
                User u = userCache.findById(x.getUniqueId()).orElse(null);
                if (u != null) {
                    String joinMessage = Constants.PREFIX + String.format(MlPlayerAction.get(u.getLanguage().toString()).join(), prefixedUsername);
                    BaseComponent[] joinMessageComponents = tc(joinMessage);
                    x.sendMessage(joinMessageComponents);
                }
            });
        } else if ("FakeQuit".equals(instructionName)) {
            String prefixedUsername = in.readUTF();
            ProxyServer.getInstance().getPlayers().forEach(x -> {
                User u = userCache.findById(x.getUniqueId()).orElse(null);
                if (u != null) {
                    String joinMessage = Constants.PREFIX + String.format(MlPlayerAction.get(u.getLanguage().toString()).quit(), prefixedUsername);
                    BaseComponent[] joinMessageComponents = tc(joinMessage);
                    x.sendMessage(joinMessageComponents);
                }
            });
        }
    }

    private void tryNextServer(ProxiedPlayer pp, MlGeneral ml, int index, List<ServerInfo> serverInfos) {
        if (index >= serverInfos.size()) {
            CONNECTING_TO_RANDOM_SERVER.remove(pp.getUniqueId());
            pp.sendMessage(tc(Constants.PREFIX + ml.noServerOnline()));
            pp.sendMessage(tc(Constants.PREFIX + ml.tryAgainLater()));
            return;
        }
        ServerInfo si = serverInfos.get(index);
        si.ping((ping, throwable) -> {
            if (throwable != null) {
                tryNextServer(pp, ml, index + 1, serverInfos);
            } else {
                ListenerServerSwitch.JOINED_ANOTHER_SERVER.add(pp.getUniqueId());
                QueueHandler.connect(pp, si);
                CONNECTING_TO_RANDOM_SERVER.remove(pp.getUniqueId());
            }
        });
    }

}
