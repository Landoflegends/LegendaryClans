/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.blackhawklex.legendaryclans.party;

import java.util.ArrayList;
import java.util.List;
import me.blackhawklex.legendaryclans.LegendaryClans;
import me.blackhawklex.legendaryclans.LegendaryPlayer;
import net.virtuallyabstract.minecraft.Dungeon;
import net.virtuallyabstract.minecraft.DungeonBuilder;

/**
 *
 * @author BlackHawkLex <me.BlackHawkLex at bondcraft.bplaced.org>
 */
public class PartyManager {
    private LegendaryClans plugin;

    private List<Party> parties = new ArrayList<Party>();

    private static final int[] requiredXpPerLevel = {50, 100, 200, 350, 500, 700, 950, 1250, 1500, 1750, 2000, 2250, 2600, 3000, 3500, 4000, 4500, 5000, 6000, 10000}; //20 level

    private static final int maxLevel = 20;

    public PartyManager(LegendaryClans plugin) {
        this.plugin = plugin;
    }

    public List<Party> getParties() {
        return parties;
    }

    public void addParty(Party p) {
        parties.add(p);
    }

    public Party getPartyByName(String partyName) {
        for (Party p : parties) {
            if (p.getName().equals(partyName)) {
                return p;
            }
        }
        return null;
    }

    public Party getPartyByLeader(LegendaryPlayer leader) {
        for (Party p : parties) {
            if (p.getLeader().equals(leader)) {
                return p;
            }
        }
        return null;
    }

    public void removeParty(Party party) {
        if (parties.contains(party)) {
            parties.remove(party);

            //support for dungeon parties.
            for (ArrayList<Dungeon> dungeons : plugin.getDungeonBuilder().dungeonMap.values()) {
                for (Dungeon d : dungeons) {
                    d.removeParty(party.getDunParty());
                }
            }
            //support for dungeon parties


        }
    }

    public boolean checkIfIsLeader(LegendaryPlayer player) {
        for (Party p : parties) {
            if (p.getLeader().equals(player)) {
                return true;
            }
        }

        return false;
    }

    public Party getPartyByPlayer(LegendaryPlayer player) {
        for (Party p : parties) {
            for (LegendaryPlayer player2 : p.getMembers()) {
                if (player2.equals(player)) {
                    return p;
                }
            }
        }
        return null;
    }

    public static int getMaxLevel() {
        return maxLevel;
    }

    public static int[] getRequiredXpPerLevel() {
        return requiredXpPerLevel;
    }

    public LegendaryClans getPlugin() {
        return plugin;
    }

    public Dungeon getDungeonByPlayer(LegendaryPlayer player) {
        DungeonBuilder dunBuilder = plugin.getDungeonBuilder();
        return dunBuilder.inDungeons.get(player.getName());
    }

}
