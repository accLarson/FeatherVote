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

        sender.sendMessage(this.getMessageAsComponent("PrefixLine")
                .append(Component.text("\n")).append(MiniMessage.miniMessage().deserialize(messagesMap.get("VoteSites"), Placeholder.parsed("username", sender.getName())))
                .append(Component.text("\n")).append(this.getMessageAsComponent("SuffixLine")));
    }


    public void displayMonthlyTopVoters(CommandSender sender) {

        VoterManager voterManager = plugin.getVoterManager();

        String currentYearMonth = plugin.getYearMonthUtility().getCurrentYearMonth();
        String previousYearMonth1 = plugin.getYearMonthUtility().getPreviousYearMonth(1);
        String previousYearMonth2 = plugin.getYearMonthUtility().getPreviousYearMonth(2);
        String previousYearMonth3 = plugin.getYearMonthUtility().getPreviousYearMonth(3);

        List<String> uuidsCurrentMonth = voterManager.getCurrentMonthTopVoters(7);

        Component leaderboard = getMessageAsComponent("PrefixLine");

        leaderboard = leaderboard.append(MiniMessage.miniMessage().deserialize("<br>" + messagesMap.get("MonthHeader"),
                Placeholder.component("voter", plugin.getChatUtility().addSpacing(Component.text("Top Month Voter"),96)),
                Placeholder.component("month_votes", plugin.getChatUtility().addSpacing(Component.text(currentYearMonth),46,true)),
                Placeholder.component("previous_votes_1", plugin.getChatUtility().addSpacing(Component.text(previousYearMonth1),46,true)),
                Placeholder.component("previous_votes_2", plugin.getChatUtility().addSpacing(Component.text(previousYearMonth2),46,true)),
                Placeholder.component("previous_votes_3", plugin.getChatUtility().addSpacing(Component.text(previousYearMonth3),46,true)),
                Placeholder.component("total_votes", plugin.getChatUtility().addSpacing(Component.text("Total"),38,true))));

        for (String uuid : uuidsCurrentMonth) {
            String name = "NullUsername";
            if (Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName() != null) {
                name = Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName();
            }

            leaderboard = leaderboard.append(MiniMessage.miniMessage().deserialize("<br>" + messagesMap.get("MonthEntry"),
                    Placeholder.component("voter", plugin.getChatUtility().addSpacing(Component.text(name), 96)),
                    Placeholder.component("month_votes", plugin.getChatUtility().addSpacing(Component.text(voterManager.getMonthVoteCount(uuid, currentYearMonth)), 46, true)),
                    Placeholder.component("previous_votes_1", plugin.getChatUtility().addSpacing(Component.text(voterManager.getMonthVoteCount(uuid, previousYearMonth1)),46,true)),
                    Placeholder.component("previous_votes_2", plugin.getChatUtility().addSpacing(Component.text(voterManager.getMonthVoteCount(uuid, previousYearMonth2)),46,true)),
                    Placeholder.component("previous_votes_3", plugin.getChatUtility().addSpacing(Component.text(voterManager.getMonthVoteCount(uuid, previousYearMonth3)),46,true)),
                    Placeholder.component("total_votes", plugin.getChatUtility().addSpacing(Component.text(voterManager.getTotalVoteCount(uuid)),38,true))));
        }

        leaderboard = leaderboard.append(MiniMessage.miniMessage().deserialize("<br>"));

        leaderboard = leaderboard.append(getMessageAsComponent("SuffixLine"));

        sender.sendMessage(leaderboard);
    }


    public void displayAllTimeTopVoters(CommandSender sender) {

        VoterManager voterManager = plugin.getVoterManager();

        String currentYearMonth = plugin.getYearMonthUtility().getCurrentYearMonth();
        String previousYearMonth1 = plugin.getYearMonthUtility().getPreviousYearMonth(1);
        String previousYearMonth2 = plugin.getYearMonthUtility().getPreviousYearMonth(2);
        String previousYearMonth3 = plugin.getYearMonthUtility().getPreviousYearMonth(3);

        List<String> uuidsAllTime = voterManager.getAllTimeTopVoters(7);

        Component leaderboard = getMessageAsComponent("PrefixLine");

        leaderboard = leaderboard.append(MiniMessage.miniMessage().deserialize("<br>" + messagesMap.get("AllTimeHeader"),
                Placeholder.component("voter", plugin.getChatUtility().addSpacing(Component.text("Top All-Time Voter"),96)),
                Placeholder.component("month_votes", plugin.getChatUtility().addSpacing(Component.text(currentYearMonth),46,true)),
                Placeholder.component("previous_votes_1", plugin.getChatUtility().addSpacing(Component.text(previousYearMonth1),46,true)),
                Placeholder.component("previous_votes_2", plugin.getChatUtility().addSpacing(Component.text(previousYearMonth2),46,true)),
                Placeholder.component("previous_votes_3", plugin.getChatUtility().addSpacing(Component.text(previousYearMonth3),46,true)),
                Placeholder.component("total_votes", plugin.getChatUtility().addSpacing(Component.text("Total"),38,true))));


        for (String uuid : uuidsAllTime) {

            String name = "NullUsername";
            if (Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName() != null) {
                name = Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName();
            }

            leaderboard = leaderboard.append(MiniMessage.miniMessage().deserialize("<br>" + messagesMap.get("AllTimeEntry"),
                    Placeholder.component("voter", plugin.getChatUtility().addSpacing(Component.text(name), 96)),
                    Placeholder.component("month_votes", plugin.getChatUtility().addSpacing(Component.text(voterManager.getMonthVoteCount(uuid, currentYearMonth)), 46, true)),
                    Placeholder.component("previous_votes_1", plugin.getChatUtility().addSpacing(Component.text(voterManager.getMonthVoteCount(uuid, previousYearMonth1)),46,true)),
                    Placeholder.component("previous_votes_2", plugin.getChatUtility().addSpacing(Component.text(voterManager.getMonthVoteCount(uuid, previousYearMonth2)),46,true)),
                    Placeholder.component("previous_votes_3", plugin.getChatUtility().addSpacing(Component.text(voterManager.getMonthVoteCount(uuid, previousYearMonth3)),46,true)),
                    Placeholder.component("total_votes", plugin.getChatUtility().addSpacing(Component.text(voterManager.getTotalVoteCount(uuid)),38,true))));
        }

        leaderboard = leaderboard.append(MiniMessage.miniMessage().deserialize("<br>"));

        leaderboard = leaderboard.append(getMessageAsComponent("SuffixLine"));

        sender.sendMessage(leaderboard);
    }


    public void displayVoterHistory(CommandSender sender, String uuid) {

        VoterManager voterManager = plugin.getVoterManager();

        String currentYearMonth = plugin.getYearMonthUtility().getCurrentYearMonth();
        String previousYearMonth1 = plugin.getYearMonthUtility().getPreviousYearMonth(1);
        String previousYearMonth2 = plugin.getYearMonthUtility().getPreviousYearMonth(2);
        String previousYearMonth3 = plugin.getYearMonthUtility().getPreviousYearMonth(3);

        Component history = getMessageAsComponent("PrefixLine");

        history = history.append(MiniMessage.miniMessage().deserialize("<br>" + messagesMap.get("HistoryHeader"),
                Placeholder.component("voter", plugin.getChatUtility().addSpacing(Component.text("Voter"),96)),
                Placeholder.component("month_votes", plugin.getChatUtility().addSpacing(Component.text(currentYearMonth),46,true)),
                Placeholder.component("previous_votes_1", plugin.getChatUtility().addSpacing(Component.text(previousYearMonth1),46,true)),
                Placeholder.component("previous_votes_2", plugin.getChatUtility().addSpacing(Component.text(previousYearMonth2),46,true)),
                Placeholder.component("previous_votes_3", plugin.getChatUtility().addSpacing(Component.text(previousYearMonth3),46,true)),
                Placeholder.component("total_votes", plugin.getChatUtility().addSpacing(Component.text("Total"),38,true))));


        history = history.append(MiniMessage.miniMessage().deserialize("<br>" + messagesMap.get("HistoryEntry"),
                Placeholder.component("voter", plugin.getChatUtility().addSpacing(Component.text(Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName()), 96)),
                Placeholder.component("month_votes", plugin.getChatUtility().addSpacing(Component.text(voterManager.getMonthVoteCount(uuid, currentYearMonth)), 46, true)),
                Placeholder.component("previous_votes_1", plugin.getChatUtility().addSpacing(Component.text(voterManager.getMonthVoteCount(uuid, previousYearMonth1)),46,true)),
                Placeholder.component("previous_votes_2", plugin.getChatUtility().addSpacing(Component.text(voterManager.getMonthVoteCount(uuid, previousYearMonth2)),46,true)),
                Placeholder.component("previous_votes_3", plugin.getChatUtility().addSpacing(Component.text(voterManager.getMonthVoteCount(uuid, previousYearMonth3)),46,true)),
                Placeholder.component("total_votes", plugin.getChatUtility().addSpacing(Component.text(voterManager.getTotalVoteCount(uuid)),38,true))));


        history = history.append(MiniMessage.miniMessage().deserialize("<br>"));

        history = history.append(getMessageAsComponent("SuffixLine"));

        sender.sendMessage(history);
    }


    public String getMessageAsString(String key) {

        return messagesMap.get(key);
    }


    public Component getMessageAsComponent(String key) {

        return MiniMessage.miniMessage().deserialize(messagesMap.get(key));
    }


}
