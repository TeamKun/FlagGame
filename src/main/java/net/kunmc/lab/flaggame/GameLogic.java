package net.kunmc.lab.flaggame;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class GameLogic implements Listener {
    private JavaPlugin plugin;

    private static String title = "旗上げゲーム";
    private static String subTitle = "";
    private static String actionBar = "§c赤§b↓§f白§b↓";

    /**
     * 旗の状態
     * 1 - 赤上げる
     * 2 - 白上げる
     * 3 - 両方あげる
     * 4 - 両方下げる
     */
    private static int flagState = 4;

    private static boolean isJudgeStart = false;

    private static boolean isJudging = false;

    private static HashMap<String, Integer> players = new HashMap<>();

    public static void init(HashMap<String, Integer> players) {
        title = "旗上げゲーム";
        subTitle = "";
        actionBar = "§c赤§b↓§f白§b↓";
        flagState = 4;
        isJudgeStart = false;
        isJudging = false;
        GameLogic.players = players;
    }

    public GameLogic(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                if(!FlagGame.isStart()) {
                    return;
                }

                if(FlagGame.isAuto() && !isJudgeStart) {
                    int r = (int) (Math.random() * 4) + 1;
                    switch (r) {
                        case 1:
                            if(flagState == 1 || flagState == 3) {
                                title = "§c赤§b下げない";
                            } else {
                                title = "§c赤§a上げて";
                            }
                            flagState = flagState == 2 || flagState == 3 ? 3 : 1;
                            setActionBar();
                            setJudgeTimer();
                            break;
                        case 2:
                            if(flagState == 2 || flagState == 3) {
                                title = "§f白§b下げない";
                            } else {
                                title = "§f白§a上げて";
                            }
                            flagState = flagState == 1 || flagState == 3 ? 3 : 2;
                            setActionBar();
                            setJudgeTimer();
                            break;
                        case 3:
                            if(flagState == 2 || flagState == 4) {
                                title = "§c赤§a上げない";
                            } else {
                                title = "§c赤§b下げて";
                            }
                            flagState = flagState == 2 || flagState == 3 ? 2 : 4;
                            setActionBar();
                            setJudgeTimer();
                            break;
                        case 4:
                            if(flagState == 1 || flagState == 4) {
                                title = "§f白§a上げない";
                            } else {
                                title = "§f白§b下げて";
                            }
                            flagState = flagState == 1 || flagState == 3 ? 1 : 4;
                            setActionBar();
                            setJudgeTimer();
                            break;
                    }
                }

                sendActionBar();

                if(isJudging) {
                    plugin.getServer().getOnlinePlayers().forEach(player -> {
                        if(player.getName().equals(FlagGame.getHostName())) {
                            return;
                        }
                        if(flagState != players.get(player.getName()) && player.getGameMode() != GameMode.SPECTATOR) {
                            player.getWorld().createExplosion(player.getLocation().add(0, 1, 0),0, false);
                            player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                            player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
                            player.damage(20);
                        }
                    });
                }
            }
        }, 0L, 2L);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(!FlagGame.isStart()) {
            return;
        }

        event.setCancelled(true);

        Action action = event.getAction();
        Player player = event.getPlayer();

        boolean isHost = player.getName().equals(FlagGame.getHostName());

        if(isJudgeStart && isHost) {
            return;
        }

        if(isJudging) {
            return;
        }

        if(action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
            if(player.isSneaking()) {
                player.getInventory().setItemInOffHand(new ItemStack(Material.WOODEN_PICKAXE));
                if(isHost) {
                    if(flagState == 1 || flagState == 4) {
                        title = "§f白§a上げない";
                    } else {
                        title = "§f白§b下げて";
                    }
                    flagState = flagState == 1 || flagState == 3 ? 1 : 4;
                    setActionBar();
                    setJudgeTimer();
                    return;
                }
                int playerFlagState = players.get(player.getName());
                playerFlagState = playerFlagState == 1 || playerFlagState == 3 ? 1 : 4;
                players.put(player.getName(), playerFlagState);
                return;
            }
            player.getInventory().setItemInOffHand(new ItemStack(Material.WOODEN_AXE));
            if(isHost) {
                if(flagState == 2 || flagState == 3) {
                    title = "§f白§b下げない";
                } else {
                    title = "§f白§a上げて";
                }
                flagState = flagState == 1 || flagState == 3 ? 3 : 2;
                setActionBar();
                setJudgeTimer();
                return;
            }
            int playerFlagState = players.get(player.getName());
            playerFlagState = playerFlagState == 1 || playerFlagState == 3 ? 3 : 2;
            players.put(player.getName(), playerFlagState);
            return;
        }

        if(action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK)) {
            if(player.isSneaking()) {
                player.getInventory().setItemInMainHand(new ItemStack(Material.WOODEN_SWORD));
                if(isHost) {
                    if(flagState == 2 || flagState == 4) {
                        title = "§c赤§a上げない";
                    } else {
                        title = "§c赤§b下げて";
                    }
                    flagState = flagState == 2 || flagState == 3 ? 2 : 4;
                    setActionBar();
                    setJudgeTimer();
                    return;
                }
                int playerFlagState = players.get(player.getName());
                playerFlagState = playerFlagState == 2 || playerFlagState == 3 ? 2 : 4;
                players.put(player.getName(), playerFlagState);
                return;
            }
            player.getInventory().setItemInMainHand(new ItemStack(Material.WOODEN_SHOVEL));
            if(isHost) {
                if(flagState == 1 || flagState == 3) {
                    title = "§c赤§b下げない";
                } else {
                    title = "§c赤§a上げて";
                }
                flagState = flagState == 2 || flagState == 3 ? 3 : 1;
                setActionBar();
                setJudgeTimer();
                return;
            }
            int playerFlagState = players.get(player.getName());
            playerFlagState = playerFlagState == 2 || playerFlagState == 3 ? 3 : 1;
            players.put(player.getName(), playerFlagState);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if(FlagGame.isStart()) {
            event.setDeathMessage(event.getEntity().getName() + "は間違えてしまった");
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if(FlagGame.isStart()) {
            Player player = event.getPlayer();
            players.put(player.getName(), 4);
            player.setGameMode(GameMode.SPECTATOR);
        }
    }

    private static void setActionBar() {
        switch (flagState) {
            case 1:
                actionBar = "§c赤§a↑§f白§b↓";
                break;
            case 2:
                actionBar = "§c赤§b↓§f白§a↑";
                break;
            case 3:
                actionBar = "§c赤§a↑§f白§a↑";
                break;
            case 4:
                actionBar = "§c赤§b↓§f白§b↓";
                break;
            default:
                actionBar = "未定義";
        }
    }

    private void sendActionBar() {
        plugin.getServer().getOnlinePlayers().forEach(player -> {
            player.sendTitle(title, subTitle, 0, 5, 1);
            if(player.getName().equals(FlagGame.getHostName()) || player.getGameMode() == GameMode.SPECTATOR) {
                player.sendActionBar("[正解] " + actionBar);
                return;
            }
            /**
            String playerFlag;
            switch (players.get(player.getName())) {
                case 1:
                    playerFlag = "§c赤§a↑§f白§b↓";
                    break;
                case 2:
                    playerFlag = "§c赤§b↓§f白§a↑";
                    break;
                case 3:
                    playerFlag = "§c赤§a↑§f白§a↑";
                    break;
                case 4:
                    playerFlag = "§c赤§b↓§f白§b↓";
                    break;
                default:
                    playerFlag = "未定義";
            }
            player.sendActionBar("[正解] " + actionBar + " §f- [あなた] " + playerFlag);
             */
        });
    }



    private void setJudgeTimer() {
        isJudgeStart = true;
        JudgeTimer judgeTimer = new JudgeTimer(plugin,1,3);
        judgeTimer.runTaskTimer(plugin, 0, 20L);
    }

    private static class JudgeTimer extends BukkitRunnable {
        private JavaPlugin plugin;
        private int start;
        private final int buffer;
        private final int end;

        public JudgeTimer(JavaPlugin plugin, int buffer, int end) {
            this.plugin = plugin;
            start = 0;
            this.buffer = buffer;
            this.end = end;
        }

        @Override
        public void run() {
            if(start <= buffer) {
                subTitle = " §f判定開始まであと§6" + (buffer - start) + "秒";
                start++;
                return;
            }
            isJudging = true;
            subTitle = " §f判定終了まであと§4" + (end - start) + "秒";
            if(start > end) {
                isJudgeStart = false;
                isJudging = false;
                subTitle = "";
                cancel();
                return;
            }
            start++;
        }
    }

}
