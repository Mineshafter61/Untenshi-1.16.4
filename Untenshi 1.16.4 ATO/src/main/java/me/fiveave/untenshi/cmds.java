package me.fiveave.untenshi;

import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.tc.CollisionMode;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartGroupStore;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.MinecartMemberStore;
import com.bergerkiller.bukkit.tc.properties.CartProperties;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.fiveave.untenshi.events.*;
import static me.fiveave.untenshi.main.*;

class cmds implements CommandExecutor, TabCompleter {
    String helphead = ChatColor.GOLD + "/uts ";
    String helpformat = " " + ChatColor.WHITE + "-" + ChatColor.GREEN + " ";
    String pDataInfo;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            if (!(sender instanceof Player)) {
                generalMsg(sender, ChatColor.RED, getlang("playeronlycmd"));
                return true;
            }
            if (!sender.hasPermission("uts.main") && !sender.isOp()) {
                noPerm(sender);
                return true;
            }
            Player sender2 = (Player) sender;
            // Initialize from playerdata.yml
            pDataInfo = "players." + sender2.getUniqueId();
            if (pContains(".freemode"))
                freemode.put(sender2, getPConfig().getBoolean(pDataInfo + ".freemode"));
            if (pContains(".allowatousage"))
                allowatousage.put(sender2, getPConfig().getBoolean(pDataInfo + ".allowatousage"));
            // Initialize if none (not duplicating)
            playing.putIfAbsent(sender2, false);
            speed.putIfAbsent(sender2, 0.0);
            freemode.putIfAbsent(sender2, false);
            signallimit.putIfAbsent(sender2, maxspeed);
            dooropen.putIfAbsent(sender2, 0);
            doordiropen.putIfAbsent(sender2, false);
            frozen.putIfAbsent(sender2, false);
            atsbraking.putIfAbsent(sender2, false);
            allowatousage.putIfAbsent(sender2, false);
            // Force freemode false if perm not given
            if (!sender.hasPermission("uts.freemode")) {
                freemode.put(sender2, false);
            }
            // Change arg to case-non-sensitive
            // For each arg need check length of arg to ensure safety
            if (args.length > 0) {
                label:
                switch (args[0].toLowerCase()) {
                    case "help":
                        if (args.length == 1) {
                            helpMsg(sender);
                            break;
                        }
                        if (args.length == 2) {
                            switch (args[1]) {
                                case "1":
                                    helpSectionTitle(sender, ChatColor.GREEN, args[1]);
                                    helpDesc(sender, getlang("cmdlist"));
                                    helpInfo(sender, "help <page>", getlang("help1a"));
                                    helpInfo(sender, "activate <true/false>", getlang("help1b"));
                                    helpInfo(sender, "atsconfirm/ac", getlang("help1e"));
                                    helpInfo(sender, "switchends/se", getlang("help1h"));
                                    helpInfo(sender, "pa <text>", getlang("help1i"));
                                    helpDesc(sender, getlang("help1g"));
                                    break;
                                case "2":
                                    helpSectionTitle(sender, ChatColor.LIGHT_PURPLE, args[1]);
                                    helpDesc(sender, getlang("cmdlist"));
                                    helpInfo(sender, "help <page>", getlang("help1a"));
                                    helpInfo(sender, "reload", getlang("help2a"));
                                    helpInfo(sender, "freemode <true/false>", getlang("help2c"));
                                    helpInfo(sender, "allowato <true/false>", getlang("help2d"));
                                    helpDesc(sender, getlang("help1g"));
                                    break;
                                case "3":
                                    helpSectionTitle(sender, ChatColor.GOLD, args[1]);
                                    sender.sendMessage(ChatColor.GREEN + getlang("help3a") + "\n" + ChatColor.YELLOW + getlang("help3b") + "\n" + getlang("help3c") + "\n" + getlang("help3d"));
                                    sender.sendMessage("\n" + ChatColor.RED + getlang("help3e") + "\n" + ChatColor.YELLOW + getlang("help3f") + "\n" + getlang("help3g") + "\n" + getlang("help3h"));
                                    sender.sendMessage(ChatColor.GOLD + "\n" + getlang("help3i") + "\n" + ChatColor.YELLOW + getlang("help3j") + "\n" + getlang("help3k"));
                                    break;
                                case "4":
                                    helpSectionTitle(sender, ChatColor.DARK_AQUA, args[1]);
                                    sender.sendMessage(ChatColor.GOLD + getlang("help4a") + "\n" + ChatColor.GREEN + getlang("help4b") + ChatColor.YELLOW + getlang("help4c") + "\n" + ChatColor.GREEN + getlang("help4f") + ChatColor.YELLOW + getlang("help4c1"));
                                    sender.sendMessage("\n" + ChatColor.GOLD + getlang("help4d") + "\n" + ChatColor.GREEN + getlang("help4b") + ChatColor.YELLOW + getlang("help4e") + "\n" + ChatColor.GREEN + getlang("help4f") + ChatColor.YELLOW + getlang("help4g"));
                                    sender.sendMessage("\n" + ChatColor.GOLD + getlang("help4h") + "\n" + ChatColor.GREEN + getlang("help4b") + ChatColor.YELLOW + getlang("help4i") + "\n" + getlang("help4j") + "\n" + getlang("help4k") + "\n" + getlang("help4l") + "\n" + ChatColor.GREEN + getlang("help4f") + "\n" + ChatColor.YELLOW + getlang("help4m"));
                                    sender.sendMessage("\n" + ChatColor.GOLD + getlang("help4n"));
                                    sender.sendMessage("\n" + ChatColor.GOLD + getlang("help4o") + "\n" + ChatColor.GREEN + getlang("help4b") + ChatColor.YELLOW + getlang("help4p") + "\n" + ChatColor.GREEN + getlang("help4f") + ChatColor.YELLOW + getlang("help4q"));
                                    break;
                                default:
                                    generalMsg(sender, ChatColor.YELLOW, getlang("pagenotexist"));
                                    break;
                            }
                        }
                        break;
                    case "activate":
                        if (args.length == 2) {
                            switch (args[1].toLowerCase()) {
                                case "true":
                                    if (playing.get(sender)) {
                                        generalMsg(sender, ChatColor.YELLOW, getlang("activatedalready"));
                                        break label;
                                    }
                                    if (!sender2.isInsideVehicle()) {
                                        generalMsg(sender, ChatColor.YELLOW, getlang("sitincart"));
                                        break label;
                                    }
                                    // Get train
                                    Entity selcart = sender2.getVehicle();
                                    MinecartGroup mg = MinecartGroupStore.get(selcart);
                                    TrainProperties tprop = mg.getProperties();
                                    if (mg.size() == 1) {
                                        generalMsg(sender, ChatColor.YELLOW, getlang("sitincart"));
                                        break label;
                                    }
                                    // Detect owner
                                    if ((tprop.getOwners().size() == 1 && tprop.getOwners().contains(sender2.getName().toLowerCase()) || tprop.getOwners().isEmpty())) {
                                        // Train settings
                                        tprop.setSlowingDown(false);
                                        tprop.setOwner(sender2.getName(), true);
                                        tprop.setSpeedLimit(0);
                                        mg.setForwardForce(0);
                                        // Anti collision
                                        tprop.setCollision(tprop.getCollision().cloneAndSetMiscMode(CollisionMode.CANCEL));
                                        tprop.setCollision(tprop.getCollision().cloneAndSetPlayerMode(CollisionMode.CANCEL));
                                        // Reset values and init
                                        dooropen.put(sender2, 0);
                                        doordiropen.put(sender2, false);
                                        doorconfirm.put(sender2, false);
                                        fixstoppos.put(sender2, false);
                                        staeb.put(sender2, false);
                                        staaccel.put(sender2, false);
                                        speedlimit.put(sender2, maxspeed);
                                        signallimit.put(sender2, maxspeed);
                                        mascon.put(sender2, -9);
                                        current.put(sender2, -480.0);
                                        points.put(sender2, 30);
                                        atsbraking.put(sender2, false);
                                        atsping.put(sender2, false);
                                        atspnear.put(sender2, false);
                                        overrun.put(sender2, false);
                                        deductdelay.put(sender2, 49);
                                        signaltype.put(sender2, "ats");
                                        reqstopping.put(sender2, false);
                                        atsforced.put(sender2, 0);
                                        atopisdirect.putIfAbsent(sender2, false);
                                        atoforcebrake.putIfAbsent(sender2, false);
                                        // Set train group for player
                                        if (selcart instanceof Minecart) {
                                            //noinspection rawtypes
                                            MinecartMember mem = (MinecartMember) CommonEntity.get(selcart).getController();
                                            if (!selcart.getPassengers().isEmpty() && selcart.getPassengers().get(0) instanceof Player) {
                                                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                                    CartProperties.setEditing((Player) selcart.getPassengers().get(0), mem.getProperties());
                                                    train.put(sender2, mem.getGroup());
                                                }, interval);
                                            }
                                        }
                                        // Save inventory
                                        inv.put(sender2, sender2.getInventory().getContents());
                                        // Clear other slots
                                        for (int i = 0; i < 41; i++) {
                                            sender2.getInventory().setItem(i, new ItemStack(Material.AIR));
                                        }
                                        // Set wands in place
                                        sender2.getInventory().setItem(0, upWand());
                                        sender2.getInventory().setItem(1, nWand());
                                        sender2.getInventory().setItem(2, downWand());
                                        sender2.getInventory().setItem(6, ebButton());
                                        sender2.getInventory().setItem(7, sbLever());
                                        sender2.getInventory().setItem(8, doorButton());
                                        // Playing = true
                                        playing.put(sender2, true);
                                        motion.recursion1(sender);
                                        generalMsg(sender, ChatColor.YELLOW, getlang("trainset"));
                                        generalMsg(sender, ChatColor.YELLOW, getlang("activate") + ChatColor.GREEN + getlang("enable"));
                                    } else {
                                        generalMsg(sender, ChatColor.RED, getlang("notowner"));
                                    }
                                    break label;
                                case "false":
                                    if (playing.get(sender)) {
                                        restoreinit(sender2);
                                    } else {
                                        generalMsg(sender, ChatColor.YELLOW, getlang("deactivatedalready"));
                                    }
                                    break label;
                            }
                        }
                        sender.sendMessage(pureutstitle + ChatColor.YELLOW + "[" + getlang("usage") + ChatColor.GOLD + "/uts activate <true/false>" + ChatColor.YELLOW + "]\n" + getlang("activateinfo1") + "\n" + getlang("activateinfo2"));
                        break;
                    case "atsconfirm":
                    case "ac":
                        if (!signallimit.get(sender).equals(0) && atsbraking.get(sender)) {
                            atsbraking.put(sender2, false);
                            sender.sendMessage(utshead + ChatColor.GOLD + getlang("acsuccess"));
                        } else if (signallimit.get(sender).equals(0) || (atsbraking.get(sender) && speed.get(sender) > 0)) {
                            sender.sendMessage(utshead + ChatColor.RED + getlang("acfailed"));
                        } else {
                            sender.sendMessage(utshead + ChatColor.YELLOW + getlang("acnotneeded"));
                        }
                        break;
                    case "freemode":
                        if (cannotSetTrain(args, sender2)) break;
                        if (args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false")) {
                            freemode.put(sender2, Boolean.valueOf(args[1].toLowerCase()));
                            sender.sendMessage(pureutstitle + ChatColor.YELLOW + getlang("freemode") + (freemode.get(sender2) ? ChatColor.GREEN + getlang("enable") : ChatColor.RED + getlang("disable")));
                            break;
                        }
                        sender.sendMessage(pureutstitle + ChatColor.YELLOW + "[" + getlang("usage") + ChatColor.GOLD + "/uts freemode <true/false>" + ChatColor.YELLOW + "]\n" + getlang("freemodeinfo1") + "\n" + getlang("freemodeinfo2"));
                        break;
                    case "allowato":
                        if (cannotSetTrain(args, sender2)) break;
                        if (args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false")) {
                            allowatousage.put(sender2, Boolean.valueOf(args[1].toLowerCase()));
                            sender.sendMessage(pureutstitle + ChatColor.YELLOW + getlang("ato") + (allowatousage.get(sender) ? ChatColor.GREEN + getlang("enable") : ChatColor.RED + getlang("disable")));
                            break;
                        }
                        sender.sendMessage(pureutstitle + ChatColor.YELLOW + "[" + getlang("usage") + ChatColor.GOLD + "/uts allowato <true/false>" + ChatColor.YELLOW + "]\n" + getlang("atoinfo1") + "\n" + getlang("atoinfo2"));
                        break;
                    case "switchends":
                    case "se":
                        if (playing.get(sender)) {
                            if (speed.get(sender).equals(0.0)) {
                                Entity selcart = sender2.getVehicle();
                                MinecartGroup mg = MinecartGroupStore.get(selcart);
                                MinecartMember<?> mm = MinecartMemberStore.getFromEntity(selcart);
                                MinecartMember<?> mm2 = mm;
                                assert mm != null;
                                // Check if head or tail is not current cart and they are minecarts
                                if ((mg.head() != mm || mg.tail() != mm) && !frozen.get(sender)) {
                                    // Must clear passenger in cart, or else player will bug out
                                    if (mg.head() != mm) {
                                        mm2 = mg.head();
                                        mm2.eject();
                                    } else if (mg.tail() != mm) {
                                        mm2 = mg.tail();
                                        mm2.eject();
                                    }
                                    if (mm2.getEntity().getEntity() instanceof RideableMinecart) {
                                        // MUST wait after eject, and teleport, and finally enter, or else will bug
                                        frozen.put(sender2, true);
                                        mm.eject();
                                        MinecartMember<?> finalMm = mm2;
                                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                            sender2.teleport(finalMm.getEntity().getLocation());
                                            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                                finalMm.addPassengerForced(sender2);
                                                generalMsg(sender2, ChatColor.YELLOW, getlang("sesuccess"));
                                                frozen.put(sender2, false);
                                            }, interval);
                                        }, interval);
                                        break;
                                    }
                                }
                                generalMsg(sender, ChatColor.RED, getlang("sefailed"));
                            } else {
                                generalMsg(sender, ChatColor.YELLOW, getlang("seinmotion"));
                            }
                        } else {
                            generalMsg(sender, ChatColor.YELLOW, getlang("activatefirst"));
                        }
                        break;
                    case "pa":
                        if (checkPerm(sender2, "uts.pa")) break;
                        if (playing.get(sender)) {
                            MinecartGroup mg = MinecartGroupStore.get(sender2.getVehicle());
                            //noinspection rawtypes
                            for (MinecartMember mm : mg) {
                                if (!mm.getEntity().getPassengers().isEmpty()) {
                                    Player p = (Player) mm.getEntity().getPassengers().get(0);
                                    String s;
                                    int arglength = args.length;
                                    if (arglength > 1 && args[1] != null) {
                                        StringBuilder sBuilder = null;
                                        for (int i = 1; i < arglength; i++) {
                                            if (sBuilder != null) {
                                                sBuilder.append(" ").append(args[i]);
                                            } else {
                                                sBuilder = new StringBuilder(args[i]);
                                            }
                                        }
                                        s = sBuilder.toString();
                                        // To keep & type \&
                                        s = s.replaceAll("\\\\&", "\\\\and");
                                        s = s.replaceAll("&", "§");
                                        s = s.replaceAll("\\\\and", "&");
                                        generalMsg(p, ChatColor.YELLOW, getlang("help1i") + ": " + s);
                                    } else {
                                        sender2.sendMessage(utshead + ChatColor.RED + getlang("panoempty"));
                                    }
                                }
                            }
                        } else {
                            generalMsg(sender, ChatColor.YELLOW, getlang("activatefirst"));
                        }
                        break;
                    case "reload":
                        if (checkPerm(sender2, "uts.reload")) break;
                        plugin.reloadConfig();
                        config = new abstractfile(plugin, "config.yml");
                        traindata = new abstractfile(plugin, "traindata.yml");
                        signalorder = new abstractfile(plugin, "signalorder.yml");
                        langdata = new abstractfile(plugin, "lang_" + plugin.getConfig().getString("lang") + ".yml");
                        sender.sendMessage(utshead + ChatColor.YELLOW + getlang("reloaded"));
                        break;
                    default:
                        generalMsg(sender, ChatColor.YELLOW, getlang("cmdnotexist"));
                        break;
                }
            } else {
                helpMsg(sender);
            }
            // Save in config
            getPConfig().set(pDataInfo + ".freemode", freemode.get(sender2));
            getPConfig().set(pDataInfo + ".allowatousage", allowatousage.get(sender2));
            playerdata.save();

        } catch (Exception e) {
            sender.sendMessage(utshead + ChatColor.RED + getlang("error"));
            e.printStackTrace();
        }
        return true;
    }

    private boolean cannotSetTrain(String[] args, Player sender2) {
        return checkPerm(sender2, "uts." + args[0].toLowerCase()) || reqDeactivate(sender2) || args.length != 2;
    }

    private boolean reqDeactivate(CommandSender sender) {
        if (playing.get((Player) sender)) {
            generalMsg(sender, ChatColor.YELLOW, getlang("deactivatefirst"));
            return true;
        }
        return false;
    }

    private boolean checkPerm(Player sender2, String name) {
        if (!sender2.hasPermission(name)) {
            noPerm(sender2);
            return true;
        }
        return false;
    }

    boolean pContains(String s) {
        return getPConfig().contains(pDataInfo + s);
    }

    void noPerm(CommandSender sender) {
        sender.sendMessage(utshead + ChatColor.RED + getlang("noperm"));
    }

    static void helpSectionTitle(CommandSender sender, ChatColor color, String arg) {
        sender.sendMessage(pureutstitle + color + " ----- " + getlang("help" + arg + "title") + " -----");
    }

    static void generalMsg(CommandSender sender, ChatColor color, String s) {
        sender.sendMessage(utshead + color + s);
    }

    void helpDesc(CommandSender sender, String s) {
        sender.sendMessage(ChatColor.YELLOW + s);
    }

    void helpInfo(CommandSender sender, String s, String t) {
        sender.sendMessage(helphead + s + helpformat + t);
    }

    void helpMsg(CommandSender sender) {
        sender.sendMessage(pureutstitle + helphead + "help <page>" + helpformat + getlang("help1a"));
    }

    FileConfiguration getPConfig() {
        return playerdata.dataconfig;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> ta = new ArrayList<>();
        List<String> result = new ArrayList<>();
        int arglength = args.length;
        if (arglength == 1) {
            ta.addAll(Arrays.asList("help", "activate", "atsconfirm", "ac", "switchends", "se", "freemode", "reload", "pa", "allowato"));
            for (String a : ta) {
                if (a.toLowerCase().startsWith(args[0].toLowerCase())) {
                    result.add(a);
                }
            }
            return result;
        } else if (arglength == 2) {
            switch (args[0].toLowerCase()) {
                case "help":
                    ta.addAll(Arrays.asList("1", "2", "3", "4"));
                    break;
                case "activate":
                case "freemode":
                case "allowato":
                    ta.addAll(Arrays.asList("true", "false"));
                    break;
                default:
                    ta.add("");
                    break;
            }
            for (String a : ta) {
                if (a.toLowerCase().startsWith(args[1].toLowerCase())) {
                    result.add(a);
                }
            }
            return result;
            // Stop spamming player names
        } else if (arglength > 2) {
            result.add("");
            return result;
        }
        return null;
    }
}