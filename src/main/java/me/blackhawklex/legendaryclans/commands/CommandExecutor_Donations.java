/*
 * LegendaryClans - by LandofLegend Custom Development Team
 * http://www.landoflegend.net
 */
package me.blackhawklex.legendaryclans.commands;

import java.util.Calendar;
import me.blackhawklex.donations.DonationManager;
import me.blackhawklex.legendaryclans.LegendaryClans;
import me.blackhawklex.legendaryclans.LegendaryPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandExecutor_Donations implements CommandExecutor {

    private LegendaryClans plugin;
    private static final int dayMillis = 86400000;

    public CommandExecutor_Donations(LegendaryClans plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command,String label, String[] args) {
        if (command.getName().equalsIgnoreCase("don")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Y U NO PLAYER??!111");
                return true;
            }
            if (LegendaryClans.getPermission().has(sender, "LegendaryClans.Donations")) {
                getPlayerDonRank(sender,args);
                addPlayerToDons(sender,args);
                changePlayerDonRank(sender,args);
                removePlayerFromDons(sender,args);   
            }
            else{
                LegendaryClans.coloredOutput(sender, "&4No permission, bro!");
            }

            return true;
        }
        return false;
    }
    
    //don checkPlayer player
    public void getPlayerDonRank(CommandSender sender, String[] args){
        if (args.length == 2 && args[0].equalsIgnoreCase("checkPlayer")) {
            LegendaryPlayer player = plugin.getPlayerManager().searchPlayerByName(args[1]);
            if (player != null) {
                int donRank = player.getDonationType();
                if(donRank >0){
                    long timeBetween = Calendar.getInstance().getTime().getTime()-player.getDonationTime();
                    long extraTime = player.getDonationExtraTime(); 
                    long timeUsed = (timeBetween - extraTime) / dayMillis;

                    LegendaryClans.coloredOutput(sender, player.getName() +" &ehas donator rank " +donRank + " and used "+timeUsed + " days of his total amount.");
                }else{
                    LegendaryClans.coloredOutput(sender, player.getName() + " &ehas donator rank " + donRank + ". He is not a donator.");
                }
            } else {
                LegendaryClans.coloredOutput(sender, "&4That player is not listed!");
            }
        } 
    }
    
    //don addPlayer player donRank
    public void addPlayerToDons(CommandSender sender, String[] args){
        if(args.length == 3 && args[0].equalsIgnoreCase("addPlayer")){
            LegendaryPlayer player = plugin.getPlayerManager().searchPlayerByName(args[1]);
            if(player != null){
                try {
                    int donRank = Integer.parseInt(args[2]);
                    if (donRank>0 && donRank<6) {
                        plugin.getDonationManager().addPlayerToDonationGroup(player, donRank);
                        LegendaryClans.coloredOutput(sender, "&2Player "+ player.getName()+" added to DT"+donRank);
                    }
                    else{
                        LegendaryClans.coloredOutput(sender, "&4Donation rank has to be between 1-5!");
                    }
                } catch (Exception e) {
                    LegendaryClans.coloredOutput(sender, "&4Rank not a number bro!");
                }

            }
            else{
                LegendaryClans.coloredOutput(sender, "&4That player is not listed!");
            }
        }
    }
    
    //don changePlayer player oldRank newRank
    public void changePlayerDonRank(CommandSender sender, String[] args){
        if (args.length == 4 && args[0].equalsIgnoreCase("changePlayer")) {
            LegendaryPlayer player = plugin.getPlayerManager().searchPlayerByName(args[1]);
            if (player != null) {
                try {
                    int donRank = Integer.parseInt(args[3]);
                    int oldRank = Integer.parseInt(args[2]);
                    if (donRank > 0 && donRank < 6 && oldRank > 0 && oldRank <6) {
                        plugin.getDonationManager().changePlayerDonationGroup(player, donRank,oldRank);
                        LegendaryClans.coloredOutput(sender, "&2Changeg donation rank successfully and added sufficient extra time.");
                    } else {
                        LegendaryClans.coloredOutput(sender, "&4Donation rank has to be between 1-5! Same for oldRank");
                    }
                } catch (Exception e) {
                    LegendaryClans.coloredOutput(sender, "&4Not a number bro!");
                }

            } else {
                LegendaryClans.coloredOutput(sender, "&4That player is not listed!");
            }
        } 
    }
     //don removePlayer player donRank
    public void removePlayerFromDons(CommandSender sender, String[] args) {
        if (args.length == 3 && args[0].equalsIgnoreCase("removePlayer")) {
            LegendaryPlayer player = plugin.getPlayerManager().searchPlayerByName(args[1]);
            if (player != null) {
                try {
                    int donRank = Integer.parseInt(args[2]);
                    if (donRank > 0 && donRank < 6) {
                        plugin.getDonationManager().removePlayerFromDonationGroup(player, donRank);
                        LegendaryClans.coloredOutput(sender, "&2Player " + player.getName() + " removed drom DT" + donRank);
                    } else {
                        LegendaryClans.coloredOutput(sender, "&4Donation rank has to be between 1-5!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LegendaryClans.coloredOutput(sender, "&4Not a number bro!");
                }

            } else {
                LegendaryClans.coloredOutput(sender, "&4That player is not listed!");
            }
        }
    }
    
    
}
