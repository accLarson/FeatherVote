package dev.zerek.feathervote.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class VoteTabCompleter implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        switch (args.length) {

            case 1:

                List<String> options = new ArrayList<>();

                if (sender.hasPermission("feather.vote.leaderboard")) options.add("leaderboard");

                if (sender.hasPermission("feather.vote.history")) options.add("history");

                List<String> match = new ArrayList<>();

                for (String option : options) if (option.toLowerCase().startsWith(args[0].toLowerCase())) match.add(option);

                return match;

            case 2:

                if (args[0].equalsIgnoreCase("history")) return null;

                else return new ArrayList<>();

            default:

                return new ArrayList<>();
        }
    }
}
