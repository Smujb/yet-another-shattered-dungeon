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

package com.shatteredpixel.yasd.general.actors.mobs;

import com.shatteredpixel.yasd.general.Dungeon;
import com.shatteredpixel.yasd.general.actors.Char;
import com.shatteredpixel.yasd.general.actors.buffs.Buff;
import com.shatteredpixel.yasd.general.actors.buffs.Poison;
import com.shatteredpixel.yasd.general.items.Gold;
import com.shatteredpixel.yasd.general.items.Item;
import com.shatteredpixel.yasd.general.sprites.BanditSprite;
import com.shatteredpixel.yasd.general.sprites.ThiefSprite;
import com.watabou.utils.Random;

public class Thief extends RangedMob {
	
	public Item item;
	
	{
		spriteClass = ThiefSprite.class;
		
		//HP = HT = 11;
		evasionFactor = 1.2f;
		//defenseSkill = 6;
		
		EXP = 2;

		loot = Gold.class;
		lootChance = 0.25f;

		properties.add(Property.UNDEAD);
	}

	public static class Bandit extends Thief {

		{
			spriteClass = BanditSprite.class;

			//1 in 50 chance to be a crazy bandit, equates to overall 1/100 chance.
			lootChance = 0.5f;
		}

		@Override
		public int attackProc(Char enemy, int damage) {
			if (Random.Int(5) == 0) {
				//Buff.prolong(enemy, Blindness.class, Random.Int(2, 5));
				Buff.affect(enemy, Poison.class).set(Random.Int(2, 3));
				//Buff.prolong(enemy, Cripple.class, Random.Int(3, 8));
				Dungeon.observe();
			}
			return super.attackProc(enemy, damage);
		}
	}

	/*@Override
	public int damageRoll() {
		return Random.NormalIntRange( 2, 5 );
	}

	@Override
	public int attackSkill( Char target ) {
		return 10;
	}

	@Override
	public int drRoll(Element element) {
		return Random.NormalIntRange(0, 2);
	}

	@Override
	public boolean fleesAtMelee() {
		return false;
	}*/
}
