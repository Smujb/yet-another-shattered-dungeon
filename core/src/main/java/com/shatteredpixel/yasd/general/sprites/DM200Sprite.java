/*
 *
 *   Pixel Dungeon
 *   Copyright (C) 2012-2015 Oleg Dolya
 *
 *   Shattered Pixel Dungeon
 *   Copyright (C) 2014-2019 Evan Debenham
 *
 *   Yet Another Shattered Dungeon
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

package com.shatteredpixel.yasd.general.sprites;

import com.shatteredpixel.yasd.general.Assets;
import com.watabou.noosa.TextureFilm;

//TODO currently just uses DM-300's sprite scaled to 80%
public class DM200Sprite extends MobSprite {

	public DM200Sprite () {
		super();

		texture( Assets.DM300 );

		TextureFilm frames = new TextureFilm( texture, 22, 20 );

		idle = new Animation( 10, true );
		idle.frames( frames, 0, 1 );

		run = new Animation( 10, true );
		run.frames( frames, 2, 3 );

		attack = new Animation( 15, false );
		attack.frames( frames, 4, 5, 6, 0 );

		zap = attack.clone();

		die = new Animation( 20, false );
		die.frames( frames, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 8 );

		play( idle );
		scale.set( 0.8f );
	}

	public static class DM201Sprite extends MobSprite {

		public DM201Sprite () {
			super();

			texture( Assets.DM300 );

			TextureFilm frames = new TextureFilm( texture, 22, 16 );

			idle = new Animation( 2, true );
			idle.frames( frames, 0, 1 );

			run = idle.clone();

			attack = new Animation( 15, false );
			attack.frames( frames, 4, 5, 6, 0 );

			zap = attack.clone();

			die = new Animation( 20, false );
			die.frames( frames, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 8 );

			play( idle );
			scale.set( 0.8f );
		}

		@Override
		public void resetColor() {
			super.resetColor();
		}

	}

}
