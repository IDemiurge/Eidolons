package apps.prompt;

import apps.prompt.enums.PromptEnums;

/**
 * Created by Alexander on 9/2/2023
 */
public class PromptModel {
    PromptEnums.PromptStyle style;
    PromptEnums.PromptType type;
    Object subtype;
    String fixedInput;

    public PromptModel(PromptEnums.PromptStyle style, PromptEnums.PromptType type, Object subtype, String fixedInput) {
        this.style = style;
        this.type = type;
        this.subtype = subtype;
        this.fixedInput = fixedInput;
    }

    public PromptEnums.PromptStyle getStyle() {
        return style;
    }

    public PromptEnums.PromptType getType() {
        return type;
    }

    public String getFixedInput() {
        return fixedInput;
    }

    public Object getSubtype() {
        return subtype;
    }
}
