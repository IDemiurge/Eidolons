package main.game.logic.dungeon.editor;

import main.entity.Entity;
import main.entity.Ref;
import main.entity.obj.DC_Cell;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.DC_Game;
import main.game.battlefield.Coordinates;
import main.swing.components.obj.CellComp;
import main.system.auxiliary.ListMaster;
import main.system.datatypes.DequeImpl;

import java.util.*;

public class LE_Simulation extends DC_Game {
	// one per mission/level???
	DequeImpl<ObjType> customTypes;

	private Mission mission;
	private Level level;

	private Entity selectedEntity;

	private LinkedList<DC_HeroObj> unitsCache;

	private Set<Obj> cells;

	private DC_Cell cell;

	public LE_Simulation() {
		super(true);

	}

	@Override
	public DC_HeroObj getUnitByCoordinate(Coordinates coordinates) {
		Obj obj = getObjectByCoordinate(coordinates, false);
        if (obj instanceof DC_HeroObj) {
            return (DC_HeroObj) obj;
        }
        return null;
	}

	@Override
	public Set<Obj> getCells() {
        if (cells != null) {
            return cells;
        }
        cells = new HashSet<>();
        for (int i = 0; i < LevelEditor.getGrid().getCellsX(); i++) {
            for (int j = 0; j < LevelEditor.getGrid().getCellsY(); j++) {
                CellComp c = LevelEditor.getGrid().getCells()[i][j];
                cells.add(c.getTerrainObj());
            }
        }
        return cells;
	}

	public void init() {
		super.init();
//		setManager(new DC_GameManager(getState(), this) {
//			@Override
//			public MicroObj createUnit(ObjType type, int x, int y, Player owner, Ref ref) {
//				Coordinates coordinates = new Coordinates(x, y);
//				MicroObj obj = LE_ObjMaster.getObjCache().get(type);
//				DC_HeroObj unit = null;
//				if (obj == null) {
//					obj = super.createUnit(type, x, y, owner, ref);
//
//				}
//				unit = (DC_HeroObj) obj;
//				getUnits().add(unit);
//				LE_ObjMaster.unitAdded(coordinates, unit);
//
//				return unit;
//			}
//
//			public void rightClicked(Obj obj) {
//				if (LevelEditor.getMouseMaster().checkEventConsumed(obj, true))
//					return;
//
//				super.rightClicked(obj);
//			}
//
//			public void objClicked(Obj obj) {
//				// ControlPanel().clicked(obj) => remove / ...
//
//				if (LevelEditor.getMouseMaster().checkEventConsumed(obj, false))
//					return;
//				deselectInfo();
//				setSelectedEntity(obj);
//				setSelectedInfoObj(obj);
//				infoSelect(obj);
//			}
//
//			@Override
//			public void deselectInfo() {
//				DrawMasterStatic.getObjImageCache().remove(getSelectedEntity());
//				if (infoObj != null)
//					infoObj.setInfoSelected(false);
//			}
//
//			public void infoSelect(Obj obj) {
//				obj.setInfoSelected(true);
//				DrawMasterStatic.getObjImageCache().remove(obj);
//				LevelEditor.getMainPanel().getInfoPanel().selectType(obj.getType());
//				LevelEditor.updateDynamicControls();
//			}
//
//			public void infoSelect(Entity entity) {
//				LevelEditor.getMainPanel().getInfoPanel().selectType(entity.getType());
//			}
//		});

	}

	public Entity getSelectedEntity() {
		return selectedEntity;
	}

    public void setSelectedEntity(Entity selectedEntity) {
        this.selectedEntity = selectedEntity;

    }

	public DC_Cell getCellByCoordinate(Coordinates c) {
        if (cell == null) {
            cell = new DC_Cell(c.x, c.y, this, new Ref(), getLevel().getDungeon());
        } else {
            cell.setCoordinates(c);
        }
        return cell;
	}

	@Override
	public void remove(Obj obj) {
		state.removeObject(obj.getId());
		// obj.removed();
        if (obj instanceof DC_HeroObj) {
            removeUnit((DC_HeroObj) obj);
        }
    }

	private void unitAdded(DC_HeroObj obj) {
		unitsCache = new LinkedList<>(units);
		// new Thread(new Runnable() { public void run() { } },
		// " thread").start();
		if (LevelEditor.DEBUG_ON) {
			main.system.auxiliary.LogMaster.log(1, toString() + " added "
					+ obj.getNameAndCoordinate() + ", units= " + units);
		}
	}

	public void removeUnit(DC_HeroObj obj) {
		getUnits().remove(obj);
		unitsCache = new LinkedList<>(units);
		if (LevelEditor.DEBUG_ON) {
			main.system.auxiliary.LogMaster.log(1, toString() + " removed "
					+ obj.getNameAndCoordinate() + ", units= " + units);
		}
	}

