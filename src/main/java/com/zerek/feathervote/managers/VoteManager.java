package com.zerek.feathervote.managers;

import com.zerek.feathervote.FeatherVote;
import com.zerek.feathervote.data.Vote;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class VoteManager {

    private final FeatherVote plugin;
    private final String prefixLineMessage;
    private final String suffixLineMessage;
    private final String leaderboardHeaderMessage;
    private final String leaderboardEntryMessage;
    private final String voteSitesMessage;
    private final Map<OfflinePlayer, Integer> voters = new HashMap<>();

    public VoteManager(FeatherVote plugin) {
        this.plugin = plugin;
        this.leaderboardHeaderMessage = this.plugin.getConfig().getString("leaderboard.header");
        this.leaderboardEntryMessage = this.plugin.getConfig().getString("leaderboard.entry");
        this.prefixLineMessage = this.plugin.getConfig().getString("prefix-line");
        this.suffixLineMessage = this.plugin.getConfig().getString("suffix-line");
        this.voteSitesMessage = this.plugin.getConfig().getString("vote-sites");
        Vote.findAll().forEach(v -> voters.put(Bukkit.getOfflinePlayer(UUID.fromString(v.getString("mojang_uuid"))), v.getInteger("vote_count")));
    }


    private boolean hasPlayerVoted(OfflinePlayer offlinePlayer){
        return this.voters.containsKey(offlinePlayer);
    }

    private int getVoteCount(OfflinePlayer offlinePlayer){
        if (this.hasPlayerVoted(offlinePlayer)) return this.voters.get(offlinePlayer);
        else return 0;
    }

    public void addVote(OfflinePlayer offlinePlayer){
        if (offlinePlayer.hasPlayedBefore()){
            if (this.hasPlayerVoted(offlinePlayer)) this.voters.put(offlinePlayer, this.getVoteCount(offlinePlayer)+1);
            else this.voters.put(offlinePlayer, 1);
            if (offlinePlayer.isOnline()) plugin.getRewardUtility().rewardPlayer((Player) offlinePlayer, this.getVoteCount(offlinePlayer));
        }
    }

    public void displayTopVoters(int amount, CommandSender sender){
        List<OfflinePlayer> topVoters = this.voters.entrySet().stream().sorted(Map.Entry.<OfflinePlayer, Integer>comparingByValue().reversed()).limit(amount).map(Map.Entry::getKey).collect(Collectors.toList());

        Component leaderboard = MiniMessage.miniMessage().deserialize(prefixLineMessage);

        leaderboard = leaderboard.append(MiniMessage.miniMessage().deserialize("<br>" + leaderboardHeaderMessage,
                Placeholder.component("voter", plugin.getChatUtility().addSpacing(Component.text("Voter"),100)),
                Placeholder.component("votes", plugin.getChatUtility().addSpacing(Component.text("Votes"),30,true))));

        for (OfflinePlayer topVoter : topVoters) {
            leaderboard = leaderboard.append(MiniMessage.miniMessage().deserialize("<br>" + leaderboardEntryMessage,
                    Placeholder.component("voter", plugin.getChatUtility().addSpacing(Component.text(topVoter.getName()), 100)),
                    Placeholder.component("votes", plugin.getChatUtility().addSpacing(Component.text(this.voters.get(topVoter)), 30, true))));
        }

        leaderboard = leaderboard.append(MiniMessage.miniMessage().deserialize("<br>" + suffixLineMessage));

        sender.sendMessage(leaderboard);
    }

    public void displayVoteSites(CommandSender sender){
        sender.sendMessage(MiniMessage.miniMessage().deserialize(prefixLineMessage + "<br>" + voteSitesMessage + "<br>" + suffixLineMessage));
    }

    public void clearVotes(){
        this.voters.clear();
        Vote.deleteAll();
    }

    public void updateDatabase() {
        plugin.getLogger().info("writing to database.");
        this.voters.forEach((v, c) -> {
            Vote vote = new Vote().set("mojang_uuid", v.getUniqueId().toString(), "updated_at", System.currentTimeMillis(), "vote_count", c);
            if (Vote.exists(v.getUniqueId().toString())) vote.saveIt();
            else vote.insert();
        });
    }

}

// -------------------------------------------------------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------------------------------------------------------
//storing as uuid or name of offline or online is causing issues. ----------------------------------------------------------------------
// -------------------------------------------------------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------------------------------------------------------


