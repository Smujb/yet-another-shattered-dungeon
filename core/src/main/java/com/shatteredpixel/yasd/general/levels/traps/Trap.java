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

package com.shatteredpixel.yasd.general.levels.traps;

import com.shatteredpixel.yasd.general.Assets;
import com.shatteredpixel.yasd.general.Dungeon;
import com.shatteredpixel.yasd.general.messages.Messages;
import com.shatteredpixel.yasd.general.scenes.GameScene;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

public abstract class Trap implements Bundlable {

	//trap colors
	public static final int RED     = 0;
	public static final int ORANGE  = 1;
	public static final int YELLOW  = 2;
	public static final int GREEN   = 3;
	public static final int TEAL    = 4;
	public static final int VIOLET  = 5;
	public static final int WHITE   = 6;
	public static final int GREY    = 7;
	public static final int BLACK   = 8;

	//trap shapes
	public static final int DOTS        = 0;
	public static final int WAVES       = 1;
	public static final int GRILL       = 2;
	public static final int STARS       = 3;
	public static final int DIAMOND     = 4;
	public static final int CROSSHAIR   = 5;
	public static final int LARGE_DOT   = 6;

	public String name = Messages.get(this, "name");

	public int color;
	public int shape;

	public int pos;

	public boolean visible;
	public boolean active = true;
	
	public boolean canBeHidden = true;
	public boolean canBeSearched = true;

	private void stateChange() {
		if (Dungeon.level != null) {
			Dungeon.level.onTrapModified();
		}
	}

	public Trap set(int pos){
		this.pos = pos;
		stateChange();
		return this;
	}

	public Trap reveal() {
		visible = true;
		GameScene.updateMap(pos);
		stateChange();
		return this;
	}

	public Trap hide() {
		if (canBeHidden) {
			visible = false;
			GameScene.updateMap(pos);
			stateChange();
			return this;
		} else {
			return reveal();
		}
	}

	public void trigger() {
		if (active) {
			if (Dungeon.level.heroFOV[pos]) {
				Sample.INSTANCE.play(Assets.Sounds.TRAP);
			}
			if (!this.visible & Dungeon.hero.pos == this.pos & this.canBeSearched) {//Did the Hero interact the trap, is it visible?
				Dungeon.hero.loseMorale(0.5f);
			}
			disarm();
			reveal();
			activate();
		}
	}

	public abstract void activate();

	public void disarm(){
		active = false;
		Dungeon.level.disarmTrap(pos);
	}

	private static final String POS	= "pos";
	private static final String VISIBLE	= "visible";
	private static final String ACTIVE = "active";

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		pos = bundle.getInt( POS );
		visible = bundle.getBoolean( VISIBLE );
		if (bundle.contains(ACTIVE)){
			active = bundle.getBoolean(ACTIVE);
		}
	}

	@Override
	public void storeInBundle( Bundle bundle ) {
		bundle.put( POS, pos );
		bundle.put( VISIBLE, visible );
		bundle.put( ACTIVE, active );
	}

	public String desc() {
		return Messages.get(this, "desc");
	}
}
