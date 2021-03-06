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

package com.shatteredpixel.yasd.general.levels;

import com.shatteredpixel.yasd.general.Assets;
import com.shatteredpixel.yasd.general.Bones;
import com.shatteredpixel.yasd.general.Dungeon;
import com.shatteredpixel.yasd.general.actors.Actor;
import com.shatteredpixel.yasd.general.actors.Char;
import com.shatteredpixel.yasd.general.actors.mobs.King;
import com.shatteredpixel.yasd.general.actors.mobs.Mob;
import com.shatteredpixel.yasd.general.items.Heap;
import com.shatteredpixel.yasd.general.items.Item;
import com.shatteredpixel.yasd.general.items.keys.SkeletonKey;
import com.shatteredpixel.yasd.general.levels.interactive.Entrance;
import com.shatteredpixel.yasd.general.levels.interactive.Exit;
import com.shatteredpixel.yasd.general.levels.painters.Painter;
import com.shatteredpixel.yasd.general.levels.terrain.Terrain;
import com.shatteredpixel.yasd.general.messages.Messages;
import com.shatteredpixel.yasd.general.scenes.GameScene;
import com.shatteredpixel.yasd.general.tiles.DungeonTileSheet;
import com.watabou.noosa.Group;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import static com.shatteredpixel.yasd.general.levels.terrain.Terrain.BOOKSHELF;
import static com.shatteredpixel.yasd.general.levels.terrain.Terrain.EMPTY;
import static com.shatteredpixel.yasd.general.levels.terrain.Terrain.EMPTY_DECO;
import static com.shatteredpixel.yasd.general.levels.terrain.Terrain.EMPTY_SP;
import static com.shatteredpixel.yasd.general.levels.terrain.Terrain.ENTRANCE;
import static com.shatteredpixel.yasd.general.levels.terrain.Terrain.LOCKED_DOOR;
import static com.shatteredpixel.yasd.general.levels.terrain.Terrain.LOCKED_EXIT;
import static com.shatteredpixel.yasd.general.levels.terrain.Terrain.PEDESTAL;
import static com.shatteredpixel.yasd.general.levels.terrain.Terrain.STATUE_SP;
import static com.shatteredpixel.yasd.general.levels.terrain.Terrain.WALL;
import static com.shatteredpixel.yasd.general.levels.terrain.Terrain.WALL_DECO;

public class OldCityBossLevel extends Level {
	
	{
		color1 = 0x4b6636;
		color2 = 0xf2f2f2;

		bossLevel = true;
	}

	@Override
	public int getScaleFactor() {
		return new CityLevel().getScaleFactor();
	}

	private static final int TOP			= 2;
	private static final int HALL_WIDTH		= 7;
	private static final int HALL_HEIGHT	= 15;
	private static final int CHAMBER_HEIGHT	= 4;

	private static final int WIDTH = 32;
	
	private static final int LEFT	= (WIDTH - HALL_WIDTH) / 2;
	private static final int CENTER	= LEFT + HALL_WIDTH / 2;
	
	private int arenaDoor;
	private boolean enteredArena = false;
	private boolean keyDropped = false;
	
	@Override
	public String tilesTex() {
		return Assets.Environment.TILES_CITY;
	}
	
	@Override
	public String waterTex() {
		return Assets.Environment.WATER_CITY;
	}

	@Override
	public String loadImg() {
		return Assets.Interfaces.LOADING_CITY;
	}

	@Override
	public String music() {
		return Assets.Music.CITY_THEME;
	}

