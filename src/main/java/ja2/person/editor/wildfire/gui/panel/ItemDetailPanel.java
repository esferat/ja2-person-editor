package ja2.person.editor.wildfire.gui.panel;

import ja2.person.editor.wildfire.core.structures.item.ItemExemplar;
import ja2.person.editor.wildfire.gui.view.NumberView;
import ja2.person.editor.wildfire.data.DataChangeEvent;
import ja2.person.editor.wildfire.data.DataChangeListener;
import ja2.person.editor.wildfire.data.DataChangeMixin;
import ja2.person.editor.wildfire.core.structures.item.Item;
import ja2.person.editor.wildfire.gui.view.ChoiceView;
import ja2.person.editor.wildfire.gui.view.FieldView;

import java.awt.*;
import java.util.Vector;
import java.util.Enumeration;

public class ItemDetailPanel extends InsetPanel implements DataChangeListener {
    // Gui Elements
    private final Frame parent;
    private final GridBagLayout layout = new GridBagLayout();
    private final GridBagConstraints constraint = new GridBagConstraints();
    // Data views
    private final Vector<FieldView> views = new Vector<>();
    private final Vector<Component> ammoComponents = new Vector<>();
    private final Vector<Component> attachmentComponents = new Vector<>();
    private final Vector<Component> moneyComponents = new Vector<>();
    private final ChoiceView idView;
    private final NumberView quantityView;
    private final Label itemPctLabel;
    private final NumberView[] itemPctViews = new NumberView[6];
    private final Label ammoIdLabel;
    private final ChoiceView ammoIdView;
    private final Label ammoQuantityLabel;
    private final NumberView ammoQuantityView;
    private final Label ammoVarietyLabel;
    private final NumberView ammoVarietyView;
    private final Label ammoPctLabel;
    private final NumberView ammoPctView;
    private final ChoiceView[] attachmentIdViews = new ChoiceView[4];
    private final NumberView[] attachmentPctViews = new NumberView[4];
    private final NumberView weightView;
    private final Label moneyValueLabel;
    private final NumberView moneyValueView;
    // Data Change event processing
    private final DataChangeMixin mixin = new DataChangeMixin();
    // State machine
    private boolean modified = false;
    // Data sources
    private Item item;

