package main.level_editor.gui.tree;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.kotcrab.vis.ui.VisUI;
import eidolons.game.battlecraft.ai.elements.generic.AiData;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.battlecraft.logic.dungeon.location.Location;
import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder;
import eidolons.game.battlecraft.logic.dungeon.location.layer.Layer;
import eidolons.game.battlecraft.logic.dungeon.location.struct.BlockData;
import eidolons.game.battlecraft.logic.dungeon.location.struct.FloorData;
import eidolons.game.battlecraft.logic.dungeon.location.struct.ModuleData;
import eidolons.game.battlecraft.logic.dungeon.location.struct.ZoneData;
import eidolons.game.battlecraft.logic.dungeon.location.struct.wrapper.ObjNode;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.exploration.dungeons.struct.LevelBlock;
import eidolons.game.exploration.dungeons.struct.LevelStruct;
import eidolons.game.exploration.dungeons.struct.LevelZone;
import libgdx.StyleHolder;
import libgdx.bf.GridMaster;
import libgdx.bf.grid.moving.PlatformController;
import libgdx.bf.grid.moving.PlatformData;
import libgdx.gui.generic.ValueContainer;
import libgdx.screens.ScreenMaster;
import libgdx.stage.camera.MotionData;
import eidolons.content.consts.Images;
import libgdx.texture.TextureCache;
import main.content.DC_TYPE;
import main.data.tree.LayeredData;
import main.data.tree.StructNode;
import main.game.bf.Coordinates;
import main.level_editor.LevelEditor;
import main.level_editor.backend.handlers.model.EditorModel;
import main.level_editor.backend.struct.boss.BossDungeon;
import main.level_editor.backend.struct.campaign.Campaign;
import main.level_editor.gui.components.TreeX;
import main.level_editor.gui.dialog.struct.PlatformEditDialog;
import main.level_editor.gui.stage.LE_GuiStage;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.graphics.FontMaster;
import main.system.threading.WaitMaster;

public class LE_TreeView extends TreeX<StructNode> {

    private Object lastSelected;
    private Object lastSelectedParent;

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    protected void rightClick(StructNode node) {

        LayeredData nodeData = node.getData();
        if (nodeData instanceof LevelBlock) {
            if (((LevelBlock) nodeData).getRoomType() == LocationBuilder.ROOM_TYPE.PLATFORM) {
                PlatformController controller = ScreenMaster.getGrid().getPlatformHandler().findByName(
                        ((LevelBlock) nodeData).getName());
                ((LE_GuiStage) getStage()).getPlatformDialog().edit((PlatformData) controller.getData());
                //confirm
                WaitMaster.waitForInput(PlatformEditDialog.EDIT_DONE);
            }

            {
                BlockData data = ((LevelBlock) nodeData) .getData();
                if (getStage() instanceof LE_GuiStage) {

                    ((LE_GuiStage) getStage()).getBlockEditor().edit(data);
                }
            }
        }
        if (nodeData instanceof LevelZone) {
            ZoneData data = ((LevelZone) nodeData).getData();
            if (getStage() instanceof LE_GuiStage) {
                ((LE_GuiStage) getStage()).getZoneEditor().edit(data);
            }
        }
        if (nodeData instanceof  Module) {
            ModuleData data = ( (Module) nodeData) .getData();
            if (getStage() instanceof LE_GuiStage) {
                ((LE_GuiStage) getStage()).getModuleDialog().edit(data);
            }
        }

        if (nodeData instanceof Location) {
            FloorData data = (FloorData) ((Location) nodeData).getData();
            if (getStage() instanceof LE_GuiStage) {
                ((LE_GuiStage) getStage()).getFloorDialog().edit(data);
            }
        }
    }

    @Override
    protected void doubleClick(StructNode node) {
        Coordinates c = null;
        if (node.getData() instanceof ObjNode) {
            c = ((ObjNode) node.getData()).getObj().getCoordinates();
            LevelEditor.getManager().getSelectionHandler().
                    select(((ObjNode) node.getData()).getObj());
        } else {
            if (node.getData() instanceof LevelBlock) {
                c = CoordinatesMaster.getCenterCoordinate(((LevelBlock) node.getData()) .getCoordinatesSet());
            } else if (node.getData() instanceof LevelZone) {
                c = CoordinatesMaster.getCenterCoordinate(
                        ((LevelZone) node.getData()).getChildren().iterator().next()
                                .getCoordinatesSet());
            }
            if (node.getData() instanceof  Module) {
                Module m = (Module) node.getData();
                c = m.getOrigin().getOffset(m.getEffectiveWidth() / 2, m.getEffectiveHeight() / 2);
            }
            //center coordinate

        }
        Vector2 v = GridMaster.getCenteredPos(c);
        MotionData data = new MotionData(v, 0.5f, null);
        GuiEventManager.trigger(GuiEventType.CAMERA_PAN_TO_COORDINATE, data);
//        data =  new CameraMan.MotionData(z, 0.5f, null);
//        GuiEventManager.trigger(GuiEventType.CAMERA_ZOOM, data);
    }

    @Override
    protected void click(int tapCount, InputEvent event, Node n, StructNode node) {
        super.click(tapCount, event, n, node);
        lastSelected = node.getData();
        if (lastSelected instanceof StructNode) {
            lastSelected = ((StructNode) lastSelected).getData() ;
        }
        if (n.getParent() != null) {
            lastSelectedParent = n.getParent().getObject();
            if (lastSelectedParent instanceof StructNode) {
                lastSelectedParent = ((StructNode) lastSelectedParent).getData() ;
            }
        }
    }

