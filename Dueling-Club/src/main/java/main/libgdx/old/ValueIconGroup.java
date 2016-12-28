package main.libgdx.old;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;

/**
 * Created with IntelliJ IDEA.
 * Date: 30.10.2016
 * Time: 15:37
 * To change this template use File | Settings | File Templates.
 */
public class ValueIconGroup extends Group {
    private String resIcon = "\\UI\\value icons\\resistance.jpg";
    private String spiritIcon = "\\UI\\value icons\\spirit.jpg";
    private String enduranceIcon = "\\UI\\value icons\\Endurance.jpg";
    private String damageIcon = "\\UI\\value icons\\damage.jpg";
    private String armorIcon = "\\UI\\value icons\\armor.jpg";
    private String offDamageIcon = "\\UI\\value icons\\Off Hand Damage.jpg";
    private String attackIcon = "\\UI\\value icons\\attack.jpg";
    private String defenceIcon = "\\UI\\value icons\\Defense.jpg";
    private String offAttackIcon = "\\UI\\value icons\\Off Hand Attack.jpg";

    private ValueIcon resValue;
    private ValueIcon spiritValue;
    private ValueIcon enduranceValue;
    private ValueIcon damageValue;
    private ValueIcon armorValue;
    private ValueIcon offDamageValue;
    private ValueIcon attackValue;
    private ValueIcon defenceValue;
    private ValueIcon offAttackValue;

    private String backIcon = "\\UI\\components\\box.jpg";

    private String imagePath;

    public ValueIconGroup(String imagePath) {
        this.imagePath = imagePath;
    }

    public ValueIconGroup init() {
        Texture t = new Texture(imagePath + backIcon);
        resValue = new ValueIcon(imagePath + resIcon, t).init();
        spiritValue = new ValueIcon(imagePath + spiritIcon, t).init();
        enduranceValue = new ValueIcon(imagePath + enduranceIcon, t).init();

        damageValue = new ValueIcon(imagePath + damageIcon, t).init();
        armorValue = new ValueIcon(imagePath + armorIcon, t).init();
        offDamageValue = new ValueIcon(imagePath + offDamageIcon, t).init();

        attackValue = new ValueIcon(imagePath + attackIcon, t).init();
        defenceValue = new ValueIcon(imagePath + defenceIcon, t).init();
        offAttackValue = new ValueIcon(imagePath + offAttackIcon, t).init();

        final int offset = 1;

        attackValue.setX(0);
        attackValue.setY(0);

        defenceValue.setX(attackValue.getWidth() + offset);
        defenceValue.setY(0);

        offAttackValue.setX(attackValue.getWidth() * 2 + offset * 2);
        offAttackValue.setY(0);

        damageValue.setX(0);
        damageValue.setY(armorValue.getHeight() + offset);

        armorValue.setX(damageValue.getWidth() + offset);
        armorValue.setY(armorValue.getHeight() + offset);

        offDamageValue.setX(attackValue.getWidth() * 2 + offset * 2);
        offDamageValue.setY(armorValue.getHeight() + offset);

        resValue.setX(0);
        resValue.setY(armorValue.getHeight() * 2 + offset * 2);

        spiritValue.setX(attackValue.getWidth() + offset);
        spiritValue.setY(armorValue.getHeight() * 2 + offset * 2);

        enduranceValue.setX(attackValue.getWidth() * 2 + offset * 2);
        enduranceValue.setY(armorValue.getHeight() * 2 + offset * 2);

        addActor(resValue);
        addActor(spiritValue);
        addActor(enduranceValue);

        addActor(damageValue);
        addActor(armorValue);
        addActor(offDamageValue);

        addActor(attackValue);
        addActor(defenceValue);
        addActor(offAttackValue);

        setHeight(attackValue.getHeight() * 3 + offset * 2);
        setWidth(attackValue.getWidth() * 3 + offset * 2);

        return this;
    }
}
