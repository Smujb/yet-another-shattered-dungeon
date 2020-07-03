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

package com.shatteredpixel.yasd.general.scenes;

import com.shatteredpixel.yasd.general.Assets;
import com.shatteredpixel.yasd.general.Badges;
import com.shatteredpixel.yasd.general.Chrome;
import com.shatteredpixel.yasd.general.Difficulty;
import com.shatteredpixel.yasd.general.Dungeon;
import com.shatteredpixel.yasd.general.GamesInProgress;
import com.shatteredpixel.yasd.general.LevelHandler;
import com.shatteredpixel.yasd.general.MainGame;
import com.shatteredpixel.yasd.general.YASDSettings;
import com.shatteredpixel.yasd.general.actors.hero.HeroClass;
import com.shatteredpixel.yasd.general.actors.hero.HeroSubClass;
import com.shatteredpixel.yasd.general.journal.Journal;
import com.shatteredpixel.yasd.general.messages.Messages;
import com.shatteredpixel.yasd.general.sprites.ItemSprite;
import com.shatteredpixel.yasd.general.sprites.ItemSpriteSheet;
import com.shatteredpixel.yasd.general.ui.ActionIndicator;
import com.shatteredpixel.yasd.general.ui.ExitButton;
import com.shatteredpixel.yasd.general.ui.IconButton;
import com.shatteredpixel.yasd.general.ui.Icons;
import com.shatteredpixel.yasd.general.ui.RenderedTextBlock;
import com.shatteredpixel.yasd.general.ui.StyledButton;
import com.shatteredpixel.yasd.general.ui.Window;
import com.shatteredpixel.yasd.general.windows.WndChallenges;
import com.shatteredpixel.yasd.general.windows.WndMessage;
import com.shatteredpixel.yasd.general.windows.WndStartGame;
import com.shatteredpixel.yasd.general.windows.WndTabbed;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.PointerArea;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.GameMath;

import java.util.ArrayList;

public class HeroSelectScene extends PixelScene {

	private Image background;
	private NinePatch border;
	private RenderedTextBlock prompt;

	//fading UI elements
	private ArrayList<StyledButton> heroBtns = new ArrayList<>();
	private StyledButton startBtn;
	private IconButton infoButton;
	private IconButton challengeButton;
	private IconButton btnExit;

