/*
 * ComplexString.java
 *
 * Created on 12.03.2005, 0:35
 *
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
 */

package ui;
import Client.Config;
import Client.StaticData;
import Fonts.*;
import java.util.*;
import javax.microedition.lcdui.*;
import Colors.ColorTheme;

/**
 *
 * @author Eugene Stahov,aqent
 */
public class ComplexString extends Vector implements VirtualElement {

    //private Vector v;
    public final static int IMAGE     = 0x00000000;
    public final static int COLOR     = 0x01000000; 
    public final static int RALIGN    = 0x02000000;
    public final static int UNDERLINE = 0x03000000;
//#if NICK_COLORS
    public final static int NICK_ON   = 0x04000000;
    public final static int NICK_OFF  = 0x05000000;
//#endif
    public final static int BOLD = 0x06000000;
    
    
    protected Font font=FontCache.getFont(false, FontCache.msg);

    private int font_height = font.getHeight();
    
    private int height;
    private int width;
    private ImageList imageList;
    private int colorBGnd;
    private int color;
    

    /** Creates a new instance of ComplexString */
    public ComplexString() {
        super();
        color=ColorTheme.getColor(ColorTheme.LIST_INK);
        colorBGnd=ColorTheme.getColor(ColorTheme.LIST_BGND);
    }

    /** Creates a new instance of ComplexString */
    public ComplexString(ImageList imageList) {
        this();
        this.imageList=imageList;
    }

    private int imgHeight(){
        return (imageList==null)?0:imageList.getHeight();
    }
    private int imgWidth(){
        return (imageList==null)?0:imageList.getWidth();
    }
    
    public int getColor() { return color; }
    public int getColorBGnd() { return colorBGnd; }
    
    public void setColorBGnd(int color){ colorBGnd=color;}
    public void setColor(int color){ 
        this.color=color;
    }
    
    public void onSelect(){};
    
    //FontClass fc = FontClass.getInstance();
    Font bold;
    char c1,c2;
    
    public void drawItem(Graphics g, int offset, boolean selected){
        boolean ralign=false;
	boolean underline=false;
//#if NICK_COLORS
	boolean nick=false;
//#endif
        int w=offset;
        int dw;
        int imageYOfs=(( getVHeight()-imgHeight() )>>1);
        int fontYOfs=(( getVHeight() - font.getHeight() )>>1);
        int imgWidth=imgWidth();
        
        //if(fc.isCheck()==false){
        if(!midlet.BombusQD.cf.boldNicks){
            g.setFont(font);
        }
        //}
        
        for (int index=0; index<elementCount;index++) {
            if (elementData[index]!=null) {
                if (elementData[index] instanceof String ){
//#if NICK_COLORS
                    if (nick) {
                        int color=g.getColor();
                        //int randColor=randomColor();
                        bold = midlet.BombusQD.cf.boldNicks?FontCache.getFont(true,font.getSize()):null;    
                        if(midlet.BombusQD.cf.boldNicks) {
                            g.setFont(bold);
                        }
                        dw=0;
                        int startDrw = 0;
                        int len = ((String)elementData[index]).length();
                           if(((String)elementData[index]).startsWith("<nick>")){
                            len-=6;//hotfix for <nick>,</nick>
                            startDrw=6;
                           }

                            if(midlet.BombusQD.cf.boldNicks) {
                              g.setColor(ColorTheme.strong(color));   /*(c1>255) ?*/ /* : color*/
                              dw=bold.substringWidth(((String)elementData[index]), startDrw, len);
                            }else{
                              g.setColor( /*(c1>255) ? ColorTheme.strong(color); :*/ color);
                              dw=font.substringWidth(((String)elementData[index]), startDrw, len);
                            }
                            if (ralign) w-=dw;
                              g.drawSubstring( ((String)elementData[index]), startDrw, len, w,fontYOfs,Graphics.LEFT|Graphics.TOP);
                            if (!ralign) w+=dw;

                        g.setColor(color);
                    } else {
//#endif
                        if(midlet.BombusQD.cf.boldNicks) {  g.setFont(font); }
                        dw=font.stringWidth((String)elementData[index]);  
                        if (ralign) w-=dw;
                          g.drawString((String)elementData[index],w,fontYOfs,Graphics.LEFT|Graphics.TOP);
                          if (underline) {
                            int y=getVHeight()-1;
                            g.drawLine(w, y-1, w+dw, y-1);
                            underline=false;
                          }
                        if (!ralign) w+=dw;
//#if NICK_COLORS
                    }
//#endif

                } else if ((elementData[index] instanceof Integer)) {
                    // image element or color
                    int i=((Integer)elementData[index]).intValue();                           
                    switch (i&0xff000000) {
                        case IMAGE:
                            if (imageList==null) break;
                              if (ralign) w-=imgWidth;
                              imageList.drawImage(g, ((Integer)elementData[index]).intValue(), w, imageYOfs);  
                              if (!ralign) w+=imgWidth;
                            break;
                        case COLOR:
                            g.setColor(0xFFFFFF&i);
                            break;
                        case RALIGN:
                            ralign=true;
                            w=g.getClipWidth()-1;
			    break;
			case UNDERLINE:
			    underline=true;
			    break;
//#if NICK_COLORS
                        case NICK_ON:
                            nick=true; 
                            break;
                        case NICK_OFF:
                            nick=false;
                            break;
//#endif
                    }
                } /* Integer*/ else if (elementData[index] instanceof VirtualElement) { 
                    int clipw=g.getClipWidth(); 
                    int cliph=g.getClipHeight();
                    ((VirtualElement)elementData[index]).drawItem(g,0,false);
                    g.setClip(g.getTranslateX(), g.getTranslateY(), clipw, cliph);
                }
            } // if ob!=null
        } // for
    }


    
    
