package eidolons.libgdx.gui.panels.headquarters;

import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.libgdx.gui.panels.TablePanel;

/**
 * Created by JustMe on 4/16/2018.
 */
public abstract class ValueTable<D, A extends Actor> extends TablePanel {
    protected D[] data;
    protected A[] actors;
    protected int wrap;
    protected int size;

    public ValueTable(int wrap, int size) {
        this.wrap = wrap;
        this.size = size;

    }

    @Override
    public void updateAct(float delta) {
        init();
    }

    public void init() {
        clear();
        data = initDataArray();
        actors = initActorArray();

        int j = 0, i = 0;
        for (D sub : data) {
            addElement(actors[i] = createElement(sub));
            j++;
            i++;
            if (j >= wrap) {
                row();
                j = 0;
            }
        }
}

    protected abstract A createElement(D datum);

    protected abstract A[] initActorArray();

    protected abstract D[] initDataArray();


}
