/*
 * LegendaryClans - by LandofLegend Custom Development Team
 * http://www.landoflegend.net
 */
package me.blackhawklex.legendaryclans.commands;

import me.blackhawklex.legendaryclans.BlackLocation;
import me.blackhawklex.legendaryclans.LegendaryClans;
import me.blackhawklex.legendaryclans.LegendaryPlayer;
import me.blackhawklex.legendaryclans.clans.Clan;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandExecutor_AdminClanCommands implements CommandExecutor {

    private LegendaryClans plugin;

    public CommandExecutor_AdminClanCommands(LegendaryClans plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command,String label, String[] args) {
        if (command.getName().equalsIgnoreCase("aclan")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Y U NO PLAYER??!111");
                return true;
            }
            if (LegendaryClans.getPermission().has(sender, "LegendaryClans.Admin")) {
                setupBlockSacrificationZone(sender, args);
                addMemberToClanManually(sender,args);
                dissmissMemberFromClanManually(sender, args);
                setClanCredits(sender,args);
                promoteClanMemberManually(sender,args);
                setClanFounderManually(sender,args);
                setupNewTeleportLocation(sender, args);
                plugin.getClanManager().saveAllClans();
            } else {
                LegendaryClans.coloredOutput(sender, "&4You don´t have permission for admin commands");
            }
            return true;

        }
        return false;
    }
    
    //aclan tel name
    public void setupNewTeleportLocation(CommandSender sender, String[]args){
        if(args.length == 2 && args[0].equalsIgnoreCase("tel")){
            Location playerLoc = plugin.getServer().getPlayer(sender.getName()).getLocation();
            BlackLocation loc = new BlackLocation(playerLoc.getWorld(),playerLoc.getBlockX(), playerLoc.getBlockY(), playerLoc.getBlockZ(), args[1]);
            plugin.getCfg().addTelLoc(loc);
            LegendaryClans.coloredOutput(sender, "&2Added teleport location "+loc.getName()+" to world "+loc.getWorld().getName()+ " correctly!");
        }
    }
    
    public void addAllPlayersToSacrificationZone(CommandSender sender){
        for(LegendaryPlayer player:plugin.getPlayerManager().getPlayers()){
            plugin.addPlayerToRegionAsMember("salva", "blocksacrification", player);
        }
        LegendaryClans.coloredOutput(sender, "&2All players added correctly, clan sacrification now fully enabled!");
    }
    
    public void setupBlockSacrificationZone(CommandSender sender, String[] args){
            if (args.length == 1 && args[0].equalsIgnoreCase("setupDist")) {
                addAllPlayersToSacrificationZone(sender);
            }
    }
    
    // aclan addMember clan member NOTE: THIS WILL BYPASS MEMBER RESTRICTIONS!
    public void addMemberToClanManually(CommandSender sender, String[] args){
        if(args.length == 3 && args[0].equalsIgnoreCase("addMember")){
            Clan clan = plugin.getClanManager().getClanByName(args[1]);
            if(clan != null){
                LegendaryPlayer player = plugin.getPlayerManager().searchPlayerByName(args[2]);
                if(player != null){
                    clan.addMember(player);
                    LegendaryClans.coloredOutput(sender, "&2Player successfully added to clan!");
                }
                else{
                    LegendaryClans.coloredOutput(sender, "&4No player with that name existing!");
                }
            }
            else{
                LegendaryClans.coloredOutput(sender, "&4No clan with that name existing!");
            }
        }
    }
    // aclan removeMember clan member NOTE: THIS MAY DISSOLVE A CLAN!
    public void dissmissMemberFromClanManually(CommandSender sender, String[]args){
        if(args.length == 3 && args[0].equalsIgnoreCase("removeMember")){
            Clan clan = plugin.getClanManager().getClanByName(args[1]);
            if (clan != null) {
                LegendaryPlayer player = plugin.getPlayerManager().searchPlayerByName(args[2]);
                if (player != null) {
                    Clan clanOfPlayer = plugin.getClanManager().getClanByPlayer(player);
                    if(clanOfPlayer != null){
                        if(clanOfPlayer.equals(clan)){
//                            LegendaryPlayer submitter = plugin.getPlayerManager().searchPlayerByName(sender.getName());
                            clanOfPlayer.leaveClan(player);
                            LegendaryClans.coloredOutput(sender, "&2Player successfully removed from clan!"); 
                        }
                        else{
                           LegendaryClans.coloredOutput(sender, "&4The clan given and the player clan are not the same!");  
                        }
                    }
                    else{
                       LegendaryClans.coloredOutput(sender, "&4That player has no clan!"); 
                    }
                } else {
                    LegendaryClans.coloredOutput(sender, "&4No player with that name existing!");
                }
            } else {
                LegendaryClans.coloredOutput(sender, "&4No clan with that name existing!");
            } 
        }
    }
    
    //aclan addCredits clan amount
    public void setClanCredits(CommandSender sender, String[] args){
        if(args.length == 3 && args[0].equalsIgnoreCase("addCredits")){
            Clan clan = plugin.getClanManager().getClanByName(args[1]);
            if(clan != null){
                int amount = 0;
                try {
                   amount = Integer.parseInt(args[2]); 
                } catch (Exception e) {    
                }
                   
                if(amount != 0){
                    clan.changeCredits(amount);
                    LegendaryClans.coloredOutput(sender, "&2Clan credits have been changed by "+ amount);
                }
                else{
                    LegendaryClans.coloredOutput(sender, "&4Don´t try to put in some shit as amount!");
                }
            }else {
                LegendaryClans.coloredOutput(sender, "&4No clan with that name existing!");
            }
        }
    }
    
    //aclan promoteMember clan member
    public void promoteClanMemberManually(CommandSender sender, String[] args) {
        if (args.length == 3 && args[0].equalsIgnoreCase("promoteMember")) {
            Clan clan = plugin.getClanManager().getClanByName(args[1]);
            if (clan != null) {
                LegendaryPlayer player = plugin.getPlayerManager().searchPlayerByName(args[2]);
                if (player != null) {
                    Clan clanOfPlayer = plugin.getClanManager().getClanByPlayer(player);
                    if (clanOfPlayer != null) {
                        if (clanOfPlayer.equals(clan)) {
                            LegendaryPlayer submitter = plugin.getPlayerManager().searchPlayerByName(sender.getName());
                            clanOfPlayer.promotePlayerAdmin(player, submitter);
                            LegendaryClans.coloredOutput(sender, "&2Player successfully promoted!");
                        } else {
                            LegendaryClans.coloredOutput(sender, "&4The clan given and the player clan are not the same!");
                        }
                    } else {
                        LegendaryClans.coloredOutput(sender, "&4That player has no clan!");
                    }
                } else {
                    LegendaryClans.coloredOutput(sender, "&4No player with that name existing!");
                }
            } else {
                LegendaryClans.coloredOutput(sender, "&4No clan with that name existing!");
            }
        }
    }
    
    //aclan setFounder clan newFounder
    public void setClanFounderManually(CommandSender sender, String[] args) {
        if (args.length == 3 && args[0].equalsIgnoreCase("setFounder")) {
            Clan clan = plugin.getClanManager().getClanByName(args[1]);
            if (clan != null) {
                LegendaryPlayer player = plugin.getPlayerManager().searchPlayerByName(args[2]);
                if (player != null) {
                    Clan clanOfPlayer = plugin.getClanManager().getClanByPlayer(player);
                    if (clanOfPlayer != null) {
                        if (clanOfPlayer.equals(clan)) {
//                            LegendaryPlayer submitter = plugin.getPlayerManager().searchPlayerByName(sender.getName());
                            clanOfPlayer.setFounder(player);
                            LegendaryClans.coloredOutput(sender, "&2"+player.getName()+" is now new founder of that clan!");
                        } else {
                            LegendaryClans.coloredOutput(sender, "&4The clan given and the player clan are not the same!");
                        }
                    } else {
                        LegendaryClans.coloredOutput(sender, "&4That player has no clan!");
                    }
                } else {
                    LegendaryClans.coloredOutput(sender, "&4No player with that name existing!");
                }
            } else {
                LegendaryClans.coloredOutput(sender, "&4No clan with that name existing!");
            }
        }
    }
}
