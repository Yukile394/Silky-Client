/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.features.command.impl;

import silky.client.features.command.ClientCommand;
import silky.client.features.command.CommandContext;
import silky.client.features.command.CommandInfo;
import silky.client.features.command.CommandOutput;
import silky.client.util.player.NetworkStatsUtil;

import java.util.Locale;

@CommandInfo(
        id = "tps",
        aliases = "servertps",
        usage = "@tps",
        descriptionKey = "command.tps.description"
)
public final class TpsCommand implements ClientCommand {
    @Override
    public boolean execute(CommandContext ctx) {
        float tps = NetworkStatsUtil.getTps(ctx.mc());
        CommandOutput.send(String.format(Locale.ROOT, "TPS: %.2f / 20.00", tps));
        return true;
    }
}
