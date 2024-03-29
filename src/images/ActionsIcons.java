/*
 * ActionsIcons.java
 *
 * Created on 29.07.2008, 13:18
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
 
package images;

import javax.microedition.lcdui.Graphics;
import ui.ImageList;

/**
 *
 * @author ad
 */
public class ActionsIcons extends ImageList {
    
    private static ActionsIcons instance;
    public static ActionsIcons getInstance() {
	if (instance==null) instance=new ActionsIcons();
	return instance;
    }

    private final static int ICONS_IN_ROW=8;
    private final static int ICONS_IN_COL=5;

    /** Creates a new instance of RosterIcons */
    private ActionsIcons() {
	super("/images/actions.png", ICONS_IN_COL, ICONS_IN_ROW);
    }

    public void drawImage(Graphics g, int index, int x, int y) {
        super.drawImage(g, index, x, y);
    }
    
    public static final byte ICON_ON             = 0x00; //Подключить
    public static final byte ICON_OFF            = 0x01; //Отключить
    public static final byte ICON_NICK_RESOLVE   = 0x02; //Преобразовать в ник
    public static final byte ICON_VCARD          = 0x03; //vCard
    public static final byte ICON_INFO           = 0x04; //инфо
    public static final byte ICON_VERSION        = 0x05; //Версия клиента
    public static final byte ICON_COMMAND        = 0x06; //Команды
    public static final byte ICON_SEND_BUFFER    = 0x07; //Послать текст из буфера
    
    public static final byte ICON_COPY_JID       = 0x10; //Копировать JID
    public static final byte ICON_SEND_COLORS    = 0x11; //Send current color scheme
    public static final byte ICON_TIME           = 0x12; //Время
    public static final byte ICON_IDLE           = 0x13; //Бездействие
    public static final byte ICON_PING           = 0x14; //Время отклика
    public static final byte ICON_ONLINE         = 0x15; //Время в сети
    public static final byte ICON_INVITE         = 0x16; //Пригласить в конференцию
    public static final byte ICON_SUBSCR         = 0x17; //Подписка
    
    public static final byte ICON_MOVE           = 0x20; //Переместить
    public static final byte ICON_DELETE         = 0x21; //Удалить
    public static final byte ICON_LEAVE          = 0x22; //Покинуть комнату
    public static final byte ICON_SET_STATUS     = 0x23; //Установить статус
    public static final byte ICON_CHANGE_NICK    = 0x24; //Сменить ник
    public static final byte ICON_OWNERS         = 0x25; //Владельцы
    public static final byte ICON_ADMINS         = 0x26; //Администраторы
    public static final byte ICON_MEMBERS        = 0x27; //Члены
    
    public static final byte ICON_OUTCASTS       = 0x30; //�?згои(Ban)
    public static final byte ICON_KICK           = 0x31; //Выгнать (kick)
    public static final byte ICON_BAN            = 0x32; //Бан (ban)
    public static final byte ICON_DEVOICE        = 0x33; //Отнять право голоса
    public static final byte ICON_VOICE          = 0x34; //Дать право голоса
    public static final byte ICON_OWNER          = 0x35; //Дать право владельца
    public static final byte ICON_ADMIN          = 0x36; //Дать право администратора
    public static final byte ICON_DEMEMBER       = 0x37; //Отнять членство
    
    public static final byte ICON_MEMBER         = 0x40; //Дать членство
    public static final byte ICON_CONFIGURE      = 0x41; //Конфигуратор комнаты
    public static final byte ICON_SEND_FILE      = 0x42; //Послать файл
    public static final byte ICON_RENAME         = 0x43; //Переименовать
    public static final byte ICON_CONSOLE        = 0x44; //Консоль
    //public static final byte ICON_      = 0x45; //
   // public static final byte ICON_      = 0x46; //
}
