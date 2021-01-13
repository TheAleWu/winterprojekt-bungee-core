package de.alewu.wpbc.listener;

import static de.alewu.wpbc.util.StaticMethodCollection.reloadPermissions;
import static de.alewu.wpbc.util.StaticMethodCollection.sendToServers;
import static de.alewu.wpbc.util.StaticMethodCollection.tc;

import de.alewu.coreapi.db.caching.CacheRegistry;
import de.alewu.coreapi.exception.CachingException;
import de.alewu.wpbc.files.Files;
import de.alewu.wpbc.ml.MlGeneral;
import de.alewu.wpbc.ml.MlPlayerAction;
import de.alewu.wpbc.ml.cmds.MlCommandMaintenance;
import de.alewu.wpbc.ml.cmds.MlPunishments;
import de.alewu.wpbc.util.Constants;
import de.alewu.wpbc.util.PlayerCapabilities;
import de.alewu.wpbc.util.PunishmentCommunicationHelper;
import de.alewu.wpbc.util.SpectateUtil;
import de.alewu.wpc.helpers.TimeZoneHelper;
import de.alewu.wpc.repository.cache.GroupCache;
import de.alewu.wpc.repository.cache.GroupPermissionCache;
import de.alewu.wpc.repository.cache.PunishmentCache;
import de.alewu.wpc.repository.cache.UserCache;
import de.alewu.wpc.repository.entity.Group;
import de.alewu.wpc.repository.entity.GroupPermission;
import de.alewu.wpc.repository.entity.Punishment;
import de.alewu.wpc.repository.entity.User;
import java.util.List;
import java.util.logging.Logger;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ListenerPostLogin implements Listener {

    private static final Logger LOG = Logger.getLogger(ListenerPostLogin.class.getName());
    private final UserCache userCache;
    private final GroupCache groupCache;
    private final GroupPermissionCache groupPermissionCache;
    private final PunishmentCache punishmentCache;

    public ListenerPostLogin() {
        userCache = CacheRegistry.getCache(UserCache.class)
            .orElseThrow(() -> new CachingException("userCache not registered"));
        groupCache = CacheRegistry.getCache(GroupCache.class)
            .orElseThrow(() -> new CachingException("groupCache not registered"));
        groupPermissionCache = CacheRegistry.getCache(GroupPermissionCache.class)
            .orElseThrow(() -> new CachingException("groupPermissionCache not registered"));
        punishmentCache = CacheRegistry.getCache(PunishmentCache.class)
            .orElseThrow(() -> new CachingException("punishmentCache not registered"));
    }

    @EventHandler
    public void on(PostLoginEvent e) {
        ProxiedPlayer pp = e.getPlayer();
        Group defaultGroup = groupCache.getDefaultGroup()
            .orElse(null);
        if (defaultGroup == null) {
            pp.disconnect(tc("§cServer is not correctly configured...\n§cPlease contact an administrator!"));
            LOG.severe("Default group is missing! Cannot let any player connect as they cannot aquire a group!");
            return;
        }

        User user = userCache.findById(pp.getUniqueId()).orElse(null);
        if (user == null) {
            user = User.newInstance(pp.getUniqueId(), pp.getName(), defaultGroup.getId());
            user.setLastOnline(-2);
            userCache.save(user);
            userCache.updateToDatabase();
        } else {
            user.setLastOnline(-2);
            if (!pp.getName().equals(user.getUsername())) {
                user.setUsername(pp.getName());
                userCache.save(user);
            }
            Punishment activeBan = punishmentCache.getActiveBan(pp.getUniqueId());
            if (activeBan != null) {
                MlPunishments mlPunishments = MlPunishments.get(user.getLanguage().toString());
                long remainingDuration = activeBan.getPunishmentDuration() != -1
                    ? activeBan.getPunishmentEndTimestamp() - TimeZoneHelper.epochSecond()
                    : -1;
                PunishmentCommunicationHelper.help(pp, mlPunishments).banPlayer(activeBan.getReason().replaceAll("_", " "), remainingDuration);
                return;
            }
            if (user.getSpectatedUser() != null) {
                user.setSpectatedUser(null);
                userCache.save(user);
            }
            SpectateUtil.backToPreviousLocation(pp, user);
        }
        MlCommandMaintenance mlMaintenance = MlCommandMaintenance.get(user.getLanguage().toString());
        if (!Files.GLOBAL_CONFIG.getMaintenanceBypassed().contains(pp.getUniqueId()) && Files.GLOBAL_CONFIG.isMaintenance()) {
            pp.disconnect(tc(mlMaintenance.joinWhileMaintenance()));
            return;
        }

        Group g = groupCache.findById(user.getGroupId()).orElse(defaultGroup);
        String playerNameWithPrefix = g.getChatPrefix() + pp.getName();
        String lang = user.getLanguage().toString();
        ProxyServer.getInstance().getPlayers().forEach(x -> {
            User u = userCache.findById(x.getUniqueId()).orElse(null);
            if (u != null) {
                String joinMessage = Constants.PREFIX + String.format(MlPlayerAction.get(u.getLanguage().toString()).join(), playerNameWithPrefix);
                BaseComponent[] joinMessageComponents = tc(joinMessage);
                x.sendMessage(joinMessageComponents);
            }
        });

        List<GroupPermission> groupPermissions = groupPermissionCache.getAllPermissions(g.getId());
        reloadPermissions(pp, groupPermissions);
        sendToServers(ProxyServer.getInstance().getServers().values(), "AddTabListPlayer", pp.getName());

        MlGeneral ml = MlGeneral.get(lang);
        pp.setTabHeader(tc(ml.tabHeader()), tc(ml.tabFooter()));

        pp.sendMessage(tc(Constants.PREFIX + String.format(ml.joinWelcome(), g.getChatPrefix() + pp.getName())));
        pp.sendMessage(tc(Constants.PREFIX + ml.joinUseHelp()));
        pp.sendMessage(tc(Constants.PREFIX + ml.joinHaveFun()));
    }

}
