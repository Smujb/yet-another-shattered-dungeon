/*
 *
 *  * Pixel Dungeon
 *  * Copyright (C) 2012-2015 Oleg Dolya
 *  *
 *  * Shattered Pixel Dungeon
 *  * Copyright (C) 2014-2019 Evan Debenham
 *  *
 *  * Yet Another Shattered Dungeon
 *  * Copyright (C) 2014-2020 Samuel Braithwaite
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 *
 */

package com.shatteredpixel.yasd.general.items.bags;

import com.shatteredpixel.yasd.general.actors.hero.Belongings;
import com.shatteredpixel.yasd.general.items.Item;
import com.shatteredpixel.yasd.general.items.bombs.Bomb;
import com.shatteredpixel.yasd.general.items.wands.Wand;
import com.shatteredpixel.yasd.general.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.yasd.general.sprites.ItemSpriteSheet;

public class MagicalHolster extends Bag {

	{
		image = ItemSpriteSheet.HOLSTER;
		
		size = Belongings.BACKPACK_SIZE;
	}

	public static final float HOLSTER_SCALE_FACTOR = 0.85f;
	public static final float HOLSTER_DURABILITY_FACTOR = 1.2f;
	
	@Override
	public boolean grab( Item item ) {
		return item instanceof Wand || item instanceof MissileWeapon || item instanceof Bomb;
	}

	@Override
	public void onDetach( ) {
		super.onDetach();
		for (Item item : items) {
			if (item instanceof Wand) {
				((Wand)item).stopCharging();
			} else if (item instanceof MissileWeapon){
				((MissileWeapon) item).holster = false;
			}
		}
	}
	
	@Override
	public int price() {
		return 60;
	}

}
