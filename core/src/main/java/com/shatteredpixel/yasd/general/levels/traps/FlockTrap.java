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

package com.shatteredpixel.yasd.general.levels.traps;

import com.shatteredpixel.yasd.general.Assets;
import com.shatteredpixel.yasd.general.Dungeon;
import com.shatteredpixel.yasd.general.actors.Actor;
import com.shatteredpixel.yasd.general.actors.mobs.npcs.Sheep;
import com.shatteredpixel.yasd.general.effects.CellEmitter;
import com.shatteredpixel.yasd.general.effects.Speck;
import com.shatteredpixel.yasd.general.scenes.GameScene;
import com.shatteredpixel.yasd.general.utils.BArray;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class FlockTrap extends Trap {

	{
		color = WHITE;
		shape = WAVES;
	}


	@Override
	public void activate() {
		//use an actor as we want to put this on a slight delay so all chars get a chance to act this turn first.
		Actor.add(new Actor() {

			{ actPriority = BUFF_PRIO; }

			protected boolean act() {
				PathFinder.buildDistanceMap( pos, BArray.not( Dungeon.level.solid(), null ), 2 );
				for (int i = 0; i < PathFinder.distance.length; i++) {
					Trap t;
					if (PathFinder.distance[i] < Integer.MAX_VALUE) {
						if (Dungeon.level.insideMap(i)
								&& Actor.findChar(i) == null
								&& !(Dungeon.level.pit()[i])) {
							Sheep sheep = new Sheep();
							sheep.lifespan = Random.NormalIntRange( 4, 8 );
							sheep.pos = i;
							GameScene.add(sheep);
							CellEmitter.get(i).burst(Speck.factory(Speck.WOOL), 4);
							//before the tile is pressed, directly interact traps to avoid sfx spam
							if ((t = Dungeon.level.trap(i)) != null && t.active){
								t.disarm();
								t.reveal();
								t.activate();
							}
							Dungeon.level.occupyCell(sheep);

						}
					}
				}
				Sample.INSTANCE.play(Assets.Sounds.PUFF);
				Actor.remove(this);
				return true;
			}
		});

	}

}
