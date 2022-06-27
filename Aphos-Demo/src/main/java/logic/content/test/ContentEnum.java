package logic.content.test;

import main.system.auxiliary.StringMaster;

import java.util.Map;

public interface ContentEnum {

     Map<String, Object> getValues();
     default String getName(){
         return StringMaster.format(toString());
     }


}
