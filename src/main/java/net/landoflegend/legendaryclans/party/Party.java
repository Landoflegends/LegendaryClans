/*
 * LegendaryClans - by LandofLegend Custom Development Team
 * http://www.landoflegend.net
 */
package me.blackhawklex.legendaryclans.party;

import java.util.List;
import me.blackhawklex.legendaryclans.LegendaryClans;
import me.blackhawklex.legendaryclans.LegendaryPlayer;
import net.virtuallyabstract.minecraft.DungeonBuilder;
import net.virtuallyabstract.minecraft.DungeonParty;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Party {
    private String name;

    private int level = 1;

    private int exp = 0;

    private LegendaryPlayer leader;

    private List<LegendaryPlayer> members;

    private PartyManager manager;

    private DungeonBuilder dunBuilder;

    private DungeonParty dunParty;

    public Party(String name, LegendaryPlayer leader, List<LegendaryPlayer> members, PartyManager manager) {
        this.name = name;
        this.leader = leader;
        this.members = members;
        this.manager = manager;
        manager.addParty(this);

        //add support for dungeon parties
        dunBuilder = manager.getPlugin().getDungeonBuilder();
        dunParty = new DungeonParty(leader.getName(), manager.getPlugin().getServer());
        dunBuilder.inParty.put(leader.getName(), dunParty);

        for (LegendaryPlayer member : members) {
            dunParty.addMember(member.getName());
            dunBuilder.inParty.put(member.getName(), dunParty);
        }
        //support for dungeonBuilders

    }

    public LegendaryPlayer getLeader() {
        return leader;
    }

    public List<LegendaryPlayer> getMembers() {
        return members;
    }

    public String getName() {
        return name;
    }

    public void addMember(LegendaryPlayer player) {
        members.add(player);

        //support for dungeon
        dunBuilder.inParty.put(player.getName(), dunParty);
        dunParty.addMember(player.getName());
    }

    public void deleteMember(LegendaryPlayer player) {
        if (members.contains(player)) {
            members.remove(player);

            //support dungeon groups
            String membername = player.getName();
            dunBuilder.inParty.remove(membername);
            dunParty.removeMember(membername);
            if (dunBuilder.inDungeons.containsKey(membername)) {
                dunBuilder.inDungeons.get(membername).removeParty(dunParty);
                Player member = manager.getPlugin().getServer().getPlayer(membername);
                if (member != null) {
                    dunBuilder.removePlayerFromDungeon(member, true);
                }
            }
            //support dungeongroups

            if (leader.equals(player)) {
                leader = members.get(0);
            }
        }
    }

    public void transferLead(LegendaryPlayer leader) {
        this.leader = leader;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMemberCount() {
        return members.size();
    }

    public void broadcastMessage(String message) {
        for (LegendaryPlayer p : members) {
            CommandSender sender = (CommandSender) p.getPlayer();
            LegendaryClans.coloredOutput(sender, message);
        }
    }

    public void broadcastRawMessage(String message) {
        for (LegendaryPlayer p : members) {
            CommandSender sender = (CommandSender) p.getPlayer();
            p.getPlayer().sendRawMessage(message);
        }
    }

    public int getExp() {
        return exp;
    }

    public int getLevel() {
        return level;
    }

    public void addExp(int amount) {
        exp = exp + amount;
    }

    // add exp via this method and its amount.
    public void regulateLevel(int amount) {
        if (level < PartyManager.getMaxLevel()) {
            if (exp + amount < PartyManager.getRequiredXpPerLevel()[level - 1]) {
                exp = exp + amount;
            } else {
                int newAmount = exp + amount - PartyManager.getRequiredXpPerLevel()[level - 1];
                level++;
                broadcastMessage("&eYour party level increased to " + level + ".");
                exp = 0;
                regulateLevel(newAmount);
            }
        }
    }

//    for (String membername
//
//    : party.listMembers () 
//        ) {
//                inParty.remove(membername);
//        if (inDungeons.containsKey(membername)) {
//            inDungeons.get(membername).removeParty(party);
//            Player member = server.getPlayer(membername);
//            if (member != null) {
//                removePlayerFromDungeon(member, true);
//            }
//        }
//    }
//    
//    for (ArrayList<Dungeon> dungeons
//
//    : dungeonMap.values () 
//        ) {
//                for (Dungeon d : dungeons) {
//            d.removeParty(party);
//        }
//    }
    public DungeonParty getDunParty() {
        return dunParty;
    }

}
