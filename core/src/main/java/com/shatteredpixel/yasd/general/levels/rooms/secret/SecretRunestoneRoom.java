/*
 *
 *  * Pixel Dungeon
 *  * Copyright (C) 2012-2015 Oleg Dolya
 *  *
 *  * Shattered Pixel Dungeon
 *  * Copyright (C) 2014-2019 Evan Debenham
 *  *
 *  * Powered Pixel Dungeon
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

package com.shatteredpixel.yasd.general.levels.rooms.secret;

import com.shatteredpixel.yasd.general.items.Generator;
import com.shatteredpixel.yasd.general.items.bombs.Bomb;
import com.shatteredpixel.yasd.general.items.stones.StoneOfEnchantment;
import com.shatteredpixel.yasd.general.levels.Level;
import com.shatteredpixel.yasd.general.levels.terrain.Terrain;
import com.shatteredpixel.yasd.general.levels.painters.Painter;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class SecretRunestoneRoom extends SecretRoom {
	
	@Override
	public void paint(Level level) {
		Painter.fill( level, this, Terrain.WALL );
		Painter.fill(level, this, 1, Terrain.EMPTY);
		
		Door entrance = entrance();
		Point center = center();
		
		if (entrance.x == left || entrance.x == right){
			Painter.drawLine(level,
					new Point(center.x, top+1),
					new Point(center.x, bottom-1),
					Random.Int(2) == 0 ? Terrain.BOOKSHELF : Terrain.SECRET_CRACKED_WALL);
			if (entrance.x == left) {
				Painter.fill(level, center.x+1, top+1, right-center.x-1, height()-2, Terrain.EMPTY_SP);
			} else {
				Painter.fill(level, left+1, top+1, center.x-left-1, height()-2, Terrain.EMPTY_SP);
			}
		} else {
			Painter.drawLine(level,
					new Point(left+1, center.y),
					new Point(right-1, center.y),
					Terrain.BOOKSHELF);
			if (entrance.y == top) {
				Painter.fill(level, left+1, center.y+1, width()-2, bottom-center.y-1, Terrain.EMPTY_SP);
			} else {
				Painter.fill(level, left+1, top+1, width()-2, center.y-top-1, Terrain.EMPTY_SP);
			}
		}
		
		level.addItemToSpawn(new Bomb());
		
		int dropPos;
		
		do{
			dropPos = level.pointToCell(random());
		} while (level.getTerrain(dropPos) != Terrain.EMPTY);
		level.drop( Generator.random(Generator.Category.STONE), dropPos);
		
		do{
			dropPos = level.pointToCell(random());
		} while (level.getTerrain(dropPos) != Terrain.EMPTY || level.heaps.get(dropPos) != null);
		level.drop( Generator.random(Generator.Category.STONE), dropPos);
		
		do{
			dropPos = level.pointToCell(random());
		} while (level.getTerrain(dropPos) != Terrain.EMPTY_SP || level.heaps.get( dropPos ) != null);
		level.drop( new StoneOfEnchantment(), dropPos);
		
		entrance.set(Door.Type.HIDDEN);
	}
	
	@Override
	public boolean canPlaceWater(Point p) {
		return false;
	}
	
	@Override
	public boolean canPlaceGrass(Point p) {
		return false;
	}
	
	@Override
	public boolean canPlaceCharacter(Point p, Level l) {
		return super.canPlaceCharacter(p, l) && l.getTerrain(l.pointToCell(p)) != Terrain.EMPTY_SP;
	}
}
