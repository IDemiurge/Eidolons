package main.game.logic.dungeon.editor;

import main.content.CONTENT_CONSTS.BF_OBJECT_GROUP;
import main.content.CONTENT_CONSTS.FLIP;
import main.content.CONTENT_CONSTS.MAP_FILL_TEMPLATE;
import main.content.OBJ_TYPE;
import main.content.OBJ_TYPES;
import main.content.properties.G_PROPS;
import main.data.DataManager;
import main.entity.Ref;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.entity.type.ObjAtCoordinate;
import main.entity.type.ObjType;
import main.game.battlefield.Coordinates;
import main.game.battlefield.Coordinates.DIRECTION;
import main.game.battlefield.DirectionMaster;
import main.game.logic.dungeon.Entrance;
import main.game.logic.dungeon.building.MapBlock;
import main.game.logic.dungeon.editor.gui.LE_MapViewComp;
import main.game.logic.macro.utils.CoordinatesMaster;
import main.game.player.DC_Player;
import main.launch.ArcaneVault;
import main.rules.mechanics.StackingRule;
import main.swing.generic.components.editors.lists.ListChooser;
import main.swing.generic.services.dialog.DialogMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.ListMaster;
import main.system.auxiliary.MapMaster;
import main.system.auxiliary.RandomWizard;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;

import javax.swing.*;
import java.util.*;

public class LE_ObjMaster {

	private static Map<Level, Stack<Map<Coordinates, List<DC_HeroObj>>>> cacheMap = new HashMap<>();
	private static boolean cachingOff;

	public static Map<ObjType, DC_HeroObj> getObjCache() {
		return LevelEditor.getCurrentLevel().getObjCache();
	}

	public static void unitAdded(Coordinates coordinates, DC_HeroObj unit) {
		cache();
		getObjCache().put(unit.getType(), unit);
		List<DC_HeroObj> objects = LevelEditor.getSimulation().getUnitMap().get(coordinates);
		if (objects == null) {
			objects = new LinkedList<>();
			LevelEditor.getSimulation().getUnitMap().put(coordinates, objects);
		}
		objects.add(unit);

	}

	public static void fillAreaRandomFromData(String dataString, OBJ_TYPE TYPE) {
		List<Coordinates> coordinates = LE_MapMaster.pickCoordinates();
		LevelEditor.cache();
		for (Coordinates c : coordinates) {
			ObjType type = RandomWizard.getObjTypeByWeight(dataString, TYPE);
			cachingOff = true;
			try {
				LevelEditor.getObjMaster().addObj(type, c);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				cachingOff = false;
			}
			// multi-layer? a couple of boulders on the same cell could be just
			// as well
		}

	}

	public static void removeObjects(List<Coordinates> c) {
		cache();
		// LevelEditor.getCurrentLevel().removeObj(c.toArray(new
		// Coordinates[c.size()]));
		cachingOff = true;
		try {
            for (Coordinates coordinate : c) {
                removeObj(coordinate);
            }
        } catch (Exception e) {
			e.printStackTrace();
		} finally {
			cachingOff = false;
		}
	}

	public static void removeObj(Coordinates coordinates) {
		// LevelEditor.getCurrentLevel().removeObj(coordinates);
		cache();
		LevelEditor.getSimulation().getUnitMap().remove(coordinates);
	}

	public static boolean fillArea(boolean diagonal) {

		List<Coordinates> coordinates = LE_MapMaster.pickCoordinates(diagonal);
        if (!ListMaster.isNotEmpty(coordinates)) {
            return false;
        }
        fill(coordinates, LevelEditor.getMainPanel().getPalette().getSelectedType());
		return true;
	}

	public static void fill(List<Coordinates> list, ObjType type) {
		cache();
		LevelEditor.getObjMaster().addObj(type, list.toArray(new Coordinates[list.size()]));

		// TODO - if wall, ought to remove c from block
	}

