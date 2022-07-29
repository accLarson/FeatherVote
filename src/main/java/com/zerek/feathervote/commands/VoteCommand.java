package com.zerek.feathervote.commands;

import com.zerek.feathervote.FeatherVote;
import net.md_5.bungee.api.ChatColor;
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

        if (args.length == 0) plugin.getVoteManager().displayVoteSites(sender);
        else if (args.length == 1 && args[0].equalsIgnoreCase("leaderboard")) plugin.getVoteManager().displayTopVoters(10,sender);
        else if (sender instanceof ConsoleCommandSender && args.length == 1 && args[0].equalsIgnoreCase("reset")) plugin.getVoteManager().clearVotes();
        else sender.sendMessage(ChatColor.of("#656b96") + "invalid command");
        return true;
    }
}
