package main.game.battlecraft.logic.meta;

import main.client.cc.logic.party.PartyObj;
import main.client.dc.MetaManager;
import main.client.dc.Simulation;
import main.content.DC_TYPE;
import main.content.PARAMS;
import main.content.PROPS;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Converter;
import main.data.xml.XML_Reader;
import main.data.xml.XML_Writer;
import main.elements.conditions.Condition;
import main.elements.conditions.Conditions;
import main.elements.conditions.PrinciplesCondition;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.game.battlecraft.logic.battle.arena.Wave;
import main.game.core.game.DC_Game;
import main.game.core.game.DC_Game.GAME_MODES;
import main.swing.generic.services.dialog.DialogMaster;
import main.system.auxiliary.Loop;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.log.LogMaster;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;

public abstract class PartyManager<E extends MetaGame> extends MetaGameHandler<E> {

    protected PartyObj party;
    public PartyManager(MetaGameMaster master) {
        super(master);
    }

    public  abstract PartyObj initPlayerParty();

    public PartyObj getParty() {
        return party;
    }
}
