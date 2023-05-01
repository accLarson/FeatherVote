package dev.zerek.feathervote;

import dev.zerek.feathervote.commands.VoteCommand;
import dev.zerek.feathervote.commands.VoteTabCompleter;
import dev.zerek.feathervote.listeners.PlayerJoinListener;
import dev.zerek.feathervote.listeners.VotifierListener;
import dev.zerek.feathervote.managers.ConfigManager;
import dev.zerek.feathervote.managers.DatabaseManager;
import dev.zerek.feathervote.managers.MessagesManager;
import dev.zerek.feathervote.managers.VoterManager;
import dev.zerek.feathervote.utilities.ChatUtility;
import dev.zerek.feathervote.utilities.ItemLabelUtility;
import dev.zerek.feathervote.utilities.YearMonthUtility;
import org.bukkit.plugin.java.JavaPlugin;

public final class FeatherVote extends JavaPlugin {

    private ConfigManager configManager;

    private MessagesManager messagesManager;

    private VoterManager voterManager;

    private DatabaseManager databaseManager;

    private ChatUtility chatUtility;

    private ItemLabelUtility itemLabelUtility;

    private YearMonthUtility yearMonthUtility;




    @Override
    public void onEnable() {

        this.chatUtility = new ChatUtility(this);

        this.itemLabelUtility = new ItemLabelUtility(this);

        this.yearMonthUtility = new YearMonthUtility(this);

        this.configManager = new ConfigManager(this);

        this.messagesManager = new MessagesManager(this);

        this.voterManager = new VoterManager(this);

        this.databaseManager = new DatabaseManager(this);

        this.getCommand("vote").setExecutor(new VoteCommand(this));

        this.getCommand("vote").setTabCompleter(new VoteTabCompleter());

        getServer().getPluginManager().registerEvents(new VotifierListener(this),this);

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this),this);
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

    public VoterManager getVoterManager() {
        return voterManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public ChatUtility getChatUtility() {
        return chatUtility;
    }

    public ItemLabelUtility getItemLabelUtility() {
        return itemLabelUtility;
    }

    public YearMonthUtility getYearMonthUtility() {
        return yearMonthUtility;
    }
}
