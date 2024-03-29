/*
 * userKeyExecute.java 
 *
 * Created on 14.09.2007, 13:38
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
//#ifdef USER_KEYS
//# package ui.keys;
//# 
//# import Client.Config;
//# import Client.StaticData;
//#ifdef STATS
//# import Statistic.Stats;
//#endif
//# import Colors.ColorTheme;
//#ifdef CONSOLE
//# import Console.XMLList;
//#endif
//#ifdef PRIVACY
//# import PrivacyLists.PrivacySelect;
//#endif
//#ifdef SERVICE_DISCOVERY
//# import ServiceDiscovery.ServiceDiscovery;
//#ifdef STATS
//# import Statistic.StatsWindow;
//#endif
//#endif
//# import java.util.Enumeration;
//# import java.util.Vector;
//# import javax.microedition.lcdui.Display;
//# import locale.SR;
//# import midlet.BombusQD;
//# import ui.VirtualList;
//# 
//# public class userKeyExec {
//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_USER_KEYS");
//#endif
//# 
//#     private static Config cf;
//#     StaticData sd=StaticData.getInstance();
//#     
//#     private static userKeyExec instance;
//#     public static userKeyExec getInstance(){
//# 	if (instance==null) {
//# 	    instance=new userKeyExec();
//#             cf=Config.getInstance();
//#             instance.initCommands();
//# 	}
//# 	return instance;
//#     }
//# 
//#     private Display display;
//# 
//#     public Vector commandsList;
//#     
//#     private void initCommands() {
//#         commandsList=null;
//#         commandsList=new Vector(0);
//#         
//#         userKey u = null;
//# 
//#         int index=0;
//#         do {
//#             u=userKey.createFromStorage(index);
//#             if (u!=null) {
//#                 commandsList.addElement(u);
//#                 index++;
//#              }
//#         } while (u!=null);
//#     }
//# 
//#     private int getCommandByKey(int key) {
//#         int commandNum = -1;
//#          for (Enumeration commands=commandsList.elements(); commands.hasMoreElements(); ) {
//#             userKey userKeyItem=(userKey) commands.nextElement();
//#             if (userKeyItem.key==key && userKeyItem.active) {
//#                 commandNum=userKeyItem.commandId;
//#                 break;
//#             }
//#          }
//#         return commandNum;
//#     }
//#     
//#     public void commandExecute(Display display, int command) { //return false if key not executed
//#         this.display=display;
//# 
//#         int commandId=getCommandByKey(command);
//#         
//#         if (commandId<1) return;
//#             
//#         boolean connected= ( sd.roster.isLoggedIn() );
//# 
//#         switch (commandId) {
//#             case 1: 
//#                 //new ConfigForm(display, sd.roster);//?
//#                 break;
//#             case 2: 
//#                 sd.roster.cmdCleanAllMessages();
//#                 break;
//#             case 3: 
//#                 sd.roster.connectionTerminated(new Exception(SR.get(SR.MS_SIMULATED_BREAK)));
//#                 break;
//#ifdef POPUPS
//#ifdef STATS
//#             case 4:
//#ifdef PLUGINS
//#                 if (sd.Stats)
//#endif
//#                     new StatsWindow(display);
//#                 break;
//#endif
//#endif
//#             case 5:
//#                 sd.roster.cmdStatus();
//#                 break;
//#             case 6: 
//#if FILE_TRANSFER
//#                 new io.file.transfer.TransferManager(display);
//#endif
//#                 break;
//#             case 7: 
//#ifdef ARCHIVE
//#ifdef PLUGINS
//#                 if (sd.Archive)
//#endif
//#                     sd.roster.cmdArchive();
//#endif
//#                 break;
//#             case 8: 
//#ifdef SERVICE_DISCOVERY
//#                 if (connected) new ServiceDiscovery(display, null, null, false);
//#endif
//#                 break;
//#             case 9: 
//#ifdef PRIVACY
//#                 if (connected) new PrivacySelect(display, sd.roster);
//#endif
//#                 break;
//#             case 10: //key pound
//#                 new userKeysList(display);
//#                 break;
//#             case 11:
//#ifdef POPUPS
//#                 sd.roster.cmdClearPopups();
//#endif
//#                 break;
//#                 /*
//#             case 12:
//#                 cf.lightState=!cf.lightState;
//#                 sd.roster.setLight(cf.lightState);
//#                 cf.saveToStorage();
//#                 break;
//#                  
//#             case 13:
//#                 sd.roster.cmdInfo();
//#                 break;
//#                  */
//#             case 14:
//#                 if (cf.allowMinimize)
//#                     BombusQD.getInstance().hideApp(true, null);
//#                 break;
//#             case 15:
//#                 ColorTheme.invertSkin();
//#                 break;
//#                 /*
//#             case 16:
//#ifdef CONSOLE
//#ifdef PLUGINS
//#                 try {
//#                     Class.forName("Console.XMLList");
//#endif
//#                     new XMLList(display, display.getCurrent());
//#ifdef PLUGINS
//#                 } catch (ClassNotFoundException ignore3) { }
//#endif
//#endif
//#                 break;
//#                  */
//#             case 17:
//#                 cf.fullscreen=!cf.fullscreen;
//#                 cf.saveToStorage();
//#                 VirtualList.fullscreen=cf.fullscreen;
//#                 sd.roster.setFullScreenMode(cf.fullscreen);
//#                 break;
//#         }
//#     }
//# }
//#endif
