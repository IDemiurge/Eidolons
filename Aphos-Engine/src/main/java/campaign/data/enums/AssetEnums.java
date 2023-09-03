package campaign.data.enums;

import apps.prompt.token.Weighted;

/**
 * Created by Alexander on 9/2/2023
 */
public class AssetEnums {
    public enum AphosEventType implements Weighted {
            intro("NF(10),Anphis(5),Eidolons(4)"),
            fascination("NF(10),Anphis(8),Omen(6),Wyrm(4),Eidolons(4)"),
            ;
            String weights;

        AphosEventType(String weights) {
            this.weights = weights;
        }

        @Override
        public String getWeights() {
            return weights;
        }
    }
}
