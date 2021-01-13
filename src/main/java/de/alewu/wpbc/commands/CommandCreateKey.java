package de.alewu.wpbc.commands;

import static de.alewu.wpbc.util.StaticMethodCollection.tc;

import de.alewu.coreapi.db.caching.CacheRegistry;
import de.alewu.coreapi.exception.CachingException;
import de.alewu.wpbc.ml.MlGeneral;
import de.alewu.wpbc.ml.cmds.MlCommandCreateKey;
import de.alewu.wpbc.util.Constants;
import de.alewu.wpc.helpers.TimeZoneHelper;
import de.alewu.wpc.repository.cache.GameKeyCache;
import de.alewu.wpc.repository.cache.UserCache;
import de.alewu.wpc.repository.entity.GameKey;
import de.alewu.wpc.repository.entity.GameKeyToken;
import de.alewu.wpc.repository.entity.User;
import java.util.Random;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandCreateKey extends Command {

    private final UserCache userCache;
    private final GameKeyCache gameKeyCache;

    public CommandCreateKey() {
        super("createkey", "wp.createkey");
        userCache = CacheRegistry.getCache(UserCache.class).orElseThrow(() -> new CachingException("userCache not registered"));
        gameKeyCache = CacheRegistry.getCache(GameKeyCache.class).orElseThrow(() -> new CachingException("gameKeyCache not registered"));
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
            MlCommandCreateKey ml = MlCommandCreateKey.get(user.getLanguage().toString());
            try {
                int retry = 0;
                String gameKeyToken = getRandomGameKey(retry);
                while (gameKeyCache.findById(gameKeyToken).isPresent()) {
                    gameKeyToken = getRandomGameKey(++retry);
                }
                GameKey gk = new GameKey();
                gk.setKey(new GameKeyToken(gameKeyToken));
                gk.setCreatedBy(pp.getUniqueId());
                gk.setCreatedAt(TimeZoneHelper.epochSecond());
                gk.setUsedAt(-1L);
                gameKeyCache.save(gk);
                TextComponent tc = new TextComponent(Constants.PREFIX + String.format(ml.successfullyCreated(), gameKeyToken));
                tc.setClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, gameKeyToken));
                tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{new TextComponent("§aKlicken, um den Key kopieren zu können")}));
                pp.sendMessage(tc);
            } catch (Exception e) {
                pp.sendMessage(tc(Constants.PREFIX + ml.errorWhileCreating()));
                pp.sendMessage(tc(Constants.PREFIX + ml.tryAgainOrCheck()));
                e.printStackTrace();
            }
        }
    }

    private String getRandomGameKey(int retry) {
        if (retry > Constants.GAME_KEY_RETRY_THRESHOLD) {
            throw new IllegalStateException("Reached createKey threshold (" + retry + ")");
        }
        String str = Constants.GAME_KEY_CHARS;
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Constants.GAME_KEY_LENGTH; i++) {
            char randomCharacter = str.charAt(rnd.nextInt(str.length()));
            sb.append(randomCharacter);
        }
        return sb.toString();
    }

}
