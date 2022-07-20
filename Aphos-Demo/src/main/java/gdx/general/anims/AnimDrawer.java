package gdx.general.anims;

import com.badlogic.gdx.math.Vector2;
import libgdx.gui.generic.GroupX;
import main.system.datatypes.DequeImpl;

public class AnimDrawer extends GroupX {
    DequeImpl<SpriteAnim> anims = new DequeImpl<>();

    public SpriteAnim add(Vector2 pos, String spritePath, ActionAnims.DUMMY_ANIM_TYPE type) {
        SpriteAnim anim = new SpriteAnim(spritePath, pos, type);
        anims.add(anim);
        addActor(anim);
        return anim;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        anims.stream().filter(anim-> anim.isFinished()).forEach(anim-> {
            removeActor(anim);
            anims.remove(anim);
            anim.finished();
        });

    }


}
