package net.kunmc.lab.flaggame;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class FlagGame extends JavaPlugin {
    private static boolean isStart = false;
    private static boolean isAuto = false;
    private static boolean isEasyMode = false;
    private static boolean isSpectator = false;

    private static int speed = 1;

    private static String hostName = "auto";

    public static boolean isStart() {
        return isStart;
    }
    public static boolean isAuto() {
        return isAuto;
    }
    public static boolean isEasyMode() {
        return isEasyMode;
    }
    public static boolean isSpectator() {
        return isSpectator;
    }
    public static int getSpeed() {
        return speed;
    }
    public static String getHostName() {
        return hostName;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        // getLogger().info("実行されました");
        FileConfiguration config = getConfig();
        try {
            isSpectator = config.getBoolean("spectator");
        } catch (Exception e) {
            getLogger().info("configが正しく読み込まれませんでした");
        }
        new GameLogic(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("flag")) {
            if(args.length == 2) {
                if(args[0].equals("speed-up")) {
                    try {
                        speed = Integer.parseInt(args[1]);
                        sender.sendMessage("§a速さを" + args[1] + "倍にしました");
                        return true;
                    } catch (Exception e) {
                        speed = 1;
                        return false;
                    }
                }
                if(args[0].equals("difficulty")) {
                    if(args[1].equals("normal")) {
                        isEasyMode = false;
                        sender.sendMessage("§a難易度をノーマルにしました");
                        return true;
                    }
                    if(args[1].equals("easy")) {
                        isEasyMode = true;
                        sender.sendMessage("§a難易度をイージーにしました");
                        return true;
                    }
                }
            }
            if (sender instanceof Player) {
                HashMap<String, Integer> players = new HashMap<>();
                HashMap<String, Boolean> canChange = new HashMap<>();
                getServer().getOnlinePlayers().forEach(player -> {
                    player.getInventory().setItemInMainHand(new ItemStack(Material.WOODEN_SWORD));
                    player.getInventory().setItemInOffHand(new ItemStack(Material.BONE));
                    players.put(player.getName(), 4);
                    canChange.put(player.getName(), true);
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
                        GameLogic.init(players, canChange);
                        sender.sendMessage("§aホスト [CPU] でゲームを開始しました");
                        return true;
                    }
                    if(args[0].equals("demo")) {
                        hostName = "demo";
                        isStart = true;
                        GameLogic.init(players, canChange);
                        sender.sendMessage("§a練習モードでゲームを開始しました");
                        return true;
                    }
                    hostName = args[0];
                } else {
                    hostName = sender.getName();
                }
                isStart = true;
                GameLogic.init(players, canChange);
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
                players.add("demo");
                players.add("stop");
                players.add("speed-up");
                players.add("difficulty");
                return players;
            }
            if(args.length == 2 && args[0].equals("speed-up")) {
                return Arrays.asList("1", "2", "3","-2");
            }
            if(args.length == 2 && args[0].equals("difficulty")) {
                return Arrays.asList("easy", "normal");
            }
        }
        return super.onTabComplete(sender, command, label, args);
    }
}
