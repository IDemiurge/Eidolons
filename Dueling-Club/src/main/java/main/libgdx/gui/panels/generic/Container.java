package main.libgdx.gui.panels.generic;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import main.libgdx.gui.layout.LayoutParser.LAYOUT;
import main.libgdx.gui.panels.generic.sub.Root;
import main.system.auxiliary.LogMaster;
import main.system.auxiliary.secondary.BooleanMaster;

import java.util.List;

/**
 * Created by JustMe on 1/6/2017.
 */
public class Container extends Comp {
    private static final boolean DEBUG_MODE = true;
    protected Actor[] comps;
    protected LAYOUT defaultLayout;
    protected LAYOUT rootLayout;
    protected WidgetContainer root;

    public Container(String imagePath) {
        this(imagePath, LAYOUT.HORIZONTAL);
    }

    public Container(LAYOUT defaultLayout, String imagePath, Actor... comps) {
        super(imagePath);
        this.defaultLayout = defaultLayout;
        this.comps = comps;
        initRoot();
        if (DEBUG_MODE) {
            debug();
        }
    }

    public Container(String imagePath, LAYOUT defaultLayout) {
        this(defaultLayout, imagePath);
    }

    public WidgetContainer getRoot() {
        if (root == null) {
            root = new Root();
        }
        return root;
    }

    protected void initRoot() {
        root = getRoot();
        addActor((Actor) root);
    }

    public void setComps(List<Actor> comps) {
        setComps(comps.toArray(new Actor[comps.size()]));
    }

    public void setComps(Actor... comps) {
        this.comps = comps;
    }

    public void initComps() {

    }

    @Override
    public void update() {
        initComps();
        super.update();
        root.clear();
        WidgetContainer group = getGroup(defaultLayout);
        root.add(group);
        for (Actor comp : comps) {

            if (comp == null) {
                LogMaster.log(1, "NULL COMP IN " + this);
                continue;
            }

            if (comp instanceof Wrap) {
//                group.layout();
                boolean horizontal = ((Wrap) comp).horizontal;
                group = getGroup(horizontal ? LAYOUT.HORIZONTAL : LAYOUT.VERTICAL);
                root.add(group);
//                group.top();
                group.setFillParent(true);

                continue;
            }
            if (comp instanceof Comp) {
                ((Comp) comp).update();

            }
            group.addActor(comp);

        }
        //alignment
//        root.top();
//        root.layout();

    }

    public WidgetContainer getGroup(LAYOUT layout) {
        if (layout == LAYOUT.HORIZONTAL) {
            return new HorizontalContainer();
        } else {
            return new VerticalContainer();
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    public LAYOUT getRootLayout() {
        if (rootLayout == null) {
            return defaultLayout;
        }
        return rootLayout;
    }

    public void setRootLayout(LAYOUT rootLayout) {
        this.rootLayout = rootLayout;
    }

    public boolean isPaged() {
        return false;
    }

    public boolean isScrolled() {
        return true;
    }

    public void setLayout(LAYOUT layout) {
        this.defaultLayout = layout;
    }

    public static class Space extends Comp {

        private float v;
        private Boolean verticalPercentage;

        public Space(int w, int h) {
            setWidth((float) w);
            setHeight((float) h);
        }

        public Space(boolean b, float v) {
            verticalPercentage = !b;
            this.v = v;

        }

        @Override
        public float getHeight() {
            if (BooleanMaster.isTrue(verticalPercentage)) {
                return getParent().getHeight() * v;
            }
            return super.getHeight();
        }

        @Override
        public float getWidth() {
            if (BooleanMaster.isFalse(verticalPercentage)) {
                return getParent().getWidth() * v;
            }
            return super.getWidth();
        }
    }

    public static class Wrap extends Comp {
        public boolean horizontal;

        public Wrap(boolean horizontal) {
            this.horizontal = horizontal;
        }
    }

//    Arrays.stream(comps).forEach(comp->{
//
//        comp.setX(x);
//    });
}
