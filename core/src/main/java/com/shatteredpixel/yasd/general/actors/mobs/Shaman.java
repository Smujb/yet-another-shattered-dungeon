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

import com.shatteredpixel.yasd.general.Element;
import com.shatteredpixel.yasd.general.actors.Char;
import com.shatteredpixel.yasd.general.items.Generator;
import com.shatteredpixel.yasd.general.mechanics.Ballistica;
import com.shatteredpixel.yasd.general.sprites.ShamanSprite;

public class Shaman extends RangedMob {
	
	{
		spriteClass = ShamanSprite.class;

		healthFactor = 0.6f;
		//HP = HT = 20;
		//defenseSkill = 10;
		
		EXP = 6;
		maxLvl = 14;
		
		loot = Generator.Category.SCROLL;
		lootChance = 0.33f;
		
		properties.add(Property.ELECTRIC);
	}

	@Override
	public Element elementalType() {
		return Element.ELECTRIC;
	}

	/*@Override
	public int damageRoll() {
		return Random.NormalIntRange( 4, 18 );
	}


	@Override
	public int attackProc(Char enemy, int damage) {
		damage = super.attackProc(enemy, damage);
		if (Dungeon.level.liquid()[enemy.pos] && !enemy.flying) {
			damage *= 1.5f;
		}
		if (Dungeon.depth > 12) {//Use a separate statement so they can stack
			damage *= 1.5f;
		}
		return damage;
	}

	@Override
	public int attackSkill( Char target ) {
		return Dungeon.depth*2;
	}//Finding Shaman later are still hard.
	
	@Override
	public int drRoll(Element element) {
		return Random.NormalIntRange(0, 6);
	}*/

	@Override
	public boolean canHit(Char enemy) {
		return new  Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
	}

	@Override
	public boolean fleesAtMelee() {
		return false;
	}

	//used so resistances can differentiate between melee and magical attacks
	public static class LightningBolt extends MagicalDamage{}

	@Override
	public MagicalDamage magicalSrc() {
		return new  LightningBolt();
	}
}
