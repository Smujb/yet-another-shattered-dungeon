package com.shatteredpixel.yasd.actors.buffs;

import com.shatteredpixel.yasd.Dungeon;
import com.shatteredpixel.yasd.actors.Char;
import com.shatteredpixel.yasd.actors.mobs.Mob;
import com.shatteredpixel.yasd.messages.Messages;
import com.watabou.utils.Bundle;

public class Agression extends FlavourBuff {

    public static class Aggression extends FlavourBuff {

        public static final float DURATION = 20f;

        {
            type = buffType.NEGATIVE;
            announced = true;
        }

        @Override
        public void storeInBundle( Bundle bundle ) {
            super.storeInBundle(bundle);
        }

        @Override
        public void restoreFromBundle( Bundle bundle ) {
            super.restoreFromBundle( bundle );
        }

        @Override
        public void detach() {
            //if our target is an enemy, reset the aggro of any enemies targeting it
            if (target.isAlive()) {
                if (target.alignment == Char.Alignment.ENEMY) {
                    for (Mob m : Dungeon.level.mobs) {
                        if (m.alignment == Char.Alignment.ENEMY && m.isTargeting(target)) {
                            m.aggro(null);
                        }
                    }
                }
            }
            super.detach();

        }

        @Override
        public String toString() {
            return Messages.get(this, "name");
        }

    }
}
