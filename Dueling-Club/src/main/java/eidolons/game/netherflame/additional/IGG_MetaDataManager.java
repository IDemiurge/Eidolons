package eidolons.game.netherflame.additional;

public class IGG_MetaDataManager  {
//     private String scenarioName;
//
//     public IGG_MetaDataManager(MetaGameMaster master) {
//         super(master);
//     }
//
//     @Override
//     public String getData() {
//         return scenarioName;
//     }
//
//     public String nextMission() {
//         IGG_Demo.IGG_MISSION next = getMetaGame().getMission().getNext();
//         if (next==null) {
//             return null;
//         }
//         getPartyManager().getParty().setProperty(PROPS.PARTY_MISSION, next.getMissionName(), true);
// //        next.missionIndex;
// //        getMaster().getSaveMaster().autoSave();
//         scenarioName=  next.getScenarioName();
//         return next.getMissionName();
//     }
//
//
//     public void initData() {
//         //path?
//         String missionName = getPartyManager().getParty().getProperty(PROPS.PARTY_MISSION);
//         IGG_Demo.IGG_MISSION mission;
//         if (missionName.isEmpty())
//             missionName = getMaster().getData(); //starting mission
//         mission = IGG_Demo.getMissionByName(missionName);
//         getGame().setBossFight(mission.isBossFight());
// //        EidolonsGame.BOSS_FIGHT = (mission.isBossFight());
//
//         if (EidolonsGame.BRIDGE) {
//             //TODO
//             EidolonsGame.TUTORIAL_MISSION = (mission.isTutorial());
//         }
//         getMetaGame().setMission(mission);
//
//         IGG_Demo.MISSION = mission;
//     }
//
//     @Override
//     public String getMissionName() {
//         return getMetaGame().getMissionType().getName();
//     }
//
//     @Override
//     public String getSoloDungeonPath() {
//         if (MainLauncher.getCustomLaunch()!=null ){
//             main.system.auxiliary.log.LogMaster.important("*******Custom Launch xml path: " +
//                     MainLauncher.getCustomLaunch().getValue(CustomLaunch.CustomLaunchValue.xml_path));
//             return MainLauncher.getCustomLaunch().getValue(CustomLaunch.CustomLaunchValue.xml_path);
//         }
//         return getMetaGame().getMission().getXmlLevelName();
// //                getMetaGame().getMissionType().getProperty(PROPS.MISSION_FILE_PATH);
//     }
}
