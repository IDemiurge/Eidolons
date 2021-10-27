package libgdx.gui.dungeon.panels.dc.topleft;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import eidolons.game.battlecraft.ai.advanced.engagement.PlayerStatus;
import libgdx.GdxMaster;
import libgdx.StyleHolder;
import libgdx.bf.generic.FadeImageContainer;
import libgdx.gui.LabelX;
import libgdx.gui.generic.GroupX;
import eidolons.content.consts.Images;
import libgdx.assets.texture.TextureCache;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.graphics.FontMaster;

public class StatusPanel extends GroupX {

    FadeImageContainer icon;
    LabelX statusMain;
    LabelX statusAdditional;
    Image background;

    public StatusPanel() {
        addBg(background = new Image(TextureCache.getOrCreateR(Images.ZARK_BOX)));

        Label.LabelStyle style = StyleHolder.getSizedLabelStyle(FontMaster.FONT.AVQ, 20);
        addActor(statusMain = new LabelX("", style));
        addActor(icon = new FadeImageContainer(Images.STATUS_EXPLORE));
        style = StyleHolder.getSizedLabelStyle(FontMaster.FONT.AVQ, 16);
        addActor(statusAdditional = new LabelX("", style));

        statusMain.setZigZagLines(true);
        GuiEventManager.bind(GuiEventType.PLAYER_STATUS_CHANGED, p -> updateStatus((PlayerStatus) p.get()));

    }


    private void updateStatus(PlayerStatus o) {
        icon.setImage(o.getIconPath());
        statusMain.setText(o.getStatusText());
        statusAdditional.setText(o.getSubText());

        statusMain.pack();
        statusAdditional.pack();

        statusMain.setX(20);
        statusAdditional.setX(15);
        GdxMaster.right(icon);
        icon.setX(icon.getX()-8);
//        GdxMaster.center(statusMain);
        GdxMaster.top(statusMain, -30);
        GdxMaster.top(icon, -42);
        statusAdditional.setY(statusMain.getY() - statusAdditional.getHeight() - 2);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }
}
