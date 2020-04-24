package eidolons.game.netherflame.igg.story.brief;

/**
 * loadVideo!
 */
public class BriefBackground extends FullscreenAnimation {

    public BriefBackground() {
        super(true);
    }
    public BriefBackground(String background) {
        super(true);
        setUserObject(background);
    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
        if (userObject instanceof String) {
            initSprite(userObject.toString());
        }
    }
}
