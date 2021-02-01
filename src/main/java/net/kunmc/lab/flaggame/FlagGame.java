package net.kunmc.lab.flaggame;

import org.bukkit.plugin.java.JavaPlugin;

public final class FlagGame extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("実行されました");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
