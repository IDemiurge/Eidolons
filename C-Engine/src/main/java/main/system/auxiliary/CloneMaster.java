package main.system.auxiliary;

import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.core.game.Game;
import main.system.auxiliary.log.LogMaster;

import java.io.*;

public class CloneMaster<T> {

    public static Object deepCopy(Object object) {
        FileOutputStream fos;
        ObjectOutputStream out;
        try {
            fos = new FileOutputStream("somefilename");
            out = new ObjectOutputStream(fos);
            out.writeObject(object);
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        FileInputStream fis;
        ObjectInputStream in;
        try {
            fis = new FileInputStream("somefilename");
            in = new ObjectInputStream(fis);
            Object deepCopy = in.readObject();
            in.close();
            return deepCopy;
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        LogMaster.log(0, "DEEP COPY FAILED!");
        return null;
    }

    public static ObjType getTypeCopy(ObjType type, String newName, Game game, String group) {
        Ref ref = type.getRef();
        type.setGame(null);
        type.setRef(null);

        ObjType newType = (ObjType) CloneMaster.deepCopy(type);
        newType.cloned();
        type.setRef(ref);
        if (ref != null) {
            type.setGame(ref.getGame());
        }

        return newType;
    }
}
