package main.libgdx.gui.panels.sub;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import main.libgdx.gui.layout.LayoutParser.LAYOUT;

/**
 * Created by JustMe on 1/6/2017.
 */
public class Container extends Comp {
    private Comp[] comps;
    Group root;
    private LAYOUT defaultLayout;

    public Container(String imagePath) {
this(imagePath, LAYOUT.HORIZONTAL);
    }
    public Container(String imagePath, LAYOUT defaultLayout) {
        super(imagePath);
        this.defaultLayout=defaultLayout;
    }

    public Group getGroup(LAYOUT layout) {
        if (layout == LAYOUT.HORIZONTAL) {
            return new HorizontalGroup();
        } else {
            return new VerticalGroup();
        }
    }

    public void setComps(Comp... comps) {
this.comps=comps;
    }

    @Override
    public void update() {
        super.update();
//        TableLayout layout;
        Group group = getGroup(defaultLayout);
        root.addActor(group);
        for (Comp comp : comps) {
            if (comp instanceof Wrap) {
                group = getGroup(((Wrap) comp).horizontal ? LAYOUT.HORIZONTAL : LAYOUT.VERTICAL);
                root.addActor(group);
                continue;
            }
            group.addActor(comp);
        }
//        Arrays.stream(comps).forEach(comp -> {

//            comp.
//        });
    }

    public static class Space extends Comp {

        public Space(int w, int h) {
            setWidth((float)w);
            setHeight((float)h);
        }
    }
        public static class Wrap extends Comp {
        public boolean horizontal;

        public Wrap(boolean horizontal) {
            this.horizontal = horizontal;
        }
    }

    public void layout() {

    }
//    Arrays.stream(comps).forEach(comp->{
//
//        comp.setX(x);
//    });
}
