/*
 * MyMenu.java
 *
 * Created on 9.07.2008, 18:17
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

package Menu;

import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import ui.ImageList;

/**
 *
 * @author ad
 */
public class MyMenu 
     extends Menu       
{
    private MenuListener ml;
    private Vector commands;

    /** Creates a new instance of MyMenu */

    public MyMenu(Display display, Displayable parentView, MenuListener menuListener, String caption, ImageList il, Vector menuCommands) {
        super(caption, il,null);
        this.ml=menuListener;
        this.commands=menuCommands;

        this.parentView=parentView;
        int size = commands.size();
        for (int index=0; index<size; index++) {
            Command c=(Command)commands.elementAt(index);
            addItem(c.getName(), index, c.getImg(), c.inCommand);
        }
        attachDisplay(display);
    }
    
    public void eventOk(){
	destroyView();
        MenuItem me=(MenuItem) getFocusedObject();
	if (me==null)  return;
        ml.commandAction(getCommand(me.index), parentView);
    }
    
    public Command getCommand(int index) {
         int size=commands.size();        
            for(int i=0;i<size;i++){    
              Command cmd =(Command)commands.elementAt(i);
              if (cmd.getName().equals(getFocusedObject().toString()))
                return cmd;             
            }    
        return null;
    }
}
