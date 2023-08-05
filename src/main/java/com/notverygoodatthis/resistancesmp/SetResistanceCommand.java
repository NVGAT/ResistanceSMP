package com.notverygoodatthis.resistancesmp;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetResistanceCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        ResistancePlayer target = new ResistancePlayer(Bukkit.getPlayerExact(strings[0]));
        int newRes = Integer.parseInt(strings[1]);
        target.setResistance(newRes);
        for(Player p : Bukkit.getServer().getOnlinePlayers()) {
            String name = String.format("[%d] %s", ResistanceSMP.getResistanceForPlayer(p.getName()), p.getName());
            p.setPlayerListName(name);
        }
        commandSender.sendMessage(String.format("<ResistanceSMP> Set the resistance of %s to %d!", strings[0], newRes));
        return true;
    }
}
