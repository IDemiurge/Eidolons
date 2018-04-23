package main.system.auxiliary;

import main.ability.AbilityType;
import main.entity.type.ObjType;
import main.game.core.game.Game;

import java.io.*;

public class CloneMaster  {
    //this used to work for ObjTypes too until 'somefilename' was deleted....
    // now it only works for tree nodes
    public static Object deepCopy(Object object) {
        FileOutputStream fos;
        ObjectOutputStream out;
        try {
            fos = new FileOutputStream("somefilename");
            out = new ObjectOutputStream(fos);
            out.writeObject(object);
            out.close();
        } catch (IOException ex) {
            main.system.ExceptionMaster.printStackTrace(ex);
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
            main.system.ExceptionMaster.printStackTrace(ex);
        } catch (ClassNotFoundException ex) {
            main.system.ExceptionMaster.printStackTrace(ex);
        }
        return null;
    }

    public static ObjType getTypeCopy(ObjType type, String newName, Game game, String group) {
//        Ref ref = type.getRef();
//        type.setGame(null);
//        type.setRef(null);

        ObjType newType = null;
        if (type instanceof AbilityType)
            newType =
             new AbilityType((AbilityType) type);
        else newType =
         new ObjType(type);
//         (ObjType) CloneMaster.deepCopy(type);
        newType.cloned();
//        type.setRef(ref);n
//        if (ref != null) {
//            type.setGame(ref.getGame());
//        }

        return newType;
    }
}
