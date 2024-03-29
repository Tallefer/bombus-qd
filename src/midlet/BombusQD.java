/*
 * BombusQD.java
 *
 * Created on 5.01.2005, 21:46
 *
 * Copyright (c) 2005-2008, Eugene Stahov (evgs), http://bombus-im.org
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

/**
 * 
 * @author Eugene Stahov
 */
package midlet;
//#ifdef AUTOTASK
//# import AutoTasks.AutoTask;
//#endif
import Account.Account;
import Account.AccountSelect;
import Colors.ColorTheme;
//#ifdef STATS
//# import Statistic.Stats;
//#endif
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import locale.*;
//import ui.*;
import Client.Config;
import Client.StaticData;
import Client.Roster;
//import Info.Version;
//import ui.GMenu;
import ui.SplashScreen;
//import java.util.Vector;
//import util.StringLoader;
import Fonts.*;
//import util.Strconv;
//import Account.YesNoAlert;
//#ifdef CONSOLE
//# import Console.DebugList;
//#endif
//#ifdef CLIPBOARD
//# import util.ClipBoard;
//#endif
import Client.Contact;
//import Client.ContactMessageList;
//#ifdef LIGHT_CONTROL
//# import LightControl.*;
//#endif
import History.*;

import ui.controls.AlertBox;
import io.NvStorage;
import java.io.DataInputStream;
import java.io.DataOutputStream;

/** Entry point class
 *
 * @author  Eugene Stahov
 * @version
 */
public class BombusQD extends MIDlet implements Runnable
{
    
    public Display display  = Display.getDisplay(this);
    private boolean isRunning;
    
    public final static StaticData sd = StaticData.getInstance();
    public final static Config cf = Config.getInstance();
    public final static ClipBoard clipboard=ClipBoard.getInstance();
    public final static Commands commands=Commands.get();
//#ifdef CONSOLE    
//#     public final static DebugList debug = DebugList.get();
//#endif    
    
    public int width = 0;
    public int height = 0;
    public int count = 0;    
    
    ColorTheme ct;
    
    public SplashScreen s;
    private static BombusQD instance;
    
//#ifdef LIGHT_CONTROL    
//#    LightConfig lcf;
//#endif    

    public BombusQD() {
        SR.changeLocale();
	instance=this; 
        ct=ColorTheme.getInstance();
        s=SplashScreen.getInstance(display);
        s.setProgress("Loading", 3);
    }
    
    /** Entry point  */
    public void startApp() {
        if (isRunning) {
	        hideApp(false,null);
            return;
        }
        isRunning=true;
        new Thread(this).start();
    }

    /**
     * Pause is a no-op since there are no background activities or
     * record stores that need to be closed.
     */
    public void pauseApp() { }

    
    public Image[] imageArr = null;
    public int wimg_menu;
    public int himg_menu;
    public int wimg_actions;
    public int himg_actions;    

    private void rmsVersion(boolean save, Displayable parentView) {
        String key = "key15628618";
        if(save == false) {
            try {
               DataInputStream is=NvStorage.ReadFileRecord("appver", 0);
               String ver = is.readUTF();
               if(ver.indexOf(key) == -1 ) {
                  //alerbox
                  AlertBox alert = new AlertBox( "WARNING", SR.get(SR.MS_WARNING_MESSAGE_INSTALL) , display, parentView, true) {
                      public void yes() { notifyDestroyed(); }
                      public void no() {}
                  };
               }
               is.close();
               is=null;
            } catch (Exception e) {
               /*
                  AlertBox alert = new AlertBox( "Info", "..." , display, parentView, true) {
                      public void yes() { }
                      public void no() { }
                   };
               */
               rmsVersion(true, null);
            }
            return;
        }
        DataOutputStream os=NvStorage.CreateDataOutputStream();
        try {
            os.writeUTF(key);
            os.close();
            os = null;
        } catch (Exception e) { }
        NvStorage.writeFileRecord(os, "appver", 0, true);
    }
    
    public void run(){ 

        //long s1 = System.currentTimeMillis();
        if(sd.roster==null) sd.roster=new Roster(display);
        //s.setProgress(18);
        
        s.getKeys();
        width=s.width;
        height=s.height;
        
        boolean selAccount=((cf.accountIndex<0));
          if (!selAccount && cf.autoLogin) {
            //sd.roster=new Roster(display);
            Account.loadAccount(cf.autoLogin, cf.accountIndex,-1);
          }
        
        display.setCurrent(sd.roster);
        rmsVersion(false, sd.roster);
        
        //long s2 = System.currentTimeMillis();
//#ifdef CONSOLE        
//#         if(cf.debug){      
//#             //debug.add("::start "+(s2-s1)+" msec",10);
//#             debug.add("::startmem free/total "+ 
//#                     Long.toString(Runtime.getRuntime().freeMemory()>>10) + "/" + 
//#                     Long.toString(Runtime.getRuntime().totalMemory()>>10), 10) ;
//#         }
//#endif        
        
        try {
           imageArr = new Image[2];
           imageArr[0] = Image.createImage("/images/menu.png");
           wimg_menu = imageArr[0].getWidth()/8;
           himg_menu = imageArr[0].getHeight()/10;
        } catch (Exception e) { }    
        
        //cf.path_skin="";
        HistoryConfig.getInstance().loadFromStorage();
        FontClass.getInstance().Init(cf.drwd_fontname);  
        
//#ifdef LIGHT_CONTROL
//#         lcf=LightConfig.getInstance();
//#         CustomLight.switchOn(lcf.light_control);
//#endif        
        
        //if(sd.roster==null) sd.roster=new Roster(display);
        
        HistoryStorage hs = new HistoryStorage(); 
//#ifdef PEP        
//#         Activity.loaded();
//#endif   
//#ifdef STATS
//#         Stats.getInstance().loadFromStorage();
//#         Stats.getInstance().updateRunValue();
//#endif
//#ifdef AUTOTASK
//#         sd.autoTask=new AutoTask(display);
//#endif        
    }

    public void destroyApp(boolean unconditional) { }

    public void hideApp(boolean hide,Contact c) {
	if (hide){
            cf.isMinimized=true;
            display.setCurrent(null);
        }
	else {
          cf.isMinimized=false;
          if(c!=null){
              display.setCurrent(c.getMessageList());
          }else{
              display.setCurrent(sd.roster);
          }
        }
    }
    
    public static BombusQD getInstance() {
        return instance;
    }
}
