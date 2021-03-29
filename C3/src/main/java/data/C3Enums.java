package data;

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
        New_Code(8),
        Code_Revamp(10),
        Bug_fixing(6),

        Content_Design(9),
        System_Design(8),
        Global_Game_Design(11),

        Lore_Writing(6),
        Game_Content_Writing(7),
        Public_Writing(5),

        Team_management(6)
        ;
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
