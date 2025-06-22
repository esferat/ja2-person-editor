package ja2.person.editor.wildfire.gui.view;

import ja2.person.editor.wildfire.core.structures.mercenary.Mercenary;
import ja2.person.editor.wildfire.core.structures.item.Item;
import ja2.person.editor.wildfire.core.structures.item.ItemExemplar;

import java.awt.*;

public class ItemView extends Button {
    // Static data
    public static final int MAX_LENGTH = 16;
    public static final String none = "           None             ";
    public static final String unknown = "          Unknown           ";

    // Instance data
    public int index;
    public Item item;

    // Instance methods
    public ItemView(int index) {
        super(none);
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }

    public void setActor(Mercenary merc) {
        // Get the item from the merc
        if (merc != null) {
            this.item = merc.items[this.index];
        } else {
            this.item = null;
        }

        // Redisplay button
        this.refresh();
    }

    public void refresh() {
        // Get name of item exemplar
        String name;
        if (this.item != null) {
            ItemExemplar exemplar = item.getExemplar();
            if (exemplar != null) {
                name = item.getExemplar().name;
                if (name.length() > MAX_LENGTH) {
                    name = name.substring(0, MAX_LENGTH);
                }
            } else {
                name = unknown;
            }
        } else {
            name = none;
        }

        // Set button label
        this.setLabel(name);
    }
}
