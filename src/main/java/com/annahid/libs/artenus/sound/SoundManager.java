/*
 *  This file is part of the Artenus 2D Framework.
 *  Copyright (C) 2015  Hessan Feghhi
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Foobar is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.annahid.libs.artenus.sound;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.util.SparseIntArray;

import java.io.IOException;

/**
 * Provides audio functionality for games. You can handle sound effects
 * and music play-back easily through static methods provided by this class.
 *
 * @author Hessan Feghhi
 */
@SuppressWarnings("UnusedDeclaration")
public final class SoundManager {
    private static final SparseIntArray soundPoolMap = new SparseIntArray();
    private static SoundPool soundPool;
    private static Context context = null;
    private static MediaPlayer bgPlayer = null;
    private static int bgLength = 0;
    private static String currentMusic = null;
    private static float generalVolume = 1.0f;

    /**
     * Adds sounds to the pool. You must add all your sound effects to the pool
     * before using them in the game.
     *
     * @param resourceIds raw resource identifiers
     */
    @SuppressWarnings("deprecation")
    public static void add(int... resourceIds) {
        for (int resourceId : resourceIds)
            soundPoolMap.put(resourceId, soundPool.load(context, resourceId, 1));
    }

    /**
     * Plays a sound effect. The sound should be already added to the pool using
     * {@link #add(int...)}.
     *
     * @param resourceId The raw resource identifier for the sound
     */
    public static void playSound(int resourceId) {
        playSound(resourceId, 1.0f);
    }

    /**
     * Plays a sound effect with the given volume. The sound should be already
     * added to the pool using
     *
     * @param resourceId The raw resource identifier for the sound
     * @param vol        The volume to play the sound. This volume is relative to the
     *                   normal play-back of the sound with the current system volume for media.
     *                   This value should be specified as a number between 0 and 1.
     */
    public static void playSound(int resourceId, float vol) {
        final AudioManager mgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        final float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
        final float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        final float volume = streamVolumeCurrent / streamVolumeMax * vol * generalVolume;
        soundPool.play(soundPoolMap.get(resourceId), volume, volume, 1, 0, 1f);
    }

    /**
     * Stops play-back of a currently playing sound.
     *
     * @param resourceId The raw resource identifier for the sound
     */
    public static void stopSound(int resourceId) {
        soundPool.stop(soundPoolMap.get(resourceId));
    }

    /**
     * Sets the general volume of the sound manager.
     *
     * @param volume The new volume which is a number between 0 and 1
     */
    public static void setGeneralVolume(float volume) {
        generalVolume = volume;

        if (bgPlayer != null) {
            if (bgPlayer.isPlaying())
                bgPlayer.setVolume(generalVolume, generalVolume);
        }
    }

    /**
     * Gets the general volume of the sound manager.
     *
     * @return A number between 0 and 1. Number 0 means the sound is mute
     * and 1 is the loudest sound.
     */
    public static float getGeneralVolume() {
        return generalVolume;
    }

    /**
     * Sets the current background music. The music will start playing
     * immediately in a loop after calling this function. Specifying
     * {@code null} for the file name will stop play-back and remove the
     * background music.
     *
     * @param fileName The file name for the music file. This name should
     *                 correspond to a file in the {@code assets} folder of the application.
     */
    public static void setMusic(String fileName) {
        setMusic(fileName, 1);
    }

    /**
     * Sets the current background music and sets the play-back volume to
     * the value specified. The music will start playing immediately in a
     * loop after calling this function. Specifying {@code null} for the
     * file name will stop play-back and remove the background music.
     *
     * @param fileName The file name for the music file
     * @param volume   Play-back volume
     */
    public static void setMusic(String fileName, float volume) {
        if (currentMusic != null)
            if (!currentMusic.equals(fileName))
                try {
                    if (bgPlayer.isPlaying())
                        stopMusic();
                } catch (Exception ex) { /* Do nothing */ }

        if (fileName == null)
            currentMusic = null;
        else if (!fileName.equals(currentMusic)) {
            bgPlayer = new MediaPlayer();

            try {
                final AssetManager assets = context.getAssets();
                final AssetFileDescriptor desc = assets.openFd(fileName);
                bgPlayer.setDataSource(desc.getFileDescriptor(), desc.getStartOffset(), desc.getLength());
                bgPlayer.prepare();
                bgPlayer.setLooping(true);
                bgPlayer.start();
                bgPlayer.setVolume(generalVolume * volume, generalVolume * volume);
                currentMusic = fileName;
            } catch (IOException ioe) {
                bgPlayer = null;
            }
        }
    }

