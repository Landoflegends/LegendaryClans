/*
 * LegendaryClans - by LandofLegend Custom Development Team
 * http://www.landoflegend.net
 */
package me.blackhawklex.legendaryclans.commands;

import me.blackhawklex.legendaryclans.LegendaryClans;
import me.blackhawklex.legendaryclans.LegendaryPlayer;
import me.blackhawklex.legendaryclans.clans.Clan;
import me.blackhawklex.legendaryclans.party.Party;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandExecutor_Chat implements CommandExecutor{
    
    private LegendaryClans plugin;

    public CommandExecutor_Chat(LegendaryClans plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command,String label, String[] args) {
        if (command.getName().equalsIgnoreCase("ch")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Y U NO PLAYER??!111");
                return true;
            }
            if (args.length == 0) {
                printHelp(sender);
                return true;
            }
            if (args.length == 1) {
                LegendaryPlayer player = plugin.getPlayerManager().searchPlayerByName(sender.getName());
                if(player != null){
                    if(args[0].equalsIgnoreCase("g")){
                        player.setChat(0);
                        LegendaryClans.coloredOutput(sender, "&bChanged chat to global.");
                        return true;
                    }
                    if (args[0].equalsIgnoreCase("c")) {
                        Clan clan = plugin.getClanManager().getClanByPlayer(player);
                        if(clan != null){
                            player.setChat(3);
                            LegendaryClans.coloredOutput(sender, "&bChanged chat to clanchat.");
                        } else{
                            LegendaryClans.coloredOutput(sender, "&4You do not have a clan.");
                        }
                        return true;
                    }
                    if (args[0].equalsIgnoreCase("l")) {
                        player.setChat(1);
                        LegendaryClans.coloredOutput(sender, "&bChanged chat to local.");
                        return true;
                    }
                    if (args[0].equalsIgnoreCase("p")) {
                        Party party = plugin.getPartyManager().getPartyByPlayer(player);
                        if(party != null){
                            player.setChat(2);
                            LegendaryClans.coloredOutput(sender, "&bChanged chat to party chat.");
                        }else{
                            LegendaryClans.coloredOutput(sender, "&4You do not have a party.");
                        }
                        return true;
                    }
                    
                    if (args[0].equalsIgnoreCase("ig")) {
                        LegendaryPlayer legP = plugin.getPlayerManager().searchPlayerByName(sender.getName());
                        if(legP != null){
                            if(legP.isIgnoreChat()){
                                LegendaryClans.coloredOutput(sender, "&bEnabled global chat.");
                                legP.setIgnoreChat(false);
                            } else {
                                LegendaryClans.coloredOutput(sender, "&bYou are now ignoring global chat. Type /ch ig to enable it again!");
                                legP.setIgnoreChat(true);
                            }
                            return true;
                        }
                    }
                }
            }

        }
        return false;
    }
    
    public void printHelp(CommandSender sender){
        LegendaryClans.coloredOutput(sender, "&6/ch c for clan chat");
        LegendaryClans.coloredOutput(sender, "&e/ch g for global chat");
        LegendaryClans.coloredOutput(sender, "/ch l for local chat");
        LegendaryClans.coloredOutput(sender, "&9/ch p for party chat");
    }
}
