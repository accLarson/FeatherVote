package com.zerek.feathervote.listeners;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import com.zerek.feathervote.FeatherVote;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class VotifierListener implements Listener{


    private final FeatherVote plugin;
    public VotifierListener(FeatherVote plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onVote(final VotifierEvent event) {
        //Get vote data
        Vote vote = event.getVote();

        //Get username provided from vote data
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(vote.getUsername());

        //Check if player is online
        plugin.getVoteManager().addVote(offlinePlayer);
    }
}
