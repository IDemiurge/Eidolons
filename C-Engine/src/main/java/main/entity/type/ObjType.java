package main.entity.type;

import main.content.OBJ_TYPE;
import main.content.values.parameters.ParamMap;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.content.values.properties.PropMap;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.handlers.EntityMaster;
import main.entity.handlers.EntityResetter;
import main.game.core.game.Game;
import main.system.ExceptionMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.launch.CoreEngine;
import org.w3c.dom.Node;

import javax.swing.*;

/**
 * Represents a game object's type, from which it is created Can also have a type field if it is a cloned/generated
 * ObjType. (e.g. leveled up unit)
 */
public class ObjType extends Entity {
    private boolean generated;
    private boolean model;
    private Node node;
    private boolean built;
    private ObjType parentType;
    private EntityResetter<ObjType> resetter;

    public ObjType() {
        this(Game.game);
    }

    public ObjType(String newName, ObjType type) {
        this(type);
        setName(newName);
    }

    public ObjType(ObjType type) {
        this(true, type);
    }

    public ObjType(ObjType type, boolean model) {
        this(type);
        this.setModel(model);
    }

    public ObjType(boolean generation, ObjType type) {
        this.var = true;
        setOBJ_TYPE_ENUM(type.getOBJ_TYPE_ENUM());

        type.checkBuild();
        cloneMaps(type);

        setRef(type.getRef());
        setGame(type.getGame());
        if (generation) {
            setBuilt(true);
            this.setType(type);
            setGenerated(true);
        }
        if (type.getGame() != null)
            type.getGame().initType(this);
    }

    public ObjType(String typeName) {
        setProperty(G_PROPS.NAME, typeName);
    }


    public ObjType(Game game) {
        this.game = game;
    }

    public ObjType(Ref ref) {
        setRef(ref);
        initType();
    }

    public ObjType(String typeName, OBJ_TYPE TYPE) {
        this((typeName));
        setOBJ_TYPE_ENUM(TYPE);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ObjType) {
            return ((ObjType) obj).getName().equals(getName());
        }
        return super.equals(obj);
    }

    @Override
    protected EntityMaster initMaster() {
        return null;
    }

    @Override
    public void setProperty(PROPERTY name, String value, boolean base) {
        super.setProperty(name, value);
    }

    @Override
    protected boolean isFiringValueEvents() {
        return false;
    }

    public ObjType getType() {
        return (type != null) ? type : this;
    }

    public boolean equalsAsBaseType(Object obj) {
        if (super.equals(obj))
            return true;
        if (obj instanceof ObjType) {
            return ((ObjType) obj).getName().equals(getName());

        }
        return false;
    }

    @Override
    public void setRef(Ref ref) {
        this.ref = ref;
        if (ref != null) {
            ref.setSource(id);
            this.game = ref.getGame();
        }

    }

    public ImageIcon getDefaultIcon() {
        return getIcon();
    }


    public void initType() {
        if (game != null && !isInitialized()) {
            game.initType(this);
        }
    }

    @Override
    public void init() {
    }

    @Override
    public void clicked() {

    }

    public boolean isGenerated() {
        return generated;
    }

    public void setGenerated(boolean generated) {
        this.generated = generated;
    }

    @Override
    public void setName(String name) {
        this.name = name;
        setProperty(G_PROPS.NAME, name, false); //don't touch base type!
        name = StringMaster.formatDisplayedName(name);
        setProperty(G_PROPS.DISPLAYED_NAME, name, false);
    }

    public boolean isModel() {
        return model;
    }

    public void setModel(boolean model) {
        this.model = model;
    }

    @Override
    protected void putProperty(PROPERTY prop, String value) {
        super.putProperty(prop, value);
    }

    public void checkBuild() {
        if (!initialized)
            return;
        if (!built) {
            if (node == null) {
                built = true;
                LogMaster.devLog("Dummy unbuildable type " + this);
                return;
            }
            try {
                TypeBuilder.buildType(node, this);
                built = true;
                node = null;
            } catch (Exception e) {
                ExceptionMaster.printStackTrace(e);
            }
        }
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public Node getNode() {
        return node;
    }

    public void setBuilt(boolean built) {
        this.built = built;
    }

    @Override
    public ParamMap getParamMap() {
        checkBuild();
        return super.getParamMap();
    }

    @Override
    public String toString() {
        if (CoreEngine.isArcaneVault()) {
            return getName();
        }
        return super.toString();
    }

    @Override
    public PropMap getPropMap() {
        checkBuild();
        return super.getPropMap();
    }

    @Override
    public void toBase() {
        if (resetter == null)
            resetter = new EntityResetter<>(this, null);
        resetter.toBase();
    }

    public void setParentType(ObjType parentType) {
        this.type = new ObjType(this);
        this.type.parentType = parentType;
        this.type.setProperty(G_PROPS.PARENT_TYPE, parentType.getName());
        setProperty(G_PROPS.PARENT_TYPE, parentType.getName());
    }

    public ObjType getParentType() {
        return  parentType;
    }
}
