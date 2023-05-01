package com.zerek.feathervote.managers;

import com.zerek.feathervote.FeatherVote;
import com.zerek.feathervote.data.Vote;
import com.zerek.feathervote.utilities.ItemLabelUtility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
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

        this.currentYearMonth = plugin.getYearMonthUtility().getCurrentYearMonth();

        this.previousYearMonth = plugin.getYearMonthUtility().getPreviousYearMonth(1);
    }


    private void init() {

        this.itemLabelUtility = plugin.getItemLabelUtility();

        this.rewardOptionsMap = plugin.getConfigManager().getRewardOptionsMap();

        this.messagesManager = plugin.getMessagesManager();

    }


    private boolean isVoter(String uuid, String yearMonth){

        return Vote.findByCompositeKeys(uuid, yearMonth) != null;
    }


    public void processVote(OfflinePlayer offlinePlayer) {

        String uuid = offlinePlayer.getUniqueId().toString();

        if (!isVoter(offlinePlayer.getUniqueId().toString(), currentYearMonth)) new Vote().set("mojang_uuid", uuid , "year_month", currentYearMonth).insert();

        Vote vote = Vote.findByCompositeKeys(uuid,currentYearMonth);

        vote.set(
                "votes", vote.getInteger("votes") + 1,
                "rewards_owed", vote.getInteger("rewards_owed") + 1 )
                .saveIt();


        if (vote.getInteger("votes") == 64) vote.setBoolean("special_reward_owed", 1).saveIt();

        if (offlinePlayer.isOnline() && !((Player)offlinePlayer).hasPermission("feather.vote.postponereward")) this.rewardPlayer((Player) offlinePlayer, currentYearMonth,true);

        else this.postponeAnnounceVote(offlinePlayer, currentYearMonth);
    }


    public void rewardPlayer(Player player, String yearMonth, boolean broadcast) {

        if (isVoter(player.getUniqueId().toString(),yearMonth)){

            Vote vote = Vote.findByCompositeKeys(player.getUniqueId().toString(),yearMonth);

            int rewardCount = vote.getInteger("rewards_owed");

            while (rewardCount > 0) {

                ItemStack reward = this.generateReward();

                player.getInventory().addItem(reward).forEach((integer, itemStack) -> player.getWorld().dropItem(player.getLocation(), itemStack).setOwner(player.getUniqueId()));

                if (broadcast && rewardCount == 1) {

                    ItemStack formattedReward = itemLabelUtility.formatItemStack(reward.clone());

                    plugin.getServer().broadcast(MiniMessage.miniMessage().deserialize(messagesManager.getMessageAsString("Announce"),
                            Placeholder.unparsed("player",player.getName()),
                            Placeholder.component("reward",formattedReward.displayName().hoverEvent(formattedReward)),
                            Placeholder.unparsed("votes", String.valueOf(vote.getInteger("votes")))));
                }

                rewardCount--;
            }

            vote.setInteger("rewards_owed", 0).saveIt();

            if (vote.getBoolean("special_reward_owed")) {

                vote.setBoolean("special_reward_owed", false).saveIt();

                ItemStack specialReward = this.generateSpecialReward(player, yearMonth);

                player.getInventory().addItem(specialReward).forEach((index, itemStack) -> player.getWorld().dropItem(player.getLocation(), itemStack).setOwner(player.getUniqueId()));

                ItemStack formattedSpecialReward = itemLabelUtility.formatItemStack(specialReward.clone());

                if (broadcast) {

                    plugin.getServer().broadcast(MiniMessage.miniMessage().deserialize(messagesManager.getMessageAsString("SpecialAnnounce"),
                            Placeholder.unparsed("player", player.getName()),
                            Placeholder.component("reward", formattedSpecialReward.displayName().hoverEvent(formattedSpecialReward))));
                }
            }
        }
    }


    public void postponeAnnounceVote(OfflinePlayer offlinePlayer, String yearMonth) {

        Vote vote = Vote.findByCompositeKeys(offlinePlayer.getUniqueId().toString(),yearMonth);

        plugin.getServer().broadcast(MiniMessage.miniMessage().deserialize(messagesManager.getMessageAsString("PostponeAnnounce"),
                Placeholder.unparsed("player",offlinePlayer.getName()),
                Placeholder.unparsed("votes", String.valueOf(vote.getInteger("votes")))));

        if (vote.getBoolean("special_reward_owed")) {

            plugin.getServer().broadcast(MiniMessage.miniMessage().deserialize(messagesManager.getMessageAsString("PostponeSpecialAnnounce"),
                    Placeholder.unparsed("player",offlinePlayer.getName())));
        }
    }


    public ItemStack generateReward() {

        return rewardOptionsMap.get(rand.nextInt(rewardOptionsMap.size()));
    }


    public ItemStack generateSpecialReward(Player player, String yearMonth) {

        ItemStack specialReward = new ItemStack(Material.PAPER, 1);

        ItemMeta itemMeta = specialReward.getItemMeta();

        itemMeta.addEnchant(Enchantment.DURABILITY,10,true);

        itemMeta.setUnbreakable(true);

        itemMeta.displayName(MiniMessage.miniMessage().deserialize("<red><username> <date> ballot",
                Placeholder.unparsed("username", player.getName()),
                Placeholder.unparsed("date", yearMonth)));

        List<Component> lore = new ArrayList<>();

        lore.add(MiniMessage.miniMessage().deserialize("<dark_red>Official: ░<username>░",Placeholder.unparsed("username", player.getName())));
        lore.add(Component.text(" "));
        lore.add(MiniMessage.miniMessage().deserialize("<gray>/tip vote"));
        lore.add(MiniMessage.miniMessage().deserialize("<gray>Rewarded to players"));
        lore.add(MiniMessage.miniMessage().deserialize("<gray>who vote 64 times"));
        lore.add(MiniMessage.miniMessage().deserialize("<gray>in a month."));

        itemMeta.lore(lore);

        specialReward.setItemMeta(itemMeta);

        return specialReward;
    }


    public void informPostponeRewards(Player player) {
        player.sendMessage(MiniMessage.miniMessage().deserialize(messagesManager.getMessageAsString("PostponeRewardNotice")));
    }


    public List<String> getCurrentMonthTop10Voters() {

        return Vote.where("year_month = ?", currentYearMonth)
                .orderBy("votes desc")
                .limit(10)
                .stream()
                .map(vote -> vote.getString("mojang_uuid"))
                .collect(Collectors.toList());
    }


    public boolean isRewardOwed(OfflinePlayer offlinePlayer) {

        return Vote.where("mojang_uuid = ?", offlinePlayer.getUniqueId().toString())
                .stream()
                .anyMatch(vote -> vote.getInteger("rewards_owed") > 0);
    }


    public List<String> getRewardOwingMonths(OfflinePlayer offlinePlayer) {
        return Vote.where("mojang_uuid = ?", offlinePlayer.getUniqueId().toString())
                .stream()
                .filter(vote -> vote.getInteger("votes") > 0)
                .map(vote -> vote.getString("year_month"))
                .collect(Collectors.toList());
    }


    public int getMonthVoteCount(String uuid, String yearMonth) {

        return isVoter(uuid, yearMonth) ? Vote.findByCompositeKeys(uuid, yearMonth).getInteger("votes") : 0;
    }


    public int getTotalVoteCount(String uuid) {

        return Vote.where("mojang_uuid = ?", uuid).stream().mapToInt(vote -> vote.getInteger("votes")).sum();
    }
}


