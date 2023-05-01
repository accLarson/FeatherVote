package com.zerek.feathervote.listeners;

import com.zerek.feathervote.FeatherVote;
import com.zerek.feathervote.managers.VoterManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final FeatherVote plugin;

    private VoterManager voterManager;

    private String currentYearMonth;

    public PlayerJoinListener(FeatherVote plugin) {

        this.plugin = plugin;

        this.init();
    }

    private void init() {

        this.voterManager = this.plugin.getVoterManager();

        this.currentYearMonth = plugin.getYearMonthUtility().getCurrentYearMonth();

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        if (voterManager.isRewardOwed(player) && !player.hasPermission("feather.vote.postponereward")) {

            voterManager.getRewardOwingMonths(player).forEach(yearMonth -> voterManager.rewardPlayer(event.getPlayer(), yearMonth, false));

            voterManager.informPostponeRewards(player);
        }
    }
}
