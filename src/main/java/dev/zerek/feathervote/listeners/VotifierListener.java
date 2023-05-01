package dev.zerek.feathervote.listeners;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import dev.zerek.feathervote.FeatherVote;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class VotifierListener implements Listener{


    private final FeatherVote plugin;
    public VotifierListener(FeatherVote plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onVote(final VotifierEvent event) {

        Vote vote = event.getVote();

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(vote.getUsername());

        if (offlinePlayer.hasPlayedBefore() || offlinePlayer.isOnline()) plugin.getVoterManager().processVote(offlinePlayer);

        else plugin.getLogger().info("Received a vote from unknown username: vote.getUsername() - no reward processed." );
    }
}
