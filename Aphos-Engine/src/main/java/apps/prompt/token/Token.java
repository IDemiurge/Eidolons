package apps.prompt.token;

import apps.prompt.enums.PromptEnums;

/**
 * Created by Alexander on 9/2/2023
 *
 * usually 2 words?
 */
public class Token {
    private PromptEnums.TokenType type;

    private PromptEnums.PromptType promptType;
    private Object subtype;

    private String input;
    long randomSeed; // to replicate prompt if needed ?

    public Token(PromptEnums.TokenType type, PromptEnums.PromptType promptType, Object subtype) {
        this.type = type;
        this.promptType = promptType;
        this.subtype = subtype;
    }


    public boolean checkInputToken(String fixedInput) {
        if (type== PromptEnums.TokenType.input){
            input = fixedInput;
            return true;
        }
        return false;
    }

    public PromptEnums.TokenType getType() {
        return type;
    }

    public PromptEnums.PromptType getPromptType() {
        return promptType;
    }

    public Object getSubtype() {
        return subtype;
    }

    public String getInput() {
        return input;
    }
}
