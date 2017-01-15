package main.gui.components.table;

import main.content.CONTENT_CONSTS.ATTRIBUTE;
import main.content.CONTENT_CONSTS.SPECIAL_REQUIREMENTS;
import main.content.CONTENT_CONSTS.SPELL_GROUP;
import main.content.CONTENT_CONSTS.WEAPON_TYPE;
import main.content.*;
import main.content.DC_ValueManager.VALUE_GROUP;
import main.content.parameters.PARAMETER;
import main.content.properties.G_PROPS;
import main.content.properties.MACRO_PROPS;
import main.data.TableDataManager;
import main.data.XLinkedMap;
import main.data.ability.construct.VariableManager;
import main.data.filesys.PathFinder;
import main.elements.conditions.Condition;
import main.elements.conditions.Conditions;
import main.elements.conditions.NotCondition;
import main.elements.conditions.StringComparison;
import main.entity.Ref.KEYS;
import main.entity.type.ObjType;
import main.gui.builders.EditViewPanel;
import main.gui.components.editors.AV_ImgChooser;
import main.launch.ArcaneVault;
import main.swing.generic.components.editors.*;
import main.swing.generic.components.editors.lists.ListChooser.SELECTION_MODE;
import main.swing.generic.components.misc.G_Table;
import main.system.ConditionMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.ListMaster;
import main.system.auxiliary.StringMaster;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TableMouseListener extends DefaultCellEditor implements MouseListener {
	public static final String[] SINGLE_RES_LIST_IDS = { G_PROPS.IMPACT_SPRITE.name(),
	// PROPS.MAP_BACKGROUND.name(),
	};
	public static final String[] MULTI_RES_FILE_IDS = {
	PROPS.ARCADE_LEVELS.name(),
			PROPS.ARCADE_ENEMY_GROUPS.name(),
	};
	public static final String[] MULTI_RES_FILE_KEYS = {
	"XML\\dungeons\\levels\\battle\\",
			"XML\\groups\\",

	};
	public static final VALUE[] SPRITE_IDS = {
	PROPS.ANIM_SPRITE_CAST,
	PROPS.ANIM_SPRITE_RESOLVE,
	PROPS.ANIM_SPRITE_MAIN,
	PROPS.ANIM_SPRITE_IMPACT,
	PROPS.ANIM_SPRITE_AFTEREFFECT,
	PROPS.ANIM_MISSILE_SPRITE,
	};
	public static final String SPRITE_PATH =
	"img\\mini\\sprites\\";

	public static final VALUE[] SFX_IDS = {
	//as single enum for now! 
	};
	public static final String SFX_PATH =
	"img\\mini\\sfx\\";
	public static final String[] SINGLE_RES_FILE_IDS = {};
	public static final String[] RES_FILE_KEYS = {};
	public static final String[] SINGLE_RES_FOLDER_IDS = { G_PROPS.SOUNDSET.name(), };
	public static final String[] RES_FOLDER_KEYS = { "sound\\soundsets\\", };

	public static final String[] RES_KEYS = {
	"img\\mini\\sprites\\impact",
	// DungeonMaster.getDungeonBackgroundFolder()
	};
	public static final String[] VAR_MULTI_ENUM_LIST_IDS = {
			G_PROPS.PRINCIPLES.name(),
			"Encounter Subgroup", // contains()? otherwise overshadows
			G_PROPS.SPECIAL_REQUIREMENTS.getName(), PROPS.FAVORED_SPELL_GROUPS.getName(),
			PROPS.ATTRIBUTE_PROGRESSION.getName(), PROPS.MASTERY_PROGRESSION.getName(),
			PROPS.ROLL_TYPES_TO_DISPEL_EACH_TURN.getName(), PROPS.ROLL_TYPES_TO_SAVE.getName(),

	};
	public static final Class<?>[] VAR_ENUM_CLASS_LIST = { SPECIAL_REQUIREMENTS.class

	};
	public static final String[] SINGLE_ENUM_LIST_IDS = { PROPS.WEAPON_ATTACKS.name(),
			G_PROPS.UNIT_GROUP.name(), G_PROPS.CUSTOM_HERO_GROUP.name(),
			PROPS.BF_OBJ_MATERIAL.name(), PROPS.BF_OBJECT_SIZE.name(), PROPS.LINK_VARIANT.name(),
			G_PROPS.GAME_VERSION.name(), PROPS.SUBDUNGEON_TYPE.name(),
			G_PROPS.DUNGEON_SUBFOLDER.name(), G_PROPS.ENCOUNTER_SUBGROUP.name(),
			MACRO_PROPS.SHOP_TYPE.name(), MACRO_PROPS.SHOP_LEVEL.name(),
			MACRO_PROPS.SHOP_MODIFIER.name(), G_PROPS.BUFF_TYPE.name(),
			G_PROPS.WORKSPACE_GROUP.name(), PROPS.DUNGEON_MAP_MODIFIER.name(),
			PROPS.DUNGEON_MAP_TEMPLATE.name(), G_PROPS.BF_OBJECT_TYPE.name(),
			G_PROPS.BF_OBJECT_GROUP.name(), G_PROPS.BF_OBJECT_CLASS.name(), PROPS.AI_TYPE.name(),
			PROPS.AI_LOGIC.name(), G_PROPS.BACKGROUND.name(), PROPS.EFFECTS_WRAP.name(),
			G_PROPS.DUNGEON_GROUP.name(), G_PROPS.DUNGEON_TYPE.name(),
			G_PROPS.DUNGEON_LEVEL.name(), G_PROPS.ENCOUNTER_TYPE.name(),
			G_PROPS.ENCOUNTER_GROUP.name(), G_PROPS.KEYS.name(), G_PROPS.ABILITY_GROUP.name(),
			G_PROPS.ABILITY_TYPE.name(), G_PROPS.ITEM_MATERIAL_GROUP.name(),
			G_PROPS.ITEM_TYPE.name(), G_PROPS.ITEM_GROUP.name(), G_PROPS.ARMOR_TYPE.name(),
			G_PROPS.ARMOR_GROUP.name(), PROPS.DAMAGE_TYPE.name(), G_PROPS.WEAPON_SIZE.name(),
			G_PROPS.WEAPON_CLASS.name(), G_PROPS.WEAPON_GROUP.name(), G_PROPS.WEAPON_TYPE.name(),
			G_PROPS.QUALITY_LEVEL.name(), G_PROPS.MATERIAL.name(), G_PROPS.SKILL_GROUP.name(),
			G_PROPS.ACTION_TYPE.name(), G_PROPS.MASTERY.name(), PROPS.PRIME_MISSION.name(),
			PROPS.SECONDARY_MISSION.name(), G_PROPS.SPELL_GROUP.name(),
			PROPS.RESISTANCE_TYPE.name(), G_PROPS.TARGETING_MODE.name(), G_PROPS.RACE.name(),
			G_PROPS.ASPECT.name(),
			// G_PROPS.SOUNDSET.name(),
			G_PROPS.RANK.name(), PROPS.FAVORED_ASPECT.name(), PROPS.SECOND_FAVORED_ASPECT.name(),
			PROPS.THIRD_FAVORED_ASPECT.name(), G_PROPS.SPELL_TYPE.name(),
			// "DEITY",
			PROPS.BF_OBJECT_SIZE.name(), PROPS.OBJECT_ARMOR_TYPE.name(), PROPS.DIMENSION.name() };
	public static final String[] MULTIPLE_ENUM_LIST_IDS = {

	PROPS.ANIM_SFX_CAST.name(),
	PROPS.ANIM_SFX_RESOLVE.name(),
	PROPS.ANIM_SFX_MAIN.name(),
	PROPS.ANIM_SFX_IMPACT.name(),
	PROPS.ANIM_SFX_AFTEREFFECT.name(),
	PROPS.ANIM_MISSILE_SFX.name(),
	PROPS.PARAMETER_BONUSES.getName(),
			PROPS.PALETTE.name(), PROPS.ATTRIBUTE_BONUSES.getName(), PROPS.DUNGEON_TAGS.name(),
			PROPS.MASTERY_GROUPS_MAGIC.name(), PROPS.MASTERY_GROUPS_WEAPONS.name(),
			PROPS.MASTERY_GROUPS_MISC.name(), G_PROPS.SPELL_UPGRADE_GROUPS.name(),
			PROPS.JEWELRY_ITEM_TRAIT_REPERTOIRE.name(),
			PROPS.JEWELRY_PASSIVE_ENCHANTMENT_REPERTOIRE.name(), G_PROPS.BF_OBJECT_TAGS.name(),
			PROPS.ARCADE_LOOT_TYPE.name(), PROPS.ALT_ARCADE_LOOT_TYPE.name(),
			G_PROPS.ARCADE_ROUTE.name(), PROPS.ROLL_TYPES_TO_DISPEL_EACH_TURN.name(),
			PROPS.ROLL_TYPES_TO_SAVE.name(), PROPS.QUALITY_LEVEL_RANGE.name(),
			PROPS.ALLOWED_MATERIAL.name(), PROPS.MASTERY_PROGRESSION.name(),
			G_PROPS.VARIABLE_TYPES.name(), PROPS.ATTRIBUTE_PROGRESSION.name(),
			PROPS.GROWTH_PRIORITIES.name(), G_PROPS.STD_BOOLS.name(),
			PROPS.TARGETING_MODIFIERS.name(), G_PROPS.SPECIAL_REQUIREMENTS.name(),
			PROPS.STANDARD_ACTION_PASSIVES.name(), PROPS.STANDARD_SPELL_PASSIVES.name(),
			G_PROPS.STANDARD_PASSIVES.name(), G_PROPS.ACTION_TAGS.name(),
			G_PROPS.SPELL_TAGS.name(), G_PROPS.CLASSIFICATIONS.name(), };

	public static final String[] ENUM_LIST_IDS = {};
	public static final Class<?>[] ENUM_LIST_CLASSES = { SPELL_GROUP.class };

	public static final String[] MULTI_TYPE_LIST_IDS = { PROPS.HERO_BACKGROUNDS.getName(),
			PROPS.HEADQUARTER_DUNGEON.getName(), PROPS.FACTION_DUNGEONS.getName(),
			PROPS.ALLY_FACTIONS.getName(), PROPS.UNIT_POOL.getName(), MACRO_PROPS.AREAS.getName(),
			G_PROPS.DEITY.getName(), PROPS.SPELL_PLAN.name(), PROPS.VERBATIM_PRIORITY.name(),
			PROPS.MEMORIZATION_PRIORITY.name(), PROPS.MAIN_HAND_REPERTOIRE.name(),
			PROPS.ARMOR_REPERTOIRE.name(), PROPS.OFF_HAND_REPERTOIRE.name(),
			PROPS.QUICK_ITEM_REPERTOIRE.name(), PROPS.ENCOUNTERS.name(),
			PROPS.ALT_ENCOUNTERS.name(),

			PROPS.CLASSES.name(), PROPS.INVENTORY.name(), PROPS.QUICK_ITEMS.name(),
			PROPS.XP_PLAN.name(), PROPS.EXTENDED_PRESET_GROUP.name(),
			PROPS.SHRUNK_PRESET_GROUP.name(), PROPS.PRESET_GROUP.name(), PROPS.FILLER_TYPES.name(),
			PROPS.UNIT_TYPES.name(), MACRO_PROPS.HEROES.name(), MACRO_PROPS.PLACES.name(),
			PROPS.SKILL_OR_REQUIREMENTS.name(), PROPS.SKILL_REQUIREMENTS.name(),
			PROPS.SKILLS.name(), PROPS.SPELLBOOK.name(), PROPS.VERBATIM_SPELLS.name(),
			PROPS.MEMORIZED_SPELLS.name(), PROPS.PARTY_UNITS.name(), PROPS.FOLLOWER_UNITS.name(),
			PROPS.ALLIED_DEITIES.name(), PROPS.FRIEND_DEITIES.name(), PROPS.ENEMY_DEITIES.name(),

	};
	public static final OBJ_TYPE[] MULTI_TYPE_LIST = { OBJ_TYPES.CHARS, OBJ_TYPES.DUNGEONS,
			OBJ_TYPES.DUNGEONS, OBJ_TYPES.FACTIONS, OBJ_TYPES.UNITS,
			MACRO_OBJ_TYPES.AREA,
			OBJ_TYPES.DEITIES,
			OBJ_TYPES.SPELLS,
			OBJ_TYPES.SPELLS,
			OBJ_TYPES.SPELLS,
			// OBJ_TYPES.JEWELRY,
			OBJ_TYPES.WEAPONS, OBJ_TYPES.ARMOR,
			OBJ_TYPES.WEAPONS,
			OBJ_TYPES.ITEMS,
			OBJ_TYPES.ENCOUNTERS,
			OBJ_TYPES.ENCOUNTERS,

			OBJ_TYPES.CLASSES,
			C_OBJ_TYPE.ITEMS,
			C_OBJ_TYPE.QUICK_ITEMS,
			OBJ_TYPES.SKILLS,

			// C_OBJ_TYPE.UNITS, C_OBJ_TYPE.UNITS, C_OBJ_TYPE.UNITS,
			// C_OBJ_TYPE.UNITS, C_OBJ_TYPE.UNITS,
			OBJ_TYPES.UNITS, OBJ_TYPES.UNITS, OBJ_TYPES.UNITS, OBJ_TYPES.UNITS, OBJ_TYPES.UNITS,
			MACRO_OBJ_TYPES.PLACE, OBJ_TYPES.SKILLS, OBJ_TYPES.SKILLS, OBJ_TYPES.SKILLS,
			OBJ_TYPES.SPELLS, OBJ_TYPES.SPELLS, OBJ_TYPES.SPELLS, OBJ_TYPES.SPELLS,
			OBJ_TYPES.UNITS, OBJ_TYPES.UNITS, OBJ_TYPES.DEITIES, OBJ_TYPES.DEITIES,
			OBJ_TYPES.DEITIES, };

	public static final String[] SINGLE_TYPE_LIST_IDS = { PROPS.FIRST_CLASS.getName(),
			PROPS.SECOND_CLASS.getName(),

			PROPS.BOSS_TYPE.getName(), G_PROPS.MAIN_HAND_ITEM.name(), G_PROPS.OFF_HAND_ITEM.name(),
			G_PROPS.ARMOR_ITEM.name(), };
	public static final OBJ_TYPE[] SINGLE_TYPE_LIST = { OBJ_TYPES.CLASSES, OBJ_TYPES.CLASSES,
			OBJ_TYPES.UNITS, OBJ_TYPES.WEAPONS, OBJ_TYPES.WEAPONS, OBJ_TYPES.ARMOR, };

	private static final Condition[] TYPE_LIST_CONDITIONS = { new StringComparison("{SOURCE_"
			+ MACRO_PROPS.REGION.getName() + "}", "{MATCH_" + MACRO_PROPS.REGION.getName() + "}",
			true), };
	private static final String[] CONDITIONAL_MULTI_LIST_IDS = { MACRO_PROPS.INTERNAL_ROUTES
			.getName() };
	private static final OBJ_TYPE[] CONDITIONAL_MULTI_TYPE_LIST = { MACRO_OBJ_TYPES.PLACE };
	public static final String[] MAP_EDITOR_IDS = { MACRO_PROPS.AREA.name(), };
	public static final String[] MULTI_VAR_TYPE_IDS = { MACRO_PROPS.INTERNAL_ROUTES.getName(), };
	public static final Object[][] MULTI_VAR_TYPES = { new Object[] { MACRO_OBJ_TYPES.ROUTE,
			Integer.class } };
	private static final String[] GROUP_FILTERED = {

	};
	private static final String[] FILTER_GROUPS = {

	};
	private static final String[] SUBGROUP_FILTERED = { PROPS.HERO_BACKGROUNDS.getName(), };
	private static final String[] FILTER_SUBGROUPS = { "Background", };
	static AV_ImgChooser imageChooser = new AV_ImgChooser();
	static ListEditor multiListEditor = new ListEditor(false);
	static ListEditor abilsListEditor = new ListEditor(false, OBJ_TYPES.ABILS);
	static TextEditor textEditor = new TextEditor();
	// static NumberEditor numberEditor = new NumberEditor();
	private static Map<String, EDITOR> editorMap = new XLinkedMap<String, EDITOR>();
	private static String imgIdentifier = G_PROPS.IMAGE.getName();
	private static String actIdentifier = G_PROPS.ACTIVES.getName();
	private static String pasIdentifier = G_PROPS.PASSIVES.getName();
	private static String emblemIdentifier = G_PROPS.EMBLEM.getName();
	private static String soundsetIdentifier = G_PROPS.CUSTOM_SOUNDSET.getName();
	private static EDITOR soundChooser = new SoundChooser();

	private static XLinkedMap<String, String> groupFilterMap = new XLinkedMap<>();
	private static XLinkedMap<String, String> subGroupFilterMap = new XLinkedMap<>();
	private G_Table table;
	private boolean second;
	private ValueEditor altHandler;

	public TableMouseListener(G_Table table, boolean second) {
		super(new JTextField());
		this.table = table;
		this.second = second;
		if (!second)
			configureEditors();
	}

	public TableMouseListener(G_Table table) {
		this(table, false);
	}

	// TODO rework into functional/lazy style
	public static void configureEditors() {
		DC_ContentManager.setEditorMap(editorMap);

		editorMap.put(soundsetIdentifier, soundChooser);
		editorMap.put(imgIdentifier, imageChooser);
		editorMap.put(PROPS.MAP_BACKGROUND.getName(), new AV_ImgChooser(null));
		editorMap.put(emblemIdentifier, imageChooser);
		editorMap.put(actIdentifier, multiListEditor);
		editorMap.put(pasIdentifier, abilsListEditor);
		// .getTYPEDcopy(OBJ_TYPES.ABILS));
		for (String id : SINGLE_ENUM_LIST_IDS) {
			id = StringMaster.getWellFormattedString(id);
			editorMap.put(id, new ListEditor(SELECTION_MODE.SINGLE, true));
		}

		for (String id : MULTIPLE_ENUM_LIST_IDS) {
			id = StringMaster.getWellFormattedString(id);
			ListEditor multiEnumListEditor = new ListEditor(true);
			multiEnumListEditor.setVarTypes(getMultiTypeVarTypes(id));
			Class<?> enumClass = null;
			if (isWeightedType(id)) {
				enumClass = EnumMaster.getEnumClass(id, DC_CONSTS.class, true);
				if (id.contains("Mastery Groups")) {
					enumClass = VALUE_GROUP.class;
				}
			} else {
				if (id.equalsIgnoreCase(PROPS.PARAMETER_BONUSES.getName())) {
					enumClass = PARAMS.class;
					multiEnumListEditor.setListData(DC_ContentManager.getBonusParamList());
				}
				if (id.equalsIgnoreCase(PROPS.ATTRIBUTE_BONUSES.getName())) {
					enumClass = ATTRIBUTE.class;
				}
				if (enumClass != null)
					multiEnumListEditor.setVarTypes(ListMaster.toList(String.class));
			}
			if (enumClass != null) {
				multiEnumListEditor.setVarTypesClass(enumClass);
				multiEnumListEditor.setEnumClass(enumClass);
			}
			editorMap.put(id, multiEnumListEditor);
		}
		int i = 0;
		for (String id : GROUP_FILTERED) {
			id = StringMaster.getWellFormattedString(id);
			groupFilterMap.put(id, FILTER_GROUPS[i]);
			i++;
		}
		for (String id : SUBGROUP_FILTERED) {
			id = StringMaster.getWellFormattedString(id);
			subGroupFilterMap.put(id, FILTER_SUBGROUPS[i]);
			i++;
		}
		i = 0;
		for (String id : VAR_MULTI_ENUM_LIST_IDS) {
			ListEditor listEditor = new ListEditor(SELECTION_MODE.MULTIPLE, true);
			if (VAR_ENUM_CLASS_LIST.length <= i)
				listEditor.setVarTypesClass(VariableManager.STRING_VAR_CLASS);
			else {
				listEditor.setEnumClass(VAR_ENUM_CLASS_LIST[i]);
				listEditor.setVarTypesClass(VAR_ENUM_CLASS_LIST[i]);
			}
			editorMap.put(id, listEditor);
			i++;
		}

		i = 0;
		for (String id : CONDITIONAL_MULTI_LIST_IDS) {
			ListEditor listEditor = new ListEditor(SELECTION_MODE.MULTIPLE, false,
					CONDITIONAL_MULTI_TYPE_LIST[i]);
			listEditor.setConditions(TYPE_LIST_CONDITIONS[i]);
			for (String arg0 : MULTI_VAR_TYPE_IDS) {
				int j = 0;
				if (id.equals(arg0)) {

					listEditor.setVarTypes(Arrays.asList(MULTI_VAR_TYPES[j]));

				}
				j++;
			}
			editorMap.put(id, listEditor);

			i++;
		}
		i = 0;
		for (String id : SINGLE_RES_LIST_IDS) {
			id = StringMaster.getWellFormattedString(id);
			editorMap.put(id, new ListEditor(SELECTION_MODE.SINGLE, RES_KEYS[i]));
			i++;
		}

		i = 0;

		for (String id : SINGLE_RES_FOLDER_IDS) {
			id = StringMaster.getWellFormattedString(id);
			final int index = i;
			editorMap.put(id, new FileChooser(true) {
				protected String getDefaultFileLocation() {
					return PathFinder.getEnginePathPlusNewResourceProject() + RES_FOLDER_KEYS[index];
				}
            });
			i++;
		}

		i = 0;
		for (String id : SINGLE_RES_FILE_IDS) {
			id = StringMaster.getWellFormattedString(id);
			final int index = i;
			editorMap.put(id, new FileChooser(false) {
				protected String getDefaultFileLocation() {
					return PathFinder.getEnginePathPlusNewResourceProject() + RES_FILE_KEYS[index];
				}
            });
			i++;
		}

		i = 0;
		for (String id : MULTI_RES_FILE_IDS) {
			id = StringMaster.getWellFormattedString(id);
			final int index = i;
			editorMap.put(id, new FileChooser(false, true) {
				protected String getDefaultFileLocation() {
					return PathFinder.getEnginePathPlusNewResourceProject() + MULTI_RES_FILE_KEYS[index];
				}
            });
			i++;
		}

		for (VALUE val : SPRITE_IDS) {
			String	id = StringMaster.getWellFormattedString(val.name());
			editorMap.put(id, new FileChooser(false, true) {
				protected String getDefaultFileLocation() {
					return PathFinder.getEnginePathPlusNewResourceProject() + SPRITE_PATH ;
				}
            });
		}
		for (VALUE val : SFX_IDS) {
			String	id = StringMaster.getWellFormattedString(val.name());
			editorMap.put(id, new FileChooser(false, true) {
				protected String getDefaultFileLocation() {
					return PathFinder.getEnginePathPlusNewResourceProject() + SFX_PATH ;
				}
            });
		}


		i = 0;
		for (String id : ENUM_LIST_IDS) {
			id = StringMaster.getWellFormattedString(id);
			editorMap.put(id, new ListEditor(SELECTION_MODE.MULTIPLE, true, ENUM_LIST_CLASSES[i]));
			i++;
		}

		i = 0;
		for (String id : MULTI_TYPE_LIST_IDS) {
			id = StringMaster.getWellFormattedString(id);
			ListEditor listEditor = new ListEditor(SELECTION_MODE.MULTIPLE, false,
					MULTI_TYPE_LIST[i]);
			listEditor.setConditions(getMultiTypeCondition(id));
			listEditor.setVarTypes(getMultiTypeVarTypes(id));
			if (isWeightedType(id))
				listEditor.setVarTypesClass(VariableManager.STRING_VAR_CLASS);
			if (subGroupFilterMap.get(id) != null)
				listEditor.setFilterSubgroup(subGroupFilterMap.get(id));
			if (groupFilterMap.get(id) != null)
				listEditor.setFilterGroup(groupFilterMap.get(id));
			editorMap.put(id, listEditor);
			i++;
		}
		i = 0;
		for (String id : SINGLE_TYPE_LIST_IDS) {
			id = StringMaster.getWellFormattedString(id);
			editorMap.put(id, new ListEditor(SELECTION_MODE.SINGLE, false, SINGLE_TYPE_LIST[i]));
			i++;
		}
		// for (String id : textIdentifiers) {
		// editorMap.put(id, te);
		// }
		// prop enum browser, ...

	}

	private static List<Object> getMultiTypeVarTypes(String id) {
		if (isWeightedType(id))
			return ListMaster.toList(String.class);
		return null;
	}

	private static Condition getMultiTypeCondition(String id) {
		if (isWeightedType(id))
			return new Conditions(new NotCondition(new StringComparison(StringMaster.getValueRef(
					KEYS.MATCH, G_PROPS.WEAPON_TYPE), "" + WEAPON_TYPE.NATURAL, true)),
					ConditionMaster.getItemBaseTypeFilterCondition());
		return null;
	}

	private static boolean isWeightedType(String id) {

		return StringMaster.getWellFormattedString(id).contains("Repertoire")
				|| id.contains("Plan") || id.contains("Mastery Groups") || id.contains("Priority");
	}

	public void handleMouseClick(MouseEvent e) {
		handleMouseClick(e, e.isAltDown());
	}

	public void handleMouseClick(MouseEvent e, boolean altDown) {

		int row = table.getSelectedRow();
		int column = table.getColumn(EditViewPanel.NAME).getModelIndex();

		Object valueAt = table.getValueAt(row, column);
		VALUE val = ContentManager.getValue(valueAt.toString());
		String value = "";
		ObjType selectedType = (second) ? ArcaneVault.getPreviousSelectedType() : ArcaneVault
				.getSelectedType();
		if (val != null)
			value = selectedType.getValue(val);
		else {
			value = table.getValueAt(row, 1).toString();
		}
		if (altHandler != null)
			if (altHandler.checkClickProcessed(e, selectedType, val, value))
				return;

		// table.setRowSelectionInterval(row, row);
		if (altDown || e.isControlDown()
		// SwingUtilities.isRightMouseButton(e)
		) {
			if (val instanceof PARAMETER && (val != PARAMS.FORMULA)) {
				new NumberEditor().launch(table, row, column, value);
				return;
			}

			textEditor.launch(table, row, column, value, getEditorByValueName(valueAt) == null);
			return;
		}
		EDITOR editor = getEditorByValueName(valueAt);
		// TODO lazy editor init!
		if (editor instanceof ListEditor) {
			((ListEditor) editor).setBASE_TYPE(selectedType.getOBJ_TYPE_ENUM());
			((ListEditor) editor).setEntity(selectedType);
		}
		if (editor != null) {
			try {
				editor.launch(table, row, column, value);
			} catch (NullPointerException ex) {
				ex.printStackTrace();
				handleMouseClick(e, true);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {
			// decorators
			launchDefault(table, row, column, value);
		}
	}

	public static EDITOR getEditorByValueName(Object valueAt) {
		return editorMap.get(valueAt);
	}

	private void launchDefault(G_Table table, int row, int column, String value) {
		String valueName = table.getValueAt(row, TableDataManager.NAME_COLUMN).toString();

		// if (ContentManager.isParameter(valueName)) {
		// numberEditor.launch(table, row, column, value);
		// } else
		textEditor.launch(table, row, column, value);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		ArcaneVault.setAltPressed(e.isAltDown());
		try {
			handleMouseClick(e);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			ArcaneVault.setAltPressed(false);
		}
		// Weaver.inNewThread(this, "handleMouseClick", e, MouseEvent.class);

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// e.getComponent().c

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
