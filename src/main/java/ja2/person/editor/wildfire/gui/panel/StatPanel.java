package ja2.person.editor.wildfire.gui.panel;

import ja2.person.editor.wildfire.core.structures.Skill;
import ja2.person.editor.wildfire.core.structures.Structure;
import ja2.person.editor.wildfire.gui.view.TextView;
import ja2.person.editor.wildfire.data.DataChangeEvent;
import ja2.person.editor.wildfire.data.DataChangeListener;
import ja2.person.editor.wildfire.data.DataChangeMixin;
import ja2.person.editor.wildfire.core.structures.actor.Actor;
import ja2.person.editor.wildfire.core.structures.mercenary.Mercenary;
import ja2.person.editor.wildfire.gui.view.ChoiceView;
import ja2.person.editor.wildfire.gui.view.FieldView;
import ja2.person.editor.wildfire.gui.view.NumberView;

import java.awt.*;
import java.util.Vector;
import java.util.Enumeration;

public class StatPanel extends InsetPanel implements DataChangeListener {
    // Gui Elements
    private final Frame parent;
    private final GridBagLayout layout = new GridBagLayout();
    private final GridBagConstraints constraint = new GridBagConstraints();

    // State machine
    private boolean modified = false;

    // Data sources
    private Actor actor;
    private Mercenary merc;

    // Data Views
    private final Vector<FieldView> views = new Vector<>();
    // Data Change event processing
    private final DataChangeMixin mixin = new DataChangeMixin();

    // Instance methods
    public StatPanel(Frame parent) {
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

        // Name and nickname
        this.addStringStat("Name", "Name", 30);
        this.addStringStat("Nickname", "Nickname", 30);

        // Health, current max and inc
        this.newRow();
        this.addText(1, "Health", Label.LEFT);
        this.addNumberView(1, "Health", 3);
        this.addText(1, "/", Label.CENTER);
        this.addNumberView(1, "Max Health", 3);
        this.addText(1, "(+", Label.RIGHT);
        this.addNumberView(1, "Health Inc", 3);
        this.addText(1, ")", Label.LEFT);

        // Ability scores
        this.addByteStat("Energy", "Energy");
        this.addByteStat("Morale", "Morale");
        this.addIncStat("Agility", "Agility", "Agility Inc");
        this.addIncStat("Dexterity", "Dexterity", "Dexterity Inc");
        this.addIncStat("Strength", "Strength", "Strength Inc");
        this.addIncStat("Leadership", "Leadership", "Leadership Inc");
        this.addIncStat("Wisdom", "Wisdom", "Wisdom Inc");
        this.addIncStat("Marksmanship", "Marksmanship", "Marksmanship Inc");
        this.addIncStat("Explosives", "Explosives", "Explosives Inc");
        this.addIncStat("Mechanical", "Mechanical", "Mechanical Inc");
        this.addIncStat("Medical", "Medical", "Medical Inc");
        this.addIncStat("Exp Lvl", "Level", "Level Inc");

        // Skills
        this.addChoiceStat("Skill1", "Skill1", Skill.list);
        this.addChoiceStat("Skill2", "Skill2", Skill.list);

        // Statistics
        this.addShortStat("Kills", "Kills");
        this.addShortStat("Assists", "Assists");
        this.addShortStat("Shots Fired", "Shots Fired");
        this.addShortStat("Shots Hit", "Shots Hit");
        this.addShortStat("Battles", "Battles");
        this.addShortStat("Times Wounded", "Wounds");

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

    private void addTextView(int colwidth, String statField, int charWidth) {
        TextView view = new TextView(statField, charWidth);
        this.addComponent(colwidth, view);
        this.views.addElement(view);
        view.addDataChangeListener(this);
    }

    private void addNumberView(int colwidth, String statField, int charWidth) {
        NumberView view = new NumberView(statField, charWidth);
        this.addComponent(colwidth, view);
        this.views.addElement(view);
        view.addDataChangeListener(this);
    }

    private void addText(int colwidth, String text, int align) {
        // Create new label
        Label label = new Label(text, align);
        this.addComponent(colwidth, label);
    }

    public void addByteStat(String statTitle, String statField) {
        this.newRow();
        this.addText(1, statTitle, Label.LEFT);
        this.addNumberView(1, statField, 3);
    }

    public void addIncStat(String statTitle, String statField, String incStatField) {
        this.newRow();
        this.addText(1, statTitle, Label.LEFT);
        this.addNumberView(1, statField, 3);
        this.addText(1, "(+", Label.RIGHT);
        this.addNumberView(1, incStatField, 3);
        this.addText(1, ")", Label.LEFT);
    }

    public void addShortStat(String statTitle, String statField) {
        this.addByteStat(statTitle, statField);
    }

    public void addStringStat(String statTitle, String statField, int charWidth) {
        this.newRow();
        this.addText(1, statTitle, Label.LEFT);
        this.addNumberView(GridBagConstraints.REMAINDER, statField, charWidth);
    }

    public void addChoiceStat(String statTitle, String statField, Vector<String> statChoices) {
        this.newRow();
        this.addText(1, statTitle, Label.LEFT);
        ChoiceView view = new ChoiceView(statField, statChoices);
        this.addComponent(GridBagConstraints.REMAINDER, view);
        this.views.addElement(view);
        view.addDataChangeListener(this);
    }

    public void setActor(Actor actor, Mercenary merc) {
        // Update fields
        this.actor = actor;
        this.merc = merc;

        Structure struct;
        if (this.merc != null) {
            struct = this.merc;
            struct.chain(this.actor);
        } else {
            struct = this.actor;
        }

        // Update views from data sources
        for (Enumeration<FieldView> e = this.views.elements(); e.hasMoreElements(); ) {
            FieldView view = e.nextElement();
            view.setStruct(struct);
            view.refresh();
        }

        // Actor now clean
        this.modified = false;
    }

    public void dataChanged(DataChangeEvent event) {
        // Do nothing if no item
        if (this.actor == null) {
            return;
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

