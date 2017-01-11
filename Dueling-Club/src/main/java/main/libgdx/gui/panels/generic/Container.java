package main.libgdx.gui.panels.generic;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import main.libgdx.gui.layout.LayoutParser.LAYOUT;
import main.libgdx.gui.panels.generic.sub.RootTable;
import main.system.auxiliary.secondary.BooleanMaster;

/**
 * Created by JustMe on 1/6/2017.
 */
public class Container extends Comp {
    protected Actor[] comps;
    protected RootTable root;
    protected LAYOUT defaultLayout;
    protected LAYOUT rootLayout;

    public Container(String imagePath) {
        this(imagePath, LAYOUT.HORIZONTAL);
    }

    public Container(LAYOUT defaultLayout, String imagePath, Actor... comps) {
        super(imagePath);
        this.defaultLayout = defaultLayout;
        root = getRoot();
        this.comps = comps;
        addActor(root);
    }

    public Container(String imagePath, LAYOUT defaultLayout) {
        this(defaultLayout, imagePath);
    }

    public RootTable getRoot() {
        if (root == null)
            root = new RootTable() ;//getGroup(getRootLayout());
        return root;
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
        getRoot().clearChildren();
        WidgetContainer group = getGroup(defaultLayout);
        root.add(group);
        root.setFillParent(true);
//        root.setPosition(0, getHeight());
//        group.setPosition(0, root.getHeight());
        int i = 0;
        int j = 0;  for (Actor comp : comps) {

            boolean horizontal = defaultLayout == LAYOUT.HORIZONTAL;
            if (comp == null) {
                main.system.auxiliary.LogMaster.log(1, "NULL COMP IN " + this);
                continue;
            }
            if (comp instanceof Wrap) {
                i=0;
                group.layout();
                horizontal = ((Wrap) comp).horizontal;
                group = getGroup(horizontal ? LAYOUT.HORIZONTAL : LAYOUT.VERTICAL);
                root.add(group);
//                group.top();
                group.setFillParent(true);
//if (!horizontal)
//    root.row();
////                    group.setY(root.getHeight() - group.getHeight());
//                } else
//                    group.setY(root.getHeight() - group.getHeight());

                continue;
            }
            if (comp instanceof Comp) {
                ((Comp) comp).update();

            }
            group.addActor(comp);
            float y = (!horizontal) ? j * comp.getHeight() : i * comp.getHeight();
               float  x = (horizontal) ? j * comp.getHeight() : i * comp.getHeight();
//            comp.setPosition(x, y);
            j++;
            i++;
        }
        //alignment
//        root.top();
        root.layout();

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
        if (rootLayout == null) return defaultLayout;
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
            if (BooleanMaster.isTrue(verticalPercentage))
                return getParent().getHeight() * v;
            return super.getHeight();
        }

        @Override
        public float getWidth() {
            if (BooleanMaster.isFalse(verticalPercentage))
                return getParent().getWidth() * v;
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
