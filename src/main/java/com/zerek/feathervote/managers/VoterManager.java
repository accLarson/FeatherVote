package com.zerek.feathervote.managers;

import com.zerek.feathervote.FeatherVote;
import com.zerek.feathervote.data.Vote;
import com.zerek.feathervote.utilities.ItemLabelUtility;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

public class VoterManager {

    private final FeatherVote plugin;

    private final Random rand = new Random();

    private MessagesManager messagesManager;

    private List<ItemStack> rewardOptionsMap = new ArrayList<>();

    public String currentYearMonth, previousYearMonth;

    private ItemLabelUtility itemLabelUtility;



    public VoterManager(FeatherVote plugin) {

        this.plugin = plugin;

        this.init();

        this.currentYearMonth = plugin.getCurrentYearMonth();

        this.previousYearMonth = plugin.getPreviousYearMonth();
    }


    private void init() {

        this.rewardOptionsMap = plugin.getConfigManager().getRewardOptionsMap();

        this.messagesManager = plugin.getMessagesManager();

        this.itemLabelUtility = plugin.getItemLabelUtility();

    }


    private boolean isVoter(OfflinePlayer offlinePlayer, String yearMonth){

        return Vote.findByCompositeKeys(offlinePlayer.getUniqueId().toString(),yearMonth).exists();
    }


    public void processVote(OfflinePlayer offlinePlayer) {

        String uuid = offlinePlayer.getUniqueId().toString();

        if (!isVoter(offlinePlayer, currentYearMonth)) new Vote().set("mojang_uuid", uuid , "year_month", currentYearMonth).insert();

        Vote vote = Vote.findByCompositeKeys(uuid,currentYearMonth);

        vote.set(
                "votes", vote.getInteger("votes") + 1,
                "rewards_owed", vote.getInteger("rewards_owed") + 1 )
                .saveIt();


        if (vote.getInteger("votes") == 64) vote.setBoolean("special_reward_owed", 1).saveIt();

        // if they are online reward them, regular and special if applicable. (should just be the 1 vote just processed)
        if (offlinePlayer.isOnline()) this.rewardPlayer((Player) offlinePlayer, currentYearMonth,true);

        // if they are offline, announce the vote, regular and special if applicable.
        else this.offlineAnnounceVote(offlinePlayer, currentYearMonth);
    }


    public void rewardPlayer(Player player, String yearMonth, boolean broadcast) {

        if (isVoter(player,yearMonth)){

            Vote vote = Vote.findByCompositeKeys(player.getUniqueId().toString(),yearMonth);

            while (vote.getInteger("rewards_owed") > 0) {

                ItemStack reward = this.generateReward();

                player.getInventory().addItem(reward).forEach((index, itemStack) -> {

                    player.getWorld().dropItem(player.getLocation(), itemStack).setOwner(player.getUniqueId());

                    vote.setInteger("rewards_owed", vote.getInteger("rewards_owed") - 1).saveIt();
                });

                ItemStack formattedReward = itemLabelUtility.formatItemStack(reward.clone());

                if (broadcast) {

                    plugin.getServer().broadcast(MiniMessage.miniMessage().deserialize(messagesManager.getMessageAsString("OnlineAnnounce"),
                            Placeholder.unparsed("player",player.getName()),
                            Placeholder.component("reward",formattedReward.displayName().hoverEvent(formattedReward)),
                            Placeholder.unparsed("votes", String.valueOf(vote.getInteger("votes")))));
                }
            }

            if (vote.getBoolean("special_reward_owed")) {

                ItemStack specialReward = this.generateSpecialReward(player, yearMonth);

                player.getInventory().addItem(specialReward).forEach((index, itemStack) -> {

                    player.getWorld().dropItem(player.getLocation(), itemStack).setOwner(player.getUniqueId());

                    vote.setBoolean("special_reward_owed", false);
                });

                ItemStack formattedSpecialReward = itemLabelUtility.formatItemStack(specialReward.clone());

                if (broadcast) {

                    plugin.getServer().broadcast(MiniMessage.miniMessage().deserialize(messagesManager.getMessageAsString("OnlineSpecialAnnounce"),
                            Placeholder.unparsed("player", player.getName()),
                            Placeholder.component("reward", formattedSpecialReward.displayName().hoverEvent(formattedSpecialReward))));
                }
            }
        }
    }


