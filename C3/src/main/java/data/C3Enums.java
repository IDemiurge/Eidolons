package data;

public class C3Enums {

    public enum QueryMode {
        relax, normal, intense
    }

    public interface Category {
        void setSubcategories(String[] subs);
    }

    public enum TaskStatus {
        Review, Outline, Implement, Test, Refine, Complete
    }

    public enum TaskCategory implements Category {
        CODE,
        DESIGN,
        WRITING,
        MISC,
        ;
        public String[] subcategories;
        public int weight;

        @Override
        public void setSubcategories(String[] subs) {
            subcategories = subs;
        }
    }

    public enum QueryCategory implements Category {
        CS,
        ;
        public String[] subcategories;
        public int weight;
        boolean youTube;

        QueryCategory() {

        }

        @Override
        public void setSubcategories(String[] subs) {
            subcategories = subs;
        }
    }

}
