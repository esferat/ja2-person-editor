package ja2.person.editor.wildfire.gui.panel;

import java.awt.*;

/**
 * A panel with specified insets on all four sides.
 */
public class InsetPanel extends Panel {
    private final Insets _insets;

    public InsetPanel(Insets insets) {
        super();
        this._insets = insets;
        //setLayout(new BorderLayout());
    }

    public InsetPanel(int top, int left, int bottom, int right) {
        super();
        this._insets = new Insets(top, left, bottom, right);
        //setLayout(new BorderLayout());
    }

    public Insets getInsets() {
        return this._insets;
    }
}
