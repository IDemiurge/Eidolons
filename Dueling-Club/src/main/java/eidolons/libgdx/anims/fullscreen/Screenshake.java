package eidolons.libgdx.anims.fullscreen;

import java.util.Random;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;

public class Screenshake {
    float[] samples;
    Random rand = new Random();
    float internalTimer = 0;
    float shakeDuration = 0;

    int duration = 5; // In seconds, make longer if you want more variation
    int frequency = 35; // hertz
    float amplitude = 20; // how much you want to shake
    boolean falloff = true; // if the shake should decay as it expires

    float coefY = 1f;
    float coefX = 1f;

    int sampleCount;
    private Vector2 center;

    private float origDur;
    Interpolation interpolation = Interpolation.fade;

    public enum ScreenShakeTemplate {
        SLIGHT(25, 15),
        MEDIUM(30, 25),
        HARD(35, 35),
        BRUTAL(40, 45),

        VERTICAL(50, 35),
        HORIZONTAL(50, 35),
        ;
        int duration = 5; // In seconds, make longer if you want more variation
        int frequency = 35; // hertz
        float amplitude = 20; // how much you want to shake
        boolean falloff = true; // if the shake should decay as it expires

        ScreenShakeTemplate(int frequency, float amplitude) {
            this.frequency = frequency;
            this.amplitude = amplitude;
        }
    }

    public Screenshake(float shakeDuration, Boolean vertical, ScreenShakeTemplate template) {
        this.shakeDuration = shakeDuration;
        duration = template.duration;
//        duration =fullDuration!=0 ? fullDuration: template.duration;
        frequency = template.frequency;
        amplitude = template.amplitude;
        if (vertical != null) {
            coefX = vertical ? 0.66f : 1.33f;
            coefY = !vertical ? 0.66f : 1.33f;
        } else {
            coefY = 1f;
            coefX = 1f;
        }
        falloff = template.falloff;
        init();
    }

    public Screenshake(float shakeDuration, int duration, int frequency, float amplitude, boolean falloff, float coefY, float coefX) {
        this.shakeDuration = shakeDuration;
        this.duration = duration;
        this.frequency = frequency;
        this.amplitude = amplitude;
        this.falloff = falloff;
        this.coefY = coefY;
        this.coefX = coefX;
        init();
    }

    private void init() {
        sampleCount = duration * frequency;
        samples = new float[sampleCount];
        for (int i = 0; i < sampleCount; i++) {
            samples[i] = rand.nextFloat() * 2f - 1f;
        }

    }

    /**
     * Will make the camera shake for the duration passed in in seconds
     *
     * @param d duration of the shake in seconds
     */
    public void shake(float d) {
        shakeDuration = d;
    }

    /**
     * Called every frame will shake the camera if it has a shake duration
     *
     * @param dt     Gdx.graphics.getDeltaTime() or your dt in seconds
     * @param camera your camera
     * @param c      Where the camera should stay centered on
     */
    public boolean update(float dt, Camera camera, Vector2 c) {
        if (this.center == null) {
            this.center = c;
            origDur = shakeDuration;
        }
        internalTimer += dt;
        if (internalTimer > duration) internalTimer -= duration;
        if (shakeDuration > 0) {
            shakeDuration -= dt;
            float shakeTime = (internalTimer * frequency);
            int first = (int) shakeTime;
            int second = (first + 1) % sampleCount;
            int third = (first + 2) % sampleCount;
            float deltaT = shakeTime - (int) shakeTime;
            float deltaX = samples[first] * deltaT + samples[second] * (1f - deltaT);
            float deltaY = samples[second] * deltaT + samples[third] * (1f - deltaT);
            float ampl = amplitude;
            if (interpolation != null) {
                float perc = shakeDuration / origDur;
                if (perc > 0) {
                    ampl += -amplitude * interpolation.apply(perc) / 10;
//                    main.system.auxiliary.log.LogMaster.dev(perc + ": amplitude " + amplitude + " interpolation " +
//                            " to " + ampl);
                }
            }

            camera.position.x = center.x + coefX * deltaX * ampl * (falloff ? Math.min(shakeDuration, 1f) : 1f);
            camera.position.y = center.y + coefY * deltaY * ampl * (falloff ? Math.min(shakeDuration, 1f) : 1f);
//            camera.update();
            return true;
        }
        return false;
    }

}

