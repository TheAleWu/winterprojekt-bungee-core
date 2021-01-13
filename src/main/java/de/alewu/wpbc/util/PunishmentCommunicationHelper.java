package de.alewu.wpbc.util;

import static de.alewu.wpbc.util.StaticMethodCollection.tc;

import de.alewu.wpbc.ml.cmds.MlPunishments;
import de.alewu.wpc.helpers.TimeUnit;
import de.alewu.wpc.helpers.Timespan;
import java.util.Objects;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PunishmentCommunicationHelper {

    private final ProxiedPlayer pp;
    private final MlPunishments ml;

    private PunishmentCommunicationHelper(ProxiedPlayer pp, MlPunishments ml) {
        this.pp = Objects.requireNonNull(pp);
        this.ml = Objects.requireNonNull(ml);
    }

    public static PunishmentCommunicationHelper help(ProxiedPlayer pp, MlPunishments ml) {
        return new PunishmentCommunicationHelper(pp, ml);
    }

    public void kickPlayer(String reason) {
        if (reason == null) {
            return;
        }
        pp.disconnect(tc(String.format(ml.kickDisconnectScreen(), reason)));
    }

    public void mutePlayer(String reason, long remainingDuration) {
        if (reason == null || (remainingDuration < 0 && remainingDuration != -1)) {
            return;
        }
        Timespan timespan = new Timespan(remainingDuration);
        String duration = timespan.getTimeUnit() == TimeUnit.PERMANENT ? ml.durationPermanent() : timespan.toString(false);
        pp.sendMessage(tc(String.format(ml.muteChatMessage(), Constants.PREFIX, reason, duration)));
    }

    public void banPlayer(String reason, long remainingDuration) {
        if (reason == null || (remainingDuration < 0 && remainingDuration != -1)) {
            return;
        }
        pp.disconnect(tc(getBanText(reason, remainingDuration)));
    }

    public String getBanText(String reason, long remainingDuration) {
        Timespan timespan = new Timespan(remainingDuration);
        String duration = timespan.getTimeUnit() == TimeUnit.PERMANENT ? ml.durationPermanent() : timespan.toString(false);
        return String.format(ml.banDisconnectScreen(), reason, duration);
    }

}
