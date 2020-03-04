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

package com.shatteredpixel.yasd.general.sprites;

import com.shatteredpixel.yasd.general.Assets;
import com.shatteredpixel.yasd.general.Dungeon;
import com.shatteredpixel.yasd.general.items.weapon.missiles.ThrowingKnife;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.Callback;

public class ThiefSprite extends MobSprite {
	
	public ThiefSprite() {
		super();
		
		texture( Assets.THIEF );
		TextureFilm film = new TextureFilm( texture, 12, 13 );
		
		idle = new Animation( 1, true );
		idle.frames( film, 0, 0, 0, 1, 0, 0, 0, 0, 1 );
		
		run = new Animation( 15, true );
		run.frames( film, 0, 0, 2, 3, 3, 4 );
		
		die = new Animation( 10, false );
		die.frames( film, 5, 6, 7, 8, 9 );
		
		attack = new Animation( 12, false );
		attack.frames( film, 10, 11, 12, 0 );

		zap = attack.clone();
		
		idle();
	}

	@Override
	public void attack( int cell ) {
		if (!Dungeon.level.adjacent(cell, ch.pos)) {

			((MissileSprite)parent.recycle( MissileSprite.class )).
					reset( ch.pos, cell, new ThrowingKnife(), new Callback() {
						@Override
						public void call() {
							ch.onAttackComplete();
						}
					} );

			play( zap );
			turnTo( ch.pos , cell );

		} else {

			super.attack( cell );

		}
	}

	@Override
	public void onComplete(Animation anim) {
		super.onComplete(anim);
		if (anim == zap) {
			idle();
		}
	}
}