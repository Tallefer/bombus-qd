/*
 * MessageEdit.java
 *
 * Created on 20.02.2005, 21:20
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
import Conference.AppendNick;
//#endif
//#ifdef DETRANSLIT
//# import util.DeTranslit;
//#endif
import javax.microedition.lcdui.*;
import locale.SR;
import ui.VirtualList;
import ui.controls.ExTextBox;
import io.TranslateSelect;
//#ifdef STATS
//# import Statistic.Stats;
//#endif
//#ifdef CLIPBOARD
//# import util.ClipBoard;
//#endif
//#ifdef ARCHIVE
import Archive.ArchiveList;
//#endif
/**
 *
 * @author Eugene Stahov,aqent
 */
public class MessageEdit 
        implements CommandListener, Runnable {
//#ifdef RUNNING_MESSAGE
//#     Thread thread = null;
//#endif
    private Display display;
    private Displayable parentView;

    private String body;
    private String subj;
    
    public Contact to;
    
    private boolean composing=true;
    
//#ifdef DETRANSLIT
//#     private boolean sendInTranslit=false;
//#     private boolean sendInDeTranslit=false;
//#     DeTranslit dt;
//#endif
    
    private static Command cmdSend=new Command(SR.MS_SEND, Command.OK, 1);
//#ifdef SMILES
    private static Command cmdSmile=new Command(SR.MS_ADD_SMILE, Command.SCREEN,2);
//#endif
    private static Command cmdInsNick=new Command(SR.MS_NICKNAMES,Command.SCREEN,3);
    private static Command cmdInsMe=new Command(SR.MS_SLASHME, Command.SCREEN, 4); ; // /me
//#ifdef DETRANSLIT
//#     private static Command cmdSendInTranslit=new Command(SR.MS_TRANSLIT, Command.SCREEN, 5);
//#     private static Command cmdSendInDeTranslit=new Command(SR.MS_DETRANSLIT, Command.SCREEN, 5);
//#endif
    private static Command cmdLastMessage=new Command(SR.MS_PREVIOUS, Command.SCREEN, 9);
    private static Command cmdSubj=new Command(SR.MS_SET_SUBJECT, Command.SCREEN, 10);
    private static Command cmdSuspend=new Command(SR.MS_SUSPEND, Command.BACK,90);
    private static Command cmdCancel=new Command(SR.MS_CANCEL, Command.SCREEN,99);
    private static Command cmdSendEvil=new Command(SR.MS_SEND_EVIL_MSG, Command.SCREEN /*Command.SCREEN*/,229);    
    private static Command cmdTranslate=new Command(SR.MS_TRANSLATE, Command.SCREEN /*Command.SCREEN*/,337);
    
    
    
//#ifdef CLIPBOARD
//#     private ClipBoard clipboard;
//#endif
    
//#ifdef ARCHIVE
    protected static Command cmdPaste=new Command(SR.MS_ARCHIVE, Command.SCREEN, 6);    
//#endif
//#if TEMPLATES
//#     protected Command cmdTemplate=new Command(SR.MS_TEMPLATE, Command.SCREEN, 7); 
//#endif  
//#ifdef CLIPBOARD
//#     protected static Command cmdPasteText=new Command(SR.MS_PASTE, Command.SCREEN, 8);  
//#endif
    
    
    
    private boolean evil=false;
    public TextBox t;
    public Ticker ticker = null;
    /** Creates a new instance of MessageEdit */

    
    public MessageEdit(Display display, Displayable pView, Contact to, String body) {
       this.to=to;
       this.display=display;
       this.parentView=pView;
       if (midlet.BombusQD.cf.notifyWhenMessageType)
       {
          t=new TextBox(to.toString(), body, 4096 , TextField.ANY);
          ticker = new Ticker("BombusQD");
          t.setTicker(ticker);
       }
       else{
        int maxSize=4096;
        t=new TextBox(to.toString(), null, maxSize, TextField.ANY);
        try {
            maxSize=t.setMaxSize(4096);
            if (body!=null) {
                if (body.length()>maxSize)
                    body=body.substring(0, maxSize-1);
                t.setString(body);
            }
        } catch (Exception e) {}
       }
//#ifdef DETRANSLIT
//#         dt=DeTranslit.getInstance();
//#endif
        this.t=t;
        
        t.addCommand(cmdSend);
        t.addCommand(cmdInsMe);
//#ifdef SMILES
        t.addCommand(cmdSmile);
        t.addCommand(cmdSendEvil);
//#endif
        t.addCommand(cmdTranslate);

        if (to.origin>=Contact.ORIGIN_GROUPCHAT){
            t.addCommand(cmdInsNick);
        }
//#ifdef DETRANSLIT
//#         t.addCommand(cmdSendInTranslit);
//#         t.addCommand(cmdSendInDeTranslit);
//#endif
        t.addCommand(cmdSuspend);
        t.addCommand(cmdCancel);
//#ifdef ARCHIVE
//#ifdef PLUGINS
//#         if (StaticData.getInstance().Archive)
//#endif
        t.addCommand(cmdPaste);
//#endif
//#ifdef CLIPBOARD
//#         if (midlet.BombusQD.cf.useClipBoard) {
//#             clipboard=ClipBoard.getInstance();
//#             if (!clipboard.isEmpty())
//#                 t.addCommand(cmdPasteText);
//#         }
//#endif        
       
        if (to.origin==Contact.ORIGIN_GROUPCHAT){
                t.addCommand(cmdSubj);
        }
        
        if (to.lastSendedMessage!=null){
                t.addCommand(cmdLastMessage);
        }
                
        t.setCommandListener(this);
//#ifdef RUNNING_MESSAGE
//#         /*
//#        if(midlet.BombusQD.cf.useLowMemory_msgedit==false){ 
//#          if (thread==null) (thread=new Thread(this)).start() ;
//#        }
//#          */
//#         
//#else
    new Thread(this).start() ;
//#endif
        
        if(thread==null){
          thread=new Thread(this);
          thread.start();
          thread.run();
        }

        display.setCurrent(t);
        this.parentView=pView;
    }
    
    public void commandAction(Command c, Displayable d){

        body=t.getString();
        if (body.length()==0) body=null;
        
        int caretPos=t.getCaretPosition();


//#ifdef ARCHIVE
	if (c==cmdPaste) { 
                composing=false; 
                to.msgSuspended=body; 
                new ArchiveList(display, t , t.getCaretPosition(), 1, t,to); return;
        }
//#endif
//#ifdef CLIPBOARD
//#         if (c==cmdPasteText) { t.insert(clipboard.getClipBoard(), t.getCaretPosition()); return; }
//#endif        
        if (c==cmdInsMe) { t.insert("/me ", 0); return; }
        if (c==cmdLastMessage) {
            t.insert(to.lastSendedMessage,t.getCaretPosition());
            return;
        }
//#ifdef SMILES
        if (c==cmdSmile) { new SmilePicker(display, display.getCurrent(), t.getCaretPosition(), t); return; }
//#endif
//#ifndef WMUC
        if (c==cmdInsNick) { new AppendNick(display, display.getCurrent(), to, t.getCaretPosition(), t); return; }
//#endif
        if (c==cmdCancel) {
            composing=false;
            body=null;
            thread=null;
            //if(to.cList!=null && midlet.BombusQD.cf.module_cashe && to.msgs.size()>3){
              display.setCurrent( parentView );
            //}else{
            //  new C ontactMessageList(to,display);  
            //}
        }
        if (c==cmdSuspend) {
                composing=false; 
                to.msgSuspended=body; 
                body=null;
                display.setCurrent(parentView);
        }
        if(c==cmdTranslate){
          new TranslateSelect(display,parentView,to,body,"none",false,-1);
          body=null;
          return;
        }
        if (c==cmdSend){
            if(body==null){
                return;
            }else{
              to.msgSuspended=null; 
            }
        }
//#ifdef DETRANSLIT
//#         if (c==cmdSendInTranslit) {
//#             sendInTranslit=true;
//#         }
//#  
//#         if (c==cmdSendInDeTranslit) {
//#             sendInDeTranslit=true;
//#         }
//#endif
        if (c==cmdSubj) {
            if (body==null) return;
            subj=body;
            body=null; //"/me "+SR.MS_HAS_SET_TOPIC_TO+": "+subj;
        }
        
        if(c==cmdSendEvil){
           evil=true; 
        }        
        
//#ifdef RUNNING_MESSAGE
//#        if(to.msgSuspended==null){
//#             /*
//#         if(midlet.BombusQD.cf.useLowMemory_msgedit==false){ 
//#           if (display!=null) display.setCurrent(parentView);
//#           new Thread(this).start();
//#           return;            
//#          } else { 
//#              */
//#             send(body,subj);
//#             display.setCurrent(parentView);
//#       // }
//#       }
//#endif
      thread=null;
      System.out.println("thread null");
    }

    
    private void send(String body,String subj) {
        String comp=null;
        String id=String.valueOf((int) System.currentTimeMillis());
        if (body!=null)
            body=body.trim();
//#ifdef DETRANSLIT
//#         if (sendInTranslit==true) {
//#             if (body!=null)
//#                body=dt.translit(body);
//#             if (subj!=null )
//#                subj=dt.translit(subj);
//#         }
//#         if (sendInDeTranslit==true || midlet.BombusQD.cf.autoDeTranslit) {
//#             if (body!=null)
//#                body=dt.deTranslit(body);
//#             if (subj!=null )
//#                subj=dt.deTranslit(subj);
//#         }
//#endif
        if (body!=null || subj!=null ) {
            String from=midlet.BombusQD.sd.account.toString();
            Msg msg=new Msg(Msg.MESSAGE_TYPE_OUT,from,subj,body);
            if(evil){
               msg.body="(!)"+msg.body;                 
            }
            msg.id=id;

            if (to.origin!=Contact.ORIGIN_GROUPCHAT) {
                to.addMessage(msg);
                comp="active";
            }
        } else if (to.acceptComposing) comp=(composing)? "composing":"paused";
        if (!midlet.BombusQD.cf.eventComposing) comp=null;
        try { //??
            if (body!=null || subj!=null || comp!=null) {
                to.lastSendedMessage=body;
                 if(evil){
                  midlet.BombusQD.sd.roster.sendMessage(to, id, body, subj, comp,true);
                  to.msgSuspended=null;
                 }
                 else{
                  midlet.BombusQD.sd.roster.sendMessage(to, id, body, subj, comp,false);
                  to.msgSuspended=null;                  
                 }                
            }
        } catch (Exception e) { }
    }    

     public void run(){
         System.out.println("runned");
        /*
         String comp=null;
         String id=String.valueOf((int) System.currentTimeMillis());
         
         if (body!=null || subj!=null ) {
//#ifdef DETRANSLIT
//#         if (sendInTranslit==true) {
//#             if (body!=null)
//#                body=dt.translit(body);
//#             if (subj!=null )
//#                subj=dt.translit(subj);
//#         }
//#         if (sendInDeTranslit==true || midlet.BombusQD.cf.autoDeTranslit) {
//#             if (body!=null)
//#                body=dt.deTranslit(body);
//#             if (subj!=null )
//#                subj=dt.deTranslit(subj);
//#         }
//#endif
             String from=midlet.BombusQD.sd.account.toString();
             Msg msg=new Msg(Msg.MESSAGE_TYPE_OUT,from,subj,body);
                msg.id=id;
                if (to.origin!=Contact.ORIGIN_GROUPCHAT) {
                    to.addMessage(msg);
                    comp="active";
                }
            } else if (to.acceptComposing) comp=(composing)? "composing":"paused";
            if (!midlet.BombusQD.cf.eventComposing) comp=null;
            try {
                if (body!=null || subj!=null || comp!=null) {
                    to.lastSendedMessage=body;
                 if(evil){
                  midlet.BombusQD.sd.roster.sendMessage(to, id, body, subj, comp,true); 
                  to.msgSuspended=null;
                 }
                 else{
                  midlet.BombusQD.sd.roster.sendMessage(to, id, body, subj, comp,false);
                  to.msgSuspended=null;
                 } 
                }
            } catch (Exception e) { }
            try {
               ((ContactMessageList)parentView).forceScrolling();
            } catch (Exception e) { }
         */
        }
}

