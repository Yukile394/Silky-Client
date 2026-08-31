/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.features.command.impl;

import silky.client.config.ConfigPaths;
import silky.client.config.ConfigSerializer;
import silky.client.features.command.ClientCommand;
import silky.client.features.command.CommandContext;
import silky.client.features.command.CommandInfo;
import silky.client.features.command.CommandOutput;
import silky.client.features.module.ModuleManager;

import java.util.List;
import java.util.Locale;

@CommandInfo(
        id = "config",
        aliases = {"cfg", "settings"},
        usage = "@config [save|load|path]",
        descriptionKey = "command.config.description"
)
public final class ConfigCommand implements ClientCommand {
    @Override
    public boolean execute(CommandContext ctx) {
        String action = ctx.arg(0) == null ? "save" : ctx.arg(0).toLowerCase(Locale.ROOT);
        switch (action) {
            case "save" -> {
                ModuleManager.saveAllModuleConfigs();
                ConfigSerializer.saveAll();
                CommandOutput.success("Client config saved.");
            }
            case "load", "reload" -> {
                ConfigSerializer.loadAll();
                ModuleManager.loadAllModuleConfigs();
                CommandOutput.success("Client config reloaded.");
            }
            case "path", "folder" -> CommandOutput.send("Config directory: " + ConfigPaths.root().toAbsolutePath());
            default -> CommandOutput.warning("Usage: " + metadata().usage());
        }
        return true;
    }

    @Override
    public List<String> suggest(CommandContext ctx, int argIndex, String token) {
        if (argIndex != 1) return List.of();
        String lower = token == null ? "" : token.toLowerCase(Locale.ROOT);
        return List.of("save", "load", "path").stream().filter(value -> value.startsWith(lower)).toList();
    }
}
