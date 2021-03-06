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

package com.shatteredpixel.yasd.general.windows;

import com.shatteredpixel.yasd.general.CustomGame;
import com.shatteredpixel.yasd.general.PPDGame;
import com.shatteredpixel.yasd.general.PPDSettings;
import com.shatteredpixel.yasd.general.messages.Messages;
import com.shatteredpixel.yasd.general.scenes.HeroSelectScene;
import com.shatteredpixel.yasd.general.scenes.PixelScene;
import com.shatteredpixel.yasd.general.ui.CheckBox;
import com.shatteredpixel.yasd.general.ui.Icons;
import com.shatteredpixel.yasd.general.ui.OptionSlider;
import com.shatteredpixel.yasd.general.ui.RedButton;
import com.shatteredpixel.yasd.general.ui.RenderedTextBlock;
import com.shatteredpixel.yasd.general.ui.Window;

import java.util.ArrayList;

//FIXME use multiple tabs as there are too many modifiers now for one tab
public class WndCustomGame extends Window {
    private static final int WIDTH	    = 120;

    private static final int SLIDER_HEIGHT	= 24;
    private static final int BTN_HEIGHT	    = 18;
    private static final float GAP          = 2;

    private ArrayList<OptionSlider> sliders = new ArrayList<>();
    private ArrayList<CheckBox> checkBoxes = new ArrayList<>();

    public WndCustomGame() {
        RenderedTextBlock title = PixelScene.renderTextBlock(Messages.get(this, "title"), 9);
        title.hardlight(TITLE_COLOR);
        title.setPos(0, 0);
        add(title);

        float pos = title.bottom() + GAP;

        for (CustomGame.Modifier modifier : CustomGame.Modifier.values()) {
            //0.5x to 4x (gets divided by 10)
            OptionSlider slider = new OptionSlider(Messages.get(this, modifier.name()), "0.5x", "4x", 1, 8) {
                @Override
                protected void onChange() {
                    modifier.setGlobalValue(getSelectedValue() / 2f);
                }
            };
            //Set to 1 (default)
            slider.setSelectedValue((int) (modifier.getGlobal() * 2));
            slider.setRect(0, pos, WIDTH, SLIDER_HEIGHT);
            sliders.add(slider);
            add(slider);
            pos = slider.bottom() + GAP;
        }

        for (CustomGame.Toggle toggle : CustomGame.Toggle.values()) {
            CheckBox checkBox = new CheckBox(Messages.get(this, toggle.name())) {
                @Override
                protected void onClick() {
                    super.onClick();
                    toggle.setGlobalValue(checked());
                }
            };
            checkBox.checked(toggle.getGlobal());
            checkBox.setRect(0, pos, WIDTH, BTN_HEIGHT);
            checkBoxes.add(checkBox);
            add(checkBox);
            pos = checkBox.bottom() + GAP;
        }

        RedButton btnChals = new RedButton(Messages.get(this, "challenges")) {

            @Override
            protected void onClick() {
                PPDGame.scene().addToFront(new WndChallenges(PPDSettings.challenges(), true) {
                    public void onBackPressed() {
                        super.onBackPressed();
                        icon(Icons.get(PPDSettings.challenges() > 0 ? Icons.CHALLENGE_ON : Icons.CHALLENGE_OFF));
                    }
                });
            }
        };
        btnChals.setRect(0, pos, WIDTH, BTN_HEIGHT);
        btnChals.icon(Icons.get(PPDSettings.challenges() > 0 ? Icons.CHALLENGE_ON : Icons.CHALLENGE_OFF));
        add(btnChals);

        pos = btnChals.bottom() + GAP;

        RedButton btnDefaults = new RedButton(Messages.get(this, "defaults")) {

            @Override
            protected void onClick() {
                //Reset all modifiers
                for (CustomGame.Modifier modifier : CustomGame.Modifier.values())
                    modifier.setGlobalValue(1f);
                //Visually reset modifiers
                for (OptionSlider slider : sliders) {
                    slider.setSelectedValue(2);
                }

                //Reset all toggles
                for (CustomGame.Toggle toggle : CustomGame.Toggle.values())
                    toggle.setGlobalValue(false);
                //Visually rest toggles
                for (CheckBox checkBox : checkBoxes) {
                    checkBox.checked(false);
                }

                //Reset challenges
                PPDSettings.challenges(0);
                //Visually reset challenges
                btnChals.icon(Icons.get(Icons.CHALLENGE_OFF));
            }
        };
        btnDefaults.setRect(0, pos, WIDTH, BTN_HEIGHT);
        add(btnDefaults);

        pos = btnDefaults.bottom() + GAP;

        RedButton btnStart = new RedButton(Messages.get(this, "start")) {
            @Override
            protected void onClick() {
                super.onClick();
                if (CustomGame.calcTotalGlobalDifficultyFactor() < CustomGame.DIFFICULTY_MIN_BADGES) {
                    PPDGame.scene().addToFront(new WndOptions(Messages.get(WndCustomGame.this, "warning"), Messages.get(WndCustomGame.this, "difficulty_too_low"), Messages.get(WndCustomGame.this, "confirm"), Messages.get(WndCustomGame.this, "cancel")) {
                        @Override
                        protected void onSelect(int index) {
                            super.onSelect(index);
                            if (index == 0) {
                                HeroSelectScene.doInitRun(true);
                            }
                            hide();
                        }
                    });
                } else {
                    HeroSelectScene.doInitRun(true);
                }
            }
        };
        btnStart.setRect(0, pos, WIDTH, BTN_HEIGHT);
        add(btnStart);

        pos = btnStart.bottom() + GAP;

        resize(WIDTH, (int) pos);
    }
}