	public static boolean fillAreaCustomFiller() {

		MAP_FILL_TEMPLATE template = new EnumMaster<MAP_FILL_TEMPLATE>().retrieveEnumConst(
				MAP_FILL_TEMPLATE.class, ListChooser.chooseEnum(MAP_FILL_TEMPLATE.class));
        if (template == null) {
            return false;
        }
        List<Coordinates> coordinates = LE_MapMaster.pickCoordinates();
		String data = template.getCenterObjects() + template.getPeripheryObjects();
		for (Coordinates c : coordinates) {
			ObjType type = null;
			int i = 0;
			for (Coordinates adj : c.getAdjacentCoordinates()) {
                if (coordinates.contains(adj)) {
                    i++;
                } else {
                    if (data.contains(LevelEditor.getCurrentLevel().getDungeon().getGame()
                            .getObjectByCoordinate(c, true).getName())) {
                        i++;
                    }
                }
			}
            if (i >= c.getAdjacentCoordinates().size() / 2) {
                type = RandomWizard.getObjTypeByWeight(template.getCenterObjects(),
                        OBJ_TYPES.BF_OBJ);
            } else {
                type = RandomWizard.getObjTypeByWeight(template.getCenterObjects(),
                        OBJ_TYPES.BF_OBJ);
            }
            LevelEditor.getObjMaster().addObj(type, c);
		}
		// keep choosing coordinates ...
		// single click allowed... highlight... done/cancel on fill() click
		// multi-template combine
		// new template

		return true;
	}

	public static void replace() {
		ObjType type = ArcaneVault.getSelectedType();
		List<Coordinates> coordinates = LE_MapMaster.pickCoordinates();
        if (!ListMaster.isNotEmpty(coordinates)) {
            return;
        }
        ObjType type2 = ArcaneVault.getSelectedType();
		if (type2 == type) {
			type2 = DataManager.getType(ListChooser.chooseType(type.getOBJ_TYPE_ENUM()), type
					.getOBJ_TYPE_ENUM());
		}
		replace(type, type2, coordinates);

	}

	public static void replace(ObjType type, ObjType type2, List<Coordinates> coordinates) {
		cache();
		for (Coordinates coordinate : coordinates) {
			List<DC_HeroObj> objects = LevelEditor.getSimulation().getUnitMap().get(coordinate);
			// LevelEditor.getGrid().getCells()[coordinate.x][coordinate.y]
			// .getObjects();
            if (objects != null) {
                for (DC_HeroObj obj : new LinkedList<>(objects)) {
                    if (obj.getType().equals(type)) {
                        objects.remove(obj);
                        objects.add(getObject(type2, coordinate));
                    }
                }
            }
        }
		LevelEditor.getMainPanel().getMapViewComp().getGrid().refresh();
	}

	public static void moveSelectedObj() {
		Coordinates c = LE_MapMaster.pickCoordinate();
		Obj selectedObj = LevelEditor.getMouseMaster().getSelectedObj();
		int offsetX = c.x - selectedObj.getX();
		int offsetY = c.y - selectedObj.getY();
		moveObj(selectedObj.getCoordinates(), offsetX, offsetY);
	}

	public static void moveObjects() {
		moveObjects(false, false);
	}

	public static boolean moveObjects(boolean copy, boolean mirror) {
		List<Coordinates> coordinates = LE_MapMaster.pickCoordinates();
        if (!ListMaster.isNotEmpty(coordinates)) {
            return false;
        }
        return moveObjects(copy, coordinates, mirror);
	}

	private static boolean moveObjects(boolean copy, List<Coordinates> coordinates, boolean mirror) {
		Coordinates destination = LevelEditor.getMouseMaster().pickCoordinate();
        if (destination == null) {
            return false;
        }
        int offsetY = destination.y - CoordinatesMaster.getMinY(coordinates);
		int offsetX = destination.x - CoordinatesMaster.getMinX(coordinates);
		moveObjects(destination, coordinates, offsetX, offsetY, copy, mirror);
		return true;
	}

