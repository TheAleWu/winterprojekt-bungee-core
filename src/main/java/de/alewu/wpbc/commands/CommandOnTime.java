package de.alewu.wpbc.commands;

import static de.alewu.wpbc.util.StaticMethodCollection.tc;

import de.alewu.coreapi.db.caching.CacheRegistry;
import de.alewu.coreapi.exception.CachingException;
import de.alewu.wpbc.ml.MlGeneral;
import de.alewu.wpbc.util.Constants;
import de.alewu.wpc.repository.cache.UserCache;
import de.alewu.wpc.repository.entity.User;
import java.util.StringJoiner;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandOnTime extends Command {

    private final UserCache userCache;

    public CommandOnTime() {
        super("ontime");
        userCache = CacheRegistry.getCache(UserCache.class).orElseThrow(() -> new CachingException("userCache not registered"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ProxiedPlayer pp = (ProxiedPlayer) sender;
        User user = userCache.findById(pp.getUniqueId()).orElse(null);
        if (user == null) {
            pp.sendMessage(tc(Constants.PREFIX + String.format(MlGeneral.get("de").noCacheObjectFound(), User.class.getSimpleName())));
            return;
        }
        MlGeneral ml = MlGeneral.get(user.getLanguage().toString());
        if (args.length == 0) {
            long onTimeSeconds = user.getOnTimeSeconds();
            String durationString = getDurationString(onTimeSeconds);
            pp.sendMessage(tc(Constants.PREFIX + String.format(ml.ontimeSelf(), durationString)));
        } else if (args.length == 1) {
            User otherUser = userCache.findByUsername(args[0]).orElse(null);
            if (otherUser == null) {
                pp.sendMessage(tc(Constants.PREFIX + String.format(ml.neverPlayed(), args[0])));
                return;
            }
            long onTimeSeconds = otherUser.getOnTimeSeconds();
            String durationString = getDurationString(onTimeSeconds);
            pp.sendMessage(tc(Constants.PREFIX + String.format(ml.ontimeOther(), otherUser.getUsername(), durationString)));
        } else {
            pp.sendMessage(tc(Constants.PREFIX + ml.ontimeUsage()));
        }
    }

    private String getDurationString(long onTimeSeconds) {
        StringJoiner sj = new StringJoiner(" ");
        long secs = onTimeSeconds;
        long mins = 0;
        long hours = 0;
        long days = 0;

        while (secs >= 60) {
            secs -= 60;
            mins++;
        }
        while (mins >= 60) {
            mins -= 60;
            hours++;
        }
        while (hours >= 24) {
            hours -= 24;
            days++;
        }

        if (days > 0) {
            sj.add(padWithZero(days) + "d");
        }
        if (!sj.toString().isEmpty() || hours > 0) {
            sj.add(padWithZero(hours) + "h");
        }
        if (!sj.toString().isEmpty() || mins > 0) {
            sj.add(padWithZero(mins) + "m");
        }
        if (!sj.toString().isEmpty() || secs > 0) {
            sj.add(padWithZero(secs) + "s");
        }
        return sj.toString();
    }

    private String padWithZero(long val) {
        return val < 10 ? "0" + val : String.valueOf(val);
    }
}
