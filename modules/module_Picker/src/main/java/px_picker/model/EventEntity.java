package px_picker.model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import px_core.model.MediaEntity;

public class EventEntity implements Serializable {
    public int what;
    public int position;
    public List<MediaEntity> mediaEntities = new ArrayList<>();

    public EventEntity() {
        super();
    }

    public EventEntity(int what) {
        super();
        this.what = what;
    }

    public EventEntity(int what, List<MediaEntity> mediaEntities) {
        super();
        this.what = what;
        this.mediaEntities = mediaEntities;
    }

    public EventEntity(int what, int position) {
        super();
        this.what = what;
        this.position = position;
    }

    public EventEntity(int what, List<MediaEntity> mediaEntities, int position) {
        super();
        this.what = what;
        this.position = position;
        this.mediaEntities = mediaEntities;
    }
}
