/*
 * ContactMessageList.java
 *
 * Created on 19.02.2005, 23:54
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

package Client;
//#ifndef WMUC
import Conference.MucContact;
import Client.contact.ChatInfo;
//#endif
import Messages.MessageItem;
import Messages.MessageUrl;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextField;
import locale.SR;
import ui.MainBar;
import java.util.*;
import ui.VirtualElement;
import ui.VirtualList;
import ui.MIDPTextBox;
//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.Command;
//#else
import Menu.Command;
import Menu.MenuListener;
//#endif
//#ifdef ARCHIVE
import Archive.MessageArchive;
//#endif
//#ifdef GRAPHICS_MENU        
//# import ui.GMenu;
//# import ui.GMenuConfig;
//#endif
import Colors.ColorTheme;
import javax.microedition.rms.RecordStore;
import History.HistoryStorage;

public final class ContactMessageList extends VirtualList implements MenuListener,MIDPTextBox.TextBoxNotify {
    
    Contact contact;
    private boolean composing=true;
    private boolean startSelection;
    private boolean tr = false;
    
    protected final Vector messages = new Vector(0);
    private Vector msgs;
    protected boolean smiles;
    
    public void destroy() {
        super.destroy();
        int size = messages.size();
        //System.out.println("    :::ContactMessageList destroy->"+contact+"->chat with "+size+" messages");
        for(int i = 0; i<size; ++i){
          MessageItem mi = (MessageItem)messages.elementAt(i);
          //System.out.println("    :::   destroyMessageItem#"+i);
          mi.destroy();
          mi = null;
        }
        //System.out.println("    :::ContactMessageList destroy->"+contact+"->cml->messages&msgs");
        messages.removeAllElements();
        contact = null;
        msgs.removeAllElements();
        msgs = null;
    }
    
    
    private RecordStore recordStore = null;
    public RecordStore getRecordStore(){
       return recordStore;
    }
    public void storeMessage(Msg msgObj) {
	synchronized (this) {
           HistoryStorage.addText(contact, msgObj, this);
       }
    }
    public void getRmsData(int type, RecordStore rs){
      switch(type){
          case 0: 
              //SAVE_RMS_STORE
              recordStore = rs;
              break;
          case 1: 
              //CLEAR_RMS_STORE
              recordStore = HistoryStorage.clearRecordStore(rs);
              break;
          case 2: 
              //CLOSE_RMS_STORE
              recordStore = HistoryStorage.closeStore(rs);
              break;
          case 3: 
              //READ_ALL_DATA
              String rName = HistoryStorage.getRSName(contact.bareJid);
              if(rName.length() > 30) rName = rName.substring(0,30);
              try {
                 if(recordStore != null) {
                      recordStore.closeRecordStore();
                      recordStore = null;
                 }
              } catch (Exception e) { 
                  //#ifdef CONSOLE
//#                   midlet.BombusQD.debug.add("errclose rms",10);
                  //#endif
              }
              try {
                 recordStore = RecordStore.openRecordStore(rName, true);
              } catch (Exception e) { 
                  //#ifdef CONSOLE
//#                   midlet.BombusQD.debug.add("erropen rms",10);
                  //#endif
              }
              HistoryStorage.loadData(contact, recordStore);
              break;
      } 
    }
    
    private MainBar mainbar = null;
    public void updateMainBar(Contact contact){
        mainbar = new MainBar(contact);
        setMainBarItem(mainbar);
        mainbar = null;
    }
    
    /** Creates a new instance of MessageList */
    public ContactMessageList(Contact contact) {
        this.contact=contact;
        midlet.BombusQD.sd.roster.activeContact=contact;
        
//#ifdef SMILES
        smiles=midlet.BombusQD.cf.smiles;
//#else
//#         smiles=false;
//#endif
        
        //MainBar mainbar=new MainBar(contact);
        msgs = contact.getChatInfo().getMsgs();
        updateMainBar(contact);

        cursor=0;
        contact.setIncoming(0);
//#ifdef FILE_TRANSFER        
        contact.fileQuery=false;
//#endif        
        enableListWrapping(false);

      resetMessages();
    }
    
    private ChatInfo getChatInfo() {
        return contact.getChatInfo();
    }
    

    public void commandState(){
//#ifdef MENU_LISTENER
        menuCommands.removeAllElements();
        cmdfirstList.removeAllElements();
        cmdsecondList.removeAllElements();
        cmdThirdList.removeAllElements();
//#endif
        if (startSelection) addCommand(midlet.BombusQD.commands.cmdSelect); 
        if (contact.msgSuspended!=null) addCommand(midlet.BombusQD.commands.cmdResume); 
        
        if (midlet.BombusQD.commands.cmdSubscribe==null) return;
        try {
            Msg msg=getMessageAt(cursor);
            if (msg.messageType==Constants.MESSAGE_TYPE_AUTH) {
                addCommand(midlet.BombusQD.commands.cmdSubscribe); 
                addCommand(midlet.BombusQD.commands.cmdUnsubscribed); 
            }
        } catch (Exception e) {}
        
        addCommand(midlet.BombusQD.commands.cmdMessage); 
        if (contact.origin!=Constants.ORIGIN_GROUPCHAT) addCommand(midlet.BombusQD.commands.cmdActions); 
        

        if(midlet.BombusQD.cf.module_history){
            addCommand(midlet.BombusQD.commands.cmdHistory); 
            switch(History.HistoryConfig.getInstance().historyTypeIndex) {
              case 0:
                addInCommand(2,midlet.BombusQD.commands.cmdHistoryRMS); 
                break;
              case 1:
                addInCommand(2,midlet.BombusQD.commands.cmdHistoryFS); 
                break;
              case 2:
                addInCommand(2,midlet.BombusQD.commands.cmdHistorySERVER); 
                break;
            }
        }
        
        if (contact.getChatInfo().getMessageCount()>0) {
//#ifndef WMUC
            if (contact instanceof MucContact && contact.origin==Constants.ORIGIN_GROUPCHAT
                    || contact.getJid().indexOf("juick@juick.com")>-1 ) {
                addCommand(midlet.BombusQD.commands.cmdReply);
            }
//#endif
            addCommand(midlet.BombusQD.commands.cmdQuote); 
            
            addCommand(midlet.BombusQD.commands.cmdMyService); 
            addInCommand(3,midlet.BombusQD.commands.cmdPurge); 
            addInCommand(3,midlet.BombusQD.commands.cmdAddSearchQuery); 
            //addInCommand(3,midlet.BombusQD.commands.cmdTranslate); 
            addInCommand(3,midlet.BombusQD.commands.cmdClrPresences); 
            
            if (!startSelection) addInCommand(3,midlet.BombusQD.commands.cmdSelect); 

        //if (contact.getChatInfo().getMessageCount()>0) {
//#ifdef ARCHIVE
//#ifdef PLUGINS
//#          if (sd.Archive)
//#endif
            addCommand(midlet.BombusQD.commands.cmdArch); 
//#endif
//#if TEMPLATES
//#ifdef PLUGINS         
//#          if (sd.Archive)
//#endif
//#             addCommand(midlet.BombusQD.commands.cmdTemplate);
//#endif
        //}
            
//#ifdef CLIPBOARD
//#             if (midlet.BombusQD.cf.useClipBoard) {
//#                 addCommand(midlet.BombusQD.commands.cmdCopy); 
//#                 if (!midlet.BombusQD.clipboard.isEmpty()) addCommand(midlet.BombusQD.commands.cmdCopyPlus); 
//#             }
//#endif
//#ifdef MENU_LISTENER
            if (isHasScheme()) addInCommand(3,midlet.BombusQD.commands.cmdxmlSkin);  
//#endif
//#ifdef MENU_LISTENER
            if (hasUrl()) 
//#endif
                addCommand(midlet.BombusQD.commands.cmdUrl); 
        }

        
//#ifdef CLIPBOARD
//#         if (midlet.BombusQD.cf.useClipBoard && !midlet.BombusQD.clipboard.isEmpty()) {
//#             addInCommand(3,midlet.BombusQD.commands.cmdSendBuffer); 
//#         }
//#endif

//#ifndef GRAPHICS_MENU        
     addCommdand(midlet.BombusQD.commands.cmdBack);
//#endif            
        
//#if BREDOGENERATOR                
//#         if(midlet.BombusQD.cf.bredoGen==true){
//#            addCommand(midlet.BombusQD.commands.cmdAutoGenOff);
//#            removeCommand(midlet.BombusQD.commands.cmdAutoGenON);
//#         }else{
//#            addCommand(midlet.BombusQD.commands.cmdAutoGenON);
//#         }
//#endif     
    }
    
    public void showNotify(){
        midlet.BombusQD.sd.roster.activeContact=contact;
        super.showNotify();
    }

    public VirtualElement getItemRef(int index) {
        MessageItem mi=(MessageItem) messages.elementAt(index);
        if (mi.msg.unread) {//������������� ���������
            getChatInfo().readMessage(mi.msg);
        }
        return mi;
    }
    
    protected Msg getMessage(int index) {
        return (Msg) msgs.elementAt(index);
    }   

    private Msg getMessageAt(int index) {
        return (Msg) msgs.elementAt(index);
    }
    

    private void forceScrolling() { //by voffk
        if (midlet.BombusQD.cf.autoScroll) {
            if (cursor >= (messages.size() - 2)) {
                moveCursorEnd();
            }
        }
    }

    protected void beginPaint() {
        markRead(cursor);
    }   
    
    protected void markRead(int msgIndex) {
    }
    
    public void deleteOldMessages() {
       cursor -= getChatInfo().getMessageCount() - midlet.BombusQD.cf.msglistLimit+1;
       if (cursor<0) cursor = 0;
       getChatInfo().deleteOldMessages(messages);
    }
    
    protected final int getItemCount(){ return messages.size(); }
    
    protected void focusedItem(int index) { 
        markRead(index); 
    }
    
    protected void touchMainPanelPressed(int x, int y) {
        contact.setCursor(cursor);
        if (x>50 && x< width-50) {
                contact.getChatInfo().opened = false;
                midlet.BombusQD.sd.roster.createActiveContacts(this, contact);
                contact.setCursor(cursor);
        } else if (x<50){
            midlet.BombusQD.sd.roster.searchActiveContact(contact, false);
        } else {
            midlet.BombusQD.sd.roster.searchActiveContact(contact, true);
        }
    }

    public void commandAction(Command c, Displayable d){
        //super.commandAction(c,d);
	//cf.clearedGrMenu=true;	
        if(c==midlet.BombusQD.commands.cmdHistoryRMS) {
            getRmsData(3, null); //READ_ALL_DATA
        }
        if(c==midlet.BombusQD.commands.cmdHistoryFS) {

        }
        if(c==midlet.BombusQD.commands.cmdHistorySERVER) {

        }      
        if (c==midlet.BombusQD.commands.cmdxmlSkin) {
           try {
               if (((MessageItem)getFocusedObject()).msg.body.indexOf("xmlSkin")>-1) {
                    ColorTheme.loadSkin(((MessageItem)getFocusedObject()).msg.body,2,true);
               }
            } catch (Exception e){}
        }
//#ifdef ARCHIVE
        if (c==midlet.BombusQD.commands.cmdArch) {
            try {
                MessageArchive.store(util.StringUtils.replaceNickTags(getMessage(cursor)),1);
            } catch (Exception e) {/*no messages*/}
        }
//#endif
//#if TEMPLATES
//#         if (c==midlet.BombusQD.commands.cmdTemplate) {
//#             try {
//#                 Msg msg = getMessage(cursor);
//#                 msg.from = "";
//#                 MessageArchive.store(util.StringUtils.replaceNickTags(msg),2);
//#             } catch (Exception e) {/*no messages*/}
//#         }
//#endif
        if (c==midlet.BombusQD.commands.cmdUrl) {
            try {
                Vector urls=((MessageItem) getFocusedObject()).getUrlList();
                new MessageUrl(midlet.BombusQD.getInstance().display, urls); //throws NullPointerException if no urls
            } catch (Exception e) {/* no urls found */}
        }
//#ifdef CLIPBOARD
//#         if (c == midlet.BombusQD.commands.cmdCopy)
//#         {
//#             try {
//#                 midlet.BombusQD.clipboard.add(  util.StringUtils.replaceNickTags( ((MessageItem)getFocusedObject()).msg )  );
//#             } catch (Exception e) {/*no messages*/}
//#         }
//#         
//#         if (c==midlet.BombusQD.commands.cmdCopyPlus) {
//#             try {
//#                 midlet.BombusQD.clipboard.append( util.StringUtils.replaceNickTags(  ((MessageItem)getFocusedObject()).msg  ) );
//#             } catch (Exception e) {/*no messages*/}
//#         }
//#endif
        
        if(c==midlet.BombusQD.commands.cmdClrPresences){
            smartPurge(true);
            return;
        }
        /*
        if(c==midlet.BombusQD.commands.cmdTranslate){
            String body = replaceNickTags(getMessage(cursor)).body.toString();
            if(body.indexOf(">")>-1){
              String nick = body.substring(0,body.indexOf(">"));
              new TranslateSelect(display,this,contact,
                      replaceNickTags(getMessage(cursor)).body.substring(body.indexOf(">")+1,body.length()).trim(),nick,
                      true,cursor);                
            }else{
              new TranslateSelect(display,this,contact,replaceNickTags(getMessage(cursor)).body.toString(),"none",true,cursor);                
            }

            tr=true;
            contact.setCursor(cursor);
            return;
        }
         */
        if (c==midlet.BombusQD.commands.cmdPurge) {
            if (messages.isEmpty()) return;
            
            if (startSelection) {
                for (Enumeration select=msgs.elements(); select.hasMoreElements(); ) {
                    Msg mess=(Msg) select.nextElement();
                    if (mess.selected) {
                        removeMessage(msgs.indexOf(mess));
                    }
                }
                startSelection = false;
            } else {
                clearReadedMessageList();
            }
            return;
        }
        if (c==midlet.BombusQD.commands.cmdSelect) {
            startSelection=true;
            Msg mess=getMessage(cursor);
            mess.selected = !mess.selected;
            mess.search_word=!mess.search_word;; //image
            mess.oldHighlite = mess.highlite;
            mess.highlite = mess.selected;
            return;
        }
        /** login-critical section */
        if (!midlet.BombusQD.sd.roster.isLoggedIn()) return;

        if (c==midlet.BombusQD.commands.cmdMessage) { 
            contact.msgSuspended=null;
            keyGreen(); 
        }
        if (c==midlet.BombusQD.commands.cmdResume) {
            keyGreen();
        }
        if (c==midlet.BombusQD.commands.cmdQuote) Quote();
        if (c==midlet.BombusQD.commands.cmdReply) {
            if(contact.getJid().indexOf("juick@juick.com")>-1){
                Reply(false); return;                  
            } else checkOffline();
        }
        if(c==midlet.BombusQD.commands.cmdAddSearchQuery) {
             new MIDPTextBox(midlet.BombusQD.getInstance().display, SR.get(SR.MS_SEARCH), null, this, TextField.ANY, 30);
             return;
        } 
//#if BREDOGENERATOR                 
//#         if (c==midlet.BombusQD.commands.cmdAutoGenON) {
//#            midlet.BombusQD.cf.bredoGen=true;
//#            midlet.BombusQD.sd.roster.showRoster();
//#            VirtualList.setWobble(3, null, SR.get(SR.MS_BREDO_ON));
//#         }
//#         if (c==midlet.BombusQD.commands.cmdAutoGenOff) {
//#            midlet.BombusQD.cf.bredoGen=false;
//#            midlet.BombusQD.sd.roster.showRoster();
//#            VirtualList.setWobble(3, null, SR.get(SR.MS_BREDO_OFF));
//#         }  
//#endif         
        if (c==midlet.BombusQD.commands.cmdActions) {
//#ifndef WMUC
            if (contact instanceof MucContact) {
                MucContact mc=(MucContact) contact;
                midlet.BombusQD.sd.roster.showUserActions(this, mc, -1);
            } else
//#endif
                midlet.BombusQD.sd.roster.showUserActions(this, contact, -1);
        }
	if (c==midlet.BombusQD.commands.cmdActive) {
            contact.getChatInfo().opened = false;
            midlet.BombusQD.sd.roster.createActiveContacts(this, contact); 
        }
        
        if (c==midlet.BombusQD.commands.cmdSubscribe) midlet.BombusQD.sd.roster.doSubscribe(contact);
		
        if (c==midlet.BombusQD.commands.cmdUnsubscribed) midlet.BombusQD.sd.roster.sendPresence(contact.bareJid, "unsubscribed", null, false);

//#ifdef CLIPBOARD
//#         if (c==midlet.BombusQD.commands.cmdSendBuffer) {
//#             String from=midlet.BombusQD.sd.account.toString();
//#             String body=midlet.BombusQD.clipboard.getClipBoard();
//# 
//#             String id=String.valueOf((int) System.currentTimeMillis());
//#             Msg msg=new Msg(Constants.MESSAGE_TYPE_OUT,from,null,body);
//#             msg.id=id;
//#             msg.itemCollapsed=true;
//#             
//#             try {
//#                 if (body!=null && body.length()>0) {
//#                     midlet.BombusQD.sd.roster.sendMessage(contact, id, body, null, null,false);
//#                     if (contact.origin<Constants.ORIGIN_GROUPCHAT) contact.addMessage(msg);
//#                 }
//#             } catch (Exception e) {
//#                 contact.addMessage(new Msg(Constants.MESSAGE_TYPE_OUT,from,null,SR.get(SR.MS_CLIPBOARD_SENDERROR)));
//#             }
//#             redraw();
//#             return;
//#         }
//#endif
    }

    private String txt = "";
    public void OkNotify(String txt) {
        this.txt = txt;
        find_str(txt);
    }
    
    public void clearReadedMessageList() {
        smartPurge(false);
        cursor=0;
        moveCursorHome();
        redraw();
    }
    
    public void eventLongOk(){
        super.eventLongOk();
	answer();
    }
    
    protected void keyClear(){
        if (!messages.isEmpty()) clearReadedMessageList();
    }
    
    public void keyRepeated(int keyCode) {
        if (keyCode==KEY_NUM0) 
            clearReadedMessageList();
	else 
            super.keyRepeated(keyCode);
    }  

    
    private Vector vectorfound = new Vector(0);
    private int found_count=0;
    

    private void clear_results(){ //end of search
                moveCursorEnd();
                for (int i=0; i<(cursor+1); i++)
                {
                  if((getMessage(i).toString().indexOf(txt)>-1))  
                  {
                    Msg m = getMessage(i);
                    m.search_word=false;
                    m.highlite=false;
                  }
                }
                midlet.BombusQD.cf.find_text=false;
                moveCursorHome();
    }
    
    
    public void find_str(String query){
                moveCursorEnd();
                //VirtualList.setWobble(1, null, "       Wait!");
                for (int i=0; i<(cursor+1); i++)
                {
                  if((getMessage(i).toString().indexOf(query)>-1))
                  {
           	    vectorfound.addElement(Integer.toString(i));
                    Msg m = getMessage(i);
                    m.search_word=true; //image
                    m.highlite=true;
                  }
                }
                if(vectorfound.size()>0) { 
                    int cursor_index = Integer.parseInt(vectorfound.elementAt(found_count).toString());
                    moveCursorTo(cursor_index, true);
                    midlet.BombusQD.cf.find_text=true;
                    //VirtualList.setWobble(1, null, "Results of Search:\nword: "+query+"\ncounts: "+vectorfound.size());
                    //setMainBarItem(new MainBar("    Search: "+Integer.toString(1)+"/"+Integer.toString(vectorfound.size()) + " ..6>"));  
                }else{
                    midlet.BombusQD.cf.find_text=false;
                    //VirtualList.setWobble(3, null, SR.get(SR.MS_NOT_FOUND));
                    moveCursorHome();
                }
    }
        
        
    private void checkOffline(){
       Msg msg;
       boolean found=false; 
       try {
           msg=getMessage(cursor);
           int size=midlet.BombusQD.sd.roster.contactList.contacts.size();
           Contact c;
           if(!found) {
             for(int i=0;i<size;i++){
              c=(Contact)midlet.BombusQD.sd.roster.contactList.contacts.elementAt(i);
              if (c instanceof MucContact){
                if(c.jid.getBareJid().equals(contact.jid.getBareJid()) && c.getNick().equals(msg.from)) found=(c.status!=5);
               }
             }
           }
       } catch (Exception e) { msg = null; }
       
       if(!found&&msg!=null) { new ui.controls.AlertBox(msg.from,
           SR.get(SR.MS_ALERT_CONTACT_OFFLINE), midlet.BombusQD.getInstance().display, this, false) {
           public void yes() { Reply(true); }
           public void no() { }
         };
       } else Reply(true);
       msg=null;
    }
    
    
    public void keyGreen(){
        if (!midlet.BombusQD.sd.roster.isLoggedIn()) return;
//#ifdef RUNNING_MESSAGE
//#         showMsgEdit(contact.msgSuspended);
//#else
    new MessageEdit(display, this, contact, contact.msgSuspended);
//#endif
    }

    private void showMsgEdit(String msgText){
      contact.msgSuspended = null;
      midlet.BombusQD.sd.roster.createMessageEdit(contact, msgText , this, false);  
    }
    
    private void Reply(boolean check) {
        if (!midlet.BombusQD.sd.roster.isLoggedIn()) return;
        
        try {
            Msg msg = getMessage(cursor);
            if(msg != null) msg=util.StringUtils.replaceNickTags(msg);
            if (msg==null ||
                msg.messageType == Constants.MESSAGE_TYPE_OUT ||
                msg.messageType == Constants.MESSAGE_TYPE_SUBJ)
                keyGreen();
            else {
//#ifdef RUNNING_MESSAGE
//#                String messg = msg.from+": "; 
//#ifdef JUICK.COM                
//#                if(msg.messageType==Constants.MESSAGE_TYPE_JUICK){
//#                     messg=util.StringUtils.replaceNickTags(msg.id);
//#                }
//#endif          
//#              if(messg==null) messg = "";
//#                
//#              if(contact.msgSuspended != null && check) {
//#                final String msgText = messg;
//#                ui.controls.AlertBox obj =  new ui.controls.AlertBox(msg.from, SR.get(SR.MS_MSGBUFFER_NOT_EMPTY),
//#                        midlet.BombusQD.getInstance().display, this, false) {
//#                    public void yes() { showMsgEdit(msgText); }
//#                    public void no()  { keyGreen(); }
//#                }; 
//#                obj = null;
//#                return;
//#              }
//#                
//#              showMsgEdit(messg);
//#             }
//#             
//#else
            new MessageEdit(display, this, contact, msg.from+": ");
//#endif
        } catch (Exception e) {/*no messages*/}
    }
    
    
    
    public void keyPressed(int keyCode) {
     if(midlet.BombusQD.cf.savePos) { 
       if(keyCode==Config.KEY_BACK || keyCode==Config.SOFT_RIGHT || keyCode==KEY_NUM3){
         contact.setCursor(cursor);
       }
     }
     if(midlet.BombusQD.cf.find_text==false){        
        if (keyCode==KEY_POUND) {
           answer();
           return;
        }
      }
//#ifdef SMILES
      if (keyCode=='*') {
            try {
                ((MessageItem)getFocusedObject()).toggleSmiles(this);
            } catch (Exception e){}
            return;
      }
//#endif
      super.keyPressed(keyCode);
   }
    
    
    public void eventOk(){
          if(midlet.BombusQD.cf.createMessageByFive) answer();
          else {
              ((MessageItem)getFocusedObject()).onSelect(this);
              moveCursorTo(cursor);
          }
    }

    
    private void answer() {
//#ifndef WMUC
            if (contact instanceof MucContact && contact.origin==Constants.ORIGIN_GROUPCHAT) {
                checkOffline();
                return;
            }
    
//#ifdef JUICK.COM
//#             else{
//#               if(contact.getJid().indexOf("juick@juick.com")>-1){
//#                 Reply(false); return;                  
//#               }
//#             }
//#endif             
//#endif
            keyGreen();
            return;
    }

    
    
    public void userKeyPressed(int keyCode) {
     if(midlet.BombusQD.cf.find_text){//next rev
          String whatPress = "<[4]..[6]>";  
          switch (keyCode) {
              case KEY_NUM4: {
                     if(found_count>0) found_count--;
                      else { 
                          VirtualList.setWobble(1, null, SR.get(SR.MS_END_SEARCH));  
                          clear_results();               
                      }
                      if(found_count==0)
                          whatPress = "..[6]>";
                      int cursor_index = Integer.parseInt(vectorfound.elementAt(found_count).toString());  
                      moveCursorTo(cursor_index, true);
                      //setMainBarItem(new MainBar("    "+SR.get(SR.MS_SEARCH+": "+
                      //        Integer.toString(found_count+1)+"/"+Integer.toString(vectorfound.size()) + "   " + whatPress));
                      redraw();      
               break;       
              }  
              case KEY_NUM6: {
                      if(found_count<vectorfound.size()-1) found_count++;
                      else { 
                          VirtualList.setWobble(1, null, SR.get(SR.MS_END_SEARCH));   
                          clear_results();
                      }
                      int cursor_index = Integer.parseInt(vectorfound.elementAt(found_count).toString());   
                      moveCursorTo(cursor_index, true); 
                      if(found_count==vectorfound.size()-1) { whatPress = "<[4].."; }
                      //setMainBarItem(new MainBar("    "+SR.get(SR.MS_SEARCH+": "+
                      //        Integer.toString(found_count+1)+"/"+Integer.toString(vectorfound.size())+ "   " + whatPress));
                      redraw();        
              break;         
             }       
          }            
       }
       else
       {
          switch (keyCode) {
            case KEY_NUM4:
                if (midlet.BombusQD.cf.useTabs)
                    midlet.BombusQD.sd.roster.searchActiveContact(contact, false);
                else
                    super.pageLeft();
                contact.setCursor(cursor);
                break;
            case KEY_NUM0:
                int size = midlet.BombusQD.sd.roster.contactList.contacts.size();
                if(midlet.BombusQD.cf.savePos) {
                  contact.setCursor(cursor);
                }
                Contact c;
                //synchronized (midlet.BombusQD.sd.roster.contactList.contacts) {
                for(int i=0;i<size;i++){
                        c = (Contact)midlet.BombusQD.sd.roster.contactList.contacts.elementAt(i);
                        if (c.getNewMsgsCount()>0){
                            contact.getChatInfo().opened = false;
                            midlet.BombusQD.getInstance().display.setCurrent(c.getMessageList());
                           break;
                        }
                    }
                 // }                
                  break;
            case KEY_NUM6:
                if (midlet.BombusQD.cf.useTabs)
                    midlet.BombusQD.sd.roster.searchActiveContact(contact, true);
                else
                    super.pageRight();
                contact.setCursor(cursor);
                break;
            case KEY_NUM3:
                contact.getChatInfo().opened = false;
                contact.setCursor(cursor);
                midlet.BombusQD.sd.roster.createActiveContacts(this, contact);
                break;        
            case KEY_NUM9:
                Quote();
                break;
          }
       }
    }

//#ifdef MENU_LISTENER
    
//#ifdef GRAPHICS_MENU        
//#     public void touchRightPressed(){
//#         contact.setCursor(cursor);
//#         if (midlet.BombusQD.cf.oldSE)
//#             showGraphicsMenu();
//#         else
//#             destroyView();
//#     }
//#     public void touchMiddlePressed(){
//#         keyGreen();
//#     }
//#     public void touchLeftPressed(){
//#         contact.setCursor(cursor);
//#         if (midlet.BombusQD.cf.oldSE)
//#             destroyView();
//#         else showGraphicsMenu();
//#     }
//#else
    public void touchRightPressed(){ if (cf.oldSE) showMenu(); else destroyView(); }    
    public void touchLeftPressed(){ if (cf.oldSE) keyGreen(); else showMenu(); }
//#endif      

//#endif

    private void Quote() {
        if (!midlet.BombusQD.sd.roster.isLoggedIn()) return;
        
        try {
            String msg=new StringBuffer(0)
                .append((char)0xab) //
                .append( util.StringUtils.quoteString( util.StringUtils.replaceNickTags(getMessage(cursor)) ) )
                .append((char)0xbb)
                .append("\n")
                .toString();
//#ifdef RUNNING_MESSAGE
//#                 showMsgEdit(msg);
//#else
            new MessageEdit(display, this, contact, msg);
//#endif
            msg=null;
        } catch (Exception e) {/*no messages*/}
    }
    
    public void resetMessages() {
        //System.out.println("resetMessages");
        messages.removeAllElements();
        Msg msg;
        MessageItem mi;
        for (int i = messages.size(); i < msgs.size(); ++i) {
            msg = (Msg)msgs.elementAt(i);
            mi = new MessageItem(msg, smiles);
            mi.setEven((messages.size() & 1) == 0);
            mi.parse(this);
            //mi.getColor();
            messages.addElement(mi);
        }
        mi = null;
    }
    
    public void addMessage(Msg msg) {
        if(contact.getChatInfo().opened) contact.getChatInfo().reEnumCounts();
        MessageItem mi = new MessageItem(msg, smiles);
        mi.setEven((messages.size() & 1) == 0);
        mi.parse(this);
        messages.addElement(mi);
        mi = null;
        forceScrolling();
        redraw();
    }
    
    
    private final void smartPurge(boolean presence) {
        int cur=cursor+1;
        try {
            if (msgs.size()>0){
                int virtCursor=msgs.size();
                boolean delete = false;
                int i=msgs.size();
                while (true) {
                    if (i<0) break;
                    if (i<cur) {
                        if (delete == false) {
                            //System.out.println("not found else");
                            if(presence){
                                if(getMessage(virtCursor).isPresence()){
                                    removeMessage(virtCursor);
                                    delete=true;    
                                }
                            }
                            else if (getMessage(virtCursor).dateGmt+1000<System.currentTimeMillis()) {
                                //System.out.println("can delete: "+ delPos);
                                removeMessage(virtCursor);
                                //delPos--;
                                delete=true;
                            }
                        } else {
                            //System.out.println("delete: "+ delPos);
                            if(presence) {
                                if(getMessage(virtCursor).isPresence()) removeMessage(virtCursor);
                            }
                            else removeMessage(virtCursor);
                            //delPos--;
                        }
                    }
                    virtCursor--;
                    i--;
                }
            }
        } catch (Exception e) { }
        
        if(msgs.size() == 0) {
          contact.clearVCard();
        }
        contact.getChatInfo().resetLastUnreadMessage();
    }
    

    public void destroyView(){
           contact.getChatInfo().opened = false;
//#ifdef GRAPHICS_MENU
//#            midlet.BombusQD.sd.roster.activeContact=null;
//#            midlet.BombusQD.sd.roster.reEnumRoster(); //to reset unread messages icon for this conference in roster
//#            //if (midlet.BombusQD.getInstance().display!=null)
//#            midlet.BombusQD.sd.roster.showRoster();
//#else
        sd.roster.activeContact=null;
        sd.roster.reEnumRoster(); //to reset unread messages icon for this conference in roster
        if (display!=null) display.setCurrent(sd.roster);
//#endif     
    }

    
    private void removeMessage(int msgIndex) {
        if (-1 == msgIndex) {
            return;
        }
        getChatInfo().readMessage(getMessage(msgIndex));
        getMessage(msgIndex).destroy();

          MessageItem mi = (MessageItem)messages.elementAt(msgIndex);
          mi.destroy();
          mi = null;
        msgs.removeElementAt(msgIndex);
        messages.removeElementAt(msgIndex);
    }
    
    
//#ifdef MENU_LISTENER
    
    
//#ifdef GRAPHICS_MENU 
//#     
//#     public int showGraphicsMenu() {
//#          GMenuConfig.getInstance().itemGrMenu = GMenu.CONTACT_MSGS_LIST;
//#          commandState();
//#          menuItem = new GMenu(midlet.BombusQD.getInstance().display , this, this, null, menuCommands, cmdfirstList, cmdsecondList, cmdThirdList);
//#          redraw();
//#         return GMenu.CONTACT_MSGS_LIST;
//#     }
//#else
    public void showMenu() {
         commandState();
         super.showMenu();
    }   
//#endif      

    

    
    public boolean isHasScheme() {
        if (msgs.size() == 0) {
            return false;
        }
        String body = getMessage(cursor).body;
        if (body.indexOf("xmlSkin")>-1) return true;
        return false;
    }

    public boolean hasUrl() {
        if (0 == msgs.size()) {
            return false;
        }
        String body = getMessage(cursor).body;
        if (-1 != body.indexOf("http://")) return true;
        if (-1 != body.indexOf("https://")) return true;
        if (-1 != body.indexOf("ftp://")) return true;
        if (-1 != body.indexOf("tel:")) return true;
        if (-1 != body.indexOf("native:")) return true;
        return false;
    }
//#endif
}