	public static void moveObjects(Coordinates destination, List<Coordinates> coordinates,
			int offsetX, int offsetY, boolean copy, boolean mirror) {
		cache();
        if (!copy) {
            removeObjects(coordinates);
        }
        for (Coordinates coordinate : coordinates) {
			moveObj(coordinate, offsetX, offsetY, copy, mirror); // copy or
																	// not...
		}
		LevelEditor.getMainPanel().getMapViewComp().getGrid().refresh();
	}

	private static void cache() {
        if (cachingOff) {
            return;
        }
        List<List<DC_HeroObj>> list = new LinkedList<>(LevelEditor.getSimulation().getUnitMap()
				.values());
		Stack<Map<Coordinates, List<DC_HeroObj>>> cache = getCache();
		cache.push(new MapMaster<Coordinates, List<DC_HeroObj>>().constructMap(LevelEditor
				.getSimulation().getUnitMap().keySet(), list));
	}

	private static Stack<Map<Coordinates, List<DC_HeroObj>>> getCache() {
		Stack<Map<Coordinates, List<DC_HeroObj>>> cache = cacheMap.get(LevelEditor
				.getCurrentLevel());
		if (cache == null) {
			cache = new Stack<>();
			cacheMap.put(LevelEditor.getCurrentLevel(), cache);
		}
		return cache;
	}

	public static void undo() {
		if (getCache().isEmpty()) {
			SoundMaster.playStandardSound(STD_SOUNDS.CLICK_BLOCKED);
			return;
		}
		LevelEditor.getSimulation().setUnitMap(getCache().pop());
		for (MapBlock b : LevelEditor.getCurrentLevel().getBlocks()) {
			b.resetObjects();
		}
		LevelEditor.refreshGrid();
	}

	public static void moveObj(Coordinates c, int offsetX, int offsetY) {
		moveObj(c, offsetX, offsetY, false, false);
	}

	public static void moveObj(Coordinates c, int offsetX, int offsetY, boolean copy, boolean mirror) {
		// don't remove all - use selective
		Map<Coordinates, List<DC_HeroObj>> unitMap = LevelEditor.getSimulation().getUnitMap();
        if (!copy) {
            unitMap = getCache().peek();
        }
        List<DC_HeroObj> objects = unitMap.get(c);
        if (!ListMaster.isNotEmpty(objects)) {
            return;
        }
        // if (!noRemove)
		// LevelEditor.getSimulation().getUnitMap().put(c, new
		// LinkedList<DC_HeroObj>());
		Coordinates newCoordinates = new Coordinates(c.x + offsetX, c.y + offsetY);
        if (mirror) {
            newCoordinates = new Coordinates(c.x + offsetY, c.y + offsetX);
        }
        LevelEditor.getSimulation().getUnitMap().put(newCoordinates, objects);
	}

	public static boolean mirror() {
		return moveObjects(true, true);
	}

	public static boolean copy() {
		// will ai groups be copied?... or members extended?
		return moveObjects(true, false);
	}

	public static List<ObjAtCoordinate> newUnitGroup(Coordinates baseCoordinate, int width,
			int height, boolean alt) {
		List<ObjAtCoordinate> list = new LinkedList<>();
		// TODO make sure base is actually top-left!
        for (int x = 0; x <= width; x++) {
            for (int y = 0; y <= height; y++) {
                List<DC_HeroObj> objects = LevelEditor.getSimulation().getObjectsOnCoordinate(
                        new Coordinates(x + baseCoordinate.x, baseCoordinate.y + y));
                // filter out bf?
                for (DC_HeroObj obj : objects) {
                    list.add(new ObjAtCoordinate(obj.getType(), new Coordinates(x, y)));
                }
            }
        }
        return list;
	}

	public static DC_HeroObj getObject(ObjType type, Coordinates c) {
		DC_HeroObj obj;
        if (type.checkProperty(G_PROPS.BF_OBJECT_GROUP, BF_OBJECT_GROUP.ENTRANCE.toString())) {
            obj = new Entrance(c.x, c.y, type, LevelEditor.getCurrentLevel().getDungeon(), null);
        } else {
            obj = getObjCache().get(type);
			if (obj == null) {
				obj = new DC_HeroObj(type, c.x, c.y, DC_Player.NEUTRAL,
						LevelEditor.getSimulation(), new Ref());

			}
		}
		getObjCache().put(type, obj);
		return obj;
	}

