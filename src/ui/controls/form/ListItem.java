/*
 * ListItem.java
 *
 * Created on 25.05.2008, 16:38
 *
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
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

package ui.controls.form; 

import ui.IconTextElement;
import images.RosterIcons;
import Colors.ColorTheme;
/**
 *
 * @author ad
 */
public class ListItem 
        extends IconTextElement {

    private String text;
    String name;
    int index;
    int icon;
    public int transport;
        
    public ListItem(String text,int icon, String name) {
       super(RosterIcons.getInstance());
       this.icon=icon;
       if(name.length()>2){
           this.name=name;
       }
       this.text=text;
    }
    public int getVWidth(){ 
        return -1;
    }
    public ListItem(String text) {
        super(null);
        this.text=text;
    }
    
    public int getColor(){ return ColorTheme.getInstance().getColor(ColorTheme.DISCO_CMD); }
    public int getImageIndex() { return RosterIcons.getInstance().getTransportIndex(name); }

    public String toString() { return text; }
    
    
    
    
}