/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.blackhawklex.legendaryclans.party;

import me.blackhawklex.gui.NormalInvitationRequest;
import me.blackhawklex.legendaryclans.LegendaryClans;
import me.blackhawklex.legendaryclans.LegendaryPlayer;
import org.bukkit.command.CommandSender;

/**
 *
 * @author BlackHawkLex <me.BlackHawkLex at bondcraft.bplaced.org>
 */
public class Invitation {

    private boolean pending = true;
    private Party party;
    private LegendaryPlayer askedPlayer;
    private LegendaryPlayer leader;
    private LegendaryClans plugin;

    public Invitation(Party party, LegendaryPlayer askedPlayer, LegendaryPlayer leader, LegendaryClans plugin) {
        this.party = party;
        this.askedPlayer = askedPlayer;
        this.leader = leader;
        this.plugin = plugin;
        String message = "&2Invite sent to "+askedPlayer.getName();
        LegendaryClans.coloredOutput((CommandSender)leader.getPlayer(), LegendaryClans.parseColor(message));
        //open Invitation menue
        openInviteMenue();        
    }

    public void acceptInvite() {
        if(!party.getMembers().contains(leader)){
            party.addMember(leader);
        }
        
        party.addMember(askedPlayer);
        
        String message = "&2Welcome "+askedPlayer.getName() + " to the party!";
        party.broadcastMessage(message);
    }

    public void denyInvite() {
        pending = false;
        CommandSender sender = (CommandSender)leader.getPlayer();
        String message = "&6"+askedPlayer.getName()+" refused to join.";
        LegendaryClans.coloredOutput(sender,message);
    }

    public void openInviteMenue() {
        NormalInvitationRequest req = new NormalInvitationRequest(plugin, plugin.getSpoutManager().getPlayer(askedPlayer.getPlayer()), this);
    }

    public LegendaryPlayer getAskedPlayer() {
        return askedPlayer;
    }

    public LegendaryPlayer getLeader() {
        return leader;
    }

    public Party getParty() {
        return party;
    }

    public boolean isPending() {
        return pending;
    }

    public void setParty(Party party) {
        this.party = party;
    }
    
    
}
