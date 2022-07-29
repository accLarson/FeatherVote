package com.zerek.feathervote;

import com.zerek.feathervote.commands.VoteCommand;
import com.zerek.feathervote.commands.VoteTabCompleter;
import com.zerek.feathervote.listeners.VotifierListener;
import com.zerek.feathervote.managers.DatabaseManager;
import com.zerek.feathervote.managers.VoteManager;
import com.zerek.feathervote.utilities.ChatUtility;
import com.zerek.feathervote.utilities.RewardUtility;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Calendar;

public final class FeatherVote extends JavaPlugin {

    private DatabaseManager databaseManager;
    private VoteManager voteManager;

    private RewardUtility rewardUtility;
    private ChatUtility chatUtility;

    @Override
    public void onEnable() {

        this.saveDefaultConfig();

        this.databaseManager = new DatabaseManager(this);
        this.rewardUtility = new RewardUtility(this);
        this.voteManager = new VoteManager(this);
        this.chatUtility = new ChatUtility(this);

        this.getCommand("vote").setExecutor(new VoteCommand(this));
        this.getCommand("vote").setTabCompleter(new VoteTabCompleter());

        getServer().getPluginManager().registerEvents(new VotifierListener(this),this);
    }

    @Override
    public void onDisable() {
        this.voteManager.updateDatabase();
    }

    public RewardUtility getRewardUtility() {
        return rewardUtility;
    }

    public VoteManager getVoteManager() {
        return voteManager;
    }

    public ChatUtility getChatUtility() {
        return chatUtility;
    }
}