	@Override
	public void create() {
		super.create();

		Badges.loadGlobal();
		Journal.loadGlobal();

		background = new Image(HeroClass.WARRIOR.splashArt()){
			@Override
			public void update() {
				if (rm > 1f){
					rm -= Game.elapsed;
					gm = bm = rm;
				} else {
					rm = gm = bm = 1;
				}
			}
		};
		background.scale.set(Camera.main.height/background.height);

		background.x = (Camera.main.width - background.width())/2f;
		background.y = (Camera.main.height - background.height())/2f;
		background.visible = false;
		PixelScene.align(background);

		border = Chrome.get(Chrome.Type.WINDOW_SILVER);
		border.size(background.width()+border.marginHor()-4, background.height()+border.marginVer()-4);
		border.x = background.x - border.marginLeft()+2;
		border.y = background.y - border.marginTop()+2;
		border.visible = false;
		add(border);
		add(background);

		prompt = PixelScene.renderTextBlock(Messages.get(WndStartGame.class, "title"), 12);
		prompt.hardlight(Window.TITLE_COLOR);
		prompt.setPos( (Camera.main.width - prompt.width())/2f, (Camera.main.height - HeroBtn.HEIGHT - prompt.height() - 4));
		PixelScene.align(prompt);
		add(prompt);

		startBtn = new StyledButton(Chrome.Type.GREY_BUTTON_TR, ""){
			@Override
			protected void onClick() {
				super.onClick();

				if (GamesInProgress.selectedClass == null) return;

				super.onClick();

				//GamesInProgress.curSlot = slot;
				Dungeon.hero = null;
				ActionIndicator.action = null;
				Dungeon.difficulty = Difficulty.fromInt(YASDSettings.difficulty());//I could just call YASDSettings.difficulty() every time I want to check difficulty, but that would mean that changing it on separate runs would interfere with each other.
				YASDSettings.lastClass(GamesInProgress.selectedClass);
				if (YASDSettings.intro()) {
					YASDSettings.intro( false );
					Game.switchScene( IntroScene.class );
				} else {
					LevelHandler.doInit();
				}
			}
		};
		startBtn.icon(Icons.get(Icons.ENTER));
		startBtn.setSize(80, 21);
		startBtn.setPos((Camera.main.width - startBtn.width())/2f, (Camera.main.height - HeroBtn.HEIGHT + 2 - startBtn.height()));
		add(startBtn);
		startBtn.visible = false;

		infoButton = new IconButton(Icons.get(Icons.INFO)){
			@Override
			protected void onClick() {
				super.onClick();
				MainGame.scene().addToFront(new WndHeroInfo(GamesInProgress.selectedClass));
			}
		};
		infoButton.visible = false;
		infoButton.setSize(21, 21);
		add(infoButton);

		HeroClass[] classes = HeroClass.values();

		int btnWidth = HeroBtn.MIN_WIDTH;
		int curX = (Camera.main.width - btnWidth * classes.length)/2;
		if (curX > 0){
			btnWidth += Math.min(curX/(classes.length/2), 15);
			curX = (Camera.main.width - btnWidth * classes.length)/2;
		}

		int heroBtnleft = curX;
		for (HeroClass cl : classes){
			HeroBtn button = new HeroBtn(cl);
			button.setRect(curX, Camera.main.height-HeroBtn.HEIGHT+3, btnWidth, HeroBtn.HEIGHT);
			curX += btnWidth;
			add(button);
			heroBtns.add(button);
		}

		challengeButton = new IconButton(
				Icons.get( YASDSettings.challenges() > 0 ? Icons.CHALLENGE_ON :Icons.CHALLENGE_OFF)){
			@Override
			protected void onClick() {
				MainGame.scene().addToFront(new WndChallenges(YASDSettings.challenges(), true) {
					public void onBackPressed() {
						super.onBackPressed();
						icon(Icons.get(YASDSettings.challenges() > 0 ? Icons.CHALLENGE_ON : Icons.CHALLENGE_OFF));
					}
				} );
			}

			@Override
			public void update() {
				if( !visible && GamesInProgress.selectedClass != null){
					visible = true;
				}
				super.update();
			}
		};
		challengeButton.setRect(heroBtnleft + 16, Camera.main.height-HeroBtn.HEIGHT-16, 21, 21);
		challengeButton.visible = false;

		if (DeviceCompat.isDebug() || Badges.isUnlocked(Badges.Badge.VICTORY)){
			add(challengeButton);
		} else {
			Dungeon.challenges = 0;
			YASDSettings.challenges(0);
		}

		btnExit = new ExitButton();
		btnExit.setPos( Camera.main.width - btnExit.width(), 0 );
		add( btnExit );

		PointerArea fadeResetter = new PointerArea(0, 0, Camera.main.width, Camera.main.height){
			@Override
			public boolean onSignal(PointerEvent event) {
				resetFade();
				return false;
			}
		};
		add(fadeResetter);
		resetFade();

		if (GamesInProgress.selectedClass != null){
			setSelectedHero(GamesInProgress.selectedClass);
		}

		fadeIn();

	}

	private void setSelectedHero(HeroClass cl){
		GamesInProgress.selectedClass = cl;

		background.texture( cl.splashArt() );
		background.visible = true;
		background.hardlight(1.5f,1.5f,1.5f);
		border.visible = true;

		prompt.visible = false;
		startBtn.visible = true;
		startBtn.text(Messages.titleCase(cl.title()));
		startBtn.textColor(Window.TITLE_COLOR);
		startBtn.setSize(startBtn.reqWidth() + 8, 21);
		startBtn.setPos((Camera.main.width - startBtn.width())/2f, startBtn.top());
		PixelScene.align(startBtn);

		infoButton.visible = true;
		infoButton.setPos(startBtn.right(), startBtn.top());

		challengeButton.visible = true;
		challengeButton.setPos(startBtn.left()-challengeButton.width(), startBtn.top());
	}

	private float uiAlpha;

	@Override
	public void update() {
		super.update();
		//do not fade when a window is open
		for (Object v : members){
			if (v instanceof Window) resetFade();
		}
		if (GamesInProgress.selectedClass != null) {
			if (uiAlpha > 0f){
				uiAlpha -= Game.elapsed/4f;
			}
			float alpha = GameMath.gate(0f, uiAlpha, 1f);
			for (StyledButton b : heroBtns){
				b.alpha(alpha);
			}
			startBtn.alpha(alpha);
			btnExit.icon().alpha(alpha);
			challengeButton.icon().alpha(alpha);
			infoButton.icon().alpha(alpha);
		}
	}

