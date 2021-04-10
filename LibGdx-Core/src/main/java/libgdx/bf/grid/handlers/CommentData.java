package libgdx.bf.grid.handlers;

import main.system.data.DataUnit;

import java.util.regex.Pattern;

public class CommentData extends DataUnit<CommentData.COMMENT_VALUE> {
    public CommentData(String text) {
        super(text);
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
    public Class<? extends  COMMENT_VALUE> getEnumClazz() {
        return COMMENT_VALUE.class;
    }

    public enum COMMENT_VALUE{
        image, unit, sequential,
        text, style, offset,
    }

}