    public void offlineAnnounceVote(OfflinePlayer offlinePlayer, String yearMonth) {

        Vote vote = Vote.findByCompositeKeys(offlinePlayer.getUniqueId().toString(),yearMonth);

        plugin.getServer().broadcast(MiniMessage.miniMessage().deserialize(messagesManager.getMessageAsString("OfflineAnnounce"),
                Placeholder.unparsed("player",offlinePlayer.getName()),
                Placeholder.unparsed("votes", String.valueOf(vote.getInteger("votes")))));

        if (vote.getBoolean("special_reward_owed")) {

            plugin.getServer().broadcast(MiniMessage.miniMessage().deserialize(messagesManager.getMessageAsString("OfflineSpecialAnnounce"),
                    Placeholder.unparsed("player",offlinePlayer.getName())));
        }
    }


    public ItemStack generateReward() {

        return rewardOptionsMap.get(rand.nextInt(rewardOptionsMap.size()));
    }


    public ItemStack generateSpecialReward(Player player, String yearMonth) {

        ItemStack specialReward = new ItemStack(Material.PAPER, 1);

        ItemMeta itemMeta = specialReward.getItemMeta();

        itemMeta.setUnbreakable(true);

        itemMeta.displayName(MiniMessage.miniMessage().deserialize("<red><username> <date> ballot",
                Placeholder.unparsed("username", player.getName()),
                Placeholder.unparsed("date", yearMonth)));

        specialReward.lore(Collections.singletonList(MiniMessage.miniMessage().deserialize(
                "&4Official: ░<username>░<br><br>/tip vote<br>Rewarded to players<br>who vote 64 times<br>in a month.",
                Placeholder.unparsed("username", player.getName()))));

        specialReward.setItemMeta(itemMeta);

        return specialReward;
    }


    public void informOfflineVotes(Player player) {
        player.sendMessage(MiniMessage.miniMessage().deserialize(messagesManager.getMessageAsString("LoginVoteNotice")));
    }


    public List<OfflinePlayer> getCurrentMonthTop10Voters() {

        return Vote.where("year_month = ?", currentYearMonth).orderBy("votes desc").limit(10).stream().map(vote -> plugin.getServer().getOfflinePlayer(vote.getString("mojang_uuid"))).collect(Collectors.toList());
    }


    public boolean isRewardsOwed(OfflinePlayer offlinePlayer, String yearMonth) {

        if (isVoter(offlinePlayer,yearMonth)) {

            Vote vote = Vote.findByCompositeKeys(offlinePlayer.getUniqueId().toString(),yearMonth);

            return vote.getInteger("rewards_owed") > 0 || vote.getBoolean("special_reward_owed");
        }

        else return false;
    }


    public int getCurrentMonthVoteCount(OfflinePlayer offlinePlayer) {

        return Vote.findByCompositeKeys(offlinePlayer.getUniqueId().toString(),currentYearMonth).getInteger("votes");
    }


    public int getPreviousMonthVoteCount(OfflinePlayer offlinePlayer) {

        return Vote.findByCompositeKeys(offlinePlayer.getUniqueId().toString(),previousYearMonth).getInteger("votes");
    }


    public int getTotalVoteCount(OfflinePlayer offlinePlayer) {

        return Vote.where("mojang_uuid = ?", offlinePlayer.getUniqueId().toString()).stream().mapToInt(vote -> vote.getInteger("votes")).sum();
    }
}


