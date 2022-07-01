package gdx.dto;

import java.util.List;

public class LaneFieldDto implements DtoManager.Dto {

    String focusAreaImage;
    List<LaneDto> lanes;

    public LaneFieldDto(String focusAreaImage, List<LaneDto> lanes) {
        this.focusAreaImage = focusAreaImage;
        this.lanes = lanes;
    }

    public String getFocusAreaImage() {
        return focusAreaImage;
    }

    public List<LaneDto> getLanes() {
        return lanes;
    }
}
