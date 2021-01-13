package de.alewu.wpbc.commands;

import static de.alewu.wpbc.util.StaticMethodCollection.sendToServer;
import static de.alewu.wpbc.util.StaticMethodCollection.tc;

import de.alewu.coreapi.db.caching.CacheRegistry;
import de.alewu.coreapi.exception.CachingException;
import de.alewu.coreapi.ml.Iso2CountryCode;
import de.alewu.wpbc.files.Files;
import de.alewu.wpbc.ml.MlGeneral;
import de.alewu.wpbc.util.Constants;
import de.alewu.wpc.repository.cache.UserCache;
import de.alewu.wpc.repository.entity.User;
import java.util.StringJoiner;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandLanguage extends Command {

    private final UserCache userCache;

    public CommandLanguage() {
        super("language", null, "lang");
        userCache = CacheRegistry.getCache(UserCache.class).orElseThrow(() -> new CachingException("userCache not registered"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer pp = (ProxiedPlayer) sender;
            User user = userCache.findById(pp.getUniqueId())
                .orElse(null);
            if (user == null) {
                pp.sendMessage(tc(Constants.PREFIX + String.format(MlGeneral.get("de").noCacheObjectFound(), User.class.getSimpleName())));
                return;
            }
            MlGeneral ml = MlGeneral.get(user.getLanguage().toString());
            if (args.length != 1) {
                StringJoiner sj = new StringJoiner("/");
                for (String code : Constants.SUPPORTED_LANGUAGES.keySet()) {
                    sj.add(code);
                }
                pp.sendMessage(tc(Constants.PREFIX + "§6/lang <" + sj.toString() + ">"));
            } else {
                String code = args[0];
                if (Constants.SUPPORTED_LANGUAGES.containsKey(code.toLowerCase())) {
                    user.setLanguage(new Iso2CountryCode(code));
                    userCache.save(user);
                    pp.disconnect(tc(String.format(ml.changedLanguageKick(), code)));
                } else {
                    pp.sendMessage(tc(Constants.PREFIX + String.format(ml.languageUnknown(), code)));
                }
            }
        }
    }
}
