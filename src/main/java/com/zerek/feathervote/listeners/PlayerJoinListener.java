package com.zerek.feathervote.listeners;

import com.zerek.feathervote.FeatherVote;
import com.zerek.feathervote.managers.VoterManager;
import com.zerek.feathervote.tasks.VoteRewardLoginTask;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final FeatherVote plugin;

    private VoterManager voterManager;

    public PlayerJoinListener(FeatherVote plugin) {

        this.plugin = plugin;

        this.init();
    }

    private void init() {

        this.voterManager = this.plugin.getVoterManager();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        new VoteRewardLoginTask(plugin, event.getPlayer()).runTaskAsynchronously(plugin);

    }
}
