/*
 * LegendaryClans - by LandofLegend Custom Development Team
 * http://www.landoflegend.net
 */
package net.landoflegend.gui;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import net.landoflegend.legendaryclans.LegendaryClans;
import net.landoflegend.legendaryclans.LegendaryPlayer;
import org.getspout.spoutapi.gui.GenericTexture;
import org.getspout.spoutapi.gui.RenderPriority;
import org.getspout.spoutapi.player.SpoutPlayer;

public class TeleportationBar {
    private LegendaryClans plugin;
    private SpoutPlayer player;
    private LegendaryPlayer legPlayer;
    private long startingTime;
    private Timer timer;
    private GenericTexture background;
    private GenericTexture animation;
    private int maxWidth = 549;
    private static final int aniHeight = 44;
    private static final GenericTexture[] aniTexts = new GenericTexture[20];
    
    public TeleportationBar (LegendaryClans plugin, SpoutPlayer player, LegendaryPlayer legPlayer){
        this.plugin = plugin;
        this.player = player;
        this.legPlayer = legPlayer;
        setupBackground();
        initializeTextures();
        animate();        
    }
    
    public void animate(){
        TimerTask animateTask = new TimerTask() {   
            int counter;
            boolean attached;
            @Override
            public boolean cancel() {
                return super.cancel();
            }

            @Override
            public void run() {
                if(Calendar.getInstance().getTime().getTime()-startingTime  >= 10000){
                    legPlayer.teleportToDestination();
                    legPlayer.setDestination(0);
                    deleteAll();
                    timer.cancel();
                }
                else{
                    if(!attached){
                        animation = aniTexts[counter]; //(int)(Calendar.getInstance().getTime().getTime()-startingTime) % 500
                        player.getMainScreen().attachWidget(plugin, animation); 
                        attached = true;
                    }
//                    player.getMainScreen().updateWidget(background);
                    animation.setWidth(aniTexts[counter].getWidth()).setHeight(aniTexts[counter].getHeight());
                    animation.setX(aniTexts[counter].getX()).setY(aniTexts[counter].getY());
                    player.getMainScreen().updateWidget(animation);
                    counter++;
                }
            }
        };
        
        timer = new Timer();
        startingTime = Calendar.getInstance().getTime().getTime();
        timer.schedule(animateTask, 0, 500);
    }
    
    public void setupBackground(){
        int width = player.getMainScreen().getMaxWidth();
        int height = player.getMainScreen().getMaxHeight();
        
        background = new GenericTexture("http://www7.pic-upload.de/01.01.12/agj6vg3k77e9.png");
        background.setWidth(width/2).setHeight(16);
        maxWidth = width/2 - 5;
        background.setX(width/2 - background.getWidth()/2).setY(height/2 - background.getHeight()/2);
//        background.setPriority(RenderPriority.Normal);
        background.setPriority(RenderPriority.Highest);
        player.getMainScreen().attachWidget(plugin, background);
    }
    
    
//    public void setupAnimation(){
//        int width = player.getMainScreen().getMaxWidth();
//        int height = player.getMainScreen().getMaxHeight();
//
//        animation = new GenericTexture("http://www7.pic-upload.de/01.01.12/wrfgfmjallwy.jpg");
//        animation.setWidth(maxWidth/20).setHeight(44);
//        animation.setX(width / 2 - animation.getWidth() / 2).setY(height - 15 - animation.getHeight() / 2);
//        player.getMainScreen().attachWidget(plugin, animation); 
//    }
    public void deleteAll(){
       timer.cancel();
       player.getMainScreen().removeWidget(background);
       player.getMainScreen().removeWidget(animation);
       animation = null;
       background = null;
    }
    
    public void initializeTextures(){
        for(int i = 20; i>0;i--){
            int width = player.getMainScreen().getMaxWidth();
            int height = player.getMainScreen().getMaxHeight();
            GenericTexture ani = new GenericTexture("http://www7.pic-upload.de/01.01.12/wrfgfmjallwy.jpg");
            ani.setWidth((maxWidth/20) *i).setHeight(12);
            ani.setX(background.getX()+5).setY(background.getY()+2);
//            ani.setPriority(RenderPriority.Highest);
            aniTexts[20-i] = ani;  
        }
    }
    
}
