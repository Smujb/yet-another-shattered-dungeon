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

package com.shatteredpixel.yasd.general.levels.rooms.standard;

import com.shatteredpixel.yasd.general.levels.Level;
import com.shatteredpixel.yasd.general.levels.terrain.Terrain;
import com.shatteredpixel.yasd.general.levels.painters.Painter;
import com.shatteredpixel.yasd.general.levels.traps.BurningTrap;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class BurnedRoom extends PatchRoom {
	
	@Override
	public float[] sizeCatProbs() {
		return new float[]{4, 1, 0};
	}
	
	@Override
	public void paint(Level level) {
		Painter.fill( level, this, Terrain.WALL );
		Painter.fill( level, this, 1, Terrain.EMPTY );
		for (Door door : connected.values()) {
			door.set( Door.Type.REGULAR );
		}
		
		//past 8x8 each point of width/height decreases fill by 3%
		// e.g. a 14x14 burned room has a fill of 54%
		float fill = Math.min( 1f, 1.48f - (width()+height())*0.03f);
		setupPatch(level, fill, 2, false );
		
		for (int i=top + 1; i < bottom; i++) {
			for (int j=left + 1; j < right; j++) {
				if (!patch[xyToPatchCoords(j, i)])
					continue;
				int cell = i * level.width() + j;
				Terrain t = Terrain.EMPTY;
				switch (Random.Int( 5 )) {
					case 0: default:
						break;
					case 1:
						t = Terrain.EMBERS;
						break;
					case 2:
						level.setTrap(new BurningTrap().reveal(), cell);
						break;
					case 3:
						level.setTrap(new BurningTrap().hide(), cell);
						break;
					case 4:
						BurningTrap trap = new BurningTrap();
						trap.reveal().active = false;
						level.setTrap(trap, cell);
						break;
				}
				level.set(cell, t);
			}
		}
	}
	
	@Override
	public boolean canPlaceWater(Point p) {
		return super.canPlaceWater(p) && !patch[xyToPatchCoords(p.x, p.y)];
	}

	@Override
	public boolean canPlaceGrass(Point p) {
		return super.canPlaceGrass(p) && !patch[xyToPatchCoords(p.x, p.y)];
	}

	@Override
	public boolean canPlaceTrap(Point p) {
		return super.canPlaceTrap(p) && !patch[xyToPatchCoords(p.x, p.y)];
	}
	
}
