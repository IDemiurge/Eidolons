package apps.prompt.token;

import apps.prompt.PromptModel;
import apps.prompt.enums.PromptEnums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static apps.prompt.enums.PromptEnums.TokenType.*;

/**
 * Created by Alexander on 9/2/2023
 */
public class TokenMixer {
    private PromptTemplate template;

    public void setTemplate(PromptTemplate template) {
        this.template = template;
    }
/*
content_scene_temple,
        content_character_temple,
        content_character_wicked,
        content_character_bone,
        content_character_ice,
        content_character_anphis,
        content_character_winter_kingdom,
        content_env_wicked,
        content_env_bone,
        content_env_ice,
        content_env_winter_kingdom,
        content_scene_victim,
        content_env_desert,
        content_character_desert,
        content_env_autumn,
        content_character_autumn,
 */
    public enum PromptTemplate{
        x(

        ),
    character_ice_bone(
                content_character_ice,
                content_character_ice,
                content_env_ice,
                content_env_bone
        ),

temple_gothic(
        content_character_temple,
        content_character_anphis,
        content_scene_temple,
        content_env_desert,
        content_env_desert
),
        wicked_temple_wicked_env(
                content_character_wicked,
                content_character_temple,
                content_env_wicked,
                content_env_wicked),

        autumn_temple(content_character_temple,
                content_character_temple,
                content_character_autumn,
                content_env_autumn),
        autumn_env(content_env_autumn,
                content_character_autumn,
                content_env_autumn,
                content_env_autumn),
        
        autumn_char(
                content_character_autumn,
                content_character_autumn,
                content_env_autumn,
                content_env_autumn),
        autumn_scene(content_scene_temple,
                content_env_autumn,
                content_character_autumn,
                content_character_autumn,
                content_env_autumn),
        ;

        PromptTemplate(PromptEnums.TokenType... tokens) {
            this.tokens = tokens;
        }

        PromptEnums.TokenType[] tokens;
    }

    public static final PromptTemplate DEFAULT = PromptTemplate.autumn_scene;


    public List<Token> createTokens(PromptModel promptModel) {
        // List<Token> list = new LinkedList<>();
        // List<PromptEnums.TokenType> tokenPlan = Arrays.asList(base);
        // PromptTemplate template =  new EnumChooser().choose(PromptTemplate.class);
        if (template==null ){
             template = DEFAULT;
        }
        return  Arrays.stream(template.tokens).map(type -> createToken(type, promptModel)).collect(Collectors.toList());
    }

    private Token createToken(PromptEnums.TokenType type, PromptModel model) {
        return new Token(type, model.getType(), model.getSubtype());
    }

    //randomize order to some extent?
    public void mix(List<Token> tokens) {

    }
}
