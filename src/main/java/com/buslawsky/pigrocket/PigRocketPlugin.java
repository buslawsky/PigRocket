package com.buslawsky.pigrocket;

import org.bukkit.plugin.java.JavaPlugin;

public final class PigRocketPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new PigRocketListener(this), this);
        getLogger().info("PigRocketPlugin zostal pomyslnie wlaczony! Prosiaki gotowe do startu. 🚀");
    }

    @Override
    public void onDisable() {
        getLogger().info("PigRocketPlugin wylaczony.");
    }
}