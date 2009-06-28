/*
 * RosterItemActions.java
 *
 * Created on 11.12.2005, 19:05
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

package Menu;
import Client.*;
import Colors.ColorTheme;
//#ifndef WMUC
import Conference.ConferenceGroup;
import Conference.InviteForm;
import Conference.MucContact;
import Conference.QueryConfigForm;
import Conference.affiliation.ConferenceQuickPrivelegeModify;
//#ifdef REQUEST_VOICE
//# import Conference.QueryRequestVoice;
//#endif
import Conference.affiliation.Affiliations;
//#endif
//#ifdef SERVICE_DISCOVERY
import ServiceDiscovery.ServiceDiscovery;
//#endif

import images.ActionsIcons;

//#if FILE_TRANSFER
import io.file.transfer.TransferImage;
import io.file.transfer.TransferSendFile;
//#endif

import ui.controls.AlertBox;

import xmpp.extensions.IqLast;
import xmpp.extensions.IqPing;
import xmpp.extensions.IqTimeReply;
import xmpp.extensions.IqVersionReply;
import xmpp.extensions.SoftwareInfo;

import com.alsutton.jabber.datablocks.Presence;

import java.util.Enumeration;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
//#ifdef CLIPBOARD
//# import util.ClipBoard;
//#endif

import VCard.VCard;
import VCard.VCardEdit;
import VCard.VCardView;
import ui.ImageList;
//#ifdef PEP
//# import Mood.EventPublish;
//#endif
import ui.MainBar;
import com.alsutton.jabber.datablocks.*;
import com.alsutton.jabber.*;
//#ifdef CHECKERS
//# import xmpp.extensions.games.*;
//#endif

/**
 *
 * @author EvgS
 */
public class RosterItemActions extends Menu {

    Object item;
//#ifdef CLIPBOARD
//#     private ClipBoard clipboard=ClipBoard.getInstance();
//#endif
    private int action;
    
    StaticData sd=StaticData.getInstance();

    ActionsIcons menuIcons=ActionsIcons.getInstance();
    
    
//#ifdef PEP
//#     EventPublish publishEvent = new EventPublish();
//#endif        
    
    Config cf=Config.getInstance();
    
