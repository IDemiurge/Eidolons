package main.level_editor.gui.grid;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.bf.grid.cell.CellBorderManager;
import eidolons.libgdx.bf.grid.cell.UnitGridView;
import eidolons.libgdx.bf.grid.cell.UnitViewOptions;
import eidolons.libgdx.gui.LabelX;
import eidolons.libgdx.texture.TextureCache;
import main.content.DC_TYPE;
import main.level_editor.LevelEditor;
import main.level_editor.backend.sim.LE_GameSim;
import main.system.graphics.FontMaster;

public class LE_UnitView extends UnitGridView {

    LabelX idLabel = new LabelX("", StyleHolder.getSizedLabelStyle(FontMaster.FONT.NYALA, 12));
    LabelX aiLabel = new LabelX("", StyleHolder.getSizedLabelStyle(FontMaster.FONT.NYALA, 12));

    public LE_UnitView(BattleFieldObject bfObj, UnitViewOptions options) {
        super(bfObj, adjustOptions(bfObj, options));
        if (bfObj.getGame() instanceof LE_GameSim) {
            initLE_Id(((LE_GameSim) bfObj.getGame()).getSimIdManager().getId(bfObj));
        }
    }

    private static UnitViewOptions adjustOptions(BattleFieldObject bfObj, UnitViewOptions options) {
        if (bfObj.getType().getOBJ_TYPE_ENUM() == DC_TYPE.ENCOUNTERS) {
//options.createFromGameObject();
            /*
            No facing
            special emblem
            additional label

            selection
            filters
            getObjs()
             */
            options.setHoverResponsive(false);
            options.setEmblem(
                    TextureCache.getOrCreateR("ui\\level_editor\\anew/jack.png"));
            options.setDirectionPointerTexture(
                    TextureCache.getOrCreateR("gen/perk/abil/1.png")
            );
            options.setTeamColor(GdxColorMaster.BLUE);
        }
//                    options.createFromGameObject();
        return options;
    }

    public void initLE_Id(Integer id) {
        idLabel.setText("[Id=" + id + "]");
    }

    public void selected() {
        setTeamColorBorder(true);
        setBorder(CellBorderManager.getTeamcolorTexture());
        setTeamColor(GdxColorMaster.YELLOW);
    }

    protected boolean isResetOutlineOnHide() {
        return false;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        idLabel.setVisible(LevelEditor.getCurrent().getManager().
                getModelManager().getModel().getDisplayMode().isShowMetaAi());
        aiLabel.setDebug(true);
        aiLabel.setVisible(LevelEditor.getCurrent().getManager().
                getModelManager().getModel().getDisplayMode().isShowMetaAi());
    }

    public LabelX getAiLabel() {
        return aiLabel;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        setVisible(true);
        batch.setColor(getColor());
        super.draw(batch, parentAlpha);
    }

    @Override
    protected void setDefaultTexture() {
    }

    @Override
    public TextureRegion getDefaultTexture() {
        return originalTexture;
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public boolean isWithinCamera() {
        return true;
    }

    @Override
    protected void initQueueView(UnitViewOptions o) {
    }


    @Override
    public boolean isHpBarVisible() {
        return false;
    }

    @Override
    public void resetHpBar() {
    }

    @Override
    public void createHpBar() {
    }

    @Override
    public void animateHpBarChange() {
    }

    @Override
    protected void checkResetOutline(float delta) {
    }

    @Override
    public TextureRegion getOutline() {
        return null;
    }
}
