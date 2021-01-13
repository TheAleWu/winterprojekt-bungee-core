package de.alewu.wpbc.commands;

import static de.alewu.wpbc.util.StaticMethodCollection.tc;

import de.alewu.coreapi.db.caching.CacheRegistry;
import de.alewu.coreapi.exception.CachingException;
import de.alewu.coreapi.ml.Iso2CountryCode;
import de.alewu.wpbc.ml.MlGeneral;
import de.alewu.wpbc.ml.cmds.MlCommandHelp;
import de.alewu.wpbc.util.Constants;
import de.alewu.wpc.repository.cache.UserCache;
import de.alewu.wpc.repository.entity.User;
import java.util.StringJoiner;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandHelp extends Command {

    private final UserCache userCache;

    public CommandHelp() {
        super("help", null, "?");
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
            MlCommandHelp ml = MlCommandHelp.get(user.getLanguage().toString());
            if(args.length == 0) {
                sendHelp(pp, ml, 1);
            } else if (args.length == 1) {
                try {
                    int page = Integer.parseInt(args[0]);
                    sendHelp(pp, ml, page);
                } catch (NumberFormatException e) {
                    pp.sendMessage(tc(Constants.PREFIX + "§6/help 1/2"));
                }
            } else {
                pp.sendMessage(tc(Constants.PREFIX + "§6/help 1/2"));
            }
        }
    }

    private void sendHelp(ProxiedPlayer pp, MlCommandHelp ml, int page) {
        if (page == 1) {
            pp.sendMessage(tc(Constants.PREFIX + ""));
            pp.sendMessage(tc(Constants.PREFIX + "§8========== §6Help 1/2 §8=========="));
            pp.sendMessage(tc(Constants.PREFIX + "§6/help §8- " + ml.help()));
            pp.sendMessage(tc(Constants.PREFIX + "§6/changelog §8- " + ml.language()));
            pp.sendMessage(tc(Constants.PREFIX + "§6/lang §8- " + ml.language()));
            pp.sendMessage(tc(Constants.PREFIX + "§6/ping §8- " + ml.ping()));
            pp.sendMessage(tc(Constants.PREFIX + "§6/spawn §8- " + ml.spawn()));
            pp.sendMessage(tc(Constants.PREFIX + "§6/fw §8- " + ml.farmwelt()));
            pp.sendMessage(tc(Constants.PREFIX + "§6/msg §8- " + ml.msg()));
            pp.sendMessage(tc(Constants.PREFIX + "§6/togglemsg §8- " + ml.togglemsg()));
            pp.sendMessage(tc(Constants.PREFIX + "§6/status §8- " + ml.status()));
            pp.sendMessage(tc(Constants.PREFIX + "§6/rang §8- " + ml.rang()));
            pp.sendMessage(tc(Constants.PREFIX + "§6/trade §8- " + ml.trade()));
            pp.sendMessage(tc(Constants.PREFIX + "§6/sit §8- " + ml.sit()));
            pp.sendMessage(tc(Constants.PREFIX + "§6/home §8- " + ml.home()));
            pp.sendMessage(tc(Constants.PREFIX + "§6/sethome §8- " + ml.sethome()));
            pp.sendMessage(tc(Constants.PREFIX + "§6/delhome §8- " + ml.delhome()));
            pp.sendMessage(tc(Constants.PREFIX + "§8========== §6Help 1/2 §8=========="));
        } else if (page == 2) {
            pp.sendMessage(tc(Constants.PREFIX + ""));
            pp.sendMessage(tc(Constants.PREFIX + "§8========== §6Help 2/2 §8=========="));
            pp.sendMessage(tc(Constants.PREFIX + "§6/dsgvo §8- " + ml.dsgvo()));
            pp.sendMessage(tc(Constants.PREFIX + "§6/wiki §8- " + ml.wiki()));
            pp.sendMessage(tc(Constants.PREFIX + "§6/nether §8- " + ml.nether()));
            pp.sendMessage(tc(Constants.PREFIX + "§6/r §8- " + ml.reply()));
            pp.sendMessage(tc(Constants.PREFIX + "§6/tpa §8- " + ml.tpa()));
            pp.sendMessage(tc(Constants.PREFIX + "§6/list §8- " + ml.list()));
            pp.sendMessage(tc(Constants.PREFIX + "§6/ontime §8- " + ml.ontime()));
            pp.sendMessage(tc(Constants.PREFIX + "§6/deaths §8- " + ml.deaths()));
            pp.sendMessage(tc(Constants.PREFIX + "§6/deathtop §8- " + ml.deathTop()));
            pp.sendMessage(tc(Constants.PREFIX + "§6/lock §8- " + ml.lock()));
            pp.sendMessage(tc(Constants.PREFIX + "§6/unlock §8- " + ml.unlock()));
            pp.sendMessage(tc(Constants.PREFIX + "§6/cpassword §8- " + ml.cpassword()));
            pp.sendMessage(tc(Constants.PREFIX + "§6/cmodify §8- " + ml.cmodify()));
            pp.sendMessage(tc(Constants.PREFIX + "§6/cinfo §8- " + ml.cinfo()));
            pp.sendMessage(tc(Constants.PREFIX + "§6/lwc flag hopper on/off §8- " + ml.lwcHopper()));
            pp.sendMessage(tc(Constants.PREFIX + "§8========== §6Help 2/2 §8=========="));
        } else {
            throw new NumberFormatException("index out of bounds");
        }
    }
}
