package main.game.logic.dungeon.editor;

import main.entity.Entity;
import main.entity.Ref;
import main.entity.obj.DC_Cell;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.game.battlecraft.logic.dungeon.location.LocationMaster;
import main.game.bf.Coordinates;
import main.game.core.game.DC_Game;
import main.swing.components.obj.CellComp;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.datatypes.DequeImpl;

import java.util.*;

public class LE_Simulation extends DC_Game {
    // one per mission/level???
    DequeImpl<ObjType> customTypes;

    private Mission mission;
    private Level level;

    private Entity selectedEntity;

    private ArrayList<Unit> unitsCache;

    private Set<Obj> cells;

    private DC_Cell cell;

    public LE_Simulation() {
        super(true);

    }

    @Override
    public LocationMaster getDungeonMaster() {
        if (super.getDungeonMaster() == null)
            return createDungeonMaster();
        return (LocationMaster) super.getDungeonMaster();
    }

    @Override
    protected LocationMaster createDungeonMaster() {
        return new LocationMaster(this);
    }

    @Override
    public Unit getUnitByCoordinate(Coordinates coordinates) {
        Obj obj = getObjectByCoordinate(coordinates, false);
        if (obj instanceof Unit) {
            return (Unit) obj;
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
        if (obj instanceof Unit) {
            removeUnit((Unit) obj);
        }
    }

    private void unitAdded(Unit obj) {
        unitsCache = new ArrayList<>(getUnits());
        // new Thread(new Runnable() { public void run() { } },
        // " thread").start();
        if (LevelEditor.DEBUG_ON) {
            LogMaster.log(1, toString() + " added "
                    + obj.getNameAndCoordinate() + ", units= " + getUnits());
        }
    }

    public void removeUnit(Unit obj) {
        getUnits().remove(obj);
        unitsCache = new ArrayList<>(getUnits());
        if (LevelEditor.DEBUG_ON) {
            LogMaster.log(1, toString() + " removed "
                    + obj.getNameAndCoordinate() + ", units= " + getUnits());
        }
    }

    @Override
    public DequeImpl<Unit> getUnits() {
        return super.getUnits();
    }

    public void setUnits(Collection<Unit> unitsCache) {
        // state.removeObject(id)
        getUnits().clear();
        getUnits().addAll(unitsCache);
        // state.addObject(obj)
    }

    public ArrayList<Unit> getUnitsCache() {
        return unitsCache;
    }

    public void setUnitsCache(ArrayList<Unit> unitsCache) {
        this.unitsCache = unitsCache;
    }

    public Collection<Obj> getUnitsForCoordinates(Set<Coordinates> coordinates) {
        Collection<Obj> list = new ArrayList<>();
        for (Coordinates c : coordinates) {
            list.addAll(getObjectsOnCoordinate(c));
        }
        return list; // TODO z-coordinate?
    }

    @Override
    public List<Unit> getObjectsOnCoordinate(Coordinates c) {
        List<Unit> list = getUnitMap().get(c);
        if (list == null) {
            return new ArrayList<>();
        }
        List<Unit> objects = new ArrayList<>();
        for (Unit obj : list) {
            if (!obj.isOverlaying()) {
                objects.add(obj);
            }
        }
        return objects;
        // return super.getObjectsOnCoordinate(getLevel().getDungeon().getZ(),
        // c, false, true, false);
    }


    public Obj getObjectByCoordinate(Coordinates c, boolean cellsIncluded) {
        List<Unit> unitObjects = getUnitMap().get(c);
        if (ListMaster.isNotEmpty(unitObjects)) {
            for (Unit o : unitObjects) {
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
