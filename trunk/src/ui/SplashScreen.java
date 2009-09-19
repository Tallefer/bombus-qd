/*
 * SplashScreen.java
 *
 * Created on 16.02.2007, 14:23
 *
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * You can also redistribute and/or modify this program under the
 * terms of the Psi License, specified in the accompanied COPYING
 * file, as published by the Psi Project; either dated January 1st,
 * 2005, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package ui; 

import Client.Config;
import Client.StaticData;
import Fonts.FontCache;
//#ifdef AUTOSTATUS
//# import Client.ExtendedStatus;
//# import Client.Roster;
//# import Client.StaticData;
//# import Client.StatusList;
//#endif
import images.RosterIcons;
import java.util.Timer;
import java.util.TimerTask;
import javax.microedition.lcdui.*;
import midlet.BombusQD;
import Colors.ColorTheme;
import ui.controls.Progress;

/**
 *
 * @author Eugene Stahov
 */
public class SplashScreen extends Canvas implements Runnable, CommandListener {
    
    private Display display;
    private Displayable parentView;
    
    private String capt;
    private int pos=-1;
    
    public int width;
    public int height;
    
    private static Image img;
    
    private ComplexString status;
    
    private char exitKey;
    private int kHold;
    
    private TimerTaskClock tc;
    
    private static SplashScreen instance;

    public int keypressed=0;

    private Font clockFont=FontCache.getFont(true,FontCache.bigSize);
    
    private Progress pb;
    
    public static SplashScreen getInstance(Display display){
        if (instance==null) 
            instance=new SplashScreen(display);
        return instance;
    }
    
    /** Creates a new instance of SplashScreen */
    public SplashScreen(Display display) {
        setFullScreenMode(midlet.BombusQD.cf.fullscreen);
        try {
            if (img==null) {
                img=Image.createImage("/images/splash.png");
                this.img=img;
            }
        } catch (Exception e) {
           System.out.println("splash NOT created ");
        }        
        display.setCurrent(this);
    }

  
   public SplashScreen(Display display, ComplexString status, char exitKey) {
        this.status=status;
        this.display=display;
        this.exitKey=exitKey;
        kHold=exitKey;
        
        parentView=display.getCurrent();
        
        
        status.setElementAt(new Integer(RosterIcons.ICON_KEYBLOCK_INDEX),6);
        repaint();
        //serviceRepaints();
        new Thread(this).start();
        
        tc=new TimerTaskClock();
        setFullScreenMode(midlet.BombusQD.cf.fullscreen);

        System.gc();
        try { Thread.sleep(50); } catch (InterruptedException ex) { }
    }

    public void paint(Graphics g){
        width=g.getClipWidth();
        height=g.getClipHeight();
        
        g.setColor(ColorTheme.getColor(ColorTheme.BLK_BGND));
        g.fillRect(0,0, width, height);
        
        if(img!=null){
           g.drawImage(img, width/2, height/2, Graphics.VCENTER|Graphics.HCENTER);  
        }

        if (pos==-1) {
            g.setColor(ColorTheme.getColor(ColorTheme.BLK_INK));

            status.drawItem(g, 0, false);

            g.setFont(clockFont);
            int h=clockFont.getHeight()+1;
            
            String time=Time.localTime();
            int tw=clockFont.stringWidth(time);

            g.drawString(time, width/2, height, Graphics.BOTTOM | Graphics.HCENTER);
        } else {
            int filled=pos*width/100;
            if (pb==null) pb=new Progress(0, height, width);
            pb.draw(g, filled, capt);
        }
    }
    
    public void setProgress(int progress) {
        pos=progress;
        repaint();
    }

    public void setFailed(){
        setProgress("Failed", 100);
    }
    
    public void setProgress(String caption, int progress){
        capt=caption;
////#if DEBUG
        System.out.println(capt);
////#endif
	setProgress(progress);
    }
    
    public int getProgress(){
        return pos;
    }
    
    // close splash
    private Command cmdExit=new Command("Hide Splash", Command.BACK, 99);
    