    public int getVWidth() {
        //g.setColor(0);
        if (width>0) return width;  // cached
        
        int w=0;
        int imgWidth=imgWidth();
        
        for (int index=0; index<elementCount;index++) {
            if (elementData[index]!=null) {
                
                if (elementData[index] instanceof String ){
                    // string element
                    w+=//(fc.isCheck()?fc.stringWidth((String)elementData[index]):
                            font.stringWidth((String)elementData[index]);
                } else if ((elementData[index] instanceof Integer)&& imageList!=null) {
                    // image element or color
                    int i=(((Integer)elementData[index]).intValue());
                    switch (i&0xff000000) {
                        case IMAGE:
                            w+=imgWidth;
                            break;
                    }
                } // Integer
            } // if ob!=null
        } // for
        return width=w;
    }

    public void setElementAt(Object obj, int index) {
        height=width=0; // discarding cached values
        if (index>=elementCount) this.setSize(index+1);
        super.setElementAt(obj, index);
    }
    
    public int getVHeight(){
        if (height!=0) return height;
        for (int i=0;i<elementCount;i++){
            int h=0;
            if (elementData[i]==null) continue;
            if (elementData[i] instanceof String) { 
                h=//(fc.isCheck()?fc.getFontHeight():
                        font.getHeight(); 
            } else
            if (elementData[i] instanceof Integer) {
                int a=((Integer)elementData[i]).intValue();
                if ((a&0xff000000) == 0) { h=imageList.getHeight(); }
            } else
            if (elementData[i] instanceof VirtualElement) { h=((VirtualElement)elementData[i]).getVHeight(); }
            if (h>height) height=h;
        }
        return height;
    }

    public void addElement(Object obj) {
        height=0;
        width=0; // discarding cached values
        super.addElement(obj);
    }

    public void addImage(int imageIndex){ addElement(new Integer(imageIndex)); }
    public void addColor(int colorRGB){ addElement(new Integer(COLOR | colorRGB)); }
    public void addRAlign(){ addElement(new Integer(RALIGN)); }
    public void addUnderline(){ addElement(new Integer(UNDERLINE)); }
    public void addBold(){ addElement(new Integer(BOLD)); }    
    
    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public String getTipString() { return null; }

    public boolean isSelectable() {
        return true;
    }
    
    public void clearWHCache() {
        width=0;
        height=0;
    }

    public boolean handleEvent(int keyCode) { return false; }

}