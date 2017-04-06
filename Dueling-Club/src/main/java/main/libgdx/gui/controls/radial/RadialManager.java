package main.libgdx.gui.controls.radial;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import main.content.C_OBJ_TYPE;
import main.elements.Filter;
import main.elements.targeting.SelectiveTargeting;
import main.entity.Ref;
import main.entity.active.DC_ActiveObj;
import main.entity.item.DC_QuickItemObj;
import main.entity.item.DC_WeaponObj;
import main.entity.obj.DC_Cell;
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.core.game.DC_Game;
import main.game.core.game.Game;
import main.libgdx.anims.text.FloatingTextMaster;
import main.libgdx.anims.text.FloatingTextMaster.TEXT_CASES;
import main.libgdx.bf.TargetRunnable;
import main.libgdx.gui.panels.dc.actionpanel.ActionValueContainer;
import main.libgdx.gui.panels.dc.unitinfo.datasource.UnitDataSource;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.images.ImageManager;
import main.system.launch.CoreEngine;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.*;
import java.util.stream.Collectors;

import static main.libgdx.texture.TextureCache.*;
import static main.system.GuiEventManager.trigger;
import static main.system.GuiEventType.SELECT_MULTI_OBJECTS;

public class RadialManager {

    public static Texture getTextureForActive(DC_ActiveObj obj, DC_Obj target) {
        Ref ref = obj.getOwnerObj().getRef().getTargetingRef(target);
        return getTextureForActive(obj, ref);
    }

    public static TextureRegion getTextureRForActive(DC_ActiveObj obj, DC_Obj target) {
        Ref ref = obj.getOwnerObj().getRef().getTargetingRef(target);
        return getTextureRForActive(obj, ref);
    }

    public static TextureRegion getTextureRForActive(DC_ActiveObj obj, Ref ref) {
        return !obj.canBeActivated(ref) ?
                getOrCreateGrayscaleR(obj.getImagePath())
                : getOrCreateR(obj.getImagePath());
    }

    public static Texture getTextureForActive(DC_ActiveObj obj, Ref ref) {
        return !obj.canBeActivated(ref) ?
                getOrCreateGrayscale(obj.getImagePath())
                : getOrCreate(obj.getImagePath());
    }

    public static List<MenuNodeDataSource> createNew(DC_Obj target) {
        Unit sourceUnit = (Unit) Game.game.getManager().getActiveObj();
        if (sourceUnit == null) {
            return Collections.EMPTY_LIST;
        }

        List<MenuNodeDataSource> moves = new ArrayList<>();
        List<MenuNodeDataSource> turns = new ArrayList<>();
        List<MenuNodeDataSource> attacks = new ArrayList<>();

        sourceUnit.getActives().parallelStream()
                .filter(el -> el.getTargeting() != null && el instanceof DC_ActiveObj)
                .map(el -> (DC_ActiveObj) el)
                .distinct()
                .sequential()
                .forEach(el -> {
                    if (el.isMove()) {
                        moves.add(configureMoveNode(target, el));
                    } else if (el.isTurn()) {
                        turns.add(() -> new ActionValueContainer(
                                        new TextureRegion(getTextureForActive(el, target)),
                                        el::invokeClicked
                                )
                        );
                    } else if (el.isAttackGeneric()) {
                        attacks.addAll(configureAttackNode(target, el));
                    }
                });

        /*
        Texture attackAction = getOrCreate("/mini/actions/New folder/Achievement_Arena_2v2_2.jpg");
        Texture yellow = new Texture(GridPanel.class.getResource("/data/marble_yellow.png").getPath());
        Texture red = new Texture(GridPanel.class.getResource("/data/marble_red.png").getPath());
        Texture green = new Texture(GridPanel.class.getResource("/data/marble_green.png").getPath());
*/

        List<MenuNodeDataSource> list = new LinkedList<>();

        if (C_OBJ_TYPE.UNITS_CHARS.equals(target.getOBJ_TYPE_ENUM())) {
            if (target instanceof Unit) {
                list.add(() -> new ActionValueContainer(
                        getOrCreateR("UI/actions/examine.png"),
                        () -> GuiEventManager.trigger(
                                GuiEventType.SHOW_UNIT_INFO_PANEL,
                                new EventCallbackParam<>(new UnitDataSource(((Unit) target)))
                        )
                ));
            }
        }

        list.add(new MenuNodeDataSource() {
            @Override
            public ActionValueContainer getCurrent() {
                return attacks.get(0).getCurrent();
            }

            @Override
            public List<MenuNodeDataSource> getChilds() {
                return attacks.get(0).getChilds();
            }
        });

        list.add(new MenuNodeDataSource() {
            @Override
            public ActionValueContainer getCurrent() {
                return new ActionValueContainer(getOrCreateR("/UI/actions/Move gold.jpg"), null);
            }

            @Override
            public List<MenuNodeDataSource> getChilds() {
                return moves;
            }
        });

        if (attacks.size() > 1) {
            list.add(new MenuNodeDataSource() {
                @Override
                public ActionValueContainer getCurrent() {
                    return attacks.get(1).getCurrent();
                }

                @Override
                public List<MenuNodeDataSource> getChilds() {
                    return attacks.get(1).getChilds();
                }
            });
        }


        if (!sourceUnit.getSpells().isEmpty()) {
            final List<MenuNodeDataSource> spellNodes =
                    SpellRadialManager.getSpellNodes(sourceUnit, target);

            list.add(new MenuNodeDataSource() {
                @Override
                public List<MenuNodeDataSource> getChilds() {
                    return spellNodes;
                }

                @Override
                public ActionValueContainer getCurrent() {
                    return new ActionValueContainer(
                            spellNodes.size() > 0 ?
                                    getOrCreateR(ImageManager.getRadialSpellIconPath()) :
                                    getOrCreateGrayscaleR(ImageManager.getRadialSpellIconPath()),
                            () -> {
                            }
                    );

                }
            });
        }

        list.add(new MenuNodeDataSource() {
            @Override
            public ActionValueContainer getCurrent() {
                return new ActionValueContainer(
                        getOrCreateR("/UI/actions/turn anticlockwise quick2 - Copy.jpg"),
                        null
                );
            }

            @Override
            public List<MenuNodeDataSource> getChilds() {
                return turns;
            }
        });

        return list;
    }

