/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2019 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.yasd.actors.mobs;

import com.shatteredpixel.yasd.Dungeon;
import com.shatteredpixel.yasd.actors.Char;
import com.shatteredpixel.yasd.actors.buffs.Light;
import com.shatteredpixel.yasd.effects.particles.SparkParticle;
import com.shatteredpixel.yasd.items.Generator;
import com.shatteredpixel.yasd.mechanics.Ballistica;
import com.shatteredpixel.yasd.messages.Messages;
import com.shatteredpixel.yasd.sprites.CharSprite;
import com.shatteredpixel.yasd.sprites.ShamanSprite;
import com.shatteredpixel.yasd.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class Shaman extends RangedMob {

	private static final float TIME_TO_ZAP	= 1f;
	
	{
		spriteClass = ShamanSprite.class;
		
		HP = HT = 20;
		defenseSkill = 10;
		
		EXP = 6;
		maxLvl = 14;
		
		loot = Generator.Category.SCROLL;
		lootChance = 0.33f;
		
		properties.add(Property.ELECTRIC);
	}
	public Shaman() {
		super();
		if (Dungeon.depth > 12) {
			HP = HT *= 2;
			defenseSkill *= 1.5f;
		}
	}
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 2, 3 + Dungeon.depth );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return Dungeon.depth*2;
	}//Finding Shaman later are still hard.
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 6);
	}

	@Override
	public boolean canHit(Char enemy) {
		return new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
	}

	@Override
	public boolean fleesAtMelee() {
		return false;
	}

	@Override
	public int magicalDamageRoll() {
		return Random.IntRange(4,18);
	}

	@Override
	public int magicalAttackProc(Char enemy, int damage) {
		damage = super.magicalAttackProc(enemy, damage);
		if (Dungeon.level.water[enemy.pos] && !enemy.flying) {
			damage *= 1.5f;
		}
		if (Dungeon.depth > 12) {//Use a separate statement so they can stack
			damage *= 1.5f;
		}
		return damage;
	}

	//used so resistances can differentiate between melee and magical attacks
	public static class LightningBolt extends MagicalDamage{}

	@Override
	public MagicalDamage magicalSrc() {
		return new LightningBolt();
	}
}
