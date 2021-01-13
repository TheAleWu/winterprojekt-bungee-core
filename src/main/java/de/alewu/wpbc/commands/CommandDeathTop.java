package de.alewu.wpbc.commands;

import static de.alewu.wpbc.util.StaticMethodCollection.tc;

import de.alewu.coreapi.db.caching.CacheRegistry;
import de.alewu.coreapi.exception.CachingException;
import de.alewu.wpbc.ml.MlGeneral;
import de.alewu.wpbc.util.Constants;
import de.alewu.wpc.repository.cache.GroupCache;
import de.alewu.wpc.repository.cache.UserCache;
import de.alewu.wpc.repository.entity.Group;
import de.alewu.wpc.repository.entity.User;
import java.util.AbstractMap.SimpleEntry;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandDeathTop extends Command {

    private final UserCache userCache;
    private final GroupCache groupCache;

    public CommandDeathTop() {
        super("deathtop", null, "dt");
        userCache = CacheRegistry.getCache(UserCache.class).orElseThrow(() -> new CachingException("userCache not registered"));
        groupCache = CacheRegistry.getCache(GroupCache.class).orElseThrow(() -> new CachingException("groupCache not registered"));
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
            Comparator<SimpleEntry<User, Integer>> comparator = Comparator.comparingInt(SimpleEntry::getValue);
            List<Entry<User, Integer>> top = userCache.getCache().stream()
                .map(u -> new SimpleEntry<>(u, u.getDeathCount()))
                .sorted(comparator.reversed().thenComparing(e -> e.getKey().getUsername()))
                .collect(Collectors.toList());
            int place = 1;
            pp.sendMessage(tc("§8======================================"));
            for (Entry<User, Integer> e : top) {
                Group g = groupCache.findById(e.getKey().getGroupId())
                    .orElse(groupCache.getDefaultGroup().orElseThrow(() -> new CachingException("default group not defined!")));
                String color = (place == 1 ? "§6" : (place == 2 ? "§7" : (place == 3 ? "§c" : "§9")));
                pp.sendMessage(tc(Constants.PREFIX + color + "#" + place + " " + g.getChatPrefix() + e.getKey().getUsername() + " §8» §7" + e.getKey().getDeathCount()));
                place++;
                if (place > 10) {
                    break;
                }
            }
            pp.sendMessage(tc("§8======================================"));
        }
    }
}