    /** Creates a new instance of RosterItemActions */
    public RosterItemActions(Display display, Displayable pView, Object item, int action) {
        super(item.toString(), ActionsIcons.getInstance(),null);

        this.item=item;
        this.action=action;
        if (!sd.roster.isLoggedIn()) return;

        if (item==null) return;
        boolean isContact=( item instanceof Contact );


        //System.out.println(contact.bareJid);
	if (isContact) {
	    Contact contact=(Contact)item;            
            MainBar mainbar=new MainBar(contact);
            setMainBarItem(mainbar);
 
        // if(contact.bareJid.equals(StaticData.HELPER_CONTACT)==false){            
	    if (contact.getGroupType()==Groups.TYPE_TRANSP) {
		addItem(SR.MS_LOGON,5, menuIcons.ICON_ON);
		addItem(SR.MS_LOGOFF,6, menuIcons.ICON_OFF);
                addItem(SR.MS_RESOLVE_NICKNAMES, 7, menuIcons.ICON_NICK_RESOLVE);
//#if CHANGE_TRANSPORT
//#ifdef PLUGINS
//#                 if (sd.ChangeTransport);
//#endif
//#                     addItem("Change transport", 915);
//#endif
	    }


            switch (contact.getCheckers()) {
                case -1:
                    addItem(SR.GAME_SEND_GAME,220, 0x06);
                    break;
                case 0:
                    addItem(SR.GAME_APPLIED,222, 0x06);
                    break;
                case 1:
                    addItem(SR.GAME_STOP,221, 0x01);
                    break;
                case 2:
                    addItem(SR.GAME_RESUME,223, 0x02);
                    break;
            }

            
	    addItem(SR.MS_VCARD,1, menuIcons.ICON_VCARD);
	    if(cf.difficulty_level>=1) {
                addItem(SR.MS_FEATURES,250,menuIcons.ICON_INFO);
            }
//#ifdef POPUPS
            addItem(SR.MS_INFO,86, menuIcons.ICON_INFO);
//#endif
            addItem(SR.MS_CLIENT_INFO,0, menuIcons.ICON_VERSION);

//#ifdef SERVICE_DISCOVERY
	    addItem(SR.MS_COMMANDS,30, menuIcons.ICON_COMMAND);
//#endif
//#ifdef CLIPBOARD
//#             if (cf.useClipBoard) {
//#                 if (!clipboard.isEmpty())
//#                     addItem(SR.MS_SEND_BUFFER,914, menuIcons.ICON_SEND_BUFFER);
//#                 if (contact.getGroupType()!=Groups.TYPE_SELF)
//#                     addItem(SR.MS_COPY_JID,892, menuIcons.ICON_COPY_JID);
//#             }
//#endif
            addItem(SR.MS_SEND_COLOR_SCHEME, 912, menuIcons.ICON_SEND_COLORS);
            if (contact.status<Presence.PRESENCE_OFFLINE) {
                addItem(SR.MS_TIME,891, menuIcons.ICON_TIME);
                addItem(SR.MS_IDLE,889, menuIcons.ICON_IDLE);
                addItem(SR.MS_PING,893, menuIcons.ICON_PING);
            }
	    
	    if (contact.getGroupType()!=Groups.TYPE_SELF && contact.getGroupType()!=Groups.TYPE_SEARCH_RESULT && contact.origin<Contact.ORIGIN_GROUPCHAT) {
		if (contact.status<Presence.PRESENCE_OFFLINE) {
                    addItem(SR.MS_ONLINE_TIME,890, menuIcons.ICON_ONLINE);    
                } else {
                    addItem(SR.MS_SEEN,894, menuIcons.ICON_ONLINE); 
                }
                if (contact.getGroupType()!=Groups.TYPE_TRANSP) {
                    addItem(SR.MS_EDIT,2, menuIcons.ICON_RENAME);
                }
		addItem(SR.MS_SUBSCRIPTION,3, menuIcons.ICON_SUBSCR);
//		addItem(SR.MS_MOVE,1003, menuIcons.ICON_MOVE);
		addItem(SR.MS_DELETE, 4, menuIcons.ICON_DELETE);
		addItem(SR.MS_DIRECT_PRESENCE,45, menuIcons.ICON_SET_STATUS);
	    }
         //}
	    if (contact.origin==Contact.ORIGIN_GROUPCHAT) 
                return;
//#ifndef WMUC
            boolean onlineConferences=true;

            if (contact instanceof MucContact) {
                MucContact selfContact= ((ConferenceGroup) contact.group).selfContact;
                MucContact mc=(MucContact) contact;
                
                
                int myAffiliation=selfContact.affiliationCode;
                if (myAffiliation==MucContact.AFFILIATION_OWNER) 
                    myAffiliation++; // allow owner to change owner's affiliation

            
                //addItem(SR.MS_TIME,891); 
                
                //invite
                if (mc.realJid!=null) {
                    //if (onlineConferences)
                    if (selfContact.roleCode==MucContact.ROLE_MODERATOR || myAffiliation==MucContact.AFFILIATION_MEMBER){
                        addItem(SR.MS_INVITE,40, menuIcons.ICON_INVITE);
                    }
                }
                //invite                
                
                if (selfContact.roleCode==MucContact.ROLE_MODERATOR) {
                    if(mc.roleCode<MucContact.ROLE_MODERATOR)
                        addItem(SR.MS_KICK,8, menuIcons.ICON_KICK);
                    
                    if (myAffiliation>=MucContact.AFFILIATION_ADMIN && mc.affiliationCode<myAffiliation)
                        addItem(SR.MS_BAN,9, menuIcons.ICON_BAN);
                    
                    if (mc.affiliationCode<MucContact.AFFILIATION_ADMIN) 
                        /* 5.1.1 *** A moderator MUST NOT be able to revoke voice privileges from an admin or owner. */ 
                    if (mc.roleCode==MucContact.ROLE_VISITOR) addItem(SR.MS_GRANT_VOICE,31, menuIcons.ICON_VOICE);
                    else addItem(SR.MS_REVOKE_VOICE, 32, menuIcons.ICON_DEVOICE);
                }
//#ifdef REQUEST_VOICE
//# 		if (selfContact.roleCode==MucContact.ROLE_VISITOR) {
//#                     //System.out.println("im visitor");
//#                     if (mc.roleCode==MucContact.ROLE_MODERATOR) {
//#                         //System.out.println(mc.getJid()+" is a moderator");
//#                         addItem(SR.MS_REQUEST_PARTICIPANT_ROLE,39);
//#                     }
//#  		}
//#endif
                if (myAffiliation>=MucContact.AFFILIATION_ADMIN) {
                    // admin use cases
                    
                    //roles
                    if (mc.affiliationCode<MucContact.AFFILIATION_ADMIN) 
                        /* 5.2.1 ** An admin or owner MUST NOT be able to revoke moderation privileges from another admin or owner. */ 
                    if (mc.roleCode==MucContact.ROLE_MODERATOR) 
                        addItem(SR.MS_REVOKE_MODERATOR,31, menuIcons.ICON_MEMBER);
                    else 
                        addItem(SR.MS_GRANT_MODERATOR,33, menuIcons.ICON_ADMIN);
                    
                    //affiliations
                    if (mc.affiliationCode<myAffiliation) {
                        if (mc.affiliationCode!=MucContact.AFFILIATION_NONE) 
                            addItem(SR.MS_UNAFFILIATE,36, menuIcons.ICON_DEMEMBER);
                        /* 5.2.2 */
                        if (mc.affiliationCode!=MucContact.AFFILIATION_MEMBER) 
                            addItem(SR.MS_GRANT_MEMBERSHIP,35, menuIcons.ICON_MEMBER);
                    }
                    
                    
               //m.addItem(new MenuItem("Set Affiliation",15));
                }
                if (myAffiliation>=MucContact.AFFILIATION_OWNER) {
                    // owner use cases
                    if (mc.affiliationCode!=MucContact.AFFILIATION_ADMIN) 
                        addItem(SR.MS_GRANT_ADMIN,37, menuIcons.ICON_ADMIN);
                    
                    if (mc.affiliationCode!=MucContact.AFFILIATION_OWNER) 
                        addItem(SR.MS_GRANT_OWNERSHIP,38, menuIcons.ICON_OWNER);
                }
                if (mc.realJid!=null && mc.status<Presence.PRESENCE_OFFLINE) {
                    
                }
            } else if (contact.getGroupType()!=Groups.TYPE_TRANSP && contact.getGroupType()!=Groups.TYPE_SEARCH_RESULT) {
                // usual contact - invite item check
                 if (onlineConferences) 
                     addItem(SR.MS_INVITE,40, menuIcons.ICON_INVITE);
            }
//#endif
//#if (FILE_IO && FILE_TRANSFER)
            if (contact.getGroupType()!=Groups.TYPE_TRANSP && cf.fileTransfer) 
                if (contact!=sd.roster.selfContact()) {
//#ifdef PLUGINS
//#                     if (sd.FileTransfer)
//#endif
                        addItem(SR.MS_SEND_FILE, 50, menuIcons.ICON_SEND_FILE);
                }
            
//#endif
//#if FILE_TRANSFER
            if (contact.getGroupType()!=Groups.TYPE_TRANSP && cf.fileTransfer) {
                if (contact!=sd.roster.selfContact()) {
                    String cameraAvailable=System.getProperty("supports.video.capture");
                    if (cameraAvailable!=null) if (cameraAvailable.startsWith("true")) {
//#ifdef PLUGINS
//#                         if (sd.ImageTransfer)
//#endif
                            addItem(SR.MS_SEND_PHOTO, 51, menuIcons.ICON_SEND_FILE);
                    }
                }
            }
//#endif
        } else {
	    Group group=(Group)item;
	    if (group.type==Groups.TYPE_SEARCH_RESULT)
		addItem(SR.MS_DISCARD,21, menuIcons.ICON_BAN);
//#ifndef WMUC
	    if (group instanceof ConferenceGroup) {
		MucContact self=((ConferenceGroup)group).selfContact;
                
		addItem(SR.MS_LEAVE_ROOM,22, menuIcons.ICON_LEAVE);
                
                if (self.status>=Presence.PRESENCE_OFFLINE) {// offline or error
		    addItem(SR.MS_REENTER,23, menuIcons.ICON_CHANGE_NICK);
                } else {
                    addItem(SR.MS_DIRECT_PRESENCE,46, menuIcons.ICON_SET_STATUS);
                    addItem(SR.MS_CHANGE_NICKNAME,23, menuIcons.ICON_CHANGE_NICK);
		    if (self.affiliationCode>=MucContact.AFFILIATION_OWNER) {
			addItem(SR.MS_CONFIG_ROOM,10, menuIcons.ICON_CONFIGURE);
                        addItem(SR.MS_DESTROY_ROOM,49,menuIcons.ICON_OUTCASTS);
                    }
		    if (self.affiliationCode>=MucContact.AFFILIATION_ADMIN) {
			addItem(SR.MS_OWNERS,11, menuIcons.ICON_OWNERS);
			addItem(SR.MS_ADMINS,12, menuIcons.ICON_ADMINS);
			addItem(SR.MS_MEMBERS,13, menuIcons.ICON_MEMBERS);
			addItem(SR.MS_BANNED,14, menuIcons.ICON_OUTCASTS);
 		    }
 		}
	    } else {
//#endif
                if (    group.type!=Groups.TYPE_IGNORE
                        && group.type!=Groups.TYPE_NOT_IN_LIST
                        && group.type!=Groups.TYPE_SEARCH_RESULT
                        && group.type!=Groups.TYPE_SELF
                        && group.type!=Groups.TYPE_TRANSP)
                {
                    addItem(SR.MS_RENAME,1001, menuIcons.ICON_RENAME);
                    addItem(SR.MS_DELETE, 1004, menuIcons.ICON_DELETE);
                }
//#ifndef WMUC
            }
//#endif
 	}
	if (getItemCount()>0) {
            if (action<0) {
                attachDisplay(display);
                this.parentView=pView;
            } else try {
                this.display=display; // to invoke dialog Y/N
                doAction(action);
            } catch (Exception e) { 
                //e.printStackTrace();
            }
        }
        moveCursorTo(Config.getInstance().cursorPos[1]);            
     }
     
