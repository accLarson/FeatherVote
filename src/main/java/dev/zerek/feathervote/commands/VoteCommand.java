package dev.zerek.feathervote.commands;

import dev.zerek.feathervote.FeatherVote;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class VoteCommand implements CommandExecutor {

    private final FeatherVote plugin;

    public VoteCommand(FeatherVote plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length == 0) plugin.getMessagesManager().displayVoteSites(sender);

        else if (args.length == 1 && args[0].equalsIgnoreCase("leaderboard"))

            if (sender.hasPermission("feather.vote.leaderboard")) plugin.getMessagesManager().displayTopVoters(sender);

            else sender.sendMessage(plugin.getMessagesManager().getMessageAsComponent("ErrorNoPermission"));

        else if (args.length == 1 && args[0].equalsIgnoreCase("history")) {

            if (sender instanceof Player && sender.hasPermission("feather.vote.history")) plugin.getMessagesManager().displayVoterHistory(sender, ((Player) sender).getUniqueId().toString());

            else sender.sendMessage(plugin.getMessagesManager().getMessageAsComponent("ErrorNoPermission"));

        }

        else if (args.length == 2 && args[0].equalsIgnoreCase("history")) {

            if (sender.hasPermission("feather.vote.history")) {

                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);

                if (offlinePlayer.hasPlayedBefore() || offlinePlayer.isOnline()) plugin.getMessagesManager().displayVoterHistory(sender, offlinePlayer.getUniqueId().toString());

                else sender.sendMessage(plugin.getMessagesManager().getMessageAsComponent("ErrorUnresolvedPlayer"));
            }
        }

        else sender.sendMessage(plugin.getMessagesManager().getMessageAsComponent("ErrorInvalid"));

        return true;
    }
}