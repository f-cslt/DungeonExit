package utils;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * SoundManager handles all audio playback in the game.
 * It supports background music and sound effects.
 */
public class SoundManager {
    private static SoundManager instance;
    private Clip backgroundMusic;
    private final Map<String, byte[]> soundEffectData;
    private final Map<String, AudioFormat> soundEffectFormats;
    private float volume = 0.5f; // Default volume (0.0 to 1.0)
    private boolean isMuted = false;

    private SoundManager() {
        soundEffectData = new HashMap<>();
        soundEffectFormats = new HashMap<>();
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    /**
     * Plays background music from a file
     * @param filePath Path to the music file
     */
    public void playBackgroundMusic(String filePath) {
        try {
            // Stop current music if playing
            if (backgroundMusic != null && backgroundMusic.isRunning()) {
                backgroundMusic.stop();
                backgroundMusic.close();
            }

            // Load and play new music
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File(filePath));
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioIn);
            
            // Set volume
            FloatControl gainControl = (FloatControl) backgroundMusic.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
            gainControl.setValue(dB);
            
            // Loop continuously
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error playing background music: " + e.getMessage());
        }
    }

    /**
     * Loads a sound effect for later use
     * @param name Name to reference the sound effect
     * @param filePath Path to the sound file
     */
    public void loadSoundEffect(String name, String filePath) {
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File(filePath));
            AudioFormat format = audioIn.getFormat();
            
            // Read all the audio data into a byte array
            byte[] data = new byte[(int) (audioIn.getFrameLength() * format.getFrameSize())];
            audioIn.read(data);
            
            // Store the format and data
            soundEffectFormats.put(name, format);
            soundEffectData.put(name, data);
            
            audioIn.close();
        } catch (UnsupportedAudioFileException | IOException e) {
            System.err.println("Error loading sound effect: " + e.getMessage());
        }
    }

    /**
     * Plays a loaded sound effect
     * @param name Name of the sound effect to play
     */
    public void playSoundEffect(String name) {
        if (isMuted) return;
        
        byte[] data = soundEffectData.get(name);
        AudioFormat format = soundEffectFormats.get(name);
        
        if (data != null && format != null) {
            try {
                // Create a new clip from the preloaded data
                Clip clip = AudioSystem.getClip();
                clip.open(format, data, 0, data.length);
                
                // Set volume
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
                gainControl.setValue(dB);
                
                // Play the sound
                clip.start();
                
                // Add a listener to close the clip when it's done playing
                clip.addLineListener(e -> {
                    if (e.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                });
            } catch (LineUnavailableException e) {
                System.err.println("Error playing sound effect: " + e.getMessage());
            }
        }
    }

    /**
     * Sets the volume for all audio
     * @param volume Volume level (0.0 to 1.0)
     */
    public void setVolume(float volume) {
        this.volume = Math.max(0.0f, Math.min(1.0f, volume));
        
        // Update background music volume
        if (backgroundMusic != null) {
            FloatControl gainControl = (FloatControl) backgroundMusic.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
            gainControl.setValue(dB);
        }
    }

    /**
     * Toggles mute state
     */
    public void toggleMute() {
        isMuted = !isMuted;
        if (isMuted) {
            if (backgroundMusic != null) {
                backgroundMusic.stop();
            }
        } else {
            if (backgroundMusic != null) {
                backgroundMusic.start();
            }
        }
    }

    /**
     * Stops all audio playback
     */
    public void stopAll() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.close();
        }
    }
} 