 package main.level_editor.backend.metadata.script;

import main.data.xml.XmlStringBuilder;
import main.game.bf.Coordinates;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.level_editor.backend.handlers.operation.Operation;
import main.system.SortMaster;
import main.system.auxiliary.StringMaster;
import main.system.data.DataUnit;
import main.system.threading.WaitMaster;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Function;

import static main.level_editor.backend.handlers.operation.Operation.LE_OPERATION.PASTE_END;
import static main.level_editor.backend.handlers.operation.Operation.LE_OPERATION.PASTE_START;

public abstract class CellDataHandler<T extends DataUnit> extends LE_Handler {
    public CellDataHandler(LE_Manager manager) {
        super(manager);
    }

    @Override
    public void afterLoaded() {
        init();
    }

    protected abstract void init();

    private static Map<Coordinates, String> copiedData;

    public void pasteData() {
        pasteData(getSelectionHandler().getSelection().getFirstCoordinate());
    }

    public void pasteData(Coordinates origin) {
        operation(PASTE_START);
        Coordinates offset = null ;
        for (Coordinates c : copiedData.keySet()) {
            if (copiedData.size() > 1 && offset == null) {
                offset = c; //the first - top left object will be our offset
            } else {
                if (offset != null) {
                    c = c.getOffset(c.getX() - offset.x,
                            c.getY() - offset.y);
                }
            }
            String data = copiedData.get(c);
            Coordinates coordinates = offset == null ? origin : origin.getOffset(offset);
            T prev = getData(coordinates);
            T result = createData(data);
            if (isAppend()){
                result = append(prev, result);
            }
            operation(getOperation(), coordinates, result,
                    prev);
        }
        operation(PASTE_END);
    }

    protected abstract T append(T prev, T result);

    protected boolean isAppend() {
        return getModel().isAppendMode();
    }


    public void copyData(boolean cut) {
        copiedData = new TreeMap() {
            @Override
            public Comparator comparator() {
                return SortMaster.getSorterByExpression(c -> -(((Coordinates) c).getX() * 10
                        + ((Coordinates) c).getY()));
            }
        };
        for (Coordinates c : getModel().getSelection().getCoordinates()) {
            T data = getData(c);
            if (data == null) {
                continue;
            }
            copiedData.put(c, (
                    data.getData()));
            if (cut)
                clear(c);
        }

    }

    @Override
    public String getPreObjXml(Function<Integer, Boolean> idFilter, Function<Coordinates, Boolean> coordinateFilter) {
        XmlStringBuilder builder = new XmlStringBuilder();
        for (Coordinates coordinates : getMap().keySet()) {
            if (!coordinateFilter.apply(coordinates)) {
                continue;
            }
            T scriptData = getMap().get(coordinates);
            builder.append(coordinates.toString()).append("=").append(scriptData.getData())
                    .append(StringMaster.VERTICAL_BAR);
        }
        return builder.wrap(getXmlNodeName()).toString();
    }

    protected abstract String getXmlNodeName();

    public void clear(Coordinates c) {
        getOperationHandler().operation(getOperation(), c,
                createData(""), getData(c));
    }

    protected abstract T getData(Coordinates c);

    public void editData(Coordinates c) {
        editData(c, data -> {
            editData(data);
        }, getEditOperation());
    }

    public void editData(Coordinates c, Consumer<T> editAction, WaitMaster.WAIT_OPERATIONS editOperation) {
        getEditHandler().setEditCoordinates(c);
        T data = getMap().get(c);
        T prev = data == null
                ? createData("")
                : createData(data.getData());
        if (data == null) {
            data = createData("");
        }

        editAction.accept(data);
        if (editOperation != null) {
            WaitMaster.waitForInputAnew(editOperation);
        }

        String text = data.getData();
        if (!text.equals(prev.getData())) {
            getOperationHandler().operation(getOperation(), c, data, prev);
        }
    }

    protected void setData(Coordinates c, String s) {
        editData(c, data -> data.setData(s), null);
    }

    protected abstract Map<Coordinates, T> getMap();

    protected abstract Operation.LE_OPERATION getOperation();

    protected abstract WaitMaster.WAIT_OPERATIONS getEditOperation();

    protected abstract T createData(String s);

}
