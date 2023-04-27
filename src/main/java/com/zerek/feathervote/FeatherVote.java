package com.zerek.feathervote;

import com.zerek.feathervote.commands.VoteCommand;
import com.zerek.feathervote.commands.VoteTabCompleter;
import com.zerek.feathervote.listeners.VotifierListener;
import com.zerek.feathervote.managers.ConfigManager;
import com.zerek.feathervote.managers.DatabaseManager;
import com.zerek.feathervote.managers.MessagesManager;
import com.zerek.feathervote.managers.VoterManager;
import com.zerek.feathervote.utilities.ChatUtility;
import com.zerek.feathervote.utilities.ItemLabelUtility;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.LocalDate;
import java.time.Year;

public final class FeatherVote extends JavaPlugin {

    private ConfigManager configManager;

    private MessagesManager messagesManager;

    private DatabaseManager databaseManager;

    private VoterManager voterManager;

    private ChatUtility chatUtility;

    private ItemLabelUtility itemLabelUtility;

    private final String currentYearMonth = LocalDate.now().getYear() + "/" + LocalDate.now().getMonthValue();

    private String previousYearMonth;


    @Override
    public void onEnable() {

        this.configManager = new ConfigManager(this);

        this.messagesManager = new MessagesManager(this);

        this.databaseManager = new DatabaseManager(this);

        this.voterManager = new VoterManager(this);

        this.chatUtility = new ChatUtility(this);

        this.itemLabelUtility = new ItemLabelUtility(this);

        this.getCommand("vote").setExecutor(new VoteCommand(this));

        this.getCommand("vote").setTabCompleter(new VoteTabCompleter());

        getServer().getPluginManager().registerEvents(new VotifierListener(this),this);

        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();

        if (month == 1) year = year - 1;

        previousYearMonth = year + "/" + LocalDate.now().getMonth().minus(1).getValue();
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

    public VoterManager getVoterManager() {
        return voterManager;
    }

    public ChatUtility getChatUtility() {
        return chatUtility;
    }

    public ItemLabelUtility getItemLabelUtility() {
        return itemLabelUtility;
    }

    public String getCurrentYearMonth() {
        return currentYearMonth;
    }

    public String getPreviousYearMonth() {
        return previousYearMonth;
    }
}