    private static Filter<Obj> getFilter(DC_ActiveObj active) {
        active.getRef().setMatch(active.getOwnerObj().getId()); // for filter
        Filter<Obj> filter = active.getTargeting().getFilter();
        filter.setRef(active.getRef());

        return filter;
    }

    private static MenuNodeDataSource configureSelectiveTargetedNode(DC_ActiveObj active) {

        Set<Obj> objSet = CoreEngine.isActionTargetingFiltersOff() ?
                DC_Game.game.getUnits().parallelStream().distinct().collect(Collectors.toSet())
                : getFilter(active).getObjects();

        final boolean valid = objSet.size() > 0;

        return () -> new ActionValueContainer(
                valid ?
                        getOrCreateR(active.getImagePath()) :
                        getOrCreateGrayscaleR(active.getImagePath()),
                () -> {
                    if (valid) {
                        trigger(SELECT_MULTI_OBJECTS, new EventCallbackParam<>(
                                new ImmutablePair<Set<Obj>, TargetRunnable>(objSet, active::activateOn))
                        );
                    } else {
                        FloatingTextMaster.getInstance().createFloatingText(TEXT_CASES.ERROR, "", active);
                    }
                });
    }

    private static List<MenuNodeDataSource> configureAttackNode(DC_Obj target, DC_ActiveObj dcActiveObj) {
        List<MenuNodeDataSource> result = new ArrayList<>();
        List<MenuNodeDataSource> list = new ArrayList<>();

        for (DC_ActiveObj dc_activeObj : dcActiveObj.getSubActions()) {
            if (dcActiveObj.getRef().getSourceObj() == target) {
                list.add(configureSelectiveTargetedNode(dc_activeObj));
            } else if (dcActiveObj.getTargeting() instanceof SelectiveTargeting) {
                MenuNodeDataSource inner =
                        () -> new ActionValueContainer(
                                getOrCreateR(dc_activeObj.getImagePath()),
                                () -> dc_activeObj.activateOn(target)
                        );
                list.add(inner);
            }
        }

        DC_WeaponObj activeWeapon = dcActiveObj.getActiveWeapon();
        if (activeWeapon != null && activeWeapon.isRanged()) {
            if (dcActiveObj.getRef().getObj(Ref.KEYS.AMMO) == null) {
                for (DC_QuickItemObj ammo : dcActiveObj.getOwnerObj().getQuickItems()) {
                    MenuNodeDataSource inner =
                            () -> new ActionValueContainer(
                                    getOrCreateR(ammo.getImagePath()),
                                    ammo::invokeClicked
                            );
                    list.add(inner);
                }
            }
        }

        MenuNodeDataSource source = new MenuNodeDataSource() {
            @Override
            public ActionValueContainer getCurrent() {
                return new ActionValueContainer(
                        new TextureRegion(getTextureForActive(dcActiveObj, target)), null);
            }

            @Override
            public List<MenuNodeDataSource> getChilds() {
                return list;
            }
        };

        result.add(source);

        return result;
    }

    private static MenuNodeDataSource configureMoveNode(DC_Obj target, DC_ActiveObj dcActiveObj) {
        MenuNodeDataSource result;

        if (target == dcActiveObj.getOwnerObj()) {
            result = configureSelectiveTargetedNode(dcActiveObj);
        } else {
            if (dcActiveObj.getTargeting() instanceof SelectiveTargeting) {
                result = () -> new ActionValueContainer(
                        getOrCreateR(dcActiveObj.getImagePath()),
                        () -> {
                            DC_Cell cell = target.getGame().getCellByCoordinate(target.getCoordinates());
                            if (dcActiveObj.getTargeter().canBeTargeted(cell.getId()))
                                dcActiveObj.activateOn(target);
                        }
                );
            } else {
                result = () -> new ActionValueContainer(
                        new TextureRegion(getTextureForActive(dcActiveObj, target)),
                        dcActiveObj::invokeClicked
                );
            }
        }
        return result;
    }
}
