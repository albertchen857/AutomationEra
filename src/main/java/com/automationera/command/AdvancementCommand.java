package com.automationera.command;

import com.automationera.Automationera;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public final class AdvancementCommand {
    private static final String[] SUGGESTED_ADVANCEMENTS = {
            "abovenether",
            "perimeter",
            "realperimeter",
            "netherperimeter",
            "stuckserver",
            "largeserver",
            "giantserver",
            "fastesttravel",
            "lighttravel",
            "worldeater",
            "tntquarry",
            "afk",
            "afkdie",
            "openfurnace",
            "placeobserver"
    };

    private AdvancementCommand() {
    }

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(buildCommands(CommandManager.literal("aeadv")));
            dispatcher.register(CommandManager.literal("automationera")
                    .then(buildCommands(CommandManager.literal("advancement"))));
        });
    }

    private static LiteralArgumentBuilder<ServerCommandSource> buildCommands(LiteralArgumentBuilder<ServerCommandSource> root) {
        return root
                .executes(context -> sendHelp(context.getSource()))
                .then(CommandManager.literal("help")
                        .executes(context -> sendHelp(context.getSource())))
                .then(CommandManager.literal("status")
                        .executes(context -> sendStatus(context.getSource(), context.getSource().getPlayerOrThrow()))
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
                                .executes(context -> sendStatus(context.getSource(), EntityArgumentType.getPlayer(context, "player")))))
                .then(CommandManager.literal("rescan")
                        .requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
                        .executes(context -> rescan(context.getSource(), context.getSource().getPlayerOrThrow()))
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .executes(context -> rescan(context.getSource(), EntityArgumentType.getPlayer(context, "player")))))
                .then(CommandManager.literal("grant")
                        .requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .then(CommandManager.argument("advancement", StringArgumentType.word())
                                        .suggests((context, builder) -> CommandSource.suggestMatching(SUGGESTED_ADVANCEMENTS, builder))
                                        .executes(context -> grantAdvancement(
                                                context.getSource(),
                                                EntityArgumentType.getPlayer(context, "player"),
                                                StringArgumentType.getString(context, "advancement"))))));
    }

    private static int sendHelp(ServerCommandSource source) {
        feedback(source, "command.automationera.advancement.help.header");
        feedback(source, "command.automationera.advancement.help.status");
        feedback(source, "command.automationera.advancement.help.rescan");
        feedback(source, "command.automationera.advancement.help.grant");
        feedback(source, "command.automationera.advancement.help.alias");
        return 1;
    }

    private static int sendStatus(ServerCommandSource source, ServerPlayerEntity player) {
        boolean perimeter = Automationera.isChunkAirSpace(player.getEntityWorld(), player.getChunkPos(), -59);
        boolean realPerimeter = Automationera.isChunkAirSpace(player.getEntityWorld(), player.getChunkPos(), -64);
        boolean aboveNether = player.getEntityWorld().getRegistryKey().equals(World.NETHER) && player.getBlockY() > 127;
        List<String> completed = getCompletedImportantAdvancements(source.getServer(), player);

        feedback(source, "command.automationera.advancement.status.player", player.getName().getString());
        feedback(source, "command.automationera.advancement.status.position",
                player.getEntityWorld().getRegistryKey().getValue().toString(),
                player.getBlockX(),
                player.getBlockY(),
                player.getBlockZ());
        feedback(source, "command.automationera.advancement.status.online", source.getServer().getCurrentPlayerCount());
        feedback(source, "command.automationera.advancement.status.airspace", yesNo(perimeter), yesNo(realPerimeter));
        feedback(source, "command.automationera.advancement.status.above_nether", yesNo(aboveNether));
        feedback(source, "command.automationera.advancement.status.completed",
                completed.isEmpty() ? "-" : String.join(", ", completed));
        return 1;
    }

    private static int rescan(ServerCommandSource source, ServerPlayerEntity player) {
        int granted = 0;
        boolean perimeter = Automationera.isChunkAirSpace(player.getEntityWorld(), player.getChunkPos(), -59);
        boolean realPerimeter = Automationera.isChunkAirSpace(player.getEntityWorld(), player.getChunkPos(), -64);

        if (player.getEntityWorld().getRegistryKey().equals(World.NETHER) && player.getBlockY() > 127) {
            granted += grantAllCriteria(source.getServer(), player, "abovenether");
        }

        if (player.getEntityWorld().getRegistryKey().equals(World.OVERWORLD)) {
            if (perimeter) {
                granted += grantAllCriteria(source.getServer(), player, "perimeter");
            }
            if (realPerimeter) {
                granted += grantAllCriteria(source.getServer(), player, "realperimeter");
            }
        } else if (player.getEntityWorld().getRegistryKey().equals(World.NETHER)) {
            if (realPerimeter) {
                granted += grantAllCriteria(source.getServer(), player, "realperimeter");
            }
            if (perimeter) {
                granted += grantAllCriteria(source.getServer(), player, "netherperimeter");
            }
        }

        int online = source.getServer().getCurrentPlayerCount();
        if (online > 49) {
            granted += grantAllCriteria(source.getServer(), player, "largeserver");
            granted += grantAllCriteria(source.getServer(), player, "giantserver");
        } else if (online > 9) {
            granted += grantAllCriteria(source.getServer(), player, "largeserver");
        }

        if (granted > 0) {
            feedback(source, "command.automationera.advancement.rescan.granted", granted, player.getName().getString());
        } else {
            feedback(source, "command.automationera.advancement.rescan.none", player.getName().getString());
        }
        return granted;
    }

    private static int grantAdvancement(ServerCommandSource source, ServerPlayerEntity player, String value) {
        Identifier id = parseAutomationEraAdvancement(source, value);
        if (id == null) {
            return 0;
        }

        int granted = grantAllCriteria(source.getServer(), player, id);
        if (granted > 0) {
            feedback(source, "command.automationera.advancement.grant.success", id.toString(), player.getName().getString());
        } else {
            feedback(source, "command.automationera.advancement.grant.already_done", player.getName().getString(), id.toString());
        }
        return granted;
    }

    private static int grantAllCriteria(MinecraftServer server, ServerPlayerEntity player, String path) {
        return grantAllCriteria(server, player, Identifier.of(Automationera.MOD_ID, path));
    }

    private static int grantAllCriteria(MinecraftServer server, ServerPlayerEntity player, Identifier id) {
        AdvancementEntry advancement = server.getAdvancementLoader().get(id);
        if (advancement == null) {
            return 0;
        }

        AdvancementProgress progress = player.getAdvancementTracker().getProgress(advancement);
        int granted = 0;
        for (String criterion : progress.getUnobtainedCriteria()) {
            if (player.getAdvancementTracker().grantCriterion(advancement, criterion)) {
                granted++;
            }
        }
        return granted;
    }

    private static Identifier parseAutomationEraAdvancement(ServerCommandSource source, String value) {
        try {
            Identifier id = value.contains(":") ? Identifier.of(value) : Identifier.of(Automationera.MOD_ID, value);
            if (!Automationera.MOD_ID.equals(id.getNamespace())) {
                source.sendError(Text.translatable("command.automationera.advancement.error.namespace"));
                return null;
            }
            if (source.getServer().getAdvancementLoader().get(id) == null) {
                source.sendError(Text.translatable("command.automationera.advancement.error.unknown", id.toString()));
                return null;
            }
            return id;
        } catch (InvalidIdentifierException exception) {
            source.sendError(Text.translatable("command.automationera.advancement.error.invalid", value));
            return null;
        }
    }

    private static List<String> getCompletedImportantAdvancements(MinecraftServer server, ServerPlayerEntity player) {
        List<String> completed = new ArrayList<>();
        for (String path : SUGGESTED_ADVANCEMENTS) {
            AdvancementEntry advancement = server.getAdvancementLoader().get(Identifier.of(Automationera.MOD_ID, path));
            if (advancement != null && player.getAdvancementTracker().getProgress(advancement).isDone()) {
                completed.add(path);
            }
        }
        return completed;
    }

    private static Text yesNo(boolean value) {
        return Text.translatable(value
                ? "command.automationera.advancement.yes"
                : "command.automationera.advancement.no");
    }

    private static void feedback(ServerCommandSource source, String key, Object... args) {
        source.sendFeedback(() -> Text.translatable(key, args), false);
    }
}
