/*
 * LegendaryClans - by LandofLegend Custom Development Team
 * http://www.landoflegend.net
 */
package me.blackhawklex.donations;

import java.util.Calendar;
import me.blackhawklex.legendaryclans.LegendaryClans;
import me.blackhawklex.legendaryclans.LegendaryPlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

public class DonationManager {
    private  LegendaryClans plugin;
    private static final long dayMillis = 86400000;
    
    public DonationManager(LegendaryClans plugin){
        this.plugin= plugin;     
    }
    
    public void addPlayerToDonationGroup(LegendaryPlayer player, int group){
        for (World w : plugin.getServer().getWorlds()) {
            LegendaryClans.getPermission().playerAddGroup(w.getName(), player.getName(), "DT" + group);
        }
        player.setDonationTime(Calendar.getInstance().getTime().getTime());
        player.setDonationType(group);
        plugin.updateLoginQueueFile();
    }
    
    public void changePlayerDonationGroup(LegendaryPlayer player, int newGroup, int oldGroup) {
        long currentTime = Calendar.getInstance().getTime().getTime();
        long donationTime = player.getDonationTime();
        int baseTimeDays = 0;
        if(oldGroup ==1){
            baseTimeDays = 30;
        }
        if (oldGroup == 2) {
            baseTimeDays = 75;
        }
        if (oldGroup == 3) {
            baseTimeDays = 165;
        }
        if (oldGroup == 4) {
            baseTimeDays = 255;
        }
        if (oldGroup == 5) {
            baseTimeDays = 720;
        }
        long timeBetween = currentTime - donationTime;
        
        //remove him from the old
        for (World w : plugin.getServer().getWorlds()) {
            LegendaryClans.getPermission().playerRemoveGroup(w.getName(), player.getName(), "DT" + oldGroup);
        }
        
        //set his extra donation time resulting from the time that he has still left from first donation.
        player.addDonationExtraTime(baseTimeDays*dayMillis - timeBetween);
        
        //add him to the new group
        for (World w : plugin.getServer().getWorlds()) {
            LegendaryClans.getPermission().playerAddGroup(w.getName(), player.getName(), "DT" + newGroup);
        }
        player.setDonationTime(Calendar.getInstance().getTime().getTime());
        player.setDonationType(newGroup);
        plugin.updateLoginQueueFile();
    }
    
    public void removePlayerFromDonationGroup(LegendaryPlayer player, int group) {
        for(World w: plugin.getServer().getWorlds()){
            LegendaryClans.getPermission().playerRemoveGroup(w.getName(),player.getName(), "DT" + group);
        }
        player.setDonationTime(0);
        player.setDonationType(0);
        player.setDonationExtraTime(0);
        plugin.updateLoginQueueFile();
    }
    
    public void checkDonations(LegendaryPlayer player){  
        int donationType = player.getDonationType();
        if(donationType >0){
           long currentTime = Calendar.getInstance().getTime().getTime();
           long donationTime = player.getDonationTime();
           LegendaryClans.coloredOutput((CommandSender)player.getPlayer(), "&aWelcome back, "+ player.getName() + "! Thanks for your donation!");
           long timeBetween = currentTime - donationTime;
           long extraTime = player.getDonationExtraTime();
           if(donationType == 1){
               if((currentTime - donationTime  - extraTime) >= (30 * dayMillis)){  
//                   System.out.println(currentTime + "   "+donationTime +"   "+extraTime + (currentTime-donationTime) + ">"+ (30*dayMillis));
//                   System.out.println(30*dayMillis);
                   LegendaryClans.getPermission().playerRemoveGroup(player.getPlayer(), "DT1");
                   player.setDonationTime(0);
                   player.setDonationType(0);
                   LegendaryClans.coloredOutput((CommandSender)player.getPlayer(), "&4Your donation has expired, redonate if you want it to be reactiviated!");            
               }
               else{
                   LegendaryClans.coloredOutput((CommandSender)player.getPlayer(), "&2You have "+(30 - ((timeBetween-extraTime)/dayMillis))+ " days left until your donation expires. Have fun!");
               }
           }
           
           else if(donationType == 2){
               if(currentTime - donationTime  - extraTime >= (75 * dayMillis)){
                   LegendaryClans.getPermission().playerRemoveGroup(player.getPlayer(), "DT2");
                   LegendaryClans.coloredOutput((CommandSender)player.getPlayer(), "&4Your donation has expired, redonate if you want it to be reactiviated!");
                   player.setDonationTime(0);
                   player.setDonationType(0);
               }
               else{
                   LegendaryClans.coloredOutput((CommandSender)player.getPlayer(), "&2You have "+(75 - ((timeBetween-extraTime)/dayMillis))+ " days left until your donation expires. Have fun!");
               }
           }
           
           else if(donationType == 3){
               if (currentTime - donationTime - extraTime >= 165 * dayMillis) {
                   LegendaryClans.getPermission().playerRemoveGroup(player.getPlayer(), "DT3");
                   LegendaryClans.coloredOutput((CommandSender)player.getPlayer(), "&4Your donation has expired, redonate if you want it to be reactiviated!");
                   player.setDonationTime(0);
                   player.setDonationType(0);
               }
               else{
                   LegendaryClans.coloredOutput((CommandSender)player.getPlayer(), "&2You have "+(165 - ((timeBetween-extraTime)/dayMillis))+ " days left until your donation expires. Have fun!");
               }
           }
           
           else if(donationType == 4){
               if (currentTime - donationTime - extraTime >= 255 * dayMillis) {
                   LegendaryClans.getPermission().playerRemoveGroup(player.getPlayer(), "DT4");
                   LegendaryClans.coloredOutput((CommandSender)player.getPlayer(), "&4Your donation has expired, redonate if you want it to be reactiviated!");
                   player.setDonationTime(0);
                   player.setDonationType(0);
               }else{
                   LegendaryClans.coloredOutput((CommandSender)player.getPlayer(), "&2You have "+(255 - ((timeBetween-extraTime)/dayMillis))+ " days left until your donation expires. Have fun!");
               }
           }
           
           else if(donationType == 5){
               if (currentTime - donationTime - extraTime >= 720 * dayMillis) {
                   LegendaryClans.getPermission().playerRemoveGroup(player.getPlayer(), "DT5");
                   LegendaryClans.coloredOutput((CommandSender)player.getPlayer(), "&4Your donation has expired, redonate if you want it to be reactiviated!");
                   player.setDonationTime(0);
                   player.setDonationType(0);
               }else{
                   LegendaryClans.coloredOutput((CommandSender)player.getPlayer(), "&2You have "+(720 - ((timeBetween-extraTime)/dayMillis))+ " days left until your donation expires. Have fun!");
               }
           }
           plugin.updateLoginQueueFile();
        }
    }
    
    
}
