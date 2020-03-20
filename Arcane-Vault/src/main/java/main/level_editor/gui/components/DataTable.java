package main.level_editor.gui.components;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.panels.headquarters.ValueTable;
import main.entity.Entity;
import main.level_editor.backend.handlers.EditHandler;
import main.system.graphics.FontMaster;

public class DataTable extends ValueTable<DataTable.DataPair, ValueContainer> {
    public DataTable(int wrap, int size) {
        super(wrap, size);
    }
    @Override
    protected ValueContainer createElement(DataPair datum) {
        TextureRegion texture=null ;
        String value="";

        if (datum.value instanceof TextureRegion) {
            texture= (TextureRegion) datum.value;
        } else {

        }
        ValueContainer comp = new ValueContainer(texture, datum.name, value);
        Label.LabelStyle style= StyleHolder.getSizedLabelStyle(FontMaster.FONT.NYALA, 14);
        comp.setStyle(style);
//        comp.setSize(w, h);
        comp.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (isEditable(datum.name))
                {
//                   boolean changed =  LevelEditor.getCurrent().getManager().getEditHandler().edit(datum);
//                   if (changed){
//                       setUpdateRequired(true);
//                   }
                }
                super.clicked(event, x, y);
            }
        });
        return comp;
    }

    @Override
    public void setUserObject(Object userObject) {
        Entity editEntity = (Entity) userObject;
        data= EditHandler.getDataPairs(editEntity);
        super.setUserObject(userObject);
    }

    @Override
    protected Vector2 getElementSize() {
        return new Vector2(getWidth(), getRowHeight(0));
    }

    @Override
    public float getRowHeight(int rowIndex) {
        return super.getRowHeight(rowIndex);
    }
//    public float getRowHeight() {
//        return super.getRowHeight(rowIndex);
//    }

    boolean isEditable(String name){
        return true;
    }

    @Override
    protected ValueContainer[] initActorArray() {
        return new ValueContainer[0];
    }

    @Override
    protected DataPair[] initDataArray() {
        return new DataPair[0];
    }

    public class DataPair{
        String name;
        Object value;
        String stringValue;
    }
}