     public void eventOk(){
         try {
             MenuItem me=(MenuItem) getFocusedObject();
            destroyView();
            if (me==null) return;
            int index=action=me.index;
            doAction(index);
        } catch (Exception e) { }
    }

    private void doAction(final int index) {
        boolean isContact=( item instanceof Contact );
        Contact c = null;
        Group g = null;
        if (isContact) c=(Contact)item; else g=(Group) item;
        
        String to=null;
        Config.getInstance().cursorPos[1]=cursor;
        if (isContact) to=(index<3)? c.getJid() : c.bareJid;
            switch (index) {
//#ifdef CHECKERS
//#                 
//#                 case 220:
//#                     sd.roster.theStream.send(new IqCheckers(c.getJid(), true));
//#                     new Checkers(display, c, true);  //������ wait ������                 
//#                     break;
//#                 case 221:
//#                     c.setCheckers(-1);
//#                     sd.roster.theStream.send(new IqCheckers(c.getJid()));
//#                     break;  
//#                 case 222:
//#                     JabberDataBlock jdb = new Iq(c.getJid(), 2, "checkers");
//#                     jdb.addChildNs("query", "checkers").setAttribute("state", "game_ok");
//#                     c.setIncoming(Contact.INC_NONE);
//#                     new Checkers(display, c, false);
//#                     sd.roster.theStream.send(jdb);
//#                     break;
//#                 case 223: //resume:
//#                     //new Checkers(display, c, false, false);
//#                     break;                              
//#endif                
                case 0: // version
                    sd.roster.setQuerySign(true);
                    cf.flagQuerySign=true;
                    sd.roster.theStream.send(IqVersionReply.query(to));
                    break;
                case 86: // info
//#ifdef POPUPS
                    sd.roster.showInfo();
//#endif
                    break;
                case 250: //FEATURES
                   sd.roster.setQuerySign(true);
                   sd.roster.theStream.addBlockListener(new SoftwareInfo());
                   sd.roster.theStream.send(SoftwareInfo.querySend(c.getJid()));
                    break;
                case 1: // vCard
                    if (c.vcard!=null) {
                        if (c.getGroupType()==Groups.TYPE_SELF)
                            new VCardEdit(display, sd.roster, c.vcard);
                        else
                            new VCardView(display, sd.roster, c);
                        return;
                    }
                    VCard.request(c.bareJid, c.getJid());
                    break;
                case 2:
                    new ContactEdit(display, sd.roster, c );
                    return; //break;
                case 3: //subscription
                    new SubscriptionEdit(display, sd.roster, c);
                    return; //break;
                case 4:
                    new AlertBox(SR.MS_DELETE_ASK, c.getNickJid(), display,  sd.roster) {
                        public void yes() {
                            sd.roster.deleteContact((Contact)item);
                        }
                        public void no() { }
                    };
                    return;
                case 6: // logoff
                    sd.roster.blockNotify(-111,10000); //block sounds to 10 sec
                    Presence presence = new Presence(
                    Presence.PRESENCE_OFFLINE, -1, "", null);
                    presence.setTo(c.getJid());
                    sd.roster.theStream.send( presence );
                    presence=null;
                    break;
                case 5: // logon
                    sd.roster.blockNotify(-111,10000); //block sounds to 10 sec
                    //querysign=true; displayStatus();
                    Presence presence2 = new Presence(sd.roster.myStatus, 0, "", null);
                    presence2.setTo(c.getJid());
                    sd.roster.theStream.send( presence2 );
                    presence2=null;                    
                    break;
                case 7: // Nick resolver
                    sd.roster.resolveNicknames(c.bareJid);
                    break;
//#if CHANGE_TRANSPORT
//#                 case 915: // change transport
//#                     new ChangeTransport(display, c.bareJid);
//#                     return;
//#endif
                case 21:
                    sd.roster.cleanupSearch();
                    break;
//#ifdef SERVICE_DISCOVERY
                case 30:
                    new ServiceDiscovery(display, c.getJid(), "http://jabber.org/protocol/commands", false);
                    return;
//#endif
/*                case 1003: 
                    new RenameGroup(display, null, c);
                    return;
 */
                case 889: //idle
                    sd.roster.setQuerySign(true);
                    sd.roster.theStream.send(IqLast.query(c.getJid(), "idle"));
                    break;
                case 890: //online
                    sd.roster.setQuerySign(true);
                    sd.roster.theStream.send(IqLast.query(c.bareJid, "online_"+c.getResource()));
                    break;
                case 894: //seen
                    sd.roster.setQuerySign(true);
                    sd.roster.theStream.send(IqLast.query(c.bareJid, "seen"));
                    break;
                case 891: //time
                    sd.roster.setQuerySign(true);
                    sd.roster.theStream.send(IqTimeReply.query(c.getJid()));
                    break;
//#ifdef PEP                    
//#                     /*
//#                 case 911:
//#                    new AlertBox("Send form", "Send my Moods+Actions+Music(PEP) to all contacts(YES-recommended)?", display, this) {
//#                        public void yes() {
//#                           publishEvent.publishActivity(cf.actCat,cf.actDescr,cf.actText);
//#                           publishEvent.publishMood(cf.moodText,cf.moodName);
//#                        }
//#                        public void no() { }
//#                   };  
//#                   break;
//#                      */
//# 
//#                 case 913:
//#                           publishEvent.stopMusic();
//#                           Config.getInstance().track=0;
//#                           c.pepTune=false;
//#                           c.pepTuneText=null;
//#                   break;  
//#endif                    
                    
//#ifdef CLIPBOARD
//#                 case 892: //Copy JID
//#ifndef WMUC
//#                     if (!(c instanceof MucContact)) {
//#endif
//#                         try {
//#                             if (c.bareJid!=null)
//#                                 clipboard.setClipBoard(c.bareJid);
//#                         } catch (Exception e) {/*no messages*/}
//#ifndef WMUC
//#                     }
//#endif
//#                     break;
//#endif
                case 893: //ping
                    try {
                        sd.roster.setQuerySign(true);
                        //c.setPing();
                        sd.roster.theStream.send(IqPing.query(c.getJid(), null));
                    } catch (Exception e) {/*no messages*/}
                    break;
                case 912: //send color scheme
                    String from=sd.account.toString();
                    String body=ColorTheme.getInstance().getSkin();
                    String id=String.valueOf((int) System.currentTimeMillis());

                    try {
                        sd.roster.sendMessage(c, id, body, null, null,false);
                        c.addMessage(new Msg(Msg.MESSAGE_TYPE_OUT,from,null,"scheme sended"));
                    } catch (Exception e) {
                        c.addMessage(new Msg(Msg.MESSAGE_TYPE_OUT,from,null,"scheme NOT sended"));
                        //e.printStackTrace();
                    }
                break;
//#ifdef CLIPBOARD
//#                 case 914: //send message from buffer
//#                     String body2=clipboard.getClipBoard();
//#                     if (body2.length()==0)
//#                         return;
//# 
//#                     String from2=sd.account.toString();
//# 
//#                     String id2=String.valueOf((int) System.currentTimeMillis());
//#                     Msg msg2=new Msg(Msg.MESSAGE_TYPE_OUT,from2,null,body2);
//#                     msg2.id=id2;
//#                     msg2.itemCollapsed=true;
//# 
//#                     try {
//#                         if (body2!=null && body2.length()>0) {
//#                             sd.roster.sendMessage(c, id2, body2, null, null,false);
//#                             
//#                             if (c.origin<Contact.ORIGIN_GROUPCHAT) c.addMessage(msg2);
//#                         }
//#                     } catch (Exception e) {
//#                         c.addMessage(new Msg(Msg.MESSAGE_TYPE_OUT,from2,null,"clipboard NOT sended"));
//#                     }
//#                     break;
//#endif
//#ifndef WMUC
                case 40: //invite
                    //new InviteForm(c, display);
                    if (c.jid!=null) {
                        new InviteForm(display, sd.roster, c);
                    } else {
                        MucContact mcJ=(MucContact) c;

                        if (mcJ.realJid!=null) {
                            boolean onlineConferences=false;
                            for (Enumeration cJ=sd.roster.getHContacts().elements(); cJ.hasMoreElements(); ) {
                                try {
                                    MucContact mcN=(MucContact)cJ.nextElement();
                                    if (mcN.origin==Contact.ORIGIN_GROUPCHAT && mcN.status==Presence.PRESENCE_ONLINE)
                                        onlineConferences=true;
                                } catch (Exception e) {}
                            }
                            if (onlineConferences) new InviteForm(display, sd.roster, mcJ);
                        }
                    }
                    return;
//#endif
                case 45: //direct presence
                    new StatusSelect(display, sd.roster, c);
                    return;
//#if (FILE_IO && FILE_TRANSFER)
                case 50: //send file
                    new TransferSendFile(display, sd.roster, c.getJid());
                    return;
//#endif
//#if FILE_TRANSFER
                case 51: //send photo
                    new TransferImage(display, sd.roster, c.getJid());
                    return;
//#endif
            }
//#ifndef WMUC
            if (c instanceof MucContact || g instanceof ConferenceGroup) {
                MucContact mc=(MucContact) c;
                
                String roomJid="";
                if (g instanceof ConferenceGroup) {
                    roomJid=((ConferenceGroup)g).confContact.getJid();
                }
                
                String myNick="";
                if (c instanceof MucContact) {
                    myNick=((ConferenceGroup)c.group).selfContact.getName();
                }
                
                switch (index) { // muc contact actions
                    case 10: // room config
                        new QueryConfigForm(display,roomJid);
                        break;
                    case 11: // owners
                    case 12: // admins
                    case 13: // members
                    case 14: // outcasts
                        new Affiliations(display, parentView, roomJid, (short)(index-10));
                        return;
                    case 22:
                        sd.roster.leaveRoom( g );
                        break;
                    case 23:
                        sd.roster.reEnterRoom( g );
                        return; //break;
                    case 46: //conference presence
                        new StatusSelect(display, sd.roster, ((ConferenceGroup)g).confContact);
                        return;
                    case 49:                  
                        new CommandForm(display,parentView,0,"Form",item,null);  
                        break;
                     case 8: // kick
                        new ConferenceQuickPrivelegeModify(display, sd.roster, mc, ConferenceQuickPrivelegeModify.KICK,myNick);
                        return;
                     case 9: // ban
                        new ConferenceQuickPrivelegeModify(display, sd.roster, mc, ConferenceQuickPrivelegeModify.OUTCAST,myNick);
                        return;
                     case 31: //grant voice and revoke moderator
                        new ConferenceQuickPrivelegeModify(display, sd.roster, mc, ConferenceQuickPrivelegeModify.PARTICIPANT,myNick); //
                        return;
                     case 32: //revoke voice
                        new ConferenceQuickPrivelegeModify(display, sd.roster, mc, ConferenceQuickPrivelegeModify.VISITOR,myNick);
                        return;
                     case 33: //grant moderator
                        new ConferenceQuickPrivelegeModify(display, sd.roster, mc, ConferenceQuickPrivelegeModify.MODERATOR,null); //
                        return;
                    case 35: //grant membership and revoke admin
                        new ConferenceQuickPrivelegeModify(display, sd.roster, mc, ConferenceQuickPrivelegeModify.MEMBER,null); //
                        return;
                    case 36: //revoke membership
                        new ConferenceQuickPrivelegeModify(display, sd.roster, mc, ConferenceQuickPrivelegeModify.NONE,null); //
                         return;
                    case 37: //grant admin and revoke owner
                        new ConferenceQuickPrivelegeModify(display, sd.roster, mc, ConferenceQuickPrivelegeModify.ADMIN,null); //
                        return;
                    case 38: //grant owner
                        new ConferenceQuickPrivelegeModify(display, sd.roster, mc, ConferenceQuickPrivelegeModify.OWNER,null); //
                        return;
//#ifdef REQUEST_VOICE		 
//#                 case 39: //request voice
//#                     new QueryRequestVoice(display, sd.roster, mc, ConferenceQuickPrivelegeModify.PARTICIPANT);
//#                     return;
//#endif
//#ifdef CLIPBOARD
//#                     case 892: //Copy JID
//#                         try {
//#                             if (mc.realJid!=null)
//#                                 clipboard.setClipBoard(mc.realJid);
//#                         } catch (Exception e) {}
//#                         break;
//#endif
             }
        } else {
//#endif
            Group sg=(Group)item;

            if (       sg.type!=Groups.TYPE_IGNORE 
                    && sg.type!=Groups.TYPE_NOT_IN_LIST
                    && sg.type!=Groups.TYPE_SEARCH_RESULT
                    && sg.type!=Groups.TYPE_SELF
                    && sg.type!=Groups.TYPE_TRANSP)
            {
                switch (index) {
                    case 1001: //rename
                        new RenameGroup(display, sd.roster, sg/*, null*/);
                        return;
                    case 1004: //delete
                        new AlertBox(SR.MS_DELETE_GROUP_ASK, sg.getName(), display, sd.roster) {
                            public void yes() {
                                sd.roster.deleteGroup((Group)item);
                            }
                            public void no() {
                            }
                        };
                        return;
                }
            }
//#ifndef WMUC
         }
//#endif
     }
}