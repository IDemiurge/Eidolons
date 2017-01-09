package main.libgdx.anims.custom;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.particles.emitters.Emitter;

import java.awt.*;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created by JustMe on 1/9/2017.
 */
public class Animated {
    int time;
    Point origin;
    Point destination;
    Shape shape;
    List<Emitter> emitterList;
    int lightEmission;

    Supplier<Texture> textureSupplier;

}
