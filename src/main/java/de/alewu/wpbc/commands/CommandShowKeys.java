package de.alewu.wpbc.commands;

import static de.alewu.wpbc.util.StaticMethodCollection.tc;

import de.alewu.coreapi.db.caching.CacheRegistry;
import de.alewu.coreapi.exception.CachingException;
import de.alewu.wpbc.ml.MlGeneral;
import de.alewu.wpbc.ml.cmds.MlCommandShowKeys;
import de.alewu.wpbc.util.Constants;
import de.alewu.wpc.helpers.TimeZoneHelper;
import de.alewu.wpc.repository.cache.GameKeyCache;
import de.alewu.wpc.repository.cache.UserCache;
import de.alewu.wpc.repository.entity.GameKey;
import de.alewu.wpc.repository.entity.User;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandShowKeys extends Command {

    private UserCache userCache;
    private GameKeyCache gameKeyCache;

    public CommandShowKeys() {
        super("showkeys", "wp.showkeys");
        this.userCache = CacheRegistry.getCache(UserCache.class).orElseThrow(() -> new CachingException("userCache not registered"));
        this.gameKeyCache = CacheRegistry.getCache(GameKeyCache.class).orElseThrow(() -> new CachingException("gameKeyCache not registered"));
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
            MlCommandShowKeys ml = MlCommandShowKeys.get(user.getLanguage().toString());
            List<GameKey> gameKeys = gameKeyCache.getCache();
            int maxPage = getPageCount(gameKeys);
            if (gameKeys.isEmpty()) {
                pp.sendMessage(tc(Constants.PREFIX + ml.noKeysExist()));
                return;
            }
            if (args.length != 1) {
                pp.sendMessage(tc(Constants.PREFIX + String.format(ml.wrongUsage(), maxPage)));
                return;
            }
            try {
                int page = Integer.parseInt(args[0]);
                if (page > maxPage) {
                    pp.sendMessage(tc(Constants.PREFIX + String.format(ml.wrongUsage(), maxPage)));
                    return;
                }
                int startIndex = (page - 1) * Constants.GAME_KEYS_PER_PAGE;
                pp.sendMessage(tc(Constants.PREFIX + "§6=============================="));
                for (int i = startIndex; i < startIndex + Constants.GAME_KEYS_PER_PAGE; i++) {
                    try {
                        String usedBy = ml.unused();
                        GameKey gk = gameKeys.get(i);
                        if (gk.getUsedBy() != null) {
                            User usedByUser = userCache.findById(gk.getUsedBy()).orElse(null);
                            if (usedByUser != null) {
                                usedBy = "§a" + usedByUser.getUsername();
                            }
                        }
                        long usedAt = gk.getUsedAt();
                        String createdBy = "?";
                        if (gk.getCreatedBy() != null) {
                            User createdByUser = userCache.findById(gk.getCreatedBy()).orElse(null);
                            if (createdByUser != null) {
                                createdBy = createdByUser.getUsername();
                            }
                        }
                        LocalDateTime usedAtDateTime = Instant.ofEpochSecond(usedAt).atZone(TimeZoneHelper.ZONE_ID).toLocalDateTime();
                        LocalDateTime createdAtDateTime = Instant.ofEpochSecond(gk.getCreatedAt()).atZone(TimeZoneHelper.ZONE_ID).toLocalDateTime();
                        BaseComponent[] info = new BaseComponent[]{
                            new TextComponent(String.format(ml.usedBy(), ChatColor.stripColor(usedBy)) + "\n"),
                            new TextComponent((usedAt == -1 ? ml.notUsedYet() : String.format(ml.usedAt(), Constants.DATE_TIME_FORMATTER.format(usedAtDateTime))) + "\n"),
                            new TextComponent(String.format(ml.createdBy(), createdBy) + "\n"),
                            new TextComponent(String.format(ml.createdAt(), Constants.DATE_TIME_FORMATTER.format(createdAtDateTime)) + "\n"),
                            new TextComponent("\n"),
                            new TextComponent("§aKlicken, um den Key kopieren zu können")
                        };
                        TextComponent tc = new TextComponent(Constants.PREFIX + "§8- §a" + gk.getKey().getToken() + " §8[" + usedBy + "§8]");
                        HoverEvent hoverEvent = new HoverEvent(Action.SHOW_TEXT, info);
                        tc.setHoverEvent(hoverEvent);
                        tc.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, gk.getKey().getToken()));
                        pp.sendMessage(tc);
                    } catch (IndexOutOfBoundsException e) {
                        break;
                    }
                }
                pp.sendMessage(tc(Constants.PREFIX + ml.hoverForMoreInfo()));
                pp.sendMessage(tc(Constants.PREFIX + "§6=============================="));
            } catch (NumberFormatException ex) {
                pp.sendMessage(tc(Constants.PREFIX + ml.enteredPageNoValidNumber()));
            }
        }
    }

    private int getPageCount(List<GameKey> gameKeys) {
        int rest = gameKeys.size() % Constants.GAME_KEYS_PER_PAGE;
        int pages = gameKeys.size() / Constants.GAME_KEYS_PER_PAGE;
        if (rest != 0) {
            pages++;
        }
        return Math.max(1, pages);
    }
}
