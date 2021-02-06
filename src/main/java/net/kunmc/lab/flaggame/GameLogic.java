package net.kunmc.lab.flaggame;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
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
import java.util.Random;

public class GameLogic implements Listener {
    private final JavaPlugin plugin;

    private static String title = "";
    private static String titleAfter = "旗上げゲーム";
    private static String subTitle = "";
    private static String actionBar = "§c赤§b↓§f白§b↓";
    private static String soundPath = "";
    private static String colorKind = "s";

    /**
     * 旗の状態
     * 1 - 赤上げる
     * 2 - 白上げる
     * 3 - 両方あげる
     * 4 - 両方下げる
     */
    private static int flagState = 4;
    private static int beforeSound = 0;

    private static boolean isJudgeStart = false;
    private static boolean titleFlag = false;
    private static boolean isDayo = false;

    private static HashMap<String, Integer> players = new HashMap<>();

    private static HashMap<String, Boolean> canChange = new HashMap<>();

    public static void init(HashMap<String, Integer> players, HashMap<String, Boolean> canChange) {
        title = "";
        titleAfter = "旗上げゲーム";
        subTitle = "";
        actionBar = "§c赤§b↓§f白§b↓";
        soundPath = "";
        colorKind = "s";
        flagState = 4;
        beforeSound = 0;
        isJudgeStart = false;
        titleFlag = false;
        isDayo = false;
        GameLogic.players = players;
        GameLogic.canChange = canChange;
    }

