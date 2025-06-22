package ja2.person.editor.wildfire.data;

import java.util.*;

public interface DataChangeListener extends EventListener {
    void dataChanged(DataChangeEvent event);
}
