package com.notverygoodatthis.resistancesmp;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WithdrawCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player) {
            ResistancePlayer p = new ResistancePlayer((Player) commandSender);
            int input = Integer.parseInt(strings[0]);
            if(input < 0) {
                p.getPlayer().sendMessage(ResistanceSMP.resistanceText("Nice try, you can only withdraw a positive amount of resistance :)"));
            }
            if(p.getResistance() - input >= -5) {
                p.setResistance(p.getResistance() - input);
                if(p.getPlayer().getInventory().firstEmpty() == -1) {
                    p.getPlayer().getWorld().dropItemNaturally(p.getPlayer().getLocation(), ResistanceSMP.getResistanceBook(input));
                    p.getPlayer().sendMessage(String.format(ResistanceSMP.resistanceText("You've withdrawn %d resistance"), input));
                } else {
                    p.getPlayer().getInventory().addItem(ResistanceSMP.getResistanceBook(input));
                    p.getPlayer().sendMessage(String.format(ResistanceSMP.resistanceText("You've withdrawn %d resistance"), input));
                }
                for(Player pl : Bukkit.getServer().getOnlinePlayers()) {
                    String name = String.format("[%d] %s", ResistanceSMP.getResistanceForPlayer(pl.getName()), pl.getName());
                    pl.setPlayerListName(name);
                }
                return true;
            }
        }
        return false;
    }
}
