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
//import ui.controls.ExTextBox;
import io.TranslateSelect;
//#ifdef STATS
//# import Statistic.Stats;
//#endif
//#ifdef ARCHIVE
import Archive.ArchiveList;
//#endif
import java.util.*;

/**
 *
 * @author Eugene Stahov,aqent
 */
public final class MessageEdit 
        implements CommandListener//, Runnable
{
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

    private Command cmdSend=new Command(SR.MS_SEND, Command.OK, 1);
//#ifdef SMILES
    private Command cmdSmile=new Command(SR.MS_ADD_SMILE, Command.SCREEN,2);
//#endif
    private Command cmdInsNick=new Command(SR.MS_NICKNAMES,Command.SCREEN,3);
    private Command cmdInsMe=new Command(SR.MS_SLASHME, Command.SCREEN, 4); ; // /me
//#ifdef DETRANSLIT
//#     private Command cmdSendInTranslit=new Command(SR.MS_TRANSLIT, Command.SCREEN, 5);
//#     private Command cmdSendInDeTranslit=new Command(SR.MS_DETRANSLIT, Command.SCREEN, 5);
//#endif
    private Command cmdLastMessage=new Command(SR.MS_PREVIOUS, Command.SCREEN, 9);
    private Command cmdSubj=new Command(SR.MS_SET_SUBJECT, Command.SCREEN, 10);
    private Command cmdSuspend=new Command(SR.MS_SUSPEND, Command.BACK,90);
    private Command cmdCancel=new Command(SR.MS_CANCEL, Command.SCREEN,99);
    private Command cmdSendEvil=new Command(SR.MS_SEND_EVIL_MSG, Command.SCREEN /*Command.SCREEN*/,229);    
    private Command cmdTranslate=new Command(SR.MS_TRANSLATE, Command.SCREEN /*Command.SCREEN*/,337);

//#ifdef ARCHIVE
    protected static Command cmdPaste=new Command(SR.MS_ARCHIVE, Command.SCREEN, 6);    
//#endif    
//#ifdef CLIPBOARD
//#     protected static Command cmdPasteText=new Command(SR.MS_PASTE, Command.SCREEN, 8);  
//#endif  
        
    
//#if TEMPLATES
//#     protected Command cmdTemplate=new Command(SR.MS_TEMPLATE, Command.SCREEN, 7); 
//#endif  

    public void replaceText(Contact to, String bodyNew, Displayable pView){
        this.parentView=pView;
        this.to = to;
         switch(midlet.BombusQD.cf.msgEditType){
            case 0: t.setString(bodyNew); break;
            case 1: textField.setString(bodyNew); break;
         }
         setText(bodyNew,to,pView);
    }
    
    public void setText(String body, Contact to, Displayable pView){
       this.body = body; 
       this.parentView=pView;
       this.to = to;
       switch(midlet.BombusQD.cf.msgEditType){
            case 0: 
                t.setTitle(to.toString());
                t.setString(body);
                if (Config.getInstance().capsState) t.setConstraints(TextField.INITIAL_CAPS_SENTENCE);
//#ifdef CLIPBOARD
//#                 if (midlet.BombusQD.cf.useClipBoard) {
//#                    if (!midlet.BombusQD.clipboard.isEmpty()) t.addCommand(cmdPasteText);
//#                 }
//#endif   
                  if (to.origin>=4) t.addCommand(cmdInsNick);//Contact.ORIGIN_GROUPCHAT==4
                  if (to.origin==4) t.addCommand(cmdSubj);
                  if (to.lastSendedMessage!=null) t.addCommand(cmdLastMessage);
                display.setCurrent(t);
                break;
            case 1:
                form.setTitle(to.toString());
                textField.setString(body);
//#ifdef CLIPBOARD
//#                 if (midlet.BombusQD.cf.useClipBoard) {
//#                   if (!midlet.BombusQD.clipboard.isEmpty()) form.addCommand(cmdPasteText);
//#                 }
//#endif  
                  if (to.origin>=4) form.addCommand(cmdInsNick);
                  if (to.origin==4) form.addCommand(cmdSubj);
                  if (to.lastSendedMessage!=null) form.addCommand(cmdLastMessage);
                display.setCurrent(form);
                break;
       }
    }

    
 //************OLD MsgEdit************   
    public TextBox t;
    public MessageEdit(Display display) {
       this.display = display;
       if (midlet.BombusQD.cf.runningMessage)
       {
          t=new TextBox("", body, 4096 , TextField.ANY);
          ticker = new Ticker("BombusQD");
          t.setTicker(ticker);
       }
       else {
        int maxSize=4096;
        t=new TextBox("", null, maxSize, TextField.ANY);
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
    }    
//************OLD MsgEdit************   
    
    
    
    private boolean evil=false;
    public Form form;
    public Ticker ticker = null; 
    public TextField textField = null;//default msgEdit
    
    public MessageEdit(Display display,boolean altMsgEdit) {
       this.display = display;
       form = new Form("");
//#ifdef DETRANSLIT
//#         dt=DeTranslit.getInstance();
//#endif

         //***************default MessageEdit Window based on TextField***************
            
          int maxSize = 4096;
          textField = new TextField("Message Edit", null, maxSize, TextField.ANY);
          try {
            maxSize = textField.setMaxSize(4096);
            if (body!=null) {
                if (body.length()>maxSize)
                    body = body.substring(0, maxSize-1);
                textField.setString(body);
            }
          } catch (Exception e) {}
          
         if (Config.getInstance().capsState) textField.setConstraints(TextField.INITIAL_CAPS_SENTENCE);
         form.append(textField);    

        
       if (midlet.BombusQD.cf.runningMessage) {
         ticker = new Ticker("BombusQD");
         form.setTicker(ticker);
       } 
        
       this.textField = textField;  
       
       if (Config.getInstance().capsState)
           textField.setConstraints(TextField.INITIAL_CAPS_SENTENCE);
       
       form.addCommand(cmdSend);
       form.addCommand(cmdInsMe);
//#ifdef SMILES
       form.addCommand(cmdSmile);
       form.addCommand(cmdSendEvil);
//#endif 
       form.addCommand(cmdTranslate);

//#ifdef DETRANSLIT
//#        form.addCommand(cmdSendInTranslit);
//#        form.addCommand(cmdSendInDeTranslit);
//#endif
       form.addCommand(cmdSuspend);
       form.addCommand(cmdCancel);
//#ifdef ARCHIVE
//#ifdef PLUGINS
//#         if (StaticData.getInstance().Archive)
//#endif
       form.addCommand(cmdPaste);
//#endif
       form.setCommandListener(this);
    }
    
    public void commandAction(Command c, Displayable d){
        if(to == null) return;
        if(midlet.BombusQD.cf.msgEditType>0){
          body=textField.getString();
        }else{
          body=t.getString();
        }
        
        if (body.length()==0) body=null;
        
        int caretPos=midlet.BombusQD.cf.msgEditType>0?textField.getCaretPosition():t.getCaretPosition();


//#ifdef ARCHIVE
	if (c==cmdPaste) { 
                composing=false; 
                to.msgSuspended=body; 
                if(midlet.BombusQD.cf.msgEditType>0){
                  new ArchiveList(display , textField.getCaretPosition(), 1, textField, null,  to); return;                    
                }else{
                  new ArchiveList(display , t.getCaretPosition(),1,null,t,to); return;
                }
        }
//#endif
//#ifdef CLIPBOARD
//#         if (c==cmdPasteText) { 
//#             if(midlet.BombusQD.cf.msgEditType>0){
//#               textField.insert(midlet.BombusQD.clipboard.getClipBoard(), textField.getCaretPosition() ); 
//#             }else{
//#               t.insert(midlet.BombusQD.clipboard.getClipBoard(), t.getCaretPosition() );                 
//#             }
//#             return;
//#         }
//#endif        
        if (c==cmdInsMe) {
            if(midlet.BombusQD.cf.msgEditType>0){
              textField.insert("/me ", 0);
            }else{
              t.insert("/me ", 0);                
            }
            return;
        }
        if (c==cmdLastMessage) {
            if(midlet.BombusQD.cf.msgEditType>0){
              textField.insert(to.lastSendedMessage,textField.getCaretPosition());
            }else{
              t.insert(to.lastSendedMessage,t.getCaretPosition());
            }
            return;
        }
//#ifdef SMILES
        if (c==cmdSmile) { 
            if(midlet.BombusQD.cf.msgEditType>0){
              new SmilePicker(display, display.getCurrent(), textField.getCaretPosition(), textField, null); 
            }else{
              new SmilePicker(display, display.getCurrent(), t.getCaretPosition(), null, t); 
            }
            return; 
        }
//#endif
//#ifndef WMUC
        if (c==cmdInsNick) {
            if(midlet.BombusQD.cf.msgEditType>0){
              new AppendNick(display, display.getCurrent(), to, textField.getCaretPosition(), textField, null);  
            }else{
              new AppendNick(display, display.getCurrent(), to, t.getCaretPosition(), null , t);  
            }
            return;
        }
//#endif
        

        if (c==cmdCancel) {
            composing=false;
            body=null;
            if(to.msgSuspended!=null) to.msgSuspended=null;
            //thread=null;
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
      //thread=null;
      //System.out.println("thread null");
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
}

