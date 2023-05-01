package dev.zerek.feathervote.managers;

import dev.zerek.feathervote.FeatherVote;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MessagesManager {

    private final FeatherVote plugin;

    File file;

    private FileConfiguration yml;

    private final Map<String,String> messagesMap = new HashMap<>();

    public String currentYearMonth, previousYearMonth1, previousYearMonth2, previousYearMonth3;

    public MessagesManager(FeatherVote plugin) {

        this.plugin = plugin;

        this.init();
    }

    private void init() {

        if (!new File(plugin.getDataFolder(), "messages.yml").exists()) plugin.saveResource("messages.yml", false);

        file = new File(plugin.getDataFolder() + File.separator + "messages.yml");

        yml = YamlConfiguration.loadConfiguration(file);

        this.generateMessagesMap();

        this.currentYearMonth = plugin.getYearMonthUtility().getCurrentYearMonth();

        this.previousYearMonth1 = plugin.getYearMonthUtility().getPreviousYearMonth(1);

        this.previousYearMonth2 = plugin.getYearMonthUtility().getPreviousYearMonth(2);

        this.previousYearMonth3 = plugin.getYearMonthUtility().getPreviousYearMonth(3);
    }

    private void generateMessagesMap() {

        yml.getKeys(false).forEach(m -> messagesMap.put(m, yml.getString(m)));
    }

    public void displayVoteSites(CommandSender sender) {

        sender.sendMessage(this.getMessageAsComponent("PrefixLine")
                .append(Component.text("\n")).append(MiniMessage.miniMessage().deserialize(messagesMap.get("VoteSites"), Placeholder.parsed("username", sender.getName())))
                .append(Component.text("\n")).append(this.getMessageAsComponent("SuffixLine")));
    }


    public void displayTopVoters(CommandSender sender) {

        VoterManager voterManager = plugin.getVoterManager();

        List<String> uuids = voterManager.getCurrentMonthTop10Voters();

        Component leaderboard = getMessageAsComponent("PrefixLine");

        leaderboard = leaderboard.append(MiniMessage.miniMessage().deserialize("<br>" + messagesMap.get("Header"),
                Placeholder.component("voter", plugin.getChatUtility().addSpacing(Component.text("Voter"),80)),
                Placeholder.component("month_votes", plugin.getChatUtility().addSpacing(Component.text(currentYearMonth),46,true)),
                Placeholder.component("previous_votes_1", plugin.getChatUtility().addSpacing(Component.text(previousYearMonth1),46,true)),
                Placeholder.component("previous_votes_2", plugin.getChatUtility().addSpacing(Component.text(previousYearMonth2),46,true)),
                Placeholder.component("previous_votes_3", plugin.getChatUtility().addSpacing(Component.text(previousYearMonth3),46,true)),
                Placeholder.component("total_votes", plugin.getChatUtility().addSpacing(Component.text("Total"),40,true))));

        for (String uuid : uuids) {

            leaderboard = leaderboard.append(MiniMessage.miniMessage().deserialize("<br>" + messagesMap.get("Entry"),
                    Placeholder.component("voter", plugin.getChatUtility().addSpacing(Component.text(Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName()), 80)),
                    Placeholder.component("month_votes", plugin.getChatUtility().addSpacing(Component.text(voterManager.getMonthVoteCount(uuid, currentYearMonth)), 46, true)),
                    Placeholder.component("previous_votes_1", plugin.getChatUtility().addSpacing(Component.text(voterManager.getMonthVoteCount(uuid, previousYearMonth1)),46,true)),
                    Placeholder.component("previous_votes_2", plugin.getChatUtility().addSpacing(Component.text(voterManager.getMonthVoteCount(uuid, previousYearMonth2)),46,true)),
                    Placeholder.component("previous_votes_3", plugin.getChatUtility().addSpacing(Component.text(voterManager.getMonthVoteCount(uuid, previousYearMonth3)),46,true)),
                    Placeholder.component("total_votes", plugin.getChatUtility().addSpacing(Component.text(voterManager.getTotalVoteCount(uuid)),40,true))));
        }

        leaderboard = leaderboard.append(MiniMessage.miniMessage().deserialize("<br>"));

        leaderboard = leaderboard.append(getMessageAsComponent("SuffixLine"));

        sender.sendMessage(leaderboard);
    }


    public void displayVoterHistory(CommandSender sender, String uuid) {

        VoterManager voterManager = plugin.getVoterManager();

        Component leaderboard = getMessageAsComponent("PrefixLine");

        leaderboard = leaderboard.append(MiniMessage.miniMessage().deserialize("<br>" + messagesMap.get("Header"),
                Placeholder.component("voter", plugin.getChatUtility().addSpacing(Component.text("Voter"),80)),
                Placeholder.component("month_votes", plugin.getChatUtility().addSpacing(Component.text(currentYearMonth),46,true)),
                Placeholder.component("previous_votes_1", plugin.getChatUtility().addSpacing(Component.text(previousYearMonth1),46,true)),
                Placeholder.component("previous_votes_2", plugin.getChatUtility().addSpacing(Component.text(previousYearMonth2),46,true)),
                Placeholder.component("previous_votes_3", plugin.getChatUtility().addSpacing(Component.text(previousYearMonth3),46,true)),
                Placeholder.component("total_votes", plugin.getChatUtility().addSpacing(Component.text("Total"),40,true))));


        leaderboard = leaderboard.append(MiniMessage.miniMessage().deserialize("<br>" + messagesMap.get("Entry"),
                Placeholder.component("voter", plugin.getChatUtility().addSpacing(Component.text(Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName()), 80)),
                Placeholder.component("month_votes", plugin.getChatUtility().addSpacing(Component.text(voterManager.getMonthVoteCount(uuid, currentYearMonth)), 46, true)),
                Placeholder.component("previous_votes_1", plugin.getChatUtility().addSpacing(Component.text(voterManager.getMonthVoteCount(uuid, previousYearMonth1)),46,true)),
                Placeholder.component("previous_votes_2", plugin.getChatUtility().addSpacing(Component.text(voterManager.getMonthVoteCount(uuid, previousYearMonth2)),46,true)),
                Placeholder.component("previous_votes_3", plugin.getChatUtility().addSpacing(Component.text(voterManager.getMonthVoteCount(uuid, previousYearMonth3)),46,true)),
                Placeholder.component("total_votes", plugin.getChatUtility().addSpacing(Component.text(voterManager.getTotalVoteCount(uuid)),40,true))));


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

