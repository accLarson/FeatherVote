package com.zerek.feathervote.managers;

import com.zerek.feathervote.FeatherVote;
import com.zerek.feathervote.data.Voter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class VoterManager {

    private final FeatherVote plugin;

    private final Random rand = new Random();

    private MessagesManager messagesManager;

    private List<ItemStack> rewardOptionsMap = new ArrayList<>();



    public VoterManager(FeatherVote plugin) {

        this.plugin = plugin;

        this.init();
    }


    private void init() {

        this.rewardOptionsMap = plugin.getConfigManager().getRewardOptionsMap();

        this.messagesManager = plugin.getMessagesManager();
    }


    private boolean isVoter(OfflinePlayer offlinePlayer){

        return Voter.exists(offlinePlayer.getUniqueId().toString());
    }


    public void processVote(OfflinePlayer offlinePlayer) {

        String uuid = offlinePlayer.getUniqueId().toString();

        if (!isVoter(offlinePlayer)) new Voter().set("mojang_uuid", uuid , "updated_at", System.currentTimeMillis()).insert();

        Voter voter = Voter.findById(uuid);

        voter.set(
                "updated_at", System.currentTimeMillis(),
                "vote_count_total", voter.getInteger("vote_count_total") + 1,
                "vote_count_current_month", voter.getInteger("vote_count_current_month") + 1 )
                .saveIt();

        this.addRewardOwedCount(offlinePlayer);

        if (voter.getInteger("vote_count_current_month") == 64) this.addSpecialRewardOwedCount(offlinePlayer);

        if (offlinePlayer.isOnline()) {

            if (voter.getInteger("rewards_owed") > 0) {

                ItemStack reward = this.generateReward();

                this.rewardPlayer((Player) offlinePlayer, reward);

                this.announceVote((Player) offlinePlayer, reward);
            }

            if (voter.getInteger("special_rewards_owed") > 0) {

                ItemStack specialReward = this.generateSpecialReward();

                this.specialRewardPlayer((Player) offlinePlayer, specialReward);

                this.announceSpecialVote((Player) offlinePlayer, specialReward);

            }
        }

        else {

            if (voter.getInteger("rewards_owed") > 0) this.announceVote(offlinePlayer);

            if (voter.getInteger("special_rewards_owed") > 0) this.announceSpecialVote(offlinePlayer);
        }

    }


    public ItemStack generateReward() {
        return rewardOptionsMap.get(rand.nextInt(rewardOptionsMap.size()));
    }


    public ItemStack generateSpecialReward() {
        return new ItemStack(Material.PAPER, 1);
    }



    public void rewardPlayer(Player player, ItemStack reward) {

        player.getInventory().addItem(reward).forEach((index, itemStack) -> {

            Item item = player.getWorld().dropItem(player.getLocation(), itemStack);

            item.setOwner(player.getUniqueId());

            this.removeRewardOwedCount(player);
        });
    }


    public void specialRewardPlayer(Player player, ItemStack specialReward) {

        player.getInventory().addItem(specialReward).forEach((index, itemStack) -> {

            Item item = player.getWorld().dropItem(player.getLocation(), itemStack);

            item.setOwner(player.getUniqueId());

            this.removeSpecialRewardOwedCount(player);
        });
    }


    private void announceVote(Player player, ItemStack reward) {

        plugin.getServer().broadcast(MiniMessage.miniMessage().deserialize(messagesManager.getMessageAsString("OnlineAnnounce"),
                Placeholder.unparsed("player",player.getName()),
                Placeholder.unparsed("reward", reward.getAmount() + " " + reward.getType().name().replace("_", " ").toLowerCase() + "s"),
                Placeholder.unparsed("votes", String.valueOf(plugin.getVoterManager().getCurrentMonthVoteCount(player)))));
    }


    public void announceVote(OfflinePlayer offlinePlayer) {

        plugin.getServer().broadcast(MiniMessage.miniMessage().deserialize(messagesManager.getMessageAsString("OfflineAnnounce"),
                Placeholder.unparsed("player",offlinePlayer.getName()),
                Placeholder.unparsed("votes", String.valueOf(plugin.getVoterManager().getCurrentMonthVoteCount(offlinePlayer)))));
    }


    private void announceSpecialVote(Player player, ItemStack reward) {

        plugin.getServer().broadcast(MiniMessage.miniMessage().deserialize(messagesManager.getMessageAsString("OnlineSpecialAnnounce"),
                Placeholder.unparsed("player",player.getName()),
                Placeholder.unparsed("reward", reward.getAmount() + " " + reward.getType().name().replace("_", " ").toLowerCase() + "s"),
                Placeholder.unparsed("votes", String.valueOf(plugin.getVoterManager().getCurrentMonthVoteCount(player)))));
    }


    public void announceSpecialVote(OfflinePlayer offlinePlayer) {

        plugin.getServer().broadcast(MiniMessage.miniMessage().deserialize(messagesManager.getMessageAsString("OfflineSpecialAnnounce"),
                Placeholder.unparsed("player",offlinePlayer.getName()),
                Placeholder.unparsed("votes", String.valueOf(plugin.getVoterManager().getCurrentMonthVoteCount(offlinePlayer)))));
    }


    public void informOfflineVotes(Player player, int rewardCount) {
        player.sendMessage(MiniMessage.miniMessage().deserialize(messagesManager.getMessageAsString("LoginVoteNotice"),
                Placeholder.unparsed("votes", String.valueOf(rewardCount))));
    }


    public void newMonthReset() {
        Voter.findAll().forEach(voter -> {
            voter.setInteger("vote_count_previous_month", voter.getInteger("vote_count_current_month"));
            voter.setInteger("vote_count_current_month", 0);
        });
    }


    public List<OfflinePlayer> getTop10Voters() {

        return Voter.findAll().orderBy("vote_count_current_month desc").limit(10).stream().map(voter -> plugin.getServer().getOfflinePlayer(voter.getString("mojang_uuid"))).collect(Collectors.toList());
    }


    private void addRewardOwedCount(OfflinePlayer offlinePlayer) {

        Voter voter = Voter.findById(offlinePlayer.getUniqueId().toString());

        voter.set("rewards_owed", voter.getInteger("rewards_owed") + 1);

        voter.saveIt();
    }


    private void addSpecialRewardOwedCount(OfflinePlayer offlinePlayer) {

        Voter voter = Voter.findById(offlinePlayer.getUniqueId().toString());

        voter.set("special_rewards_owed", voter.getInteger("special_rewards_owed") + 1);

        voter.saveIt();
    }


    private void removeRewardOwedCount(OfflinePlayer offlinePlayer) {

        Voter voter = Voter.findById(offlinePlayer.getUniqueId().toString());

        voter.set("rewards_owed", voter.getInteger("rewards_owed") - 1);

        voter.saveIt();
    }


    private void removeSpecialRewardOwedCount(OfflinePlayer offlinePlayer) {

        Voter voter = Voter.findById(offlinePlayer.getUniqueId().toString());

        voter.set("special_rewards_owed", voter.getInteger("special_rewards_owed") - 1);

        voter.saveIt();
    }


    public int getRewardsOwedCount(OfflinePlayer offlinePlayer) {

        if (isVoter(offlinePlayer)) return Voter.findById(offlinePlayer.getUniqueId().toString()).getInteger("rewards_owed");

        else return 0;
    }


    public int getSpecialRewardsOwedCount(OfflinePlayer offlinePlayer) {

        if (isVoter(offlinePlayer)) return Voter.findById(offlinePlayer.getUniqueId().toString()).getInteger("special_rewards_owed");

        else return 0;
    }


    public int getCurrentMonthVoteCount(OfflinePlayer offlinePlayer) {

        if (isVoter(offlinePlayer)) return Voter.findById(offlinePlayer.getUniqueId().toString()).getInteger("vote_count_current_month");

        else return 0;
    }


    public int getPreviousMonthVoteCount(OfflinePlayer offlinePlayer) {

        if (isVoter(offlinePlayer)) return Voter.findById(offlinePlayer.getUniqueId().toString()).getInteger("vote_count_previous_month");

        else return 0;
    }


    public int getTotalVoteCount(OfflinePlayer offlinePlayer) {

        if (isVoter(offlinePlayer)) return Voter.findById(offlinePlayer.getUniqueId().toString()).getInteger("vote_count_total");

        else return 0;
    }
}


