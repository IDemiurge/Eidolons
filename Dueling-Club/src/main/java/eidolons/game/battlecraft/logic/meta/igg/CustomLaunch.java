package eidolons.game.battlecraft.logic.meta.igg;

import main.system.data.DataUnit;

import java.util.regex.Pattern;

public class CustomLaunch extends DataUnit<CustomLaunch.CustomLaunchValue> {

    public CustomLaunch(String text) {
        super(text);
    }

    @Override
    protected void handleMalformedData(String entry) {
        setValue(CustomLaunchValue.xml_path, entry);
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
    public Class<? extends CustomLaunchValue> getEnumClazz() {
        return CustomLaunchValue.class;
    }

    public enum CustomLaunchValue{
        xml_path,
        party_members,
        main_hero,

    }
}
