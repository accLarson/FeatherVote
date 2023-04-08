package com.zerek.feathervote.managers;

import com.zerek.feathervote.FeatherVote;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessagesManager {

    private final FeatherVote plugin;

    File file;

    private FileConfiguration yml;

    private final Map<String,String> messagesMap = new HashMap<>();

    public MessagesManager(FeatherVote plugin) {

        this.plugin = plugin;

        this.init();
    }

    private void init() {

        if (!new File(plugin.getDataFolder(), "messages.yml").exists()) plugin.saveResource("messages.yml", false);

        file = new File(plugin.getDataFolder() + File.separator + "messages.yml");

        yml = YamlConfiguration.loadConfiguration(file);

        this.generateMessagesMap();
    }

    private void generateMessagesMap() {

        yml.getKeys(false).forEach(m -> messagesMap.put(m, yml.getString(m)));
    }

    public void displayVoteSites(CommandSender sender) {

        sender.sendMessage(MiniMessage.miniMessage().deserialize(
                this.getMessageAsComponent("PrefixLine") +
                        "<br>" + this.getMessageAsComponent("VoteSites") +
                        "<br>" + this.getMessageAsComponent("SuffixLine")));
    }


    public void displayTopVoters(CommandSender sender) {

        VoterManager voterManager = plugin.getVoterManager();

        List<OfflinePlayer> topVoters = voterManager.getTop10Voters();

        Component leaderboard = getMessageAsComponent("PrefixLine");

        leaderboard = leaderboard.append(MiniMessage.miniMessage().deserialize("<br>" + messagesMap.get("LeaderboardHeader"),
                Placeholder.component("voter", plugin.getChatUtility().addSpacing(Component.text("Voter"),80)),
                Placeholder.component("month_votes", plugin.getChatUtility().addSpacing(Component.text("Votes"),36,true)),
                Placeholder.component("previous_votes", plugin.getChatUtility().addSpacing(Component.text("Prior"),36,true)),
                Placeholder.component("total_votes", plugin.getChatUtility().addSpacing(Component.text("Total"),36,true))));

        for (OfflinePlayer topVoter : topVoters) {

            leaderboard = leaderboard.append(MiniMessage.miniMessage().deserialize("<br>" + messagesMap.get("LeaderboardEntry"),
                    Placeholder.component("voter", plugin.getChatUtility().addSpacing(Component.text(topVoter.getName()), 80)),
                    Placeholder.component("month_votes", plugin.getChatUtility().addSpacing(Component.text(voterManager.getCurrentMonthVoteCount(topVoter)), 36, true)),
                    Placeholder.component("previous_votes", plugin.getChatUtility().addSpacing(Component.text(voterManager.getPreviousMonthVoteCount(topVoter)),36,true)),
                    Placeholder.component("total_votes", plugin.getChatUtility().addSpacing(Component.text(voterManager.getTotalVoteCount(topVoter)),36,true))));
        }

        leaderboard = leaderboard.append(MiniMessage.miniMessage().deserialize("<br>"));

        leaderboard = leaderboard.append(getMessageAsComponent("SuffixLine"));

        sender.sendMessage(leaderboard);
    }


    public String getMessageAsString(String key) {

        return messagesMap.get(key);
    }


    public Component getMessageAsComponent(String key) {

        return MiniMessage.miniMessage().deserialize(messagesMap.get(key));
    }


}

