/*
 * IqQueryRoster.java
 *
 * Created on 12.01.2005, 0:17
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

package xmpp.extensions;

import com.alsutton.jabber.*;
import com.alsutton.jabber.datablocks.*;

/**
 * Class representing the iq message block
 */

public class IqQueryRoster extends Iq 
{
  /**
   * Constructor including an Attribute list
   *
   * @param _parent The parent of this datablock
   * @param _attributes The list of element attributes
   */

  public IqQueryRoster() {
    super(null, Iq.TYPE_GET, "getros" );
    addChildNs("query", "jabber:iq:roster" );
  }
  
  /** add to roster*/
  public IqQueryRoster(String jid, String name, String group, String subscription) {
    super(null, Iq.TYPE_SET, "addros");
    JabberDataBlock qB = addChildNs("query", "jabber:iq:roster" );
    JabberDataBlock item= qB.addChild("item",null);
    item.setAttribute("jid", jid);
    if (name!=null)
        item.setAttribute("name", name);
    item.setAttribute("subscription", subscription);
    if (group!=null && group.length()>0) //patch by Tishka17
        item.addChild("group",group);
  }
}
