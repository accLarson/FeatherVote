package com.zerek.feathervote.commands;

import com.zerek.feathervote.FeatherVote;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;


public class VoteCommand implements CommandExecutor {

    private final FeatherVote plugin;

    public VoteCommand(FeatherVote plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length == 0) plugin.getMessagesManager().displayVoteSites(sender);

        else if (args.length == 1 && args[0].equalsIgnoreCase("leaderboard")) plugin.getMessagesManager().displayTopVoters(sender);

        else if (args.length == 1 && args[0].equalsIgnoreCase("reset") && sender instanceof ConsoleCommandSender) plugin.getVoterManager().newMonthReset();

        else sender.sendMessage(plugin.getMessagesManager().getMessageAsComponent("ErrorInvalid"));

        return true;
    }
}