	public static void setFlip(DC_HeroObj obj, Coordinates c) {
		int i = DialogMaster.optionChoice("Set flip", FLIP.values());
		FLIP d = null;
        if (i == -1) {
            return;
        }
        d = FLIP.values()[i];

		Map<DC_HeroObj, FLIP> map = obj.getGame().getFlipMap().get(c);

		if (map == null) {
			map = new HashMap<>();
			obj.getGame().getFlipMap().put(c, map);
		}
		map.put(obj, d);
	}

	public static void setDirection(DC_HeroObj obj, Coordinates c) {
		List list = new LinkedList<>(Arrays.asList(DIRECTION.values()));
		list.add(0, "Center");
		int i = DialogMaster.optionChoice("Set direction (none==center)", list.toArray());
		DIRECTION d = null;
		if (i == -1) {
			d = DirectionMaster.getRandomDirection();
			// random?
		}
		if (i > 0) {
			d = DIRECTION.values()[i - 1];

		}

		Map<DC_HeroObj, DIRECTION> map = obj.getGame().getDirectionMap().get(c);

		if (map == null) {
			map = new HashMap<>();
			obj.getGame().getDirectionMap().put(c, map);
		}
		map.put(obj, d);

	}

    public void addEntrance() {

    }

    public DC_HeroObj stackObj(ObjType type, Coordinates... coordinates) {
        return addObj(type, true, coordinates);
    }

    public DC_HeroObj addObj(ObjType type, Coordinates... coordinates) {
        return addObj(type, false, coordinates);
    }

    public void removeObj(DC_Obj obj) {
        // LevelEditor.getCurrentLevel().removeObj(obj);
    }

    public DC_HeroObj addObj(ObjType type, boolean stack, Coordinates... coordinates) {
        DC_HeroObj obj = null;
        for (Coordinates c : coordinates) {
            List<DC_HeroObj> list = LevelEditor.getSimulation().getObjectsOnCoordinate(c);

            if (!StackingRule.checkCanPlace(c, type, list)) {
                // replace cases? if 1 coordinate, prompt...
                if (LevelEditor.getCurrentLevel().isInitialized()) {
                    SoundMaster.playStandardSound(STD_SOUNDS.DIS__BLOCKED);
                    // DialogMaster.inform(type +
                    // " cannot be placed onto wall - " + c);
                }
                continue;

            }

            obj = getObject(type, c);
            List<DC_HeroObj> objects = LevelEditor.getSimulation().getUnitMap().get(c);
            if (objects == null) {
                objects = new LinkedList<>();
                LevelEditor.getSimulation().getUnitMap().put(c, objects);
            }
            objects.add(obj);
            LevelEditor.getCurrentLevel().addObj(obj, c, stack);
            if (obj.isOverlaying()) {
                LE_ObjMaster.setDirection(obj, c);
            }
            try {
                if (LE_MapViewComp.isMinimapMode()) {
                    LevelEditor.getMainPanel().getMiniGrid().refreshComp(null, c);
                } else {
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            LevelEditor.getGrid().refresh();

                        }
                    });
                }
                // LevelEditor.getGrid().repaintComp(c);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Chronos.mark("Main Panel refresh");
        // if (LevelEditor.getMainPanel().getCurrentLevel().isInitialized())
        // LevelEditor.getMainPanel().refreshGui();
        // Chronos.logTimeElapsedForMark("Main Panel refresh");

        // check wall ?
        return obj;
    }

	public void editObj(Obj obj) {

	}

	public void fillAreaRandomFromSubGroup() {

	}

	public void fillAreaRandomFromGroup() {

	}

	public void fillAreaRandomFromPalette() {

	}

}
