/*
 * MessageParser.java
 *
 * Created on 6.02.2005, 19:38
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

package Messages; 

//#ifdef SMILES
import images.SmilesIcons;
//#endif
import Fonts.FontCache;
import java.io.*;
import javax.microedition.lcdui.Font;
import Colors.ColorTheme;
import java.util.*;
import ui.*;
import Client.Msg;
import Client.Config;
import util.Strconv;

public final class MessageParser // implements Runnable
{
    private final static int BOLD=-3;
    private final static byte URL=-2;
    private final static byte NOSMILE=-1;
    //Config cf = Config.getInstance();
    private Vector smileTable;

    private Leaf root;
    private Leaf emptyRoot;
    
    // Singleton
    private static MessageParser instance=null;
    
    private int width; // window width
//#ifdef SMILES 
    private ImageList smileImages;
//#endif
    
    //private Vector tasks=new Vector();
    //private Thread thread;
    
    boolean wordsWrap;
    private static String wrapSeparators=" .,-=/\\;:+()[]<>~!@#%^_&";
    
    
    public static MessageParser getInstance() {
        if (instance==null) 
            instance=new MessageParser();
        return instance;
    }

//#ifdef SMILES 
    public Vector getSmileTable() { return smileTable; }
//#endif
    
    private static class Leaf {
        public int smile=NOSMILE;
        private String smileChars;
        private Vector child;

        public Leaf() {
            child=new Vector(0);
            smileChars=new String("");
        }
        
        public Leaf findChild(char c){
            int index=smileChars.indexOf(c);
            return (index==-1)?null:(Leaf)child.elementAt(index);
        }

        private void addChild(char c, Leaf child){
            this.child.addElement(child);
            smileChars=smileChars+c;
        }
    }
  
    private void addSmile(Leaf rootSmile, String smile, int index) {
	Leaf p=rootSmile;
	Leaf p1;
	
	int len=smile.length();
	for (int i=0; i<len; i++) {
	    char c=smile.charAt(i);
	    p1=p.findChild(c);
	    if (p1==null) {
		p1=new Leaf();
		p.addChild((char)c,p1);
	    }
	    p=p1;
	}
	p.smile=index;
    }

    
    public void parseMsg(MessageItem messageItem,  int width) {
        //synchronized (tasks) {
            wordsWrap=midlet.BombusQD.cf.textWrap==1;
            messageItem.msgLines=new Vector(0);
//#ifdef SMILES
            this.smileImages=SmilesIcons.getInstance();
//#endif
            this.width=width;

            //if (tasks.indexOf(messageItem)>=0) return;

            parseMessage(messageItem);
            messageItem.notifyRepaint();
  
        /* 
             tasks.addElement(messageItem);
            if (thread==null) {
                thread=new Thread(this);
                thread.setPriority(Thread.MAX_PRIORITY);
                thread.start();
            }
        }
        */
        //return;
    }
    
    /*
    public void run() {
        while(true) {
            MessageItem task=null;
            synchronized (tasks) {
                if (tasks.size()==0) {
                    thread=null;
                    return;
                }
                task=(MessageItem) tasks.lastElement();
            }
            parseMessage(task);
            synchronized (tasks) {
                tasks.removeElement(task);
            }
        }
    }
     */

    private MessageParser() {
        smileTable=null;
        smileTable=new Vector(0);
        root=new Leaf();
//#ifdef SMILES
        StringBuffer s=new StringBuffer(10);
        try { // generic errors
            int strnumber=0;
            boolean strhaschars=false;
            boolean endline=false;

            InputStream in=this.getClass().getResourceAsStream("/images/smiles/smiles.txt");

            boolean firstSmile=true;
            
            int c;
            while (true) {
                c=in.read();
                if (c<0) break;
                switch (c) {
                    case 0x0d:
                    case 0x0a:
                        if (strhaschars) 
                            endline=true; else break;
                    case 0x09:
                        String smile=Strconv.convCp1251ToUnicode(s.toString());
                        if (firstSmile) smileTable.addElement(smile);

                        addSmile(root, smile, strnumber);

                        s.setLength(0);
                        firstSmile=false;

                        break;
                    default:
                        s.append((char)c);
                        strhaschars=true;
                }
                if (endline) {
                    endline=strhaschars=false;
                    strnumber++;
                    firstSmile=true;
                }
            }
            s.setLength(0);
            in.close();
            in=null;
        } catch (Exception e) {
            s.setLength(0);
        }
//#endif
        
 	addSmile(root, "http://", URL);
        addSmile(root, "tel:",URL);
        addSmile(root, "ftp://",URL);
        addSmile(root, "https://",URL);
        addSmile(root, "native:",URL);
//#if NICK_COLORS
        addSmile(root, "\01", ComplexString.NICK_ON);
        addSmile(root, "\02", ComplexString.NICK_OFF);
//#endif
        
        emptyRoot=new Leaf();
	addSmile(emptyRoot, "http://", URL);
        addSmile(emptyRoot, "tel:",URL);
        addSmile(emptyRoot, "ftp://",URL);
        addSmile(emptyRoot, "https://",URL);
        addSmile(emptyRoot, "native:",URL);

//#if NICK_COLORS
        addSmile(emptyRoot, "\01", ComplexString.NICK_ON);
        addSmile(emptyRoot, "\02", ComplexString.NICK_OFF);
//#endif
    }
    
    private static StringBuffer s = new StringBuffer(0);
    private static StringBuffer buf = new StringBuffer(0);

    private void parseMessage(final MessageItem task) {
//long s1 = System.currentTimeMillis();          
        Vector lines=task.msgLines;
        boolean singleLine=task.msg.itemCollapsed;
        
        boolean underline=false;
        
        Leaf smileRoot=(
//#ifdef SMILES
                task.smilesEnabled()
//#else
//#                 false
//#endif
                )? root: emptyRoot;
        
        int state=0;
        if (task.msg.subject==null) state=1;
        while (state<2) {
//#ifdef SMILES
            if (task.smilesEnabled())
                smileRoot=(state==0)?emptyRoot:root;
//#else
//#             smileRoot=emptyRoot;
//#endif
            int w=0;
            s.setLength(0);
            
	    int wordWidth=0;
	    int wordStartPos=0;
//#ifdef SMILES
            ComplexString l=new ComplexString(smileImages);
//#else
//#             ComplexString l=new ComplexString();
//#endif
            lines.addElement(l);
            
            Font f = FontCache.getFont( (task.msg.highlite || state==0) , FontCache.msg);
            l.setFont(f);
            
            buf.setLength(0);
            buf.append( (state==0)? task.msg.subject: task.msg.toString() );
            
            int size = buf.length();
            int color=(state==0)?
                ColorTheme.getColor(ColorTheme.MSG_SUBJ):
                ColorTheme.getColor(ColorTheme.LIST_INK);
            l.setColor(color);

            if (buf==null) {
                state++;
                continue;
            }
            
            int pos=0;
            Leaf smileLeaf;
            char c;
            while (pos<size) {
                smileLeaf=smileRoot;
                int smileIndex=-1;
                int smileStartPos=pos;
                int smileEndPos=pos;
                while (pos<size) {
                    c=buf.charAt(pos);
                    if (underline) {//���
                        switch (c) {
                            case ' ':
                            case 0x09:
                            case 0x0d:
                            case 0x0a:
                            case 0xa0:
                            case ')':
                                underline=false;
                                if (wordStartPos!=pos) {
                                    for(int j=wordStartPos;j<pos;j++) s.append(buf.charAt(j));
                                    wordStartPos=pos;
				    w+=wordWidth;
                                    wordWidth=0;
                                }
                                if (s.length()>0) {
                                    l.addUnderline();
                                    l.addElement(s.toString());
                                }
                                s.setLength(0);
                        }
                        break;
                    }

                    smileLeaf=smileLeaf.findChild(c);
                    if (smileLeaf==null) {
                        break;
                    }
                    if (smileLeaf.smile!=-1) {
                        smileIndex=smileLeaf.smile;
                        smileEndPos=pos;
                    }
                  pos++;
                }

              switch(smileIndex) {

                 case URL:
                       w+=50;//hardfix
                       if (s.length()>0) l.addElement(s.toString());
                       s.setLength(0);
                       underline=true;
                      break;
                  
                 case -1: //text NOSMILE
                    pos=smileStartPos;
                    c=buf.charAt(pos);

                    int cw=f.charWidth(c);//+fHeight

                    if (c>0x1f) wordWidth+=cw;
                    
                    if (c!=0x20) { //�� ������
                        boolean newline = ( c==0x0d || c==0x0a );
                        
                        if (wordWidth+cw>width || newline) {
                            for(int j=wordStartPos;j<pos;j++) s.append(buf.charAt(j));
                            w+=wordWidth;
                            wordWidth=0;
                            wordStartPos=pos;
                            if (newline) wordStartPos++;
                        }
                        if (w+wordWidth+cw>width || newline) {
                            if (underline) l.addUnderline();
                            
                            l.addElement(s.toString());
                            s.setLength(0); w=0;

                            if (c==0xa0) l.setColor(ColorTheme.getColor(ColorTheme.MSG_HIGHLIGHT));
//#ifdef SMILES
                            l=new ComplexString(smileImages);
//#else
//#                         l=new ComplexString();
//#endif
                            lines.addElement(l);

                            if (singleLine) return;

                            l.setColor(color);
                            l.setFont(f);//?
                        }
                    }

                    if (c==0x09) c=0x20;


                    if(pos<=10 && !midlet.BombusQD.cf.useLowMemory_iconmsgcollapsed){
                      cw+=3;
                    }

                      if (wrapSeparators.indexOf(c)>=0 || !wordsWrap) { //by chars
                        if (pos>wordStartPos){
                           for(int j=wordStartPos;j<pos;j++) s.append(buf.charAt(j));
                        }
                        if (c>0x1f) s.append(c);
                        w+=wordWidth;
                        wordStartPos=pos+1;
                        wordWidth=0;
                      }
                    break;

                 default: //smile
                    if (wordStartPos!=smileStartPos) {
                        for(int j=wordStartPos;j<smileStartPos;j++) s.append(buf.charAt(j));
                        w+=wordWidth;
                        wordWidth=0;
                    }
                    if (s.length()>0) {
                        if (underline){
                          l.addUnderline();
                        }                    
                      l.addElement(s.toString());
                    }
                    
                    // �������
                    s.setLength(0);
                    // ������� �����
                    int iw=(smileIndex<0x01000000)? smileImages.getWidth() : 0;
                    if (w+iw>width) {
                        l=new ComplexString(smileImages);
                        lines.addElement(l);
                        if (singleLine) return;
                        l.setColor(color);
                        l.setFont(f);
                        w=0;
                    }
                    l.addImage(smileIndex); 
                    w+=iw; //������ + ������ ������
                    pos=smileEndPos;
                    wordStartPos=pos+1; 
                    break;
              }
              pos++;
            }
            
 	    if (wordStartPos!=pos){
                for(int j=wordStartPos;j<pos;j++) s.append(buf.charAt(j));
            }
            if (s.length()>0) {
                if (underline) l.addUnderline();
                l.addElement(s.toString());
            }
             
            if (l.isEmpty()) lines.removeElementAt(lines.size()-1);

            state++;
            s.setLength(0);
            buf.setLength(0);
        }
        lines=null;
        smileRoot=null;

//long s2 = System.currentTimeMillis();  
//System.out.println("parse "+(s2-s1)+" msec");   
//parse speed  old - 21-25 msec,new - 10-14 msec

    }
    
    public Font getFont(boolean bold) {
        return FontCache.getFont(bold, FontCache.msg);
    }

}