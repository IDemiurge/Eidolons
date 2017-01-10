package main.libgdx.gui.panels.generic;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import main.libgdx.gui.layout.LayoutParser.LAYOUT;
import main.system.auxiliary.secondary.BooleanMaster;

/**
 * Created by JustMe on 1/6/2017.
 */
public class Container extends Comp {
    protected Actor[] comps;
    protected Group root;
    protected LAYOUT defaultLayout;
    protected LAYOUT rootLayout;

    public Container(String imagePath) {
        this(imagePath, LAYOUT.HORIZONTAL);
    }

    public Container( LAYOUT defaultLayout, String imagePath,Actor...comps) {
        super(imagePath);
        this.defaultLayout = defaultLayout;
        root = getRoot();
        this.comps=comps;
        addActor(root);
    }
    public Container(String imagePath, LAYOUT defaultLayout) {
       this(defaultLayout, imagePath  );
    }

    public Group getRoot() {
        if (root==null )
            root = getGroup(getRootLayout());
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
        Group group = getGroup(defaultLayout);
        root.addActor(group);
//        root.setPosition(0, getHeight());
        group.setPosition(0,root.getHeight());
        for (Actor comp : comps) {
            if (comp==null){
                main.system.auxiliary.LogMaster.log(1,"NULL COMP IN " +this);
                continue;
            }
            if (comp instanceof Wrap) {
                group = getGroup(((Wrap) comp).horizontal ? LAYOUT.HORIZONTAL : LAYOUT.VERTICAL);
                root.addActor(group);
//                group.setY(root.getHeight()-group.getY());
                continue;
            }
            if (comp instanceof Comp) {
                ((Comp) comp).update();

            }
            group.addActor(comp);
        }

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    public LAYOUT getRootLayout() {
        if (rootLayout==null )return defaultLayout;
        return rootLayout;
    }

    public void setRootLayout(LAYOUT rootLayout) {
        this.rootLayout = rootLayout;
    }

    public boolean isPaged(){
        return false;
    }
    public boolean isScrolled(){
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

    public Group getGroup(LAYOUT layout) {
        if (layout == LAYOUT.HORIZONTAL) {
            return new HorizontalGroup(){
                @Override
                public String toString() {
                    return   getClass().getSimpleName()+ " " +getWidth() + " by " + getHeight()
                     + " at " + getX() + ":" + getY()
                     + " with " + getChildren().size + " children: " + getChildren();
                }
            };
        } else {
            return new VerticalGroup(){
                @Override
                public String toString() {
                    return   getClass().getSimpleName()+ " " +getWidth() + " by " + getHeight()
                     + " at " + getX() + ":" + getY()
                     + " with " + getChildren().size + " children: " + getChildren();
                }
            };
        }
    }
//    Arrays.stream(comps).forEach(comp->{
//
//        comp.setX(x);
//    });
}
