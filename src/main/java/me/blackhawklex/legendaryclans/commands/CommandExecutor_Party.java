/*
 * LegendaryClans - by BlackHawkLex
 * http://
 *
 * powered by Kickstarter
 */

package me.blackhawklex.legendaryclans.commands;


import me.blackhawklex.gui.NoPartyMenue;
import me.blackhawklex.gui.PartyMemberListMenue;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.blackhawklex.legendaryclans.LegendaryClans;
import me.blackhawklex.legendaryclans.LegendaryPlayer;
import me.blackhawklex.legendaryclans.PlayerManager;
import me.blackhawklex.legendaryclans.party.Invitation;
import me.blackhawklex.legendaryclans.party.Party;
import me.blackhawklex.legendaryclans.party.PartyManager;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;


public class CommandExecutor_Party implements CommandExecutor {
	private LegendaryClans plugin;

	public CommandExecutor_Party(LegendaryClans plugin){
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,String label, String[] args) {
		if (command.getName().equalsIgnoreCase("party")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("Y U NO PLAYER??!111");
				return true;
			}
                        if(args.length == 0){
                            showPartyMenue(sender);
                            return true;
                        } else if (args.length == 2 && args[0].equalsIgnoreCase("invite")) {
                            String playerName = args[1];
                            inviteDirectly(playerName, sender);
                            return true;
                        }
                        return true;
		}
		return false;
	}
        
        public void showPartyMenue(CommandSender sender){
            PartyManager partyManager = plugin.getPartyManager();
            PlayerManager playerManager = plugin.getPlayerManager();
            Player senderAsPlayer = (Player)sender;
            SpoutPlayer spoutPlayer = SpoutManager.getPlayer(senderAsPlayer);
            if(partyManager.getPartyByPlayer(playerManager.searchPlayerByName(senderAsPlayer.getName())) == null){
                NoPartyMenue partyMenue = new NoPartyMenue(plugin,spoutPlayer);
            }
            else{
                PartyMemberListMenue menue = new PartyMemberListMenue(plugin, spoutPlayer, partyManager.getPartyByPlayer(playerManager.searchPlayerByName(senderAsPlayer.getName())));
            }
        }
        
        
        public void inviteDirectly(String playerName, CommandSender sender){
            LegendaryPlayer playerToBeInvited = plugin.getPlayerManager().searchPlayerByName(playerName);
            LegendaryPlayer leader = plugin.getPlayerManager().searchPlayerByName(sender.getName());
            Party party = plugin.getPartyManager().getPartyByLeader(leader);
            Party party2 = plugin.getPartyManager().getPartyByPlayer(leader);
            if((party == null && party2 == null) || (party != null && party2 != null)){
                if (playerToBeInvited != null && !playerToBeInvited.getName().equalsIgnoreCase(leader.getName()) && plugin.getPartyManager().getPartyByPlayer(playerToBeInvited) == null && playerToBeInvited.isOnline()) {
                        Invitation invite = new Invitation(party, playerToBeInvited, leader, plugin);
                } else {
                    String message = "&6No such player online or player already in party!";
                    LegendaryClans.coloredOutput(sender, message);
                }
            }
            if(party == null && party2 != null){
                String message = "&4Only the party leader can invite people!";
                LegendaryClans.coloredOutput(sender, message);
            }
        }
}