	private void resetFade(){
		//starts fading after 4 seconds, fades over 4 seconds.
		uiAlpha = 2f;
	}

	private class HeroBtn extends StyledButton {

		private HeroClass cl;

		private static final int MIN_WIDTH = 20;
		private static final int HEIGHT = 24;

		HeroBtn ( HeroClass cl ){
			super(Chrome.Type.GREY_BUTTON_TR, "");

			this.cl = cl;

			icon(new Image(cl.spritesheet(), 0, 90, 12, 15));

		}

		@Override
		public void update() {
			super.update();
			if (cl != GamesInProgress.selectedClass){
				if (cl.locked()){
					icon.brightness(0.1f);
				} else {
					icon.brightness(0.6f);
				}
			} else {
				icon.brightness(1f);
			}
		}

		@Override
		protected void onClick() {
			super.onClick();

			if( cl.locked() ){
				MainGame.scene().addToFront( new WndMessage(cl.unlockMsg()));
			} else if (GamesInProgress.selectedClass == cl) {
				MainGame.scene().add(new WndHeroInfo(cl));
			} else {
				setSelectedHero(cl);
			}
		}
	}

	private static class WndHeroInfo extends WndTabbed {

		private RenderedTextBlock info;

		private int WIDTH = 120;
		private int MARGIN = 4;
		private int INFO_WIDTH = WIDTH - MARGIN*2;

		public WndHeroInfo( HeroClass cl ){

			Tab tab;
			Image[] tabIcons;
			switch (cl){
				case WARRIOR: default:
					tabIcons = new Image[]{
							new ItemSprite(ItemSpriteSheet.SEAL, null),
							new ItemSprite(ItemSpriteSheet.WORN_SHORTSWORD, null),
							new ItemSprite(ItemSpriteSheet.RATION, null)
					};
					break;
				case MAGE:
					tabIcons = new Image[]{
							new ItemSprite(ItemSpriteSheet.MAGES_STAFF, null),
							new ItemSprite(ItemSpriteSheet.HOLDER, null),
							new ItemSprite(ItemSpriteSheet.WAND_MAGIC_MISSILE, null)
					};
					break;
				case ROGUE:
					tabIcons = new Image[]{
							new ItemSprite(ItemSpriteSheet.ARTIFACT_CLOAK, null),
							new ItemSprite(ItemSpriteSheet.DAGGER, null),
							Icons.get(Icons.DEPTH)
					};
					break;
				case HUNTRESS:
					tabIcons = new Image[]{
							new ItemSprite(ItemSpriteSheet.SPIRIT_BOW, null),
							new ItemSprite(ItemSpriteSheet.GLOVES, null),
							new Image(Assets.Environment.TILES_SEWERS, 112, 96, 16, 16 )
					};
					break;
			}

			tab = new IconTab( tabIcons[0] ){
				@Override
				protected void select(boolean value) {
					super.select(value);
					if (value){
						info.text(Messages.get(cl, cl.name() + "_desc_item"), INFO_WIDTH);
					}
				}
			};
			add(tab);

			tab = new IconTab( tabIcons[1] ){
				@Override
				protected void select(boolean value) {
					super.select(value);
					if (value){
						info.text(Messages.get(cl, cl.name() + "_desc_loadout"), INFO_WIDTH);
					}
				}
			};
			add(tab);

			tab = new IconTab( tabIcons[2] ){
				@Override
				protected void select(boolean value) {
					super.select(value);
					if (value){
						info.text(Messages.get(cl, cl.name() + "_desc_misc"), INFO_WIDTH);
					}
				}
			};
			add(tab);

			tab = new IconTab(new ItemSprite(ItemSpriteSheet.MASTERY, null)){
				@Override
				protected void select(boolean value) {
					super.select(value);
					if (value){
						String msg = Messages.get(cl, cl.name() + "_desc_subclasses");
						for (HeroSubClass sub : cl.subClasses()){
							msg += "\n\n" + sub.desc();
						}
						info.text(msg, INFO_WIDTH);
					}
				}
			};
			add(tab);

			info = PixelScene.renderTextBlock(6);
			info.setPos(MARGIN, MARGIN);
			add(info);

			select(0);

		}

		@Override
		public void select(Tab tab) {
			super.select(tab);
			resize(WIDTH, (int)info.bottom()+MARGIN);
			layoutTabs();
		}
	}
}