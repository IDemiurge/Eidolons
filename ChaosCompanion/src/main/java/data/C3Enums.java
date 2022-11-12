package data;

import main.system.auxiliary.StringMaster;
import main.system.sound.AudioEnums;

public class C3Enums {

    public enum QueryMode {
        relax, normal, intense
    }

    public enum TaskMode {
        relax, normal, intense
    }

    public enum C3ItemEvent {
        Started,
    }

    public enum SessionType {
        Preparation(1,3),
        Perseverance(4,8),

        Liberation_Short(2,4),
        Night_Short(2,4),
        Liberation_Long(3,6),
        Night_Long(3,5),

        Freedom(4,8),
        ;

        private int minBreaks;
        private int maxBreaks;

        public int getMaxBreaks() {
            return maxBreaks;
        }

        SessionType(int minBreaks, int maxBreaks) {
            this.minBreaks = minBreaks;
            this.maxBreaks = maxBreaks;
        }

        public int getMinBreaks() {
            return minBreaks;
        }
    }
    public enum Direction implements Category {
        Code, Design, Project, Meta;

        public String[] subs;
        public TaskCategory[] categories;

        public void setCategories(TaskCategory[] subs) {
            this.categories = subs;
        }
        @Override
        public void setSubcategories(String[] subs) {
            this.subs = subs;
        }
    }

    public enum C3Sound {
        ONWARD(AudioEnums.STD_SOUNDS.NEW__ENTER.getPath()),
        BACK_TO_WORK(AudioEnums.STD_SOUNDS.NEW__BATTLE_START.getPath()),
        GET_INTO_IT(AudioEnums.STD_SOUNDS.NEW__BATTLE_START2.getPath()),
        PAUSE(AudioEnums.STD_SOUNDS.NEW__PAUSE.getPath());

        private String path;

        C3Sound(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }
    }

    public enum C3Option {New_Session,
        New_Task, Task_Report,
        EZ_Choice,
        Music_Reset,
        Session(Music_Reset, EZ_Choice), Task(New_Task, Task_Report), Query,
        ;
        public C3Option[] children;

        C3Option(C3Option... children) {
            this.children = children;
        }
    }

    public enum EZ_Option {
        comfy_chair,
        query,
        shift_break
    }

    public enum BreakTip{
        Breath,
        Hydrate,
        Sit_Up,
        Commit(Direction.Code),
        // Try_Silence,
        Do_the_main_thing,
        Stretch,
        Dance,
        Smash_Ya_Eyes,
        Structure(Direction.Design, Direction.Project),
        Reality_check(Direction.Design, Direction.Project),

        ;
        public Direction[] directions;
        BreakTip(Direction... directions) {
            this.directions = directions;
        }

        @Override
        public String toString() {
            return StringMaster.format(super.toString());
        }
    }

    public interface Category {
        void setSubcategories(String[] subs);
    }

    public enum CodeTaskStatus {
        Review, Outline, Implement, Test, Refine, Complete
    }

    public enum ContentTaskStatus {
        Outline, Implement, Test, Refine, Complete
    }

    public enum TaskCategory implements Category {
        Code_New(8),
        Code_Revamp(10),
        Bug_fixing(6),

        Content_Design(9),
        System_Design(8),
        Global_Game_Design(11),

        Lore_Writing(6),
        Game_Content_Writing(7),
        Public_Writing(5),

        Team_management(6);
        public String[] subcategories;
        public int weight;

        TaskCategory(int weight) {
            this.weight = weight;
        }

        @Override
        public void setSubcategories(String[] subs) {
            subcategories = subs;
        }
    }

    public enum QueryCategory implements Category {
        CS(10, false),
        GAMEDEV(8, false),
        PSY(8, false),
        LANGUAGES(7, false),
        SCIENCE(7, false),
        TRIVIA(6, false),
        ART(6, false),

        CS_VIDEOS(6, true),
        GAMEDEV_VIDEOS(5, true),
        LANGUAGES_VIDEOS(5, true),
        SCIENCE_VIDEOS(4, true),
        PSY_VIDEOS(3, true),
        TRIVIA_VIDEOS(2, true),
        ART_VIDEOS(2, true),
        ;
        public String[] subcategories;
        public int weight;
        public boolean youTube;

        QueryCategory(int weight, boolean youTube) {
            this.weight = weight;
            this.youTube = youTube;
        }


        @Override
        public void setSubcategories(String[] subs) {
            subcategories = subs;
        }
    }

}
