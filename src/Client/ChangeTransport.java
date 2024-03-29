/*
 * ChangeTransport.java
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
 *
 */
//#if CHANGE_TRANSPORT
//# package Client;
//# 
//# import com.alsutton.jabber.datablocks.Presence;
//# import java.util.Enumeration;
//# import javax.microedition.lcdui.ChoiceGroup;
//# import javax.microedition.lcdui.Command;
//# import javax.microedition.lcdui.CommandListener;
//# import javax.microedition.lcdui.Display;
//# import javax.microedition.lcdui.Displayable;
//# import javax.microedition.lcdui.Form;
//# import locale.SR;
//# 
//# public class ChangeTransport implements CommandListener {
//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_CHANGE_TRANSPORT");
//#endif
//# 
//#     private Display display;
//#     private Form f;
//#     private ChoiceGroup tTranspList;
//#     private String srcTransport;
//#     
//#     private Command cmdOk;
//#     private Command cmdCancel;
//#     
//#     StaticData sd=StaticData.getInstance();
//#     
//#     public ChangeTransport(Display display, String srcTransport) {
//#         this.srcTransport=srcTransport;
//#         this.display=display;
//#         
//#         cmdOk=new Command(SR.get(SR.MS_OK), Command.SCREEN, 1);
//#         cmdCancel=new Command(SR.get(SR.MS_CANCEL), Command.BACK, 99);
//#         
//#         f=new Form(SR.get(SR.MS_TRANSPORT));
//#         
//#         f.append("Warning!\nGateway "+srcTransport+" will be replaced by one from the list of online gateways for all JIDs in your roster (please logoff old gateway to avoid damaging contact list of your guest IM account)");
//#         
//#         tTranspList=new ChoiceGroup(SR.get(SR.MS_TRANSPORT), ChoiceGroup.POPUP);
//#         for (Enumeration e=sd.roster.getHContacts().elements(); e.hasMoreElements(); ){
//#             Contact ct=(Contact)e.nextElement();
//#             if (ct.jid.isTransport() && ct.status<Constants.PRESENCE_OFFLINE) //New transport must be online! If old transport is online and new transport is offline, contact list of guest IM account may be damaged
//#                 tTranspList.append(ct.bareJid, null);
//#         }
//#         if (tTranspList.size()==0) {
//#             tTranspList.append(srcTransport, null); //for avoiding exceptions and for resubscribing to all users of the transport ;)
//#         }
//#         f.append(tTranspList); 
//#         
//#         f.addCommand(cmdOk);
//#         f.addCommand(cmdCancel);
//#         
//#         f.setCommandListener(this);
//#         
//#         display.setCurrent(f);
//#     }
//#     
//#     public void commandAction(Command command, Displayable displayable) {
//#         if (command==cmdOk) {
//#              sd.roster.contactChangeTransport(srcTransport, tTranspList.getString(tTranspList.getSelectedIndex()));
//#              //System.out.println(srcTransport+"->"+tTranspList.getString(tTranspList.getSelectedIndex()));
//#         }
//#         display.setCurrent(sd.roster);
//#     }
//# }
//#endif