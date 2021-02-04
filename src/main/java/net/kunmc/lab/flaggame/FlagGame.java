package net.kunmc.lab.flaggame;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class FlagGame extends JavaPlugin {
    private static boolean isStart = false;
    private static boolean isAuto = false;

    private static String hostName = "auto";

    public static boolean isStart() {
        return isStart;
    }

    public static boolean isAuto() {
        return isAuto;
    }

    public static String getHostName() {
        return hostName;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        // getLogger().info("実行されました");
        new GameLogic(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("flag")) {
            if (sender instanceof Player) {
                HashMap<String, Integer> players = new HashMap<>();
                getServer().getOnlinePlayers().forEach(player -> {
                    player.getInventory().setItemInMainHand(new ItemStack(Material.WOODEN_SWORD));
                    player.getInventory().setItemInOffHand(new ItemStack(Material.WOODEN_PICKAXE));
                    players.put(player.getName(), 4);
                });

                if(args.length >= 1) {
                    if(args[0].equals("stop")) {
                        hostName = "auto";
                        isStart = false;
                        isAuto = false;
                        sender.sendMessage("§aゲームを停止しました");
                        return true;
                    }
                    if(args[0].equals("auto")) {
                        hostName = "auto";
                        isAuto = true;
                        isStart = true;
                        GameLogic.init(players);
                        sender.sendMessage("§aホスト [CPU] でゲームを開始しました");
                        return true;
                    }
                    hostName = args[0];
                } else {
                    hostName = sender.getName();
                }
                isStart = true;
                GameLogic.init(players);
                sender.sendMessage("§aホスト [" + hostName +  "] でゲームを開始しました");
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equals("flag")) {
            if(args.length == 1) {
                List<String> players = new ArrayList<>();
                getServer().getOnlinePlayers().forEach(player -> {
                    players.add(player.getName());
                });
                players.add("auto");
                players.add("stop");
                return players;
            }
        }
        return super.onTabComplete(sender, command, label, args);
    }
}