    public void setExit(Display display, Displayable nextDisplayable){
        this.display=display;
        parentView=nextDisplayable;
        setCommandListener(this);
        addCommand(cmdExit);
    }
    
    public void commandAction(Command c, Displayable d) {
        if (c==cmdExit) 
            close();
    }
    
    public void close(){
        display.setCurrent(StaticData.getInstance().roster);
        instance=null;
        System.gc();
    }

    public void run() {
        display.setCurrent(this);
    }

    private class TimerTaskClock extends TimerTask {
        private Timer t;
        public TimerTaskClock(){
            t=new Timer();
            t.schedule(this, 10, 20000);
        }
        public void run() {
            repaint();
            //serviceRepaints();
        }
        public void stop(){
            cancel();
            t.cancel();
        }
    }

    public void keyPressed(int keyCode) {
        keypressed=keyCode;
        if (pos>=20)
            close();
        kHold=0;
    }

    protected void keyRepeated(int keyCode) { 
        if (kHold==0)
            if (keyCode==exitKey) 
                destroyView(); 
    }

    private void destroyView(){
        status.setElementAt(null,6);
        if (display!=null) 
            display.setCurrent(parentView);
        tc.stop();
//#ifdef AUTOSTATUS
//#         if (midlet.BombusQD.sd.roster.autoAway && midlet.BombusQD.cf.autoAwayType==Config.AWAY_LOCK) {
//#             int newStatus=midlet.BombusQD.sd.roster.oldStatus;
//#             ExtendedStatus es=StatusList.getInstance().getStatus(newStatus);
//#             String ms=es.getMessage();
//#             midlet.BombusQD.sd.roster.autoAway=false;
//#             midlet.BombusQD.sd.roster.autoXa=false;
//#             midlet.BombusQD.sd.roster.sendPresence(newStatus, ms);
//#         }
//#endif
        System.gc();
    }

    public void getKeys() {
        int pm=midlet.BombusQD.cf.phoneManufacturer;
        if (pm==Config.SIEMENS || pm==Config.SIEMENS2) {
             Config.SOFT_LEFT=-1;
             Config.SOFT_RIGHT=-4;
             Config.KEY_BACK=-12;
             return;
        }
        if (pm==Config.WINDOWS) {
             Config.SOFT_LEFT=40;
             Config.SOFT_RIGHT=41;
             return;     
        }
        if (pm==Config.NOKIA || pm==Config.SONYE || pm==Config.SAMSUNG) {
            Config.SOFT_LEFT=-6;
            Config.SOFT_RIGHT=-7;
            return;
        } 
        if (pm==Config.MOTOEZX) {
            Config.SOFT_LEFT=-21;
            Config.SOFT_RIGHT=-22;
            return;
        } 
        try {
            //Set Motorola specific keycodes
            Class.forName("com.motorola.phonebook.PhoneBookRecord");
            if (getKeyName(-21).toUpperCase().indexOf("SOFT")>=0) {
                Config.SOFT_LEFT=-21;
                Config.SOFT_RIGHT=-22;
            } else {
                Config.SOFT_LEFT=21;
                Config.SOFT_RIGHT=22;
            }
        } catch (ClassNotFoundException ignore2) {
            try {   
                if (getKeyName(21).toUpperCase().indexOf("SOFT")>=0) {
                    Config.SOFT_LEFT=21;
                    Config.SOFT_RIGHT=22;
                }
                if (getKeyName(-6).toUpperCase().indexOf("SOFT")>=0) {
                    Config.SOFT_LEFT=-6;
                    Config.SOFT_RIGHT=-7;
                }
            } catch(Exception e) {}

            for (int i=-210;i<127;i++) {//LG-?
            //run thru all the keys
                try {
                   if (getKeyName(i).toUpperCase().indexOf("SOFT")>=0) {// Check for "SOFT" in name description
                      if (getKeyName(i).indexOf("1")>=0) Config.SOFT_LEFT=i;// check for the 1st softkey
                      if (getKeyName(i).indexOf("2")>=0) Config.SOFT_RIGHT=i;// check for 2nd softkey
                   }
                } catch(Exception e){ }
            }
        }
    }
}