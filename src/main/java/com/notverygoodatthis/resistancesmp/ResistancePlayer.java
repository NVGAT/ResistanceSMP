package com.notverygoodatthis.resistancesmp;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ResistancePlayer {
    public Player player;
    public int resistance;

    public ResistancePlayer(Player player) {
        this.player = player;
        this.resistance = ResistanceSMP.getResistanceForPlayer(player.getName());
    }

    public Player getPlayer() {
        return player;
    }

    public int getResistance() {
        return resistance;
    }

    public void incrementResistance() {
        if(resistance != 5) {
            ResistanceSMP.playerResistance.remove(player.getName(), resistance);
            resistance++;
            ResistanceSMP.playerResistance.put(player.getName(), resistance);
            Bukkit.getLogger().info(String.format("Successfully incremented the tier of %s to %d", player.getName(), resistance));
        } else {
            Bukkit.getLogger().info(String.format("Couldn't increment the resistance of %s because it's at 5", player.getName()));
        }
    }

    public void decrementResistance() {
        if(resistance != -5) {
            ResistanceSMP.playerResistance.remove(player.getName(), resistance);
            resistance--;
            ResistanceSMP.playerResistance.put(player.getName(), resistance);
            Bukkit.getLogger().info(String.format("Successfully decremented the tier of %s to %d", player.getName(), resistance));
        } else {
            Bukkit.getLogger().info(String.format("Couldn't decrement the resistance of %s because it's at -5", player.getName()));
        }
    }

    public void setResistance(int newRes) {
        ResistanceSMP.playerResistance.remove(player.getName(), resistance);
        resistance = newRes;
        ResistanceSMP.playerResistance.put(player.getName(), resistance);
    }
}