	@Override
	public DequeImpl<DC_HeroObj> getUnits() {
		return super.getUnits();
	}

	public void setUnits(Collection<DC_HeroObj> unitsCache) {
		// state.removeObject(id)
		this.units = new DequeImpl<DC_HeroObj>(unitsCache);
		// state.addObject(obj)
	}

    public LinkedList<DC_HeroObj> getUnitsCache() {
        return unitsCache;
    }

	public void setUnitsCache(LinkedList<DC_HeroObj> unitsCache) {
		this.unitsCache = unitsCache;
	}

	public Collection<Obj> getUnitsForCoordinates(Set<Coordinates> coordinates) {
		Collection<Obj> list = new LinkedList<>();
        for (Coordinates c : coordinates) {
            list.addAll(getObjectsOnCoordinate(c));
        }
        return list; // TODO z-coordinate?
	}

	@Override
	public List<DC_HeroObj> getObjectsOnCoordinate(Coordinates c) {
		List<DC_HeroObj> list = getUnitMap().get(c);
        if (list == null) {
            return new LinkedList<>();
        }
        List<DC_HeroObj> objects = new LinkedList<>();
		for (DC_HeroObj obj : list) {
            if (!obj.isOverlaying()) {
                objects.add(obj);
            }
        }
		return objects;
		// return super.getObjectsOnCoordinate(getLevel().getDungeon().getZ(),
		// c, false, true, false);
	}

	@Override
	public List<DC_HeroObj> getObjectsOnCoordinate(Integer z, Coordinates c,
			Boolean overlayingIncluded, boolean passableIncluded, boolean cellsIncluded) {
		List<DC_HeroObj> list = getUnitMap().get(c);
        if (list == null) {
            return new LinkedList<>();
        }
        List<DC_HeroObj> objects = new LinkedList<>();
		for (DC_HeroObj obj : list) {
			if (overlayingIncluded != null) {
                if (overlayingIncluded) {
                    if (!obj.isOverlaying()) {
                        continue;
                    }
                }
                if (obj.isOverlaying()) {
                    continue;
                }
            }
			objects.add(obj);
		}
		return objects;

	}

	public List<DC_HeroObj> getOverlayingObjects(Coordinates c) {
		// return getObjectsOnCoordinate(getLevel().getDungeon().getZ(), c,
		// true, true, false);
		List<DC_HeroObj> list = getUnitMap().get(c);
        if (list == null) {
            return new LinkedList<>();
        }

		List<DC_HeroObj> objects = new LinkedList<>();
		for (DC_HeroObj obj : list) {
            if (obj.isOverlaying()) {
                objects.add(obj);
            }
        }
		return objects;
	}

	public Obj getObjectByCoordinate(Coordinates c, boolean cellsIncluded) {
		List<DC_HeroObj> unitObjects = getUnitMap().get(c);
		if (ListMaster.isNotEmpty(unitObjects)) {
			for (DC_HeroObj o : unitObjects) {
                if (!o.isOverlaying()) {
                    return o;
                }
            }
			return unitObjects.get(0);
		}
        if (!cellsIncluded) {
            return null;
        }
        return getCellByCoordinate(c);
		// if (obj == null) {
		// MapBlock block = getDungeon().getPlan().getBlockByCoordinate(c);
		// if (block != null) {
		// DC_Cell cell = new DC_Cell(c.x, c.y, this, new Ref(),
		// getLevel().getDungeon());
		// // TODO
		// return cell;
		// }
		// for (MapZone z : getDungeon().getPlan().getZones()) {
		// // if (CoordinatesMaster.isWithinBounds(c, z.getX1(), z.getX2(),
		// // z.getY1(), z.getY2())) {
		// LevelEditor.getObjMaster().stackObj(
		// DataManager.getType(z.getFillerType(), OBJ_TYPES.BF_OBJ), c);
        // obj = getLevel().getTopObjMap().getOrCreate(c);
        // return obj;
        // // }
        // }
        // }
        // return obj;
        // for (DC_Obj obj : getLevel().getMapObjects())
        // if (obj.getCoordinates().equals(c))
		// return obj;
		// if (!cellsIncluded)
		// return null;
		// for (DC_Obj obj : getLevel().getCells())
		// if (obj.getCoordinates().equals(c))
		// return obj;

	}

	public Mission getMission() {
		return mission;
	}

	public void setMission(Mission mission) {
		this.mission = mission;
	}

	public Level getLevel() {
		return LevelEditor.getCurrentLevel();
	}

	public LE_MouseMaster getMouseListener() {
		return LevelEditor.getMouseMaster();
	}

	public void addType(ObjType type) {
		getCustomTypes().add(type);

	}

	public DequeImpl<ObjType> getCustomTypes() {
        if (customTypes == null) {
            customTypes = new DequeImpl<>();
        }
        return customTypes;
	}

}
