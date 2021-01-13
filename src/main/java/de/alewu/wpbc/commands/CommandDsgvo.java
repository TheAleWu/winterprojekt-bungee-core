package de.alewu.wpbc.commands;

import static de.alewu.wpbc.util.StaticMethodCollection.tc;

import de.alewu.coreapi.db.caching.CacheRegistry;
import de.alewu.coreapi.exception.CachingException;
import de.alewu.wpbc.ml.MlGeneral;
import de.alewu.wpbc.util.Constants;
import de.alewu.wpc.repository.cache.UserCache;
import de.alewu.wpc.repository.entity.User;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandDsgvo extends Command {

    private final UserCache userCache;

    public CommandDsgvo() {
        super("dsgvo");
        userCache = CacheRegistry.getCache(UserCache.class).orElseThrow(() -> new CachingException("userCache not registered"));
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (commandSender instanceof ProxiedPlayer) {
            ProxiedPlayer pp = (ProxiedPlayer) commandSender;
            User user = userCache.findById(pp.getUniqueId())
                .orElse(null);
            if (user == null) {
                pp.sendMessage(tc(Constants.PREFIX + String.format(MlGeneral.get("de").noCacheObjectFound(), User.class.getSimpleName())));
                return;
            }
            MlGeneral ml = MlGeneral.get(user.getLanguage().toString());
            pp.sendMessage(tc(Constants.PREFIX + ml.gdpr()));
            pp.sendMessage(tc(Constants.PREFIX + ml.gdprReadHere()));
            TextComponent tc = new TextComponent(Constants.PREFIX + ml.gdprClick());
            tc.setClickEvent(new ClickEvent(Action.OPEN_URL, "https://www.winter-projekt.de/dsgvo"));
            pp.sendMessage(tc);
        }
    }
}