    public GameLogic(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                if (!FlagGame.isStart()) {
                    return;
                }

                if (FlagGame.isAuto() && !isJudgeStart) {
                    int r = (new Random().nextInt(5)) + 1;
                    switch (r) {
                        case 1:
                            int r1 = (new Random().nextInt(4)) + 1;
                            titleFlag = true;
                            if (flagState == 1 || flagState == 3) {
                                title = "§c赤§b下げ";
                                if (beforeSound == 1) {
                                    titleAfter = "§c赤§b下げない";
                                    if(r1 == 4) {
                                        soundPath = "a_sagenai_d";
                                    } else {
                                        soundPath = "a_sagenai_h";
                                    }
                                } else {
                                    titleAfter = "§c赤§b下げないで";
                                    if(r1 == 4) {
                                        soundPath = "a_sagenaide_d";
                                    } else {
                                        soundPath = "a_sagenaide_h";
                                    }
                                }
                            } else {
                                title = "§c赤§a上げ";
                                if (beforeSound == 1) {
                                    titleAfter = "§c赤§a上げる";
                                    if(r1 == 4) {
                                        soundPath = "a_ageru_d";
                                    } else {
                                        soundPath = "a_ageru_h";
                                    }
                                } else {
                                    titleAfter = "§c赤§a上げて";
                                    if(r1 == 4) {
                                        soundPath = "a_agete_d";
                                    } else {
                                        soundPath = "a_agete_h";
                                    }
                                }
                            }
                            beforeSound = beforeSound == 1 ? 0 : 1;
                            if(r1 != 4) {
                                flagState = flagState == 2 || flagState == 3 ? 3 : 1;
                            }
                            setActionBar();
                            setJudgeTimer();
                            break;
                        case 2:
                            titleFlag = true;
                            int r2 = (new Random().nextInt(4)) + 1;
                            if (flagState == 2 || flagState == 3) {
                                title = "§f白§b下げ";
                                if (beforeSound == 1) {
                                    titleAfter = "§f白§b下げない";
                                    if(r2 == 4) {
                                        soundPath = "s_sagenai_d";
                                    } else {
                                        soundPath = "s_sagenai_h";
                                    }
                                } else {
                                    titleAfter = "§f白§b下げないで";
                                    if(r2 == 4) {
                                        soundPath = "s_sagenaide_d";
                                    } else {
                                        soundPath = "s_sagenaide_h";
                                    }
                                }
                            } else {
                                title = "§f白§a上げ";
                                if (beforeSound == 1) {
                                    titleAfter = "§f白§a上げる";
                                    if(r2 == 4) {
                                        soundPath = "s_ageru_d";
                                    } else {
                                        soundPath = "s_ageru_h";
                                    }
                                } else {
                                    titleAfter = "§f白§a上げて";
                                    if(r2 == 4) {
                                        soundPath = "s_agete_d";
                                    } else {
                                        soundPath = "s_agete_h";
                                    }
                                }
                            }
                            beforeSound = beforeSound == 1 ? 0 : 1;
                            if(r2 != 4) {
                                flagState = flagState == 1 || flagState == 3 ? 3 : 2;
                            }
                            setActionBar();
                            setJudgeTimer();
                            break;
                        case 3:
                            titleFlag = true;
                            int r3 = (new Random().nextInt(4)) + 1;
                            if (flagState == 2 || flagState == 4) {
                                title = "§c赤§a上げ";
                                if (beforeSound == 1) {
                                    titleAfter = "§c赤§a上げない";
                                    if(r3 == 4) {
                                        soundPath = "a_agenai_d";
                                    } else {
                                        soundPath = "a_agenai_h";
                                    }
                                } else {
                                    titleAfter = "§c赤§a上げないで";
                                    if(r3 == 3) {
                                        soundPath = "a_agenaide_d";
                                    } else {
                                        soundPath = "a_agenaide_h";
                                    }
                                }
                            } else {
                                title = "§c赤§b下げ";
                                if (beforeSound == 1) {
                                    titleAfter = "§c赤§b下げる";
                                    if(r3 == 4) {
                                        soundPath = "a_sageru_d";
                                    } else {
                                        soundPath = "a_sageru_h";
                                    }
                                } else {
                                    titleAfter = "§c赤§b下げて";
                                    if(r3 == 4) {
                                        soundPath = "a_sagete_d";
                                    } else {
                                        soundPath = "a_sagete_h";
                                    }
                                }
                            }
                            beforeSound = beforeSound == 1 ? 0 : 1;
                            if(r3 != 4) {
                                flagState = flagState == 2 || flagState == 3 ? 2 : 4;
                            }
                            setActionBar();
                            setJudgeTimer();
                            break;
                        case 4:
                            titleFlag = true;
                            int r4 = (new Random().nextInt(4) ) + 1;
                            if (flagState == 1 || flagState == 4) {
                                title = "§f白§a上げ";
                                if (beforeSound == 1) {
                                    titleAfter = "§f白§a上げない";
                                    if(r4 == 4) {
                                        soundPath = "s_agenai_d";
                                    } else {
                                        soundPath = "s_agenai_h";
                                    }
                                } else {
                                    titleAfter = "§f白§a上げないで";
                                    if(r4 == 4) {
                                        soundPath = "s_agenaide_d";
                                    } else {
                                        soundPath = "s_agenaide_h";
                                    }
                                }
                            } else {
                                title = "§f白§b下げ";
                                if (beforeSound == 1) {
                                    titleAfter = "§f白§b下げる";
                                    if(r4 == 4) {
                                        soundPath = "s_sageru_d";
                                    } else {
                                        soundPath = "s_sageru_h";
                                    }
                                } else {
                                    titleAfter = "§f白§b下げて";
                                    if(r4 == 4) {
                                        soundPath = "s_sagete_d";
                                    } else {
                                        soundPath = "s_sagete_h";
                                    }
                                }
                            }
                            beforeSound = beforeSound == 1 ? 0 : 1;
                            if(r4 != 4) {
                                flagState = flagState == 1 || flagState == 3 ? 1 : 4;
                            }
                            setActionBar();
                            setJudgeTimer();
                            break;
                        case 5:
                            titleFlag = true;
                            int r5 = (new Random().nextInt(4)) + 1;
                            switch (r5) {
                                case 1:
                                    title = "§c青§a上げ";
                                    titleAfter = "§c青§a上げて";
                                    soundPath = "ao_h";
                                    break;
                                case 2:
                                    title = "§f黄§a上げ";
                                    titleAfter = "§f黄§a上げないで";
                                    soundPath = "ki_h";
                                    break;
                                case 3:
                                    title = "§f黒§b下げ";
                                    titleAfter = "§f黒§b下げて";
                                    soundPath = "ku_h";
                                    break;
                                case 4:
                                    title = "§c緑§b下げ";
                                    titleAfter = "§c緑§b下げないで";
                                    soundPath = "mi_h";
                                    break;
                            }
                            setActionBar();
                            setJudgeTimer();
                            break;
                    }
                }

                sendActionBar();

                if (!isJudgeStart) {
                    plugin.getServer().getOnlinePlayers().forEach(player -> {
                        if (player.getName().equals(FlagGame.getHostName())) {
                            return;
                        }
                        if (flagState != players.get(player.getName()) && player.getGameMode() != GameMode.SPECTATOR && player.getGameMode() != GameMode.CREATIVE) {
                            player.getWorld().createExplosion(player.getLocation().add(0, 1, 0), 0, false);
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
        if (!FlagGame.isStart()) {
            return;
        }

        Action action = event.getAction();
        Player player = event.getPlayer();

        boolean isHost = player.getName().equals(FlagGame.getHostName());

        if (isJudgeStart && isHost) {
            return;
        }

        if (action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK)) {
            if(canChange.get(player.getName())) {
                canChange.put(player.getName(), false);
            } else {
                return;
            }
            if (player.isSneaking()) {
                if (isHost) {
                    seeBlock(player);
                    if(!colorKind.equals("s")) {
                        changeColor();
                    } else if (flagState == 1 || flagState == 4) {
                        title = "§f白§a上げ";
                        if (beforeSound == 1) {
                            titleAfter = "§f白§a上げない";
                            if (isDayo) {
                                soundPath = "s_agenai_d";
                            } else {
                                soundPath = "s_agenai_h";
                            }
                        } else {
                            titleAfter = "§f白§a上げないで";
                            if (isDayo) {
                                soundPath = "s_agenaide_d";
                            } else {
                                soundPath = "s_agenaide_h";
                            }
                        }
                    } else {
                        title = "§f白§b下げ";
                        if (beforeSound == 1) {
                            titleAfter = "§f白§b下げる";
                            if (isDayo) {
                                soundPath = "s_sageru_d";
                            } else {
                                soundPath = "s_sageru_h";
                            }
                        } else {
                            titleAfter = "§f白§b下げて";
                            if (isDayo) {
                                soundPath = "s_sagete_d";
                            } else {
                                soundPath = "s_sagete_h";
                            }
                        }
                    }
                    beforeSound = beforeSound == 1 ? 0 : 1;
                    if (!isDayo && colorKind.equals("s")) {
                        flagState = flagState == 1 || flagState == 3 ? 1 : 4;
                        player.getInventory().setItemInOffHand(new ItemStack(Material.WOODEN_PICKAXE));
                    }
                    setActionBar();
                    setJudgeTimer();
                    return;
                }
                int playerFlagState = players.get(player.getName());
                playerFlagState = playerFlagState == 1 || playerFlagState == 3 ? 1 : 4;
                players.put(player.getName(), playerFlagState);
                player.getInventory().setItemInOffHand(new ItemStack(Material.WOODEN_PICKAXE));
                return;
            }
            if (isHost) {
                seeBlock(player);
                if(!colorKind.equals("s")) {
                    changeColor();
                } else if (flagState == 2 || flagState == 3) {
                    title = "§f白§b下げ";
                    if (beforeSound == 1) {
                        titleAfter = "§f白§b下げない";
                        if (isDayo) {
                            soundPath = "s_sagenai_d";
                        } else {
                            soundPath = "s_sagenai_h";
                        }
                    } else {
                        titleAfter = "§f白§b下げないで";
                        if (isDayo) {
                            soundPath = "s_sagenaide_d";
                        } else {
                            soundPath = "s_sagenaide_h";
                        }
                    }
                } else {
                    title = "§f白§a上げ";
                    if (beforeSound == 1) {
                        titleAfter = "§f白§a上げる";
                        if (isDayo) {
                            soundPath = "s_ageru_d";
                        } else {
                            soundPath = "s_ageru_h";
                        }
                    } else {
                        titleAfter = "§f白§a上げて";
                        if (isDayo) {
                            soundPath = "s_agete_d";
                        } else {
                            soundPath = "s_agete_h";
                        }
                    }
                }
                beforeSound = beforeSound == 1 ? 0 : 1;
                if (!isDayo && colorKind.equals("s")) {
                    flagState = flagState == 1 || flagState == 3 ? 3 : 2;
                    player.getInventory().setItemInOffHand(new ItemStack(Material.WOODEN_HOE));
                }
                setActionBar();
                setJudgeTimer();
                return;
            }
            int playerFlagState = players.get(player.getName());
            playerFlagState = playerFlagState == 1 || playerFlagState == 3 ? 3 : 2;
            players.put(player.getName(), playerFlagState);
            player.getInventory().setItemInOffHand(new ItemStack(Material.WOODEN_HOE));
            return;
        }

            if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
                if(canChange.get(player.getName())) {
                    canChange.put(player.getName(), false);
                } else {
                    return;
                }
                if (player.isSneaking()) {
                    if (isHost) {
                        seeBlock(player);
                        if(!colorKind.equals("s")) {
                            changeColor();
                        } else if (flagState == 2 || flagState == 4) {
                            title = "§c赤§a上げ";
                            if (beforeSound == 1) {
                                titleAfter = "§c赤§a上げない";
                                if (isDayo) {
                                    soundPath = "a_agenai_d";
                                } else {
                                    soundPath = "a_agenai_h";
                                }
                            } else {
                                titleAfter = "§c赤§a上げないで";
                                if (isDayo) {
                                    soundPath = "a_agenaide_d";
                                } else {
                                    soundPath = "a_agenaide_h";
                                }
                            }
                        } else {
                            title = "§c赤§b下げ";
                            if (beforeSound == 1) {
                                titleAfter = "§c赤§b下げる";
                                if (isDayo) {
                                    soundPath = "a_sageru_d";
                                } else {
                                    soundPath = "a_sageru_h";
                                }
                            } else {
                                titleAfter = "§c赤§b下げて";
                                if (isDayo) {
                                    soundPath = "a_sagete_d";
                                } else {
                                    soundPath = "a_sagete_h";
                                }
                            }
                        }
                        beforeSound = beforeSound == 1 ? 0 : 1;
                        if (!isDayo && colorKind.equals("s")) {
                            flagState = flagState == 2 || flagState == 3 ? 2 : 4;
                            player.getInventory().setItemInMainHand(new ItemStack(Material.WOODEN_SWORD));
                        }
                        setActionBar();
                        setJudgeTimer();
                        return;
                    }
                    int playerFlagState = players.get(player.getName());
                    playerFlagState = playerFlagState == 2 || playerFlagState == 3 ? 2 : 4;
                    players.put(player.getName(), playerFlagState);
                    player.getInventory().setItemInMainHand(new ItemStack(Material.WOODEN_SWORD));
                    return;
                }
                if (isHost) {
                    seeBlock(player);
                    if(!colorKind.equals("s")) {
                        changeColor();
                    } else if (flagState == 1 || flagState == 3) {
                        title = "§c赤§b下げ";
                        if (beforeSound == 1) {
                            titleAfter = "§c赤§b下げない";
                            if (isDayo) {
                                soundPath = "a_sagenai_d";
                            } else {
                                soundPath = "a_sagenai_h";
                            }
                        } else {
                            titleAfter = "§c赤§b下げないで";
                            if (isDayo) {
                                soundPath = "a_sagenaide_d";
                            } else {
                                soundPath = "a_sagenaide_h";
                            }
                        }
                    } else {
                        title = "§c赤§a上げ";
                        if (beforeSound == 1) {
                            titleAfter = "§c赤§a上げる";
                            if (isDayo) {
                                soundPath = "a_ageru_d";
                            } else {
                                soundPath = "a_ageru_h";
                            }
                        } else {
                            titleAfter = "§c赤§a上げて";
                            if (isDayo) {
                                soundPath = "a_agete_d";
                            } else {
                                soundPath = "a_agete_h";
                            }
                        }
                    }
                    beforeSound = beforeSound == 1 ? 0 : 1;
                    if (!isDayo && colorKind.equals("s")) {
                        flagState = flagState == 2 || flagState == 3 ? 3 : 1;
                        player.getInventory().setItemInMainHand(new ItemStack(Material.WOODEN_SHOVEL));
                    }
                    setActionBar();
                    setJudgeTimer();
                    return;
                }
                int playerFlagState = players.get(player.getName());
                playerFlagState = playerFlagState == 2 || playerFlagState == 3 ? 3 : 1;
                players.put(player.getName(), playerFlagState);
                player.getInventory().setItemInMainHand(new ItemStack(Material.WOODEN_SHOVEL));
            }
        }

        @EventHandler
        public void onPlayerDeath (PlayerDeathEvent event){
            if (FlagGame.isStart()) {
                event.setDeathMessage(event.getEntity().getName() + "は間違えてしまった");
            }
        }

        @EventHandler
        public void onPlayerRespawn (PlayerRespawnEvent event){
            if (FlagGame.isStart()) {
                Player player = event.getPlayer();
                players.put(player.getName(), 4);
                if(FlagGame.isSpectator()) {
                    player.setGameMode(GameMode.SPECTATOR);
                }
            }
        }

        private static void setActionBar () {
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

        private void sendActionBar () {
            String message = titleFlag ? title : titleAfter;
            plugin.getServer().getOnlinePlayers().forEach(player -> {
                player.sendTitle(message, subTitle, 0, 5, 1);
                if (player.getName().equals(FlagGame.getHostName()) || player.getGameMode() == GameMode.SPECTATOR || player.getGameMode() == GameMode.CREATIVE) {
                    player.sendActionBar("[正解] " + actionBar);
                    return;
                }
                if(FlagGame.isEasyMode()) {
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
                }
            });
        }

        private void seeBlock(Player player) {
            Block block = player.getWorld().getBlockAt(player.getLocation().add(0, -1, 0));
            isDayo = false;
            colorKind = "s";
            if (block.getType() == Material.DIAMOND_BLOCK) {
                isDayo = true;
            } else if (block.getType() == Material.BLUE_WOOL) {
                colorKind = "ao";
            } else if (block.getType() == Material.YELLOW_WOOL) {
                colorKind = "ki";
            } else if (block.getType() == Material.BLACK_WOOL) {
                colorKind = "ku";
            } else if (block.getType() == Material.GREEN_WOOL) {
                colorKind = "mi";
            }
            titleFlag = true;
        }

        private void changeColor() {
            soundPath = colorKind + "_h";
            switch (colorKind) {
                case "ao":
                    title = "§c青§a上げ";
                    titleAfter = "§c青§a上げて";
                    break;
                case "ki":
                    title = "§f黄§a上げ";
                    titleAfter = "§f黄§a上げないで";
                    break;
                case "ku":
                    title = "§f黒§b下げ";
                    titleAfter = "§f黒§b下げて";
                    break;
                case "mi":
                    title = "§c緑§b下げ";
                    titleAfter = "§c緑§b下げないで";
                    break;
            }
        }

        private void setJudgeTimer () {
            isJudgeStart = true;
            JudgeTimer judgeTimer = new JudgeTimer(plugin, 1);
            judgeTimer.runTaskTimer(plugin, 0, 10L);
        }

        private static class JudgeTimer extends BukkitRunnable {
            private final JavaPlugin plugin;
            private int start;
            private final int buffer;

            private boolean isEndTitle;
            private boolean isDelay;

            public JudgeTimer(JavaPlugin plugin, int buffer) {
                this.plugin = plugin;
                start = 0;
                this.buffer = buffer;
                isEndTitle = false;
                isDelay = true;
            }

            @Override
            public void run() {
                if (start == 0 && !isEndTitle) {
                    plugin.getServer().getOnlinePlayers().forEach(player -> {
                        player.getWorld().playSound(player.getLocation(), soundPath, 1, 1);
                    });
                }
                if (titleFlag && !isEndTitle) {
                    isEndTitle = true;
                    return;
                }
                titleFlag = false;
                if (start <= buffer) {
                    subTitle = " §f判定開始まであと§6" + (buffer - start) + "秒";
                    if (isDelay) {
                        isDelay = false;
                        return;
                    } else {
                        start++;
                        isDelay = true;
                    }
                    return;
                }
                isJudgeStart = false;
                subTitle = "";
                plugin.getServer().getOnlinePlayers().forEach(player -> {
                    if (player.getName().equals(FlagGame.getHostName())) {
                        canChange.put(player.getName(), true);
                        return;
                    }
                    if (flagState != players.get(player.getName()) && player.getGameMode() != GameMode.SPECTATOR && player.getGameMode() != GameMode.CREATIVE) {
                        player.getWorld().createExplosion(player.getLocation().add(0, 1, 0), 0, false);
                        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                        player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
                        player.damage(20);
                    }
                    canChange.put(player.getName(), true);
                });
                cancel();
            }
    }
}
