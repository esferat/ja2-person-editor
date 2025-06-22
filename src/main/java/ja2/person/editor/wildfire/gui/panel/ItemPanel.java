package ja2.person.editor.wildfire.gui.panel;

import ja2.person.editor.wildfire.gui.view.ItemView;
import ja2.person.editor.wildfire.core.structures.mercenary.Mercenary;
import ja2.person.editor.wildfire.data.DataChangeEvent;
import ja2.person.editor.wildfire.data.DataChangeListener;
import ja2.person.editor.wildfire.data.DataChangeMixin;
import ja2.person.editor.wildfire.core.structures.actor.Actor;
import ja2.person.editor.wildfire.core.structures.item.Item;
import ja2.person.editor.wildfire.gui.view.FieldView;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

public class ItemPanel extends InsetPanel implements DataChangeListener {
    // Gui Elements
    private final Frame parent;
    private final GridBagLayout layout = new GridBagLayout();
    private final GridBagConstraints constraint = new GridBagConstraints();
    private final ItemDetailPanel itemDetailPanel;

    // State machine
    private boolean modified = false;

    // Data sources
    private Actor actor;
    private Mercenary merc;

    // Data Views
    private final Vector<Component> views = new Vector<>();
    private ItemView currentView;
    // Data Change event processing
    private final DataChangeMixin mixin = new DataChangeMixin();

    // Instance methods
    public ItemPanel(Frame parent) {
        super(new Insets(10, 10, 10, 10));
        this.parent = parent;
        this.setLayout(this.layout);

        // Constraints
        this.constraint.anchor = GridBagConstraints.SOUTHWEST;
        this.constraint.fill = GridBagConstraints.BOTH;
        this.constraint.gridx = 0;
        this.constraint.gridheight = 1;
        this.constraint.gridwidth = 1;
        this.constraint.weightx = 1;
        this.constraint.insets = new Insets(0, 0, 0, 0);

        // Add buttons for item slots
        this.newRow();
        this.addItem(Mercenary.HEADGEAR_1_INDEX);
        this.addSpace();
        this.addItem(Mercenary.HELMET_INDEX);

        this.newRow();
        this.addItem(Mercenary.HEADGEAR_2_INDEX);
        this.addSpace();
        this.addItem(Mercenary.BODY_ARMOR_INDEX);

        this.newRow();
        this.addItem(Mercenary.RIGHT_HAND_INDEX);
        this.addSpace();
        this.addItem(Mercenary.LEG_ARMOR_INDEX);

        this.newRow();
        this.addItem(Mercenary.LEFT_HAND_INDEX);

        this.newRow();
        this.addItem(Mercenary.BACKPACK_1_1_INDEX);
        this.addItem(Mercenary.BACKPACK_1_2_INDEX);
        this.addItem(Mercenary.BACKPACK_1_3_INDEX);

        this.newRow();
        this.addItem(Mercenary.BACKPACK_2_1_INDEX);
        this.addItem(Mercenary.BACKPACK_2_2_INDEX);
        this.addItem(Mercenary.BACKPACK_2_3_INDEX);

        this.newRow();
        this.addItem(Mercenary.BACKPACK_3_1_INDEX);
        this.addItem(Mercenary.BACKPACK_3_2_INDEX);
        this.addItem(Mercenary.BACKPACK_3_3_INDEX);

        this.newRow();
        this.addItem(Mercenary.BACKPACK_4_1_INDEX);
        this.addItem(Mercenary.BACKPACK_4_2_INDEX);
        this.addItem(Mercenary.BACKPACK_4_3_INDEX);

        // Create item detail panel
        this.newRow();
        this.itemDetailPanel = new ItemDetailPanel(this.parent);
        this.constraint.gridwidth = 3;
        this.constraint.weightx = 1;
        this.constraint.weighty = 1;
        this.add(this.itemDetailPanel, this.constraint);
        this.itemDetailPanel.addDataChangeListener(this);
    }

    private void newRow() {
        // Start a new row
        this.constraint.gridy++;
        this.constraint.gridx = 0;
    }

    private void addComponent(int colwidth, Component component) {
        // Add component to panel
        this.constraint.gridwidth = colwidth;
        this.add(component, this.constraint);
        this.constraint.gridx += colwidth;
    }

    private void addSpace() {
        this.constraint.gridx++;
    }

    private void addItem(final int index) {
        // Create new button
        final ItemView view = new ItemView(index);
        this.addComponent(1, view);
        this.views.addElement(view);
        view.addActionListener(e -> doItemSelected(view));
    }

    public void setActor(Actor actor, Mercenary merc) {
        // Set fields
        this.actor = actor;
        this.merc = merc;

        // Update views from data sources
        for (int idx = 0; idx < this.views.size(); ++idx) {
            ItemView view = (ItemView) this.views.elementAt(idx);
            view.setActor(merc);
        }

        // Actor now clean
        this.modified = false;

        // Set detail fields
        this.doItemSelected((ItemView) this.views.elementAt(0));
    }

    public boolean isModified() {
        return this.itemDetailPanel.isModified();
    }

    public void setModified(boolean modified) {
        this.itemDetailPanel.setModified(modified);
        this.modified = modified;
    }

    public void doItemSelected(ItemView view) {
        // Get item index
        int index = view.getIndex();
        this.currentView = view;

        // Get new item
        Item item = null;
        if (merc != null) {
            item = this.merc.items[index];
        }

        // Change to new item
        this.itemDetailPanel.setItem(item);
    }

    public void dataChanged(DataChangeEvent event) {
        // Update views from data sources
        this.currentView.refresh();

        this.modified = true;
        this.fireDataChangeEvent(event);
    }

    public void addDataChangeListener(DataChangeListener l) {
        this.mixin.addDataChangeListener(l);
    }

    public void removeDataChangeListener(DataChangeListener l) {
        this.mixin.removeDataChangeListener(l);
    }

    public void fireDataChangeEvent(DataChangeEvent e) {
        this.mixin.fireDataChangeEvent(e);
    }
}