    /**
     * Plays a music file from the {@code assets} folder of the application.
     * The music is played only once.
     *
     * @param fileName The file name for the music file
     */
    public static void playMusic(String fileName) {
        if (currentMusic != null)
            if (!currentMusic.equals(fileName))
                try {
                    if (bgPlayer.isPlaying())
                        stopMusic();
                } catch (Exception ex) { /* Do nothing */ }

        if (fileName == null)
            currentMusic = null;
        else if (!fileName.equals(currentMusic)) {
            bgPlayer = new MediaPlayer();

            try {
                final AssetManager assets = context.getAssets();
                final AssetFileDescriptor desc = assets.openFd(fileName);
                bgPlayer.setDataSource(desc.getFileDescriptor(), desc.getStartOffset(), desc.getLength());
                bgPlayer.prepare();
                bgPlayer.setVolume(generalVolume, generalVolume);
                bgPlayer.start();
                currentMusic = fileName;
            } catch (IOException ioe) {
                bgPlayer = null;
            }
        }
    }

    /**
     * Pauses play-back of the current background music. The music will
     * remain at its current position until resumed. This method has no
     * effect it there is no background music or if the music has already
     * been paused.
     */
    public static void pauseMusic() {
        if (bgPlayer == null)
            return;

        if (bgPlayer.isPlaying()) {
            bgPlayer.pause();
            bgLength = bgPlayer.getCurrentPosition();
        }
    }

    /**
     * Resumes a previously paused background music, from where it was
     * paused. This method has no effect if there is no background music
     * or if the music is not paused.
     */
    public static void resumeMusic() {
        if (bgPlayer == null)
            return;

        if (!bgPlayer.isPlaying()) {
            bgPlayer.seekTo(bgLength);
            bgPlayer.start();
        }
    }

    /**
     * Stops play-back  of the current background music and releases any
     * resources allocated for it. Resuming the music will have no effect
     * after it has been stopped. In order to restart play-back, you should
     * use {@code playMusic} or {@code setMusic} methods again.
     */
    public static void stopMusic() {
        bgPlayer.stop();
        bgPlayer.release();
        bgPlayer = null;
        bgLength = 0;
        currentMusic = null;
    }

    /**
     * Unloads all music resources and clears the sound effect pool. After
     * calling this method, you cannot play any of the sound effects that
     * were added to the pool.
     */
    public static void unloadAll() {
        if (bgPlayer != null)
            try {
                stopMusic();
            } catch (Exception ex) { /* Do nothing */ }

        for (int i = 0; i < soundPoolMap.size(); i++)
            soundPool.unload(soundPoolMap.valueAt(i));

        soundPoolMap.clear();
    }

    /**
     * Initializes the class using the given application context.
     *
     * @param context The application context
     */
    @SuppressWarnings("deprecation")
    public static void initContext(Context context) {
        SoundManager.context = context;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(12)
                    .setAudioAttributes(
                            new AudioAttributes.Builder()
                                    .setUsage(AudioAttributes.USAGE_GAME)
                                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                    .build())
                    .build();
        } else {
            soundPool = new SoundPool(12, AudioManager.STREAM_MUSIC, 0);
        }
    }

    /**
     * Determines whether the class has already been initialized.
     *
     * @return {@code true} if initialized, or {@code false} otherwise
     */
    public static boolean isContextInitialized() {
        return context != null;
    }
}
