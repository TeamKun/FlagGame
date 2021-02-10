package net.kunmc.lab.flaggame;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class FlagGame extends JavaPlugin {
    private static boolean isStart = false;
    private static boolean isAuto = false;
    private static boolean isEasyMode = false;
    private static boolean isSpectator = false;

    private static float speed = 1.0F;

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
    public static float getSpeed() {
        return speed;
    }
    public static String getHostName() {
        return hostName;
    }

    private static HashMap<String, Integer> players;
    private static HashMap<String, Boolean> canChange;

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
        if(args.length == 0) {
            return false;
        }
        if (command.getName().equals("flag")) {
            if(args.length == 3) {
                if(args[0].equals("option")) {
                    if(args[1].equals("speed")) {
                        try {
                            speed = Float.parseFloat(args[2]);
                            if(speed <= 0) {
                                sender.sendMessage("無効な値です");
                            } else {
                                sender.sendMessage("§a速さを" + args[2] + "倍にしました");
                            }
                            return true;
                        } catch (Exception e) {
                            speed = 1;
                            sender.sendMessage("無効な値です");
                            return true;
                        }
                    }
                    if(args[1].equals("difficulty")) {
                        if(args[2].equals("normal")) {
                            isEasyMode = false;
                            sender.sendMessage("§a難易度をノーマルにしました");
                            return true;
                        }
                        if(args[2].equals("easy")) {
                            isEasyMode = true;
                            sender.sendMessage("§a難易度をイージーにしました");
                            return true;
                        }
                    }
                }
            }
            if (sender instanceof Player) {
                if(args.length == 2) {
                    if(args[0].equals("start")) {
                        if(args[1].equals("auto")) {
                            hostName = "auto";
                            isAuto = true;
                            isStart = true;
                            setPlayers();
                            GameLogic.init(players, canChange);
                            sender.sendMessage("§aホスト [CPU] でゲームを開始しました");
                            return true;
                        }
                        if(args[1].equals("demo")) {
                            hostName = "demo";
                            isAuto = false;
                            isStart = true;
                            setPlayers();
                            GameLogic.init(players, canChange);
                            sender.sendMessage("§a練習モードでゲームを開始しました");
                            return true;
                        }
                        hostName = args[1];
                        isAuto = false;
                        isStart = true;
                        setPlayers();
                        GameLogic.init(players, canChange);
                        sender.sendMessage("§aホスト [" + hostName +  "] でゲームを開始しました");
                        return true;

                    }
                }
                if(args.length == 1) {
                    if(args[0].equals("start")) {
                        hostName = sender.getName();
                        isAuto = false;
                        isStart = true;
                        setPlayers();
                        GameLogic.init(players, canChange);
                        sender.sendMessage("§aホスト [" + hostName +  "] でゲームを開始しました");
                        return true;
                    }
                    if(args[0].equals("stop")) {
                        hostName = "auto";
                        isAuto = false;
                        isStart = false;
                        sender.sendMessage("§aゲームを停止しました");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equals("flag")) {
            if(args.length == 1) {
                return Stream.of("start", "stop", "option").filter(e -> e.startsWith(args[0]))
                        .collect(Collectors.toList());
            }
            if(args.length == 2 && args[0].equals("start")) {
                ArrayList<String> players = new ArrayList<>();
                getServer().getOnlinePlayers().forEach(player -> players.add(player.getName()));
                players.add("auto");
                players.add("demo");
                return players.stream().filter(e -> e.startsWith(args[1])).collect(Collectors.toList());
            }
            if(args.length == 2 && args[0].equals("option")) {
                return Stream.of("speed", "difficulty").filter(e -> e.startsWith(args[1])).collect(Collectors.toList());
            }
            if(args.length == 3 && args[1].equals("speed")) {
                return Collections.singletonList("数値");
            }
            if(args.length == 3 && args[1].equals("difficulty")) {
                return Stream.of("normal", "easy").filter(e -> e.startsWith(args[2])).collect(Collectors.toList());
            }
        }
        return super.onTabComplete(sender, command, label, args);
    }

    private void setPlayers() {
        players = new HashMap<>();
        canChange = new HashMap<>();
        getServer().getOnlinePlayers().forEach(player -> {
            player.getInventory().setItem(0, new ItemStack(Material.WOODEN_SWORD));
            player.getInventory().setItemInOffHand(new ItemStack(Material.BONE));
            players.put(player.getName(), 4);
            canChange.put(player.getName(), true);
        });
    }
}