    // Instance methods
    public ItemDetailPanel(Frame parent) {
        super(new Insets(10, 10, 10, 10));
        this.parent = parent;
        this.setLayout(this.layout);

        // Constraints
        this.constraint.anchor = GridBagConstraints.SOUTHWEST;
        this.constraint.fill = GridBagConstraints.NONE;
        this.constraint.gridx = 0;
        this.constraint.gridheight = 1;
        this.constraint.gridwidth = 1;
        this.constraint.insets = new Insets(0, 0, 0, 0);

        // Field views
        this.newRow();
        this.addText(1, "Item");
        this.idView = addItemView(6, "Item ID", ItemExemplar.nameList);

        this.newRow();
        this.addText(1, "Number of Items");
        this.quantityView = addByteView(1, "Quantity");

        this.newRow();
        this.itemPctLabel = this.addText(1, "Item %");
        for (int idx = 0; idx < 6; ++idx) {
            this.itemPctViews[idx] = addByteView(1, "Item " + (idx + 1) + " %");
        }

        this.newRow();
        this.ammoIdLabel = this.addText(1, "Ammo");
        this.ammoComponents.addElement(this.ammoIdLabel);
        this.ammoIdView = addItemView(6, "Ammo ID", ItemExemplar.ammoNameList);
        this.ammoComponents.addElement(this.ammoIdView);

        this.newRow();
        this.ammoQuantityLabel = this.addText(1, "Number of Rounds");
        this.ammoComponents.addElement(this.ammoQuantityLabel);
        this.ammoQuantityView = addByteView(1, "Ammo Quantity");
        this.ammoComponents.addElement(this.ammoQuantityView);

        this.newRow();
        this.ammoVarietyLabel = this.addText(1, "Ammo Variety");
        this.ammoComponents.addElement(this.ammoVarietyLabel);
        this.ammoVarietyView = addByteView(1, "Ammo Variety");
        this.ammoComponents.addElement(this.ammoVarietyView);

        this.newRow();
        this.ammoPctLabel = this.addText(1, "Ammo %");
        this.ammoComponents.addElement(this.ammoPctLabel);
        this.ammoPctView = addByteView(1, "Ammo %");
        this.ammoComponents.addElement(this.ammoPctView);

        for (int idx = 0; idx < 4; ++idx) {
            String str = "Attachment " + (idx + 1);

            this.newRow();
            Label label = this.addText(1, str);
            this.attachmentComponents.addElement(label);

            ChoiceView view = addItemView(6, str + " ID", ItemExemplar.attachmentNameList);
            this.attachmentIdViews[idx] = view;
            this.attachmentComponents.addElement(view);

            NumberView pctView = this.addByteView(1, str + " %");
            this.attachmentPctViews[idx] = pctView;
            this.attachmentComponents.addElement(pctView);

            Label pctLabel = this.addText(1, "%");
            this.attachmentComponents.addElement(pctLabel);
        }

        this.newRow();
        this.addText(1, "Weight (x 0.1 kg)");
        this.weightView = this.addByteView(1, "Weight");

        this.newRow();
        this.moneyValueLabel = this.addText(1, "Money Value");
        this.moneyComponents.addElement(this.moneyValueLabel);
        this.moneyValueView = this.addShortView(2, "Money Value");
        this.moneyComponents.addElement(this.moneyValueView);

        //addByteStat("Unknown 1", "Unknown 1");
        //addByteStat("Unknown 4", "Unknown 4");

        // Alignment hack
        // Throw in extra label to make it look right
        this.newRow();
        this.constraint.fill = GridBagConstraints.BOTH;
        this.constraint.weightx = 1;
        this.constraint.weighty = 1;
        this.addComponent(GridBagConstraints.REMAINDER, new Label());
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

    private NumberView addNumberView(int colwidth, String statField, int charWidth) {
        NumberView view = new NumberView(statField, charWidth);
        this.constraint.fill = GridBagConstraints.NONE;
        this.addComponent(colwidth, view);
        this.views.addElement(view);
        view.addDataChangeListener(this);
        return view;
    }

    private Label addText(int colwidth, String text) {
        return this.addText(colwidth, text, Label.LEFT);
    }

    private Label addText(int colwidth, String text, int align) {
        // Create new label
        Label label = new Label(text, align);
        this.constraint.fill = GridBagConstraints.HORIZONTAL;
        this.addComponent(colwidth, label);
        return label;
    }

    public NumberView addByteView(int colwidth, String statField) {
        return this.addNumberView(1, statField, 3);
    }

    public NumberView addShortView(int colwidth, String statField) {
        return this.addNumberView(1, statField, 3);
    }

    public ChoiceView addItemView(int colwidth, String statField, Vector<String> statChoices) {
        // Create new view
        ChoiceView view = new ChoiceView(statField, statChoices);
        this.constraint.fill = GridBagConstraints.HORIZONTAL;
        this.addComponent(colwidth, view);
        this.views.addElement(view);
        view.addDataChangeListener(this);
        return view;
    }

    public void setItem(Item item) {
        // Update fields
        this.item = item;
//  	if( this.item != null ) {
//  	    for( int foo = 0; foo < this.item.data.length; ++foo ) {
//  		System.err.println("" + foo + ":\t" + this.item.data[foo]);
//  	    }
//  	}

        // Update views from data sources
        for (Enumeration<FieldView> e = this.views.elements(); e.hasMoreElements(); ) {
            FieldView view = e.nextElement();
            view.setStruct(item);
            view.refresh();
        }

        // Decide which views to show
        this.setEnabledAll();

        // Actor now clean
        this.modified = false;
    }

    public void setEnabled(Vector<Component> views, boolean enabled) {
        for (Enumeration<Component> e = views.elements(); e.hasMoreElements(); ) {
            Component view = e.nextElement();
            view.setEnabled(enabled);
        }
    }

    public void setEnabledAll() {
        // Noop if not showing any item
        if (this.item == null) {
            return;
        }

        // Decide which fields to show based on item type
        ItemExemplar exemplar = this.item.getExemplar();
        switch (exemplar.category) {
            case ItemExemplar.WEAPON_CATEGORY:
                this.setEnabled(this.ammoComponents, true);
                this.setEnabled(this.attachmentComponents, true);
                this.setEnabled(this.moneyComponents, false);
                break;

            case ItemExemplar.BODY_ARMOR_CATEGORY:
                this.setEnabled(this.ammoComponents, false);
                this.setEnabled(this.attachmentComponents, true);
                this.setEnabled(this.moneyComponents, false);
                break;

            case ItemExemplar.MONEY_CATEGORY:
                this.setEnabled(this.ammoComponents, false);
                this.setEnabled(this.attachmentComponents, false);
                this.setEnabled(this.moneyComponents, true);
                break;

            default:
                this.setEnabled(this.ammoComponents, false);
                this.setEnabled(this.attachmentComponents, false);
                this.setEnabled(this.moneyComponents, false);
                break;
        }

        // Show the appropriate number of %'s
        int quantity = this.item.getInt("Quantity");
        for (int idx = 0; idx < 6; ++idx) {
            this.itemPctViews[idx].setEnabled((idx < quantity));
        }
        this.itemPctLabel.setEnabled(quantity != 0);

        // Set label appropriately
        if (exemplar.category == ItemExemplar.AMMO_CATEGORY) {
            this.itemPctLabel.setText("Number of Rounds");
        } else {
            this.itemPctLabel.setText("Item %");
        }
    }

    public void dataChanged(DataChangeEvent event) {
        // Extract the control was changed
        FieldView view = (FieldView) event.getSource();
        String oldValue = event.getOldValue();
        String newValue = event.getNewValue();

        //System.err.println("doValueChanged(" + view + ",'" + oldValue + "','" + newValue + "')");

        // Do nothing if no item
        if (this.item == null) {
            return;
        }

        // Do control-specific stuff
        if (view == this.idView) {
            //	    System.err.println(" idview");

            // Make the input more palatable
            if ((oldValue == null) || (oldValue.isEmpty())) {
                oldValue = "None";
            }
            if ((newValue == null) || (newValue.isEmpty())) {
                newValue = "None";
            }

            // If switching from none to something, change % to 100 and count to 1
            if (oldValue.equals("None") && (!newValue.equals("None"))) {
                this.quantityView.setText("1");
                this.itemPctViews[0].setEnabled(true);
                this.itemPctViews[0].setText("100");
            }
            // If switching from something to none, change % to 0 and count to 0
            else if (newValue.equals("None")) {
                this.quantityView.setText("0");
                this.itemPctViews[0].setEnabled(true);
                this.itemPctViews[0].setText("0");
            }

            // If switching to weapon, change count to 1
            ItemExemplar exemplar = this.item.getExemplar();
            if (exemplar.category == ItemExemplar.WEAPON_CATEGORY) {
                this.quantityView.setText("1");
            }
            // If switching to new ammo, set variety
            else if (exemplar.category == ItemExemplar.AMMO_CATEGORY) {
                int quantity = this.item.getInt("Quantity");
                for (int idx = 0; idx < quantity; ++idx) {
                    this.itemPctViews[idx].setEnabled(true);
                    this.itemPctViews[idx].setText("" + exemplar.ammoCapacity);
                }
            }

            // Decide which fields to show
            this.setEnabledAll();
        } else if (view == this.ammoIdView) {
            //	    System.err.println(" ammoIdView");

            // Make the input more palatable
            if ((oldValue == null) || (oldValue.isEmpty())) {
                oldValue = "None";
            }
            if ((newValue == null) || (newValue.isEmpty())) {
                newValue = "None";
            }

            // If switching to new ammo, set variety
            if ((!newValue.equals("None")) && (!newValue.equals(oldValue))) {
                int ammoId = this.item.getInt("Ammo ID");
                ItemExemplar exemplar = ItemExemplar.exemplarTable.get(ammoId);
                System.err.println("" + exemplar.ammoVariety);
                this.ammoVarietyView.setText("" + exemplar.ammoVariety);
                this.ammoQuantityView.setText("" + exemplar.ammoCapacity);
                this.ammoPctView.setText("100");
            }
        } else if (view == this.quantityView) {
            // Decide which fields to show
            this.setEnabledAll();
        } else {
            for (int idx = 0; idx < this.attachmentIdViews.length; ++idx) {
                if (view == this.attachmentIdViews[idx]) {
                    // Make the input more palatable
                    if ((oldValue == null) || (oldValue.isEmpty())) {
                        oldValue = "None";
                    }
                    if ((newValue == null) || (newValue.isEmpty())) {
                        newValue = "None";
                    }

                    // If switching attachment from none to something
                    if (oldValue.equals("None") && (!newValue.equals("None"))) {
                        this.attachmentPctViews[idx].setText("100");
                    }

                    break;
                }
            }
        }

        this.modified = true;
        this.fireDataChangeEvent(event);
    }

    public boolean isModified() {
        return this.modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
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

