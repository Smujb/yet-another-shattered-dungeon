/*
 *
 *   Pixel Dungeon
 *   Copyright (C) 2012-2015 Oleg Dolya
 *
 *   Shattered Pixel Dungeon
 *   Copyright (C) 2014-2019 Evan Debenham
 *
 *   Powered Pixel Dungeon
 *   Copyright (C) 2014-2020 Samuel Braithwaite
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 *
 */

package com.shatteredpixel.yasd.general.levels.interactive;

import com.shatteredpixel.yasd.general.LevelHandler;
import com.shatteredpixel.yasd.general.actors.buffs.Buff;
import com.shatteredpixel.yasd.general.actors.buffs.LockedFloor;
import com.shatteredpixel.yasd.general.actors.hero.Hero;
import com.shatteredpixel.yasd.general.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.yasd.general.plants.Swiftthistle;
import com.watabou.noosa.Camera;

public class Exit extends InteractiveCell {
	@Override
	public void interact(Hero hero) {
		descend(hero);
	}

	public static void descend(Hero hero) {
		if (hero.rooted || hero.buff(LockedFloor.class) != null) {
			Camera.main.shake(1, 1f);
			hero.ready();
			return;
		}
		hero.curAction = null;

		Buff buff = hero.buff(TimekeepersHourglass.timeFreeze.class);
		if (buff != null) buff.detach();
		buff = hero.buff(Swiftthistle.TimeBubble.class);
		if (buff != null) buff.detach();

		LevelHandler.descend();

	}
}
