/*
 * ConferenceForm.java
 *
 * Created on 24.07.2005, 18:32
 * Copyright (c) 2005-2008, Eugene Stahov (evgs), http://bombus-im.org
 * Copyright (c) 2009, Alexej Kotov (aqent), http://bombusmod-qd.wen.ru
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

package Conference;
import Client.*;
import com.alsutton.jabber.JabberDataBlock;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextField;
import locale.SR;
import ui.controls.form.CheckBox;
import ui.controls.form.DefForm;
import ui.controls.form.NumberInput;
import ui.controls.form.PasswordInput;
import ui.controls.form.TextInput;
//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.CommandListener;
//# import javax.microedition.lcdui.Command;
//#else
import Menu.MenuListener;
import Menu.Command;
import Menu.MyMenu;
//#endif
//#ifdef GRAPHICS_MENU        
//# import ui.GMenu;
//# import ui.GMenuConfig;
//#endif  
/**
 *
 * @author EvgS,aqent
 */
public final class ConferenceForm
    extends DefForm {
    
    private Display display;
    
//#ifndef MENU
    Command cmdJoin;
    Command cmdAdd;
    Command cmdEdit;
//#endif
    private TextInput roomField;
    private TextInput hostField;
    private TextInput nickField;
    private TextInput nameField;
    private PasswordInput passField;
    private NumberInput msgLimitField;
    private CheckBox autoJoin;
    
    BookmarkItem editConf;

    //private static boolean sndprs=false;

    private int cursor;
    
    private void initCommands(){
//#ifndef MENU
        cmdJoin=new Command(SR.get(SR.MS_JOIN), Command.SCREEN, 1);
        cmdAdd=new Command(SR.get(SR.MS_ADD_BOOKMARK), Command.SCREEN, 5);
        cmdEdit=new Command(SR.get(SR.MS_SAVE), Command.SCREEN, 6);
//#endif
    }
    
    /** Creates a new instance of GroupChatForm */
    public ConferenceForm(Display display, Displayable pView, String name, String confJid, String password, boolean autojoin) {
        super(display, pView, SR.get(SR.MS_JOIN_CONFERENCE));
        initCommands();
        int roomEnd=confJid.indexOf('@');
        String room="";
        if (roomEnd>0) room=confJid.substring(0, roomEnd);
        String server;
        String nick = null;
        int serverEnd=confJid.indexOf('/');
        if (serverEnd>0) {
            server=confJid.substring(roomEnd+1,serverEnd);
            nick=confJid.substring(serverEnd+1);
        } else {
            server=confJid.substring(roomEnd+1);
        }
        createForm(display, pView, name, room, server, nick, password, autojoin);
        room=null;
        server=null;
        nick=null;
        this.parentView=pView;
    }
    
    /** Creates a new instance of GroupChatForm */
    public ConferenceForm(Display display, Displayable pView, BookmarkItem join, int cursor) {
        super(display, pView, SR.get(SR.MS_JOIN_CONFERENCE));
        if (join==null) return;
        
        initCommands();
        if (join.isUrl) return;
        
        this.editConf=join;
        this.cursor=cursor;

        String confJid=join.getJidNick();
        int roomEnd=confJid.indexOf('@');
        String room="";
        if (roomEnd>0) room=confJid.substring(0, roomEnd);
        String server;
        String nick=null;
        int serverEnd=confJid.indexOf('/');
        if (serverEnd>0) {
            server=confJid.substring(roomEnd+1,serverEnd);
            nick=confJid.substring(serverEnd+1);
        } else {
            server=confJid.substring(roomEnd+1);
        }
        createForm(display, pView, join.desc, room, server, nick, join.password, join.autojoin);
        confJid=null;
        room=null;
        server=null;
        nick=null;
    }
    
    /** Creates a new instance of GroupChatForm */
    public ConferenceForm(Display display, Displayable pView) {
        super(display, pView, SR.get(SR.MS_JOIN_CONFERENCE));
        
        initCommands();
        String room=midlet.BombusQD.cf.defGcRoom;
        String server=null;
        // trying to split string like room@server
        int roomE=room.indexOf('@');
        if (roomE>0) {
            server=room.substring(roomE+1);
            room=room.substring(0, roomE);
        }
        // default server
        if (server==null) server="conference."+midlet.BombusQD.sd.account.getServer();
        createForm(display, pView, null, room, server, null, null, false); 
        room=null;
        server=null;
    }
	
    /** Creates a new instance of GroupChatForm */
    public ConferenceForm(Display display, Displayable pView, String name, String room, String server, String nick, String password, boolean autojoin) {
        super(display, pView, SR.get(SR.MS_JOIN_CONFERENCE));
        initCommands();
        createForm(display, pView, name, room, server, nick, password, autojoin);
    }
    
     private void createForm(final Display display, Displayable pView, String name, String room, String server, String nick, final String password, boolean autojoin) {
        this.display=display;

        roomField=new TextInput(display, SR.get(SR.MS_ROOM), room, null, TextField.ANY);//, 64, TextField.ANY);
        itemsList.addElement(roomField);

        hostField=new TextInput(display, SR.get(SR.MS_AT_HOST), server, "muc-host", TextField.ANY);//, 64, TextField.ANY, "muc-host", display);
        itemsList.addElement(hostField);
        
        if (nick==null) nick=midlet.BombusQD.sd.account.getNickName();
        nickField=new TextInput(display, SR.get(SR.MS_NICKNAME), nick, "roomnick", TextField.ANY);//, 32, TextField.ANY, "roomnick", display);
        itemsList.addElement(nickField);

        msgLimitField=new NumberInput(display, SR.get(SR.MS_MSG_LIMIT), Integer.toString(midlet.BombusQD.cf.confMessageCount), 0, 100);
        itemsList.addElement(msgLimitField);

        nameField=new TextInput(display, SR.get(SR.MS_DESCRIPTION), name, null, TextField.ANY);//, 128, TextField.ANY);
        itemsList.addElement(nameField);

        passField=new PasswordInput(display, SR.get(SR.MS_PASSWORD), password);//, 32, TextField.ANY | TextField.SENSITIVE );
        itemsList.addElement(passField);

        autoJoin=new CheckBox(SR.get(SR.MS_AUTOLOGIN), autojoin);
        itemsList.addElement(autoJoin);

        commandState();        
        
	setCommandListener(this);

        moveCursorTo(getNextSelectableRef(-1));
        attachDisplay(display);
        this.parentView=pView;
    }

    public void commandAction(Command c, Displayable d){
        super.commandAction(c, d);
        
        String nick=nickField.getValue();
        String name=nameField.getValue();
        String host=hostField.getValue();
        String room=roomField.getValue();
        String pass=passField.getValue();
        int msgLimit=Integer.parseInt(msgLimitField.getValue());

        boolean autojoin=autoJoin.getValue();
        
        if (nick.length()==0) return;
        if (room.length()==0) return;
        if (host.length()==0) return;
        
        StringBuffer gchat=new StringBuffer(room.trim()).append('@').append(host.trim());
        
        if (name.length()==0) name=gchat.toString();
        
        saveMsgCount(msgLimit);
            
        if (c==cmdEdit) {
            midlet.BombusQD.sd.roster.bookmarks.removeElement(editConf);
            midlet.BombusQD.sd.roster.bookmarks.insertElementAt(new BookmarkItem(name, gchat.toString(), nick, pass, autojoin), cursor);
            new BookmarkQuery(BookmarkQuery.SAVE);
            display.setCurrent(parentView);
        } else if (c==cmdAdd) {
            new Bookmarks(display, midlet.BombusQD.sd.roster, new BookmarkItem(name, gchat.toString(), nick, pass, autojoin));
        } else if (c==cmdJoin) {
            try {
                midlet.BombusQD.cf.defGcRoom=room+"@"+host;
                //cf.saveToStorage();//?
                gchat.append('/').append(nick);
                join(name, gchat.toString(),pass, msgLimit);
                midlet.BombusQD.sd.roster.showRoster();
            } catch (Exception e) { }
        }
        gchat=null;
        nick=null;
        name=null;
        host=null;
        room=null;
        pass=null;
    }
    

    public void commandState(){
//#ifdef MENU_LISTENER        
        menuCommands.removeAllElements();
//#endif        
        addCommand(cmdJoin); cmdJoin.setImg(0x43);
        addCommand(cmdAdd); cmdAdd.setImg(0x42);
        addCommand(cmdEdit); cmdEdit.setImg(0x40);
//#ifndef GRAPHICS_MENU        
     addCommand(cmdCancel);
//#endif     
    }
    
//#ifdef MENU_LISTENER
    public String touchLeftCommand(){ return SR.get(SR.MS_MENU); }
    

    
//#ifdef GRAPHICS_MENU    
//#     public void touchLeftPressed(){
//#         showGraphicsMenu();
//#     }      
//#     public int showGraphicsMenu() {
//#         commandState();
//#         menuItem = new GMenu(display, parentView, this, null, menuCommands);
//#         GMenuConfig.getInstance().itemGrMenu = GMenu.CONFERENCE_FORM;
//#         redraw();
//#         return GMenu.CONFERENCE_FORM;
//#     }
//#else
    public void touchLeftPressed(){
        showMenu();
    }    
    public void showMenu() {
        commandState();
        new MyMenu(display, parentView, this, SR.get(SR.MS_JOIN_CONFERENCE), null, menuCommands);
   }  
//#endif      
    

//#endif    
    


    private void saveMsgCount(int msgLimit) {
        if (midlet.BombusQD.cf.confMessageCount!=msgLimit) {
            midlet.BombusQD.cf.confMessageCount=msgLimit;
            midlet.BombusQD.cf.saveToStorage();
        }
    }

    public static void join(String name, String jid, String pass, int maxStanzas) {
        ConferenceGroup grp=midlet.BombusQD.sd.roster.initMuc(jid, pass);
        grp.desc=name;
        JabberDataBlock x=new JabberDataBlock("x", null, null);
        x.setNameSpace("http://jabber.org/protocol/muc");
        if (pass.length()!=0) {
            x.addChild("password", pass); // adding password to presence
        }
        
        JabberDataBlock history=x.addChild("history", null);
        history.setAttribute("maxstanzas", Integer.toString(maxStanzas));
        history.setAttribute("maxchars","32768");
        try {
            long last=grp.confContact.lastMessageTime;
            long delay= ( grp.conferenceJoinTime - last ) /1000 ;
            if (last!=0) 
                history.setAttribute("seconds",String.valueOf(delay)); // todo: change to since
        } catch (Exception e) {}
        
        int status=midlet.BombusQD.sd.roster.myStatus;
        if (status==Constants.PRESENCE_INVISIBLE) 
            status=Constants.PRESENCE_ONLINE;
        midlet.BombusQD.sd.roster.sendDirectPresence(status, jid, x);
        grp.inRoom=true;
        //midlet.BombusQD.sd.roster.reEnumRoster();
        x=null;
        grp=null;
        history=null;
    }
}
