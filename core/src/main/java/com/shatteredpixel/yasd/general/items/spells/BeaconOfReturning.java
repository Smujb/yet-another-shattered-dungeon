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

package com.shatteredpixel.yasd.general.items.spells;

import com.shatteredpixel.yasd.general.Assets;
import com.shatteredpixel.yasd.general.Dungeon;
import com.shatteredpixel.yasd.general.actors.Actor;
import com.shatteredpixel.yasd.general.actors.Char;
import com.shatteredpixel.yasd.general.actors.buffs.Buff;
import com.shatteredpixel.yasd.general.actors.hero.Hero;
import com.shatteredpixel.yasd.general.actors.mobs.Mob;
import com.shatteredpixel.yasd.general.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.yasd.general.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.yasd.general.items.scrolls.exotic.ScrollOfPassage;
import com.shatteredpixel.yasd.general.messages.Messages;
import com.shatteredpixel.yasd.general.plants.Swiftthistle;
import com.shatteredpixel.yasd.general.scenes.GameScene;
import com.shatteredpixel.yasd.general.scenes.InterlevelScene;
import com.shatteredpixel.yasd.general.sprites.ItemSprite;
import com.shatteredpixel.yasd.general.sprites.ItemSpriteSheet;
import com.shatteredpixel.yasd.general.utils.GLog;
import com.shatteredpixel.yasd.general.windows.WndOptions;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;

import org.jetbrains.annotations.NotNull;

public class BeaconOfReturning extends Spell {
	
	{
		image = ItemSpriteSheet.RETURN_BEACON;
	}
	
	public int returnDepth	= -1;
	public int returnPos;
	
	@Override
	protected void onCast(final Hero hero) {
		
		if (returnDepth == -1){
			setBeacon(hero);
		} else {
			GameScene.show(new WndOptions(Messages.titleCase(name()),
					Messages.get(BeaconOfReturning.class, "wnd_body"),
					Messages.get(BeaconOfReturning.class, "wnd_set"),
					Messages.get(BeaconOfReturning.class, "wnd_return")){
				@Override
				protected void onSelect(int index) {
					if (index == 0){
						setBeacon(hero);
					} else if (index == 1){
						returnBeacon(hero);
					}
				}
			});
			
		}
	}
	
	//we reset return yPos when beacons are dropped to prevent
	//having two stacks of beacons with different return locations
	
	@Override
	protected void onThrow(int cell) {
		returnDepth = -1;
		super.onThrow(cell);
	}
	
	@Override
	public void doDrop(@NotNull Hero hero) {
		returnDepth = -1;
		super.doDrop(hero);
	}
	
	private void setBeacon(Hero hero ){
		returnDepth = Dungeon.yPos;
		returnPos = hero.pos;
		
		hero.spend( 1f );
		hero.busy();
		
		GLog.i( Messages.get(this, "set") );
		
		hero.sprite.operate( hero.pos );
		Sample.INSTANCE.play( Assets.SND_BEACON );
		updateQuickslot();
	}
	
	private void returnBeacon( Hero hero ){
		if (Dungeon.bossLevel()) {
			GLog.w( Messages.get(this, "preventing") );
			return;
		}
		
		for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
			Char ch = Actor.findChar(hero.pos + PathFinder.NEIGHBOURS8[i]);
			if (ch != null && ch.alignment == Char.Alignment.ENEMY) {
				GLog.w( Messages.get(this, "creatures") );
				return;
			}
		}
		
		if (returnDepth == Dungeon.yPos) {
			ScrollOfTeleportation.appear( hero, returnPos );
			for(Mob m : Dungeon.level.mobs){
				if (m.pos == hero.pos){
					//displace mob
					for(int i : PathFinder.NEIGHBOURS8){
						if (Actor.findChar(m.pos+i) == null && Dungeon.level.passable()[m.pos + i]){
							m.pos += i;
							m.sprite.point(m.sprite.worldToCamera(m.pos));
							break;
						}
					}
				}
			}
			Dungeon.level.occupyCell(hero );
			Dungeon.observe();
			GameScene.updateFog();
		} else {
			
			Buff buff = Dungeon.hero.buff(TimekeepersHourglass.timeFreeze.class);
			if (buff != null) buff.detach();
			buff = Dungeon.hero.buff(Swiftthistle.TimeBubble.class);
			if (buff != null) buff.detach();
			
			InterlevelScene.returnTo(returnDepth, returnPos);
		}
		detach(hero.belongings.backpack);
	}
	
	@Override
	public String desc() {
		String desc = super.desc();
		if (returnDepth != -1){
			desc += "\n\n" + Messages.get(this, "desc_set", returnDepth);
		}
		return desc;
	}
	
	private static final ItemSprite.Glowing WHITE = new ItemSprite.Glowing( 0xFFFFFF );
	
	@Override
	public ItemSprite.Glowing glowing() {
		return returnDepth != -1 ? WHITE : null;
	}
	
	private static final String DEPTH	= "yPos";
	private static final String POS		= "pos";
	
	@Override
	public void storeInBundle(@NotNull Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( DEPTH, returnDepth );
		if (returnDepth != -1) {
			bundle.put( POS, returnPos );
		}
	}
	
	@Override
	public void restoreFromBundle(@NotNull Bundle bundle ) {
		super.restoreFromBundle(bundle);
		returnDepth	= bundle.getInt( DEPTH );
		returnPos	= bundle.getInt( POS );
	}
	
	@Override
	public int price() {
		//prices of ingredients, divided by output quantity
		return Math.round(quantity * ((50 + 40) / 5f));
	}
	
	public static class Recipe extends com.shatteredpixel.yasd.general.items.Recipe.SimpleRecipe {
		
		{
			inputs =  new Class[]{ScrollOfPassage.class, ArcaneCatalyst.class};
			inQuantity = new int[]{1, 1};
			
			cost = 10;
			
			output = BeaconOfReturning.class;
			outQuantity = 5;
		}
		
	}
}
