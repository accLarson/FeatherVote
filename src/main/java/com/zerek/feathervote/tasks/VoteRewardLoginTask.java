package com.zerek.feathervote.tasks;

import com.zerek.feathervote.FeatherVote;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class VoteRewardLoginTask extends BukkitRunnable {

    private final FeatherVote plugin;

    private final Player player;

    public String currentYearMonth;

    public String previousYearMonth;


    public VoteRewardLoginTask(FeatherVote plugin, Player player) {

        this.plugin = plugin;

        this.player = player;

        this.currentYearMonth = plugin.getCurrentYearMonth();

        this.previousYearMonth = plugin.getPreviousYearMonth();
    }

    @Override
    public void run() {

        int offlineVotes = 0;

        if (plugin.getVoterManager().isRewardsOwed(player, currentYearMonth)) {

            plugin.getVoterManager().rewardPlayer(player, currentYearMonth,false);

            plugin.getVoterManager().informOfflineVotes(player);
        }
    }
}
