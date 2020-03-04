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

package com.shatteredpixel.yasd.general.journal;

import com.shatteredpixel.yasd.general.Dungeon;
import com.shatteredpixel.yasd.general.items.keys.Key;
import com.shatteredpixel.yasd.general.messages.Messages;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

import java.util.ArrayList;
import java.util.Collections;

public class Notes {
	
	public static abstract class Record implements Comparable<Record>, Bundlable {
		
		protected int yPos;
		protected int zPos;
		protected int xPos;

		public int yPos(){
			return yPos;
		}

		public int xPos(){
			return xPos;
		}

		public int zPos(){
			return zPos;
		}
		
		public abstract String desc();
		
		@Override
		public abstract boolean equals(Object obj);
		
		@Override
		public int compareTo( Record another ) {
			return another.yPos() - yPos();
		}
		
		private static final String DEPTH	= "yPos";
		
		@Override
		public void restoreFromBundle( Bundle bundle ) {
			yPos = bundle.getInt( DEPTH );
		}

		@Override
		public void storeInBundle( Bundle bundle ) {
			bundle.put( DEPTH, yPos);
		}
	}
	
	public enum Landmark {
		WELL_OF_HEALTH,
		WELL_OF_AWARENESS,
		WELL_OF_TRANSMUTATION,
		ALCHEMY,
		GARDEN,
		STATUE,
		SACRIFICIAL_FIRE,
		GHOST,
		WANDMAKER,
		TROLL,
		IMP;
		
		public String desc() {
			return Messages.get(this, name());
		}
	}
	
	public static class LandmarkRecord extends Record {
		
		protected Landmark landmark;
		
		public LandmarkRecord() {}

		public LandmarkRecord(Landmark landmark, int depth) {
			this(landmark, Dungeon.xPos, depth, Dungeon.zPos);
		}
		
		public LandmarkRecord(Landmark landmark, int xPos, int yPos, int zPos ) {
			this.landmark = landmark;
			this.xPos = xPos;
			this.zPos = zPos;
			this.yPos = yPos;
		}
		
		@Override
		public String desc() {
			return landmark.desc();
		}
		
		@Override
		public boolean equals(Object obj) {
			return (obj instanceof LandmarkRecord)
					&& landmark == ((LandmarkRecord) obj).landmark
					&& yPos() == ((LandmarkRecord) obj).yPos();
		}
		
		private static final String LANDMARK	= "landmark";
		
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			landmark = Landmark.valueOf(bundle.getString(LANDMARK));
		}
		
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put( LANDMARK, landmark.toString() );
		}
	}
	
	public static class KeyRecord extends Record {
		
		protected Key key;
		
		public KeyRecord() {}
		
		public KeyRecord( Key key ){
			this.key = key;
		}
		
		@Override
		public int yPos() {
			return key.yPos;
		}

		@Override
		public int xPos() {
			return key.xPos;
		}

		@Override
		public int zPos() {
			return key.zPos;
		}

		@Override
		public String desc() {
			return key.toString();
		}
		
		public Class<? extends Key> type(){
			return key.getClass();
		}
		
		public int quantity(){
			return key.quantity();
		}
		
		public void quantity(int num){
			key.quantity(num);
		}
		
		@Override
		public boolean equals(Object obj) {
			return (obj instanceof KeyRecord)
					&& key.isSimilar(((KeyRecord) obj).key);
		}
		
		private static final String KEY	= "key";
		
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			key = (Key) bundle.get(KEY);
		}
		
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put( KEY, key );
		}
	}
	
	private static ArrayList<Record> records;
	
	public static void reset() {
		records = new ArrayList<>();
	}
	
	private static final String RECORDS	= "records";
	
	public static void storeInBundle( Bundle bundle ) {
		bundle.put( RECORDS, records );
	}
	
	public static void restoreFromBundle( Bundle bundle ) {
		records = new ArrayList<>();
		for (Bundlable rec : bundle.getCollection( RECORDS ) ) {
			records.add( (Record) rec );
		}
	}
	
	public static boolean add( Landmark landmark ) {
		LandmarkRecord l = new LandmarkRecord( landmark, Dungeon.yPos);
		if (!records.contains(l)) {
			boolean result = records.add(new LandmarkRecord(landmark, Dungeon.xPos, Dungeon.yPos, Dungeon.zPos));
			Collections.sort(records);
			return result;
		}
		return false;
	}
	
	public static boolean remove( Landmark landmark ) {
		return records.remove( new LandmarkRecord(landmark, Dungeon.xPos, Dungeon.yPos, Dungeon.zPos) );
	}
	
	public static boolean add( Key key ){
		KeyRecord k = new KeyRecord(key);
		if (!records.contains(k)){
			boolean result = records.add(k);
			Collections.sort(records);
			return result;
		} else {
			k = (KeyRecord) records.get(records.indexOf(k));
			k.quantity(k.quantity() + key.quantity());
			return true;
		}
	}
	
	public static boolean remove( Key key ){
		KeyRecord k = new KeyRecord( key );
		if (records.contains(k)){
			k = (KeyRecord) records.get(records.indexOf(k));
			k.quantity(k.quantity() - key.quantity());
			if (k.quantity() <= 0){
				records.remove(k);
			}
			return true;
		}
		return false;
	}
	
	public static int keyCount( Key key ){
		KeyRecord k = new KeyRecord( key );
		if (records.contains(k)){
			k = (KeyRecord) records.get(records.indexOf(k));
			return k.quantity();
		} else {
			return 0;
		}
	}
	
	public static ArrayList<Record> getRecords(){
		return getRecords(Record.class);
	}
	
	public static <T extends Record> ArrayList<T> getRecords( Class<T> recordType ){
		ArrayList<T> filtered = new ArrayList<>();
		for (Record rec : records){
			if (recordType.isInstance(rec)){
				filtered.add((T)rec);
			}
		}
		return filtered;
	}
	
	public static void remove( Record rec ){
		records.remove(rec);
	}
	
}