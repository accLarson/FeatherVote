package dev.zerek.feathervote.managers;

import dev.zerek.feathervote.FeatherVote;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ConfigManager {

    private final FeatherVote plugin;

    private final List<ItemStack> rewardOptionsMap = new ArrayList<>();

    public ConfigManager(FeatherVote plugin) {

        this.plugin = plugin;

        this.init();
    }

    private void init() {

        plugin.saveDefaultConfig();

        plugin.getConfig().getStringList("rewards").forEach(material -> rewardOptionsMap.add(new ItemStack(Material.valueOf(material))));
    }

    public List<ItemStack> getRewardOptionsMap() {
        return rewardOptionsMap;
    }

}
