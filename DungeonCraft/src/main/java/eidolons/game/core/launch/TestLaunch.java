package eidolons.game.core.launch;

import main.system.data.DataUnit;

import java.util.regex.Pattern;

public class TestLaunch extends DataUnit<TestLaunch.TestValue> {
    public TestLaunch() {
    }

    public TestLaunch(String text) {
        super(text);
    }

    @Override
    protected void handleMalformedData(String entry) {
        setValue(TestValue.module, entry);
    }

    @Override
    protected String getSeparator() {
        return Pattern.quote("|");
    }

    @Override
    protected String getPairSeparator() {
        return Pattern.quote("::");
    }

    @Override
    public Class<? extends TestValue> getEnumClazz() {
        return TestValue.class;
    }

    public enum TestValue {
        module,
        chain,
        hero,
        encounter,
    }
}
