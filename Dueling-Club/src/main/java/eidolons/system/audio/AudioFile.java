package eidolons.system.audio;

/**
 * Created by JustMe on 11/15/2017.
 */
public interface AudioFile {
    void play();

    void pause();

    /**
     * Stops a playing or paused Music instance. Next time play() is invoked the Music will start from the beginning.
     */
    void stop();

    /**
     * @return whether this music stream is playing
     */
    boolean isPlaying();

    /**
     * @return whether the music stream is playing.
     */
    boolean isLooping();

    /**
     * Sets whether the music stream is looping. This can be called at any time, whether the stream is playing.
     *
     * @param isLooping whether to loop the stream
     */
    void setLooping(boolean isLooping);

    /**
     * @return the volume of this music stream.
     */
    float getVolume();

    /**
     * Sets the volume of this music stream. The volume must be given in the range [0,1] with 0 being silent and 1 being the
     * maximum volume.
     *
     * @param volume
     */
    void setVolume(float volume);

    /**
     * Sets the panning and volume of this music stream.
     *
     * @param pan    panning in the range -1 (full left) to 1 (full right). 0 is center position.
     * @param volume the volume in the range [0,1].
     */
    void setPan(float pan, float volume);

    /**
     * Returns the playback position in seconds.
     */
    float getPosition();

    /**
     * Set the playback position in seconds.
     */
    void setPosition(float position);

    /**
     * Needs to be called when the Music is no longer needed.
     */
    void dispose();
}
