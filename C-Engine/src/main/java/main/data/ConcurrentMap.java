package main.data;

import main.entity.type.ObjType;

import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentMap<K, V> extends ConcurrentHashMap<K, V> {
    public ConcurrentMap(int i, float f) {
        super(i, f);
    }

    public ConcurrentMap() {
        super();
    }

    @Override
    public V put(K key, V value) {
        if (key == null)
            return null;
        if (value == null)
            return remove(key);
        if (key instanceof ObjType) {
            ObjType objType = (ObjType) key;
            if (value instanceof ObjType) {
                ObjType objType2 = (ObjType) value;

                if (!objType2.getName().contains(objType.getName()))
                    return super.put(key, value);
            }
        }
        return super.put(key, value);
    }

    @Override
    public V get(Object key) {
        if (key == null)
            return null;
        return super.get(key);
    }
}
