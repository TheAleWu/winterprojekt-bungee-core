package de.alewu.wpbc.commands;

import static de.alewu.wpbc.util.StaticMethodCollection.tc;

import de.alewu.coreapi.db.caching.CacheRegistry;
import de.alewu.coreapi.exception.CachingException;
import de.alewu.wpbc.files.Files;
import de.alewu.wpbc.ml.MlGeneral;
import de.alewu.wpbc.ml.cmds.MlCommandMaintenance;
import de.alewu.wpbc.util.Constants;
import de.alewu.wpc.repository.cache.UserCache;
import de.alewu.wpc.repository.entity.User;
import java.util.List;
import java.util.UUID;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandMaintenance extends Command {

    private final UserCache userCache;

    public CommandMaintenance() {
        super("maintenance", "wp.maintenance");
        userCache = CacheRegistry.getCache(UserCache.class).orElseThrow(() -> new CachingException("userCache not registered"));
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
            MlCommandMaintenance ml = MlCommandMaintenance.get(user.getLanguage().toString());
            boolean maintenance = Files.GLOBAL_CONFIG.isMaintenance();
            if (!maintenance) {
                Files.GLOBAL_CONFIG.setMaintenance(true);
                pp.sendMessage(tc(Constants.PREFIX + ml.turnedOn()));
                pp.sendMessage(tc(Constants.PREFIX + ml.nonBypassedKicked()));
                List<UUID> bypassed = Files.GLOBAL_CONFIG.getMaintenanceBypassed();
                for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
                    User allUser = userCache.findById(all.getUniqueId()).orElse(null);
                    String lang = allUser != null ? allUser.getLanguage().toString() : "de";
                    MlCommandMaintenance mlAll = MlCommandMaintenance.get(lang);
                    if (!bypassed.contains(all.getUniqueId())) {
                        all.disconnect(tc(mlAll.turnOnKick()));
                    } else {
                        all.sendMessage(tc(Constants.PREFIX + String.format(mlAll.someoneTurnedOn(), pp.getName())));
                    }
                }
            } else {
                Files.GLOBAL_CONFIG.setMaintenance(false);
                pp.sendMessage(tc(Constants.PREFIX + ml.turnedOff()));
                List<UUID> bypassed = Files.GLOBAL_CONFIG.getMaintenanceBypassed();
                for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
                    User allUser = userCache.findById(all.getUniqueId()).orElse(null);
                    String lang = allUser != null ? allUser.getLanguage().toString() : "de";
                    MlCommandMaintenance mlAll = MlCommandMaintenance.get(lang);
                    if (bypassed.contains(all.getUniqueId())) {
                        all.sendMessage(tc(Constants.PREFIX + String.format(mlAll.someoneTurnedOff(), pp.getName())));
                    }
                }
            }
        }
    }
}
