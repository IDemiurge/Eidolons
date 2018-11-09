package eidolons.system.utils;

import eidolons.content.PARAMS;
import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.core.game.DC_Game;
import main.content.C_OBJ_TYPE;
import main.content.DC_TYPE;
import main.content.VALUE;
import main.data.DataManager;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.system.launch.CoreEngine;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import static eidolons.content.PARAMS.*;

/**
 * Created by JustMe on 5/31/2017.
 */
public class XlsMaster {
    private static final String TYPE_KEY = "active"; //TODO get for TYPE!
    static DC_TYPE[] TYPES = {
     DC_TYPE.ACTIONS,
//     DC_TYPE.WEAPONS,

    };
    static PARAMS[] ACTION_PARAMS = {
     DAMAGE_MOD, DAMAGE_BONUS,

     STR_DMG_MODIFIER,
     AGI_DMG_MODIFIER, INT_DMG_MODIFIER, SP_DMG_MODIFIER,
     CRITICAL_MOD, IMPACT_AREA, AUTO_ATTACK_RANGE,
     SIDE_DAMAGE_MOD, DIAGONAL_DAMAGE_MOD, SIDE_ATTACK_MOD,
     DIAGONAL_ATTACK_MOD,

     ARMOR_PENETRATION, ARMOR_MOD,
     BLEEDING_MOD, COUNTER_MOD, FORCE_MOD, FORCE_DAMAGE_MOD,
     FORCE_MAX_STRENGTH_MOD, FORCE_MOD_SOURCE_WEIGHT, STR_DMG_MODIFIER,
     AGI_DMG_MODIFIER, INT_DMG_MODIFIER, SP_DMG_MODIFIER,
     DURABILITY_DAMAGE_MOD, CRITICAL_MOD, ACCURACY, IMPACT_AREA,
     AUTO_ATTACK_RANGE, SIDE_DAMAGE_MOD, DIAGONAL_DAMAGE_MOD,
     SIDE_ATTACK_MOD, DIAGONAL_ATTACK_MOD

     , CLOSE_QUARTERS_ATTACK_MOD, CLOSE_QUARTERS_DAMAGE_MOD,
     LONG_REACH_ATTACK_MOD, LONG_REACH_DAMAGE_MOD,

    };
    static String[] ACTION_FORMULAS = {
     "Dmg coef:{Strength}*{active_str_dmg_modifier}" //attackCalculator?!
    };
    static PARAMS[][] PARAM_ARRAYS = {
     ACTION_PARAMS,
    };
    static String[][] FORMULA_ARRAYS = {
     ACTION_FORMULAS,
    };
    static String[][] GROUPS_ARRAYS = {
     {
      "Standard Attack"
     },
    };
    private static String simUnitName = "Thief";
    private static String formula_sep = ":";

    private static void parseXls(
     String path) {
        //from excel to types!
    }

    private static void createSheet(HSSFWorkbook workbook,
                                    DC_TYPE TYPE, String[] groups,
                                    PARAMS[] params, String[] formulas) {
        HSSFSheet sheet = workbook.createSheet(TYPE.getName());
        int row = 0;

        HSSFRow rowhead = sheet.createRow((short) row);
        rowhead.createCell(0).setCellValue("Name");
        int column = 1;
        for (String formula : formulas) {
            rowhead.createCell(column).setCellValue(
             formula.split(formula_sep)[0]);
            column++;
        }
        for (PARAMS param : params) {
            rowhead.createCell(column).setCellValue((param).getShortName());
            column++;
        }
        row++;
        for (String group : groups) {
            rowhead = sheet.createRow((short) row);
            row++;
            rowhead.createCell(0).setCellValue(group);

            for (ObjType objType : DataManager.getTypesGroup(TYPE, group)) {
                rowhead = sheet.createRow((short) row);
                row++;
                column = 0;
                rowhead.createCell(column).setCellValue(objType.getName());
                column++;

                final ObjType simUnit = DataManager.getType(simUnitName, C_OBJ_TYPE.UNITS_CHARS);
                Ref ref = new Ref() {
                    @Override
                    public ObjType getType(String string) {
                        if (string.equalsIgnoreCase(TYPE_KEY))
                            return objType;
                        return simUnit;
                    }
                };

                Map<VALUE, Integer> valueMap = null;
                int simUnitRow = 0;
//                Map<VALUE, Integer> entityMap;
                for (String raw_formula : formulas) {
                    String formula = getFormula(raw_formula, row, column, valueMap, simUnitRow);
                    rowhead.createCell(column).setCellFormula(
                     formula
//                     String.valueOf(new Formula(formula.split(formula_sep)[1]).getInt(ref))
                    );
                    column++;
                }
                for (PARAMS param : params) {
                    rowhead.createCell(column).setCellValue(objType.getParam(param));
                    column++;
                }


            }

        }

    }

    private static String getFormula(String raw_formula, int row, int column, Map<VALUE, Integer> valueMap, int simUnitRow) {
//
        return getLetter(raw_formula) + ":" + getNumber(raw_formula);
    }

    private static String getNumber(String raw_formula) {
        return raw_formula;
    }

    private static String getLetter(String raw_formula) {
        return raw_formula;
    }

    public static void main(String[] args) {
        String types = "units;";
        for (DC_TYPE type : TYPES) {
            types += type.getName() + ";";
        }
        CoreEngine.setSelectivelyReadTypes(types);
        String path = "Y:/Google Drive/Project Eidolons/content/"; // args[0];
        String filename = path +
         "content.xls";
        DC_Engine.mainMenuInit();
        int i = 0;
        HSSFWorkbook workbook = new HSSFWorkbook();
        new DC_Game();
        for (DC_TYPE TYPE : TYPES) {


            PARAMS[] params = PARAM_ARRAYS[i];
            String[] formulas = FORMULA_ARRAYS[i];
            createSheet(workbook,
             TYPE, GROUPS_ARRAYS[i], params, formulas);

            i++;


        }
        try {
            FileOutputStream fileOut = new FileOutputStream(filename);
            workbook.write(fileOut);
            fileOut.close();
        } catch (IOException e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        System.out.println("Content excel file has been generated!");
    }

}