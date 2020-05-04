package eidolons.libgdx.gui.panels.dc.topleft;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import eidolons.game.battlecraft.ai.advanced.engagement.PlayerStatus;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.gui.LabelX;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.texture.Images;
import eidolons.libgdx.texture.TextureCache;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.graphics.FontMaster;

public class StatusPanel extends TablePanelX {

    FadeImageContainer icon;
    LabelX statusMain;
    LabelX statusAdditional;
    Image background;

    public StatusPanel() {
        addActor(background = new Image(TextureCache.getOrCreateR(Images.ZARK_TITLE)));
        Label.LabelStyle style= StyleHolder.getSizedLabelStyle(FontMaster.FONT.AVQ, 20);
        add(statusMain = new LabelX("", style));
        add(icon = new FadeImageContainer(Images.STATUS_EXPLORE)).row();
        add(statusAdditional = new LabelX("", style));

        GuiEventManager.bind(GuiEventType.PLAYER_STATUS_CHANGED , p-> updateStatus((PlayerStatus) p.get()));
    }

    private void updateStatus(PlayerStatus o) {
        icon.setImage(o.getIconPath());
        statusMain.setText(o.getStatusText());
        statusAdditional.setText(o.getSubText());

    }
}
