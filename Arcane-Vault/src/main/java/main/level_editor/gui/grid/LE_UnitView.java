package main.level_editor.gui.grid;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.bf.grid.CellBorderManager;
import eidolons.libgdx.bf.grid.GridUnitView;
import eidolons.libgdx.bf.grid.UnitViewOptions;
import eidolons.libgdx.gui.LabelX;
import main.system.graphics.FontMaster;

public class LE_UnitView extends GridUnitView {

    LabelX idLabel;

    public LE_UnitView(BattleFieldObject bfObj, UnitViewOptions options) {
        super(bfObj, adjustOptions(options));
    }

    private static UnitViewOptions adjustOptions(UnitViewOptions options) {
//                    options.createFromGameObject();
        return options;
    }

    public void initLE_Id(Integer id){

        idLabel=new LabelX(id+"", StyleHolder.getSizedLabelStyle(FontMaster.FONT.NYALA, 12));
    }
        public void selected(){
        setTeamColorBorder(true);
        setBorder(CellBorderManager.getTeamcolorTexture());
        setTeamColor(GdxColorMaster.YELLOW);
    }
    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
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
        return null ;
    }
}