    public void reselect() {
        if (!forceSelect(lastSelected)) {
            forceSelect(lastSelectedParent);
        }
    }

    @Override
    public void select(Object o) {
        forceSelect(o);
    }

    private boolean forceSelect(Object lastSelected) {
        for (Node node : nodes) {
            try {
                if (node.getObject() instanceof StructNode) {
                    if (((StructNode) node.getObject()).getData()  == lastSelected) {
                        click(1, null, node, (StructNode) node.getObject());
                        node.expandTo();
                        return true;
                    }
                }
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        return false;
    }

    @Override
    protected void selected(StructNode node, Node n) {
        if (node.getData() instanceof ObjNode) {
            if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) ||
                    Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT)) {
                LevelEditor.getManager().getSelectionHandler().addToSelected(
                        ((ObjNode) node.getData()).getObj());
            } else {
                LevelEditor.getManager().getSelectionHandler().select(((ObjNode) node.getData()).getObj());
            }
        } else {
            EditorModel model = LevelEditor.getModel();
            if (node.getData() instanceof LevelStruct){
                model.setLastSelectedStruct(((LevelStruct) node.getData()));
            }
        if (node.getData() instanceof LevelBlock) {

            LevelBlock block = ((LevelBlock) node.getData());
            model. setBlock(block);
            model.setZone(block.getZone());
            WaitMaster.receiveInput(LevelEditor.SELECTION_EVENT, true);
        } else if (node.getData() instanceof LevelZone) {
            model.setZone(((LevelZone) node.getData()));
            //camera!
        } else if (node.getData() instanceof Module) {
            model.setModule((Module) node.getData());
        } else if (node.getData() instanceof Layer) {
            //select all?
        }
        }
    }

    @Override
    protected Actor createNodeComp(StructNode node) {

        Label.LabelStyle style = StyleHolder.getSizedLabelStyle(FontMaster.FONT.NYALA, 16);
        TextureRegion texture = null;
        if (node == null) {
            return new Label("NULL", VisUI.getSkin());
        }
        if (node.getData() == null) {
            return new Label("NULL", VisUI.getSkin());
        }
        String name = node.getData().toString();
        String val = null;
        float w = 64;
        float h = 64;

        Color c = null;
        if (node.getData() instanceof Campaign) {
            Campaign campaign = ((Campaign) node.getData());
            style = StyleHolder.getSizedLabelStyle(FontMaster.FONT.NYALA, 16);
            texture = TextureCache.getOrCreateR(campaign.getImagePath());
        }
        if (node.getData() instanceof BossDungeon) {
            BossDungeon dungeon = ((BossDungeon) node.getData());
        }


        if (node.getData() instanceof Location) {
            style = StyleHolder.getSizedLabelStyle(FontMaster.FONT.AVQ, 21);
            Location floor = ((Location) node.getData());
            texture = TextureCache.getSizedRegion(64, floor.getFloor().getImagePath());
            name = "--- " + floor.getName() +" --- ";
        }
        if (node.getData() instanceof  Module) {
            style = StyleHolder.getSizedLabelStyle(FontMaster.FONT.NYALA, 21);
            Module module = (Module) node.getData();
            name = "-- " + module.getName()  +" --";
            texture = TextureCache.getOrCreateR(Images.ITEM_BACKGROUND_STONE);
        }
        if (node.getData() instanceof LevelZone) {
            style = StyleHolder.getSizedLabelStyle(FontMaster.FONT.NYALA, 19);
            LevelZone zone = (LevelZone) node.getData();
            name = "- " + "[Zone] " + zone.getName()  +" -";
//            val = zone.getSubParts().size();
//            zone.getTemplateGroup();
//            zone.getStyle();
        }
        if (node.getData() instanceof LevelBlock) {
            LevelBlock block = (LevelBlock) node.getData();
            name = "- " +
                    (block.getName().isEmpty()? "": block.getName()) +
                    "" +
                    //(block.isTemplate() ? "Template " : "Custom ") +
                    block.getRoomType() +
                    " at " + block.getOrigin() + " with Cells [" +
                    block.getCoordinatesSet().size() +
                    "]" ;
            c = LevelEditor.getCurrent().getManager().getStructureHandler().getColorForBlock(block);

            //img per room type
//            texture = TextureCache.getOrCreateR(Images.ITEM_BACKGROUND_STONE);
            w = 32;
            h = 32;
            texture = TextureCache.getOrCreateR(Images.COLOR_EMBLEM);
        }
        if (node.getData() instanceof ObjNode) { // or id?
            ObjNode dcObj = ((ObjNode) node.getData());
            texture = TextureCache.getOrCreateR(dcObj.getObj().getImagePath());
            if (dcObj.getObj().getOBJ_TYPE_ENUM() == DC_TYPE.ENCOUNTERS) {
                name = "[Encounter]" + name;
                //get ai info!
            }
            AiData ai = LevelEditor.getManager().getAiHandler().getAiForEncounter(dcObj.getObj());
        }

        ValueContainer actor = new ValueContainer(style, texture, name, val);
        if (c != null) {
            actor.getImageContainer().getActor().setColor(c);
        }

        if (actor.getImageContainer() != null) {
            actor.getImageContainer().size(w, h);
            actor.getImageContainer().getActor().setSize(w, h);
        }
        return (actor);
    }

    public String toString(LayeredData data) {
        switch (data.getClass().getSimpleName()) {

        }
        return super.toString();
    }
}
