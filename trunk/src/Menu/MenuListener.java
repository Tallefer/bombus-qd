/*
 * MenuListener.java
 *
 * Created on 9.07.2008, 17:54
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

import javax.microedition.lcdui.Displayable;
/**
 *
 * @author ad
 */
public interface MenuListener {
    public void commandAction(Command command, Displayable displayable);
    //public void addCommand(Command command);
    //public void removeCommand(Command command);
    //public void setCommandListener(MenuListener menuListener);
    //public void showMenu();
    //public Command getCommand(int index);
}
