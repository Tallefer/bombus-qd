/*
 * ArchiveList.java
 *
 * Created on 11.12.2005, 5:24
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

package Archive;

import Client.Msg;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import ui.MainBar;
import Messages.MessageList;
import java.util.Vector;
//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.Command;
//#else
import Menu.Command;
//#endif
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
import Client.Contact;
import Client.MessageEdit;
import ui.controls.AlertBox;

/**
 *
 * @author EvgS,aqent
 */
public class ArchiveList 
    extends MessageList {
//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_ARCHIVE");
//#endif
    
    Command cmdPaste=new Command(SR.MS_PASTE_BODY, Command.SCREEN, 1);
    Command cmdJid=new Command(SR.MS_PASTE_JID /*"Paste Jid"*/, Command.SCREEN, 2);
    Command cmdSubj=new Command(SR.MS_PASTE_SUBJECT, Command.SCREEN, 3);
    Command cmdEdit=new Command(SR.MS_EDIT, Command.SCREEN, 4);
    Command cmdNew=new Command(SR.MS_NEW, Command.SCREEN, 5);
    Command cmdDelete=new Command(SR.MS_DELETE, Command.SCREEN, 9);
    Command cmdDeleteAll=new Command(SR.MS_DELETE_ALL, Command.SCREEN, 10);

    MessageArchive archive;
    
    private int caretPos;
    
    private int where=1;

    private TextField tf;
    private TextBox tb;
    
    Contact cc;
    /** Creates a new instance of ArchiveList */
    public ArchiveList(Display display, int caretPos, int where, TextField tf, TextBox tb, Contact cc) {
 	super ();
        this.where=where;
        this.caretPos=caretPos;
        if(midlet.BombusQD.cf.msgEditType>0){
           this.tf=tf;
        }else{
           this.tb=tb;
        };
        this.cc=cc;
        archive=new MessageArchive(where);
	MainBar mainbar=new MainBar(
                (where==1)?
                SR.MS_ARCHIVE
                :SR.MS_TEMPLATE
                );
	mainbar.addElement(null);
	mainbar.addRAlign();
	mainbar.addElement(null);
	mainbar.addElement(SR.MS_FREE /*"free "*/);
        setMainBarItem(mainbar);
        
        commandState();
        addCommands();
        setCommandListener(this);
        
        if(midlet.BombusQD.cf.animatedSmiles) images.SmilesIcons.startTimer();
        
        attachDisplay(display);
    }

    public void commandState() {
//#ifdef MENU_LISTENER
        menuCommands.removeAllElements();
//#endif

        if (getItemCount()>0) {
            if(midlet.BombusQD.cf.msgEditType>0){
             if (tf!=null) {
                addCommand(cmdPaste); cmdPaste.setImg(0x13);
                addCommand(cmdJid); cmdJid.setImg(0x60);
                addCommand(cmdSubj); cmdSubj.setImg(0x81);
              }
            }else{
              if (tb!=null) {
                addCommand(cmdPaste); cmdPaste.setImg(0x13);
                addCommand(cmdJid); cmdJid.setImg(0x60);
                addCommand(cmdSubj); cmdSubj.setImg(0x81);
              }
            };            
            addCommand(cmdEdit); cmdEdit.setImg(0x40);
            
            addCommand(cmdDelete); cmdDelete.setImg(0x41);
            addCommand(cmdDeleteAll); cmdDeleteAll.setImg(0x41);
        }
        addCommand(cmdNew); cmdNew.setImg(0x47);
//#ifdef MENU_LISTENER
        super.addCommands();
//#endif
    }

    protected void beginPaint() {
        getMainBarItem().setElementAt(" ("+getItemCount()+")",1);
	getMainBarItem().setElementAt(String.valueOf(getFreeSpace()),3);
    }
    
    public int getItemCount() {
	return archive.size();
    }
    
    public Msg getMessage(int index) {
	return archive.msg(index);
    }

//#ifdef MENU_LISTENER    
    public void userKeyPressed(int keyCode){
     switch (keyCode) {
        case KEY_NUM4:
            pageLeft();
            break; 
        case KEY_NUM6:
            pageRight();
            break;  
     }
    }
//#endif 

    public void commandAction(Command c, Displayable d) {
        super.commandAction(c,d);
        
	Msg m=getMessage(cursor);
     
        if (c==cmdNew) { new archiveEdit(display, this, -1, where, this); }
	if (m==null) return;
        
	if (c==cmdDelete) { keyClear(); }
        if (c==cmdDeleteAll) { deleteAllMessages(); redraw(); }
	if (c==cmdPaste) { pasteData(0); }
	if (c==cmdSubj) { pasteData(1); }
	if (c==cmdJid) { pasteData(2); }
        if (c==cmdEdit) {
            try {
                new archiveEdit(display, this, cursor, where, this);
            } catch (Exception e) {/*no messages*/}
        }
    }
    
    public void reFresh() {
        archive=new MessageArchive(where);
        messages=null;
        messages=new Vector();
    }

    private void deleteMessage() {
        archive.delete(cursor);
        messages=null;
        messages=new Vector();
    }
    
    private void deleteAllMessages() {
        new AlertBox(SR.MS_ACTION, SR.MS_DELETE_ALL+"?", display, this) {
            public void yes() {
                archive.deleteAll();
                messages=null;
                messages=new Vector();
            }
            public void no() { }
        };
    }
    
    private void pasteData(int field) {
        if(midlet.BombusQD.cf.msgEditType>0){
           if (tf==null) return;
        }else{
           if (tb==null) return;
        };        
	Msg m=getMessage(cursor);
	if (m==null) return;
	String data;
	switch (field) {
	case 1: 
	    data=m.subject;
	    break;
	case 2: 
	    data=m.from;
	    break;
	default:
	    data=m.quoteString();
	}
        if(midlet.BombusQD.cf.msgEditType>0){
           tf.insert(data, caretPos);
        }else{
           tb.insert(data, caretPos);
        };        
	destroyView();
    }
    
    public void keyGreen() { pasteData(0); }
    
    public void keyClear() { 
        if (getItemCount()>0) {
            new AlertBox(SR.MS_DELETE, SR.MS_SURE_DELETE, display, this) {
                public void yes() {
                    deleteMessage();
                }
                public void no() { }
            };
            redraw();
        }
    }

    public void destroyView(){
        super.destroyView();
        if(midlet.BombusQD.cf.animatedSmiles) images.SmilesIcons.stopTimer();
        archive.close();
    }

    private int getFreeSpace() {
        return archive.freeSpace();
    }
}