package main.system.audio;

/**
 * Created by JustMe on 11/15/2017.
 */
public interface AudioFile  {
    public void play();
    public void pause ();

    /** Stops a playing or paused Music instance. Next time play() is invoked the Music will start from the beginning. */
    public void stop ();

    /** @return whether this music stream is playing */
    public boolean isPlaying ();

    /** Sets whether the music stream is looping. This can be called at any time, whether the stream is playing.
     *
     * @param isLooping whether to loop the stream */
    public void setLooping (boolean isLooping);

    /** @return whether the music stream is playing. */
    public boolean isLooping ();

    /** Sets the volume of this music stream. The volume must be given in the range [0,1] with 0 being silent and 1 being the
     * maximum volume.
     *
     * @param volume */
    public void setVolume (float volume);

    /** @return the volume of this music stream. */
    public float getVolume ();

    /** Sets the panning and volume of this music stream.
     * @param pan panning in the range -1 (full left) to 1 (full right). 0 is center position.
     * @param volume the volume in the range [0,1]. */
    public void setPan (float pan, float volume);

    /** Set the playback position in seconds. */
    public void setPosition (float position);

    /** Returns the playback position in seconds. */
    public float getPosition ();

    /** Needs to be called when the Music is no longer needed. */
    public void dispose ();
}
