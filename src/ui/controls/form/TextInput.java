/*
 * TextInput.java
 *
 * Created on 19.05.2008, 23:01 
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

import Colors.ColorTheme;
import Fonts.FontCache;
import io.NvStorage;
import java.io.DataInputStream;
import java.io.EOFException;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import ui.IconTextElement;
import ui.VirtualList;

/**
 *
 * @author ad
 */
public class TextInput 
        extends IconTextElement {

    private String text="";
    private String caption="";
    private Display display;
    
    public String id;

    private int boxType;
    
    private Font font;
    private int fontHeight;
    
    private Font captionFont;
    private int captionFontHeight;

    private int itemHeight=0;

    private int colorItem;
    private int colorBorder;
    private int colorBGnd;
    
    /**
     * Creates a new instance of TextInput
     */
    public TextInput(Display display, String caption, String text, String id, int boxType) {
        super(null);
        this.display=display;
        this.caption=(caption==null)?"":caption;
        this.id=id;
        this.boxType=boxType;

        colorItem=ColorTheme.getColor(ColorTheme.CONTROL_ITEM);
        colorBorder=ColorTheme.getColor(ColorTheme.CURSOR_OUTLINE);
        colorBGnd=ColorTheme.getColor(ColorTheme.LIST_BGND);
        
        font=FontCache.getFont(false, FontCache.roster);
        fontHeight=font.getHeight();
        itemHeight=fontHeight;
        
        if (caption!=null) {
            captionFont=FontCache.getFont(true, FontCache.msg);
            captionFontHeight=captionFont.getHeight();
            itemHeight+=captionFontHeight;
        }
        if (text==null && id!=null) {
            String tempText="";
            try {
                DataInputStream is=NvStorage.ReadFileRecord(id, 0);
                try { 
                    tempText=is.readUTF();
                } catch (EOFException e) { 
                    is.close(); 
                    is = null;
                }
            } catch (Exception e) {/* no history available */}
            this.text=(tempText==null)?"":tempText;
        } else {
            this.text=(text==null)?"":text;
        }
    }
    
    public int getCaptionLength() {
        if (caption==null) return 0;
        if (caption.equals("")) return 0;
        return captionFont.stringWidth(caption);
    }

    public int getTextLength() {
        if (text==null) return 0;
        if (text.equals("")) return 0;
        return font.stringWidth(text);
    }
    
    //public String toString() { return (getCaptionLength()>getTextLength())?caption:getValue(); }
    public String toString() { return (0==getTextLength())?caption:getValue(); }//Tishka17
    
    public void onSelect(VirtualList view) { 
        new EditBox(display, caption, text, this, boxType);
    }
    
    public String getValue() { return (text==null)?"":text; }

    public void setValue(String text) { this.text=(text==null)?"":text; }
    
    public int getVHeight(){
        return itemHeight;
    }
    public int getVWidth(){ 
        return -1;
    }    
    
    public String getText() {
        return getValue();
    }

    public void drawItem(VirtualList view, Graphics g, int ofs, boolean sel) {
        int width=g.getClipWidth();
        int height=fontHeight;

        int oldColor=g.getColor();
        
        int thisOfs=0;
        
        int y=0;
        if (caption!=null) {
            thisOfs=(getCaptionLength()>width)?-ofs:2;
            g.setFont(captionFont);
            g.drawString(caption, thisOfs, y, Graphics.TOP|Graphics.LEFT);
            y=captionFontHeight;
        }

        if(text.length() == 0) {
          width = width - midlet.BombusQD.cf.scrollWidth - 5;
          g.setColor(colorBGnd);
          g.fillRect(5, y, width, height-3);

          g.setColor((sel)?colorBorder:colorItem);
          //g.drawRect(0, y, width-1, height-1);
          g.drawRoundRect(5, y, width, height-3,8,8);//Tishka17
        }

        g.setColor(oldColor);
        
        if (getTextLength()>0) {
            thisOfs=(getTextLength()>width)?-ofs+4:4;
            g.setFont(font);
            g.drawString(getText(), thisOfs, y, Graphics.TOP|Graphics.LEFT); 
        }
    }

    public boolean isSelectable() { return true; }
}