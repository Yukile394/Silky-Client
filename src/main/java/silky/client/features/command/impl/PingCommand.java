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

@CommandInfo(
        id = "ping",
        aliases = "latency",
        usage = "@ping",
        descriptionKey = "command.ping.description"
)
public final class PingCommand implements ClientCommand {
    @Override
    public boolean execute(CommandContext ctx) {
        int ping = NetworkStatsUtil.getPing(ctx.mc());
        if (ping < 0) CommandOutput.warning("Ping is unavailable.");
        else CommandOutput.send("Ping: " + ping + " ms");
        return true;
    }
}
