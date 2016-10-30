package main.libgdx;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;

/**
 * Created with IntelliJ IDEA.
 * Date: 30.10.2016
 * Time: 15:37
 * To change this template use File | Settings | File Templates.
 */
public class DC_GDX_ValueIconGroup extends Group {
    private String resIcon = "\\UI\\value icons\\resistance.jpg";
    private String spiritIcon = "\\UI\\value icons\\spirit.jpg";
    private String enduranceIcon = "\\UI\\value icons\\Endurance.jpg";
    private String damageIcon = "\\UI\\value icons\\damage.jpg";
    private String armorIcon = "\\UI\\value icons\\armor.jpg";
    private String offDamageIcon = "\\UI\\value icons\\Off Hand Damage.jpg";
    private String attackIcon = "\\UI\\value icons\\attack.jpg";
    private String defenceIcon = "\\UI\\value icons\\Defense.jpg";
    private String offAttackIcon = "\\UI\\value icons\\Off Hand Attack.jpg";

    private DC_GDX_ValueIcon resValue;
    private DC_GDX_ValueIcon spiritValue;
    private DC_GDX_ValueIcon enduranceValue;
    private DC_GDX_ValueIcon damageValue;
    private DC_GDX_ValueIcon armorValue;
    private DC_GDX_ValueIcon offDamageValue;
    private DC_GDX_ValueIcon attackValue;
    private DC_GDX_ValueIcon defenceValue;
    private DC_GDX_ValueIcon offAttackValue;

    private String backIcon = "\\UI\\components\\box.jpg";

    private String imagePath;

    public DC_GDX_ValueIconGroup(String imagePath) {
        this.imagePath = imagePath;
    }

    public DC_GDX_ValueIconGroup init() {
        Texture t = new Texture(imagePath + backIcon);
        resValue = new DC_GDX_ValueIcon(imagePath + resIcon, t);
        spiritValue = new DC_GDX_ValueIcon(imagePath + spiritIcon, t);
        enduranceValue = new DC_GDX_ValueIcon(imagePath + enduranceIcon, t);

        damageValue = new DC_GDX_ValueIcon(imagePath + damageIcon, t);
        armorValue = new DC_GDX_ValueIcon(imagePath + armorIcon, t);
        offDamageValue = new DC_GDX_ValueIcon(imagePath + offDamageIcon, t);

        attackValue = new DC_GDX_ValueIcon(imagePath + attackIcon, t);
        defenceValue = new DC_GDX_ValueIcon(imagePath + defenceIcon, t);
        offAttackValue = new DC_GDX_ValueIcon(imagePath + offAttackIcon, t);

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

        return this;
    }
}
