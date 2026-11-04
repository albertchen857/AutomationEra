package com.automationera.command;

import com.automationera.Automationera;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.IdentifierException;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
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
            dispatcher.register(buildCommands(Commands.literal("aeadv")));
            dispatcher.register(Commands.literal("automationera")
                    .then(buildCommands(Commands.literal("advancement"))));
        });
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildCommands(LiteralArgumentBuilder<CommandSourceStack> root) {
        return root
                .executes(context -> sendHelp(context.getSource()))
                .then(Commands.literal("help")
                        .executes(context -> sendHelp(context.getSource())))
                .then(Commands.literal("status")
                        .executes(context -> sendStatus(context.getSource(), context.getSource().getPlayerOrException()))
                        .then(Commands.argument("player", EntityArgument.player())
                                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                                .executes(context -> sendStatus(context.getSource(), EntityArgument.getPlayer(context, "player")))))
                .then(Commands.literal("rescan")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(context -> rescan(context.getSource(), context.getSource().getPlayerOrException()))
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(context -> rescan(context.getSource(), EntityArgument.getPlayer(context, "player")))))
                .then(Commands.literal("grant")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("advancement", StringArgumentType.word())
                                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(SUGGESTED_ADVANCEMENTS, builder))
                                        .executes(context -> grantAdvancement(
                                                context.getSource(),
                                                EntityArgument.getPlayer(context, "player"),
                                                StringArgumentType.getString(context, "advancement"))))));
    }

    private static int sendHelp(CommandSourceStack source) {
        feedback(source, "command.automationera.advancement.help.header");
        feedback(source, "command.automationera.advancement.help.status");
        feedback(source, "command.automationera.advancement.help.rescan");
        feedback(source, "command.automationera.advancement.help.grant");
        feedback(source, "command.automationera.advancement.help.alias");
        return 1;
    }

    private static int sendStatus(CommandSourceStack source, ServerPlayer player) {
        boolean perimeter = Automationera.isChunkAirSpace(player.level(), player.chunkPosition(), -59);
        boolean realPerimeter = Automationera.isChunkAirSpace(player.level(), player.chunkPosition(), -64);
        boolean aboveNether = player.level().dimension().equals(Level.NETHER) && player.getBlockY() > 127;
        List<String> completed = getCompletedImportantAdvancements(source.getServer(), player);

        feedback(source, "command.automationera.advancement.status.player", player.getName().getString());
        feedback(source, "command.automationera.advancement.status.position",
                player.level().dimension().identifier().toString(),
                player.getBlockX(),
                player.getBlockY(),
                player.getBlockZ());
        feedback(source, "command.automationera.advancement.status.online", source.getServer().getPlayerCount());
        feedback(source, "command.automationera.advancement.status.airspace", yesNo(perimeter), yesNo(realPerimeter));
        feedback(source, "command.automationera.advancement.status.above_nether", yesNo(aboveNether));
        feedback(source, "command.automationera.advancement.status.completed",
                completed.isEmpty() ? "-" : String.join(", ", completed));
        return 1;
    }

    private static int rescan(CommandSourceStack source, ServerPlayer player) {
        int granted = 0;
        boolean perimeter = Automationera.isChunkAirSpace(player.level(), player.chunkPosition(), -59);
        boolean realPerimeter = Automationera.isChunkAirSpace(player.level(), player.chunkPosition(), -64);

        if (player.level().dimension().equals(Level.NETHER) && player.getBlockY() > 127) {
            granted += grantAllCriteria(source.getServer(), player, "abovenether");
        }

        if (player.level().dimension().equals(Level.OVERWORLD)) {
            if (perimeter) {
                granted += grantAllCriteria(source.getServer(), player, "perimeter");
            }
            if (realPerimeter) {
                granted += grantAllCriteria(source.getServer(), player, "realperimeter");
            }
        } else if (player.level().dimension().equals(Level.NETHER)) {
            if (realPerimeter) {
                granted += grantAllCriteria(source.getServer(), player, "realperimeter");
            }
            if (perimeter) {
                granted += grantAllCriteria(source.getServer(), player, "netherperimeter");
            }
        }

        int online = source.getServer().getPlayerCount();
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

    private static int grantAdvancement(CommandSourceStack source, ServerPlayer player, String value) {
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

    private static int grantAllCriteria(MinecraftServer server, ServerPlayer player, String path) {
        return grantAllCriteria(server, player, Identifier.fromNamespaceAndPath(Automationera.MOD_ID, path));
    }

    private static int grantAllCriteria(MinecraftServer server, ServerPlayer player, Identifier id) {
        AdvancementHolder advancement = server.getAdvancements().get(id);
        if (advancement == null) {
            return 0;
        }

        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
        int granted = 0;
        for (String criterion : progress.getRemainingCriteria()) {
            if (player.getAdvancements().award(advancement, criterion)) {
                granted++;
            }
        }
        return granted;
    }

    private static Identifier parseAutomationEraAdvancement(CommandSourceStack source, String value) {
        try {
            Identifier id = value.contains(":") ? Identifier.parse(value) : Identifier.fromNamespaceAndPath(Automationera.MOD_ID, value);
            if (!Automationera.MOD_ID.equals(id.getNamespace())) {
                source.sendFailure(Component.translatable("command.automationera.advancement.error.namespace"));
                return null;
            }
            if (source.getServer().getAdvancements().get(id) == null) {
                source.sendFailure(Component.translatable("command.automationera.advancement.error.unknown", id.toString()));
                return null;
            }
            return id;
        } catch (IdentifierException exception) {
            source.sendFailure(Component.translatable("command.automationera.advancement.error.invalid", value));
            return null;
        }
    }

    private static List<String> getCompletedImportantAdvancements(MinecraftServer server, ServerPlayer player) {
        List<String> completed = new ArrayList<>();
        for (String path : SUGGESTED_ADVANCEMENTS) {
            AdvancementHolder advancement = server.getAdvancements().get(Identifier.fromNamespaceAndPath(Automationera.MOD_ID, path));
            if (advancement != null && player.getAdvancements().getOrStartProgress(advancement).isDone()) {
                completed.add(path);
            }
        }
        return completed;
    }

    private static Component yesNo(boolean value) {
        return Component.translatable(value
                ? "command.automationera.advancement.yes"
                : "command.automationera.advancement.no");
    }

    private static void feedback(CommandSourceStack source, String key, Object... args) {
        source.sendSuccess(() -> Component.translatable(key, args), false);
    }
}
