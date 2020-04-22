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

package com.shatteredpixel.yasd.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Preferences;
import com.badlogic.gdx.utils.SharedLibraryLoader;
import com.shatteredpixel.yasd.UpdateImpl;
import com.shatteredpixel.yasd.general.GameSettings;
import com.shatteredpixel.yasd.general.MainGame;
import com.shatteredpixel.yasd.general.services.Updates;
import com.watabou.noosa.Game;
import com.watabou.utils.FileUtils;
import com.watabou.utils.Point;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class DesktopLauncher {

    public static void main (String[] args) {

        if (!DesktopLaunchValidator.verifyValidJVMState(args)){
            return;
        }

        final String title;
        if (DesktopLauncher.class.getPackage().getSpecificationTitle() == null){
            title = System.getProperty("Specification-Title");
        } else {
            title = DesktopLauncher.class.getPackage().getSpecificationTitle();
        }

        try {
            UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
        } catch (Exception e) {
            e.printStackTrace();
        }

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                throwable.printStackTrace(pw);
                pw.flush();
                JOptionPane.showMessageDialog(null, title + " has run into an error it can't recover from and has crashed, sorry about that!\n\n" +
                        /*"If you could, please email this error message to the developer [TBA]:\n\n" +*/
                        sw.toString(), title + " Has Crashed!", JOptionPane.ERROR_MESSAGE);
                Gdx.app.exit();
            }
        });

        Game.version = DesktopLauncher.class.getPackage().getSpecificationVersion();
        if (Game.version == null) {
            Game.version = System.getProperty("Specification-Version");
        }

        try {
            Game.versionCode = Integer.parseInt(DesktopLauncher.class.getPackage().getImplementationVersion());
        } catch (NumberFormatException e) {
            Game.versionCode = Integer.parseInt(System.getProperty("Implementation-Version"));
        }

        if (UpdateImpl.supportsUpdates()){
            Updates.service = UpdateImpl.getUpdateService();
        }


        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();


        config.setTitle( title );

        //TODO this is currently the same location and filenames as the old desktop codebase
        // If I want to move it now would be the time

        String basePath = "";
        if (SharedLibraryLoader.isWindows) {
            if (System.getProperties().getProperty("os.name").equals("Windows XP")) {
                basePath = "Application Data/.shatteredpixel/Shattered Pixel Dungeon/";
            } else {
                basePath = "AppData/Roaming/.shatteredpixel/Shattered Pixel Dungeon/";
            }
        } else if (SharedLibraryLoader.isMac) {
            basePath = "Library/Application Support/Shattered Pixel Dungeon/";
        } else if (SharedLibraryLoader.isLinux) {
            basePath = ".shatteredpixel/shattered-pixel-dungeon/";
        }
        config.setPreferencesConfig( basePath, Files.FileType.External );
        GameSettings.set( new Lwjgl3Preferences( "pd-prefs", basePath) );
        FileUtils.setDefaultFileProperties( Files.FileType.External, basePath );

        config.setWindowSizeLimits( 960, 640, -1, -1 );
        Point p = GameSettings.windowResolution();
        config.setWindowedMode( p.x, p.y );
        config.setAutoIconify( true );

        //we set fullscreen/maximized in the listener as doing it through the config seems to be buggy
        DesktopWindowListener listener = new DesktopWindowListener();
        config.setWindowListener( listener );
        //TODO: use YASD icons
        config.setWindowIcon( "icon_16.png", "icon_32.png", "icon_64.png", "icon_128.png", "icon_256.png" );

        new Lwjgl3Application(new MainGame(new DesktopPlatformSupport()), config);
    }
}