	private static final String DOOR	= "door";
	private static final String ENTERED	= "entered";
	private static final String DROPPED	= "droppped";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( DOOR, arenaDoor );
		bundle.put( ENTERED, enteredArena );
		bundle.put( DROPPED, keyDropped );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		arenaDoor = bundle.getInt( DOOR );
		enteredArena = bundle.getBoolean( ENTERED );
		keyDropped = bundle.getBoolean( DROPPED );
	}
	
	@Override
	protected boolean build() {
		
		setSize(32, 32);
		
		Painter.fill( this, LEFT, TOP, HALL_WIDTH, HALL_HEIGHT, EMPTY );
		Painter.fill( this, CENTER, TOP, 1, HALL_HEIGHT, EMPTY_SP );
		
		int y = TOP + 1;
		while (y < TOP + HALL_HEIGHT) {
			setMultiple(STATUE_SP, y * width() + CENTER - 2, y * width() + CENTER + 2);
			//map[y * width() + CENTER - 2] = STATUE_SP;
			//map[y * width() + CENTER + 2] = STATUE_SP;
			y += 2;
		}
		
		int left = pedestal( true );
		int right = pedestal( false );
		setMultiple(PEDESTAL, left, right);
		//map[left] = map[right] = PEDESTAL;
		for (int i=left+1; i < right; i++) {
			set(i, EMPTY_SP);
		}
		
		int exit = (TOP - 1) * width() + CENTER;
		interactiveAreas.add(new Exit().setPos(this, exit));
		set(getExitPos(), LOCKED_EXIT);
		
		arenaDoor = (TOP + HALL_HEIGHT) * width() + CENTER;
		set(arenaDoor, Terrain.DOOR);
		
		Painter.fill( this, LEFT, TOP + HALL_HEIGHT + 1, HALL_WIDTH, CHAMBER_HEIGHT, EMPTY );
		Painter.fill( this, LEFT, TOP + HALL_HEIGHT + 1, HALL_WIDTH, 1, BOOKSHELF);
		set(arenaDoor + width(), EMPTY);
		Painter.fill( this, LEFT, TOP + HALL_HEIGHT + 1, 1, CHAMBER_HEIGHT, BOOKSHELF );
		Painter.fill( this, LEFT + HALL_WIDTH - 1, TOP + HALL_HEIGHT + 1, 1, CHAMBER_HEIGHT, BOOKSHELF );
		
		int entrance = (TOP + HALL_HEIGHT + 3 + Random.Int( CHAMBER_HEIGHT - 2 )) * width() + LEFT + (/*1 +*/ Random.Int( HALL_WIDTH-2 ));
		interactiveAreas.add(new Entrance().setPos(this, entrance));
		set(getEntrancePos(),  ENTRANCE);
		
		for (int i=0; i < length() - width(); i++) {
			if (getTerrain(i) == EMPTY && Random.Int( 10 ) == 0) {
				set(i, EMPTY_DECO);
			} else if (getTerrain(i) == WALL
					&& DungeonTileSheet.floorTile(getTerrain(i + width()))
					&& Random.Int( 21 - Dungeon.depth) == 0) {
				set(i, WALL_DECO);
			}
		}
		
		return true;
	}
	
	public int pedestal( boolean left ) {
		if (left) {
			return (TOP + HALL_HEIGHT / 2) * width() + CENTER - 2;
		} else {
			return (TOP + HALL_HEIGHT / 2) * width() + CENTER + 2;
		}
	}
	
	@Override
	protected void createMobs() {
	}
	
	public Actor respawner() {
		return null;
	}
	
	@Override
	protected void createItems() {
		Item item = Bones.get();
		if (item != null) {
			int pos;
			do {
				pos =
					Random.IntRange( LEFT + 1, LEFT + HALL_WIDTH - 2 ) +
					Random.IntRange( TOP + HALL_HEIGHT + 2, TOP + HALL_HEIGHT  + CHAMBER_HEIGHT ) * width();
			} while (pos == getEntrancePos());
			drop( item, pos ).setHauntedIfCursed().type = Heap.Type.REMAINS;
		}
	}
	
	@Override
	public int randomRespawnCell(Char target) {
		int cell;
		do {
			cell = getEntrancePos() + PathFinder.NEIGHBOURS8[Random.Int(8)];
		} while (!passable(cell) || Actor.findChar(cell) != null);
		return cell;
	}
	
	@Override
	public void occupyCell( Char ch ) {
		
		super.occupyCell( ch );
		
		if (!enteredArena && outsideEntraceRoom( ch.pos ) && ch == Dungeon.hero) {
			
			enteredArena = true;
			seal();
			
			for (Mob m : mobs){
				//bring the first ally with you
				if (m.alignment == Char.Alignment.ALLY && !m.properties().contains(Char.Property.IMMOVABLE)){
					m.pos = Dungeon.hero.pos + (Random.Int(2) == 0 ? +1 : -1);
					m.sprite.place(m.pos);
					break;
				}
			}
			
			King boss = Mob.create(King.class, this);
			boss.state = boss.WANDERING;
			int count = 0;
			do {
				boss.pos = Random.Int( length() );
			} while (
				!passable(boss.pos) ||
				!outsideEntraceRoom( boss.pos ) ||
				(heroFOV[boss.pos] && count++ < 20));
			GameScene.add( boss );

			if (heroFOV[boss.pos]) {
				boss.notice();
				boss.sprite.alpha( 0 );
				boss.sprite.parent.add( new AlphaTweener( boss.sprite, 1, 0.1f ) );
			}

			set( arenaDoor, LOCKED_DOOR );
			GameScene.updateMap( arenaDoor );
			Dungeon.observe();
		}
	}
	
	@Override
	public Heap drop( Item item, int cell ) {
		
		if (!keyDropped && item instanceof SkeletonKey) {
			
			keyDropped = true;
			unseal();
			
			set( arenaDoor, Terrain.DOOR );
			GameScene.updateMap( arenaDoor );
			Dungeon.observe();
		}
		
		return super.drop( item, cell );
	}
	
	private boolean outsideEntraceRoom( int cell ) {
		return cell / width() < arenaDoor / width();
	}
	
	@Override
	public String tileName( Terrain tile ) {
		switch (tile) {
			case WATER:
				return Messages.get(CityLevel.class, "water_name");
			case HIGH_GRASS:
				return Messages.get(CityLevel.class, "high_grass_name");
			default:
				return super.tileName( tile );
		}
	}
	
	@Override
	public String tileDesc( Terrain tile) {
		switch (tile) {
			case ENTRANCE:
				return Messages.get(CityLevel.class, "entrance_desc");
			case EXIT:
				return Messages.get(CityLevel.class, "exit_desc");
			case WALL_DECO:
			case EMPTY_DECO:
				return Messages.get(CityLevel.class, "deco_desc");
			case EMPTY_SP:
				return Messages.get(CityLevel.class, "sp_desc");
			case STATUE:
			case STATUE_SP:
				return Messages.get(CityLevel.class, "statue_desc");
			case BOOKSHELF:
				return Messages.get(CityLevel.class, "bookshelf_desc");
			default:
				return super.tileDesc( tile );
		}
	}
	
	@Override
	public Group addVisuals( ) {
		super.addVisuals();
		CityLevel.addCityVisuals(this, visuals);
		return visuals;
	}
}
