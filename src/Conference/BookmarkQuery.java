/*
 * BookmarkQuery.java
 *
 * Created on 6.11.2006, 22:24
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
 *
 */

package Conference;

import Client.StaticData;
import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Iq;
import java.util.Vector;
import Client.Constants;
import util.StringLoader;

/**
 *
 * @author Evg_S 
 */
public class BookmarkQuery implements JabberBlockListener{

    public final static boolean SAVE=true;
    public final static boolean LOAD=false;
    
    public void destroy() {
    }
    /** Creates a new instance of BookmarkQurery */
    public BookmarkQuery(boolean saveBookmarks) {
        JabberDataBlock request=new Iq(null, (saveBookmarks)?Iq.TYPE_SET: Iq.TYPE_GET, "getbookmarks");
        JabberDataBlock query=request.addChildNs("query", "jabber:iq:private");

        JabberDataBlock storage=query.addChildNs("storage", "storage:bookmarks");
        if (saveBookmarks) {
          int size=midlet.BombusQD.sd.roster.bookmarks.size();        
            for(int i=0;i<size;i++){    
                storage.addChild( ((BookmarkItem)midlet.BombusQD.sd.roster.bookmarks.elementAt(i)).constructBlock() );              
            } 
        }
        midlet.BombusQD.sd.roster.theStream.send(request);
        request=null;
        query=null;
        storage=null;
    }
    
    
    public int blockArrived(JabberDataBlock data) {
        try {
            if (!(data instanceof Iq)) 
                return JabberBlockListener.BLOCK_REJECTED;
            if (data.getAttribute("id").equals("getbookmarks")) {
                JabberDataBlock storage=data.findNamespace("query", "jabber:iq:private"). findNamespace("storage", "storage:bookmarks");
                Vector bookmarks=new Vector(0);
		boolean autojoin=midlet.BombusQD.cf.autoJoinConferences && midlet.BombusQD.sd.roster.myStatus!=Constants.PRESENCE_INVISIBLE;

                try {
                    int size=storage.getChildBlocks().size();
                       for(int i=0;i<size;i++){    
                        BookmarkItem bm=new BookmarkItem((JabberDataBlock)storage.getChildBlocks().elementAt(i));
                        bookmarks.addElement(bm);
                        if (bm.autojoin && autojoin) {
                            ConferenceForm.join(bm.desc, bm.getJidNick(), bm.password, midlet.BombusQD.cf.confMessageCount);
                        }
                    }
                } catch (Exception e) { } //no any bookmarks

                if (bookmarks.isEmpty()) 
                    loadDefaults(bookmarks);
					
                midlet.BombusQD.sd.roster.bookmarks=bookmarks;
                //midlet.BombusQD.sd.roster.redraw();
                
                return JabberBlockListener.NO_MORE_BLOCKS;
            }
        } catch (Exception e) { }
        return JabberBlockListener.BLOCK_REJECTED;
    }

    private void loadDefaults(Vector bookmarks) {
	Vector defs[]=new StringLoader().stringLoader("/def_bookmarks.txt", 4);
        for (int i=0; i<defs[0].size(); i++) {
            String jid      =(String) defs[0].elementAt(i);
            String nick = null;    //=sd.account.getNick();//(String) defs[1].elementAt(i);
            String pass = null;//     =(String) defs[2].elementAt(i);
            String desc     =(String) defs[1].elementAt(i);
            if (desc==null) desc=jid;
            if (pass==null) pass="";
            if (nick==null) nick=midlet.BombusQD.sd.account.getNickName();
            BookmarkItem bm=new BookmarkItem(desc, jid, nick, pass, false);
            bookmarks.addElement(bm);
        }
    }
}
