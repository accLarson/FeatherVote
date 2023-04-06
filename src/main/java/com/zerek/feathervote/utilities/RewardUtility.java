package com.zerek.feathervote.utilities;

import com.zerek.feathervote.FeatherVote;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class RewardUtility {
    private final FeatherVote plugin;
    List<ItemStack> rewards = new ArrayList<>();
    private String broadcast;
    Random rand = new Random();


    public RewardUtility(FeatherVote plugin) {

        this.plugin = plugin;

        this.init();
    }

    private void init() {

        for (String item : plugin.getConfig().getConfigurationSection("rewards").getKeys(false)) {

            Material reward = Material.matchMaterial(item);

            if (reward != null) this.rewards.add(new ItemStack(reward,plugin.getConfig().getInt("rewards." + item)));
        }

        broadcast = plugin.getConfig().getString("broadcast");
    }

    public void rewardPlayer(Player player, int voteCount) {

        ItemStack reward = rewards.get(rand.nextInt(rewards.size()));

        HashMap<Integer, ItemStack> remainingItems = player.getInventory().addItem(reward);

        if (!remainingItems.isEmpty()) remainingItems.values().forEach(i -> player.getWorld().dropItem(player.getLocation(),i));

        plugin.getServer().broadcast(MiniMessage.miniMessage().deserialize(broadcast,
                Placeholder.unparsed("player",player.getName()),
                Placeholder.unparsed("reward", reward.getAmount() + " " + reward.getType().name().replace("_", " ").toLowerCase() + "s"),
                Placeholder.unparsed("votes", String.valueOf(voteCount))));
    }

}
