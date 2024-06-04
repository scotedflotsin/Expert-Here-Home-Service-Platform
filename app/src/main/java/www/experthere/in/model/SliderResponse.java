package www.experthere.in.model;

import java.util.List;

public class SliderResponse {

    boolean success;
    String message;

    List<SliderData> image_slider_data;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<SliderData> getImage_slider_data() {
        return image_slider_data;
    }
}
