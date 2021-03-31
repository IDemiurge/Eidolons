package main.content.dataunit;

import main.system.data.DataUnit;

public class XmlDataUnit<T extends Enum<T>> extends DataUnit<T> {
    public XmlDataUnit(String text) {
        super(text);
    }

    @Override
    protected String getPairSeparator() {
        return "=";
    }

    @Override
    protected String getSeparator() {
        return ";";
    }
}
