package com.zerek.feathervote;

import com.zerek.feathervote.commands.VoteCommand;
import com.zerek.feathervote.commands.VoteTabCompleter;
import com.zerek.feathervote.listeners.VotifierListener;
import com.zerek.feathervote.managers.ConfigManager;
import com.zerek.feathervote.managers.DatabaseManager;
import com.zerek.feathervote.managers.MessagesManager;
import com.zerek.feathervote.managers.VoterManager;
import com.zerek.feathervote.utilities.ChatUtility;
import org.bukkit.plugin.java.JavaPlugin;

public final class FeatherVote extends JavaPlugin {

    private ConfigManager configManager;

    private MessagesManager messagesManager;

    private DatabaseManager databaseManager;

    private VoterManager voterManager;
    private RewardManager rewardManager;
    private ChatUtility chatUtility;

    @Override
    public void onEnable() {

        this.configManager = new ConfigManager(this);

        this.messagesManager = new MessagesManager(this);

        this.databaseManager = new DatabaseManager(this);

        this.voterManager = new VoterManager(this);

        this.rewardManager = new RewardManager(this);

        this.chatUtility = new ChatUtility(this);

        this.getCommand("vote").setExecutor(new VoteCommand(this));

        this.getCommand("vote").setTabCompleter(new VoteTabCompleter());

        getServer().getPluginManager().registerEvents(new VotifierListener(this),this);
    }
    @Override
    public void onDisable() {
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MessagesManager getMessagesManager() {
        return messagesManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public RewardManager getRewardManager() {
        return rewardManager;
    }

    public VoterManager getVoterManager() {
        return voterManager;
    }

    public ChatUtility getChatUtility() {
        return chatUtility;
    }
}
