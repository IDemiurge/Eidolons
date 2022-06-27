package logic.content.test;

import java.util.Map;

public class TestUnitContent {

    public enum TestUnit implements ContentEnum{
        Zombie,
        Rogue,
        Fiend,
        Archer,

        ;

        @Override
        public Map<String, Object> getValues() {
            return null;
        }
    }
}
