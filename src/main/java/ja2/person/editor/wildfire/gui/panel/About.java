package ja2.person.editor.wildfire.gui.panel;

import java.awt.*;
import java.awt.event.*;

public class About extends Dialog {
    public About(Frame parent) {
        super(parent, "About", true);

        // Create body panel
        Panel body = new Panel();
        body.setLayout(new BorderLayout());
        this.add(body);

        // Add icon

        // Text Panel
        Panel textPanel = new InsetPanel(5, 5, 5, 5);
        textPanel.setLayout(new GridLayout(4, 1));
        textPanel.setBackground(SystemColor.text);

        Label line1 = new Label("A Jagged Alliance 2 Save Game Editor", Label.CENTER);
        textPanel.add(line1);
        Label line2 = new Label("Version 0.0.1", Label.CENTER);
        textPanel.add(line2);

        // Button bar
        Panel buttonPanel = new InsetPanel(0, 5, 0, 5);
        buttonPanel.setLayout(new FlowLayout());
        Button okButton = new Button("OK");
        okButton.addActionListener(e -> dispose());
        buttonPanel.add(okButton);

        // Add elements to dialog
        body.add(buttonPanel, "South");
        body.add(textPanel, "Center");

        // Handle window events
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        // Instantiate peers for the size calculation below
        this.addNotify();
        this.pack();

        // Put this dialog in the center of the parent frame
        if (parent != null) {
            Point parentLoc = parent.getLocation();
            Dimension parentSize = parent.getSize();
            Dimension size = this.getSize();
            Point loc = new Point(
                    parentLoc.x + (parentSize.width - size.width) / 2,
                    parentLoc.y + (parentSize.height - size.height) / 2
            );
            this.setLocation(loc);
        }
    }
}
