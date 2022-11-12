package framework.sim;

import main.content.enums.GenericEnums;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.NumberUtils;
import main.system.math.MathMaster;

import java.util.Locale;
import java.util.Map;

public abstract class MathSim<T> {

    protected Boolean min_max_average;

    protected Map<String, Double> varMap;
    protected Map<String, String> varStringMap;

    public abstract T evaluate();


    public Double getVar(String name) {
        Double result = varMap.get(name.toLowerCase(Locale.ROOT));
        if (result == null) {
            result = 0d;
        }
        return result;
    }

    public int getVarInt(String name) {
        Double d = getVar(name);
        return (int) Math.round(d);
    }
    public void setMin_max_average(Boolean min_max_average) {
        this.min_max_average = min_max_average;
    }

    public void setVars(String names, String values) {
        int i = 0;
            String[] vals = values.split(";");
        for (String name : ContainerUtils.openContainer(names)) {
            setVar(name, vals[i++]);
        }
    }

    private void setVar(String name, String val) {
        Double number = NumberUtils.getDouble(val);
        varMap.put(name, number);
    }


    protected abstract GenericEnums.DieType getDiceType();

    protected abstract Integer getDice();
}
