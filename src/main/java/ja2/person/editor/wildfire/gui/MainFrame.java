package ja2.person.editor.wildfire.gui;

import ja2.person.editor.wildfire.core.structures.actor.Actor;
import ja2.person.editor.wildfire.data.DataChangeEvent;
import ja2.person.editor.wildfire.data.DataChangeListener;
import ja2.person.editor.wildfire.gui.panel.ItemPanel;
import ja2.person.editor.wildfire.gui.panel.About;
import ja2.person.editor.wildfire.core.structures.mercenary.Mercenary;
import ja2.person.editor.wildfire.gui.view.OptionDialog;
import ja2.person.editor.wildfire.data.SaveGame;
import ja2.person.editor.wildfire.gui.panel.StatPanel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.TreeSet;


public class MainFrame extends Frame implements DataChangeListener {
    // Major GUI elements
    private final GridBagLayout layout = new GridBagLayout();
    private final List actorList;
    private final StatPanel statPanel;
    private final ItemPanel itemPanel;

    // Menu
    private MenuBar menuBar;
    private Menu fileMenu;
    private MenuItem openItem;
    private MenuItem saveItem;
    private MenuItem closeItem;
    private MenuItem quitItem;
    private Menu langMenu;
    private Menu helpMenu;
    private MenuItem aboutItem;

    // The save game engine
    private SaveGame saveGame;
    private boolean saveGameModified;
    private Actor currentActor;
    private Mercenary currentMerc;
    private String currentDir = System.getProperty("user.dir");

    public MainFrame() {
        super("JA2: person editor");

        // Menu
        this.createMenuBar();

        // Create body panel
        Panel body = new Panel();
        body.setLayout(this.layout);
        this.add("Center", body);

        // Create actor list
        this.actorList = new List();
        this.actorList.addItemListener(e -> doSelectActor());
        GridBagConstraints c1 = new GridBagConstraints();
        c1.insets = new Insets(10,10,10,0);
        c1.fill = GridBagConstraints.BOTH;
        c1.gridx = 0;
        c1.gridy = 0;
        c1.weighty = 1;
        c1.weightx = 0;
        body.add(this.actorList, c1);

        // Create stat panel
        this.statPanel = new StatPanel(this);
        this.statPanel.addDataChangeListener(this);
        GridBagConstraints c2 = new GridBagConstraints();
        c2.fill = GridBagConstraints.BOTH;
        c2.anchor = GridBagConstraints.NORTHWEST;
        c2.gridx = 1;
        c2.gridy = 0;
        c2.weighty = 1;
        c2.weightx = 1;
        body.add(this.statPanel, c2);

        // Create item panel
        this.itemPanel = new ItemPanel(this);
        this.itemPanel.addDataChangeListener(this);
        GridBagConstraints c3 = new GridBagConstraints();
        c3.fill = GridBagConstraints.BOTH;
        c3.anchor = GridBagConstraints.NORTHWEST;
        c3.gridx = 2;
        c3.gridy = 0;
        c3.weighty = 1;
        c3.weightx = 1;
        body.add(this.itemPanel, c3);

        // Handle window events
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                doQuit();
            }
        });

        // Size the frame
        this.addNotify();
        //	Insets frameInsets = this.getInsets();
        //	int width = frameInsets.left + frameInsets.right + DEFAULT_WIDTH;
        //	int height = frameInsets.top + frameInsets.bottom + DEFAULT_HEIGHT;
        //	this.setSize(width, height);
        //	this.setLocation(50,50);

        // Init the panels
        this.populateActorList();

        // Show
        this.pack();
        this.repaint();
    }

    // Class methods
    public static void main(String[] args) {
        // Create frame
        MainFrame mainFrame = new MainFrame();

        // Make frame visible
        mainFrame.setVisible(true);
    }

    private void createMenuBar() {
        // Create menu bar
        this.menuBar = new MenuBar();

        // File menu
        this.fileMenu = new Menu("Файл");
        this.menuBar.add(this.fileMenu);

        this.openItem = new MenuItem("Открыть...");
        this.openItem.addActionListener(e -> doOpen());
        this.fileMenu.add(this.openItem);

        this.saveItem = new MenuItem("Сохранить");
        this.saveItem.addActionListener(e -> doSave());
        this.fileMenu.add(this.saveItem);
        this.saveItem.setEnabled(false);

        this.closeItem = new MenuItem("Закрыть");
        this.closeItem.addActionListener(e -> doClose());
        this.fileMenu.add(this.closeItem);
        this.closeItem.setEnabled(false);

        this.fileMenu.addSeparator();

        this.quitItem = new MenuItem("Выход");
        this.quitItem.addActionListener(e -> doQuit());
        this.fileMenu.add(this.quitItem);

        //Lang menu
        this.helpMenu = new Menu("Язык");
        this.menuBar.add(this.langMenu);

        // About menu
        this.helpMenu = new Menu("Помощь");
        this.menuBar.add(this.helpMenu);
        this.menuBar.setHelpMenu(this.helpMenu);

        this.aboutItem = new MenuItem("О приложении...");
        this.aboutItem.addActionListener(e -> doAbout());
        this.helpMenu.add(this.aboutItem);

        // Attach menubar to frame
        this.setMenuBar(this.menuBar);
    }

    public boolean doAbout() {
        new About(this).setVisible(true);
        return true;
    }

    public boolean doClose() {
        // Do we currently have a save game open?
        if ((this.saveGame != null) && (this.saveGameModified)) {
            // Prompt user for save
            String shortName = new File(this.saveGame.filename).getName();
            int option = OptionDialog.showConfirmDialog(
                    this,
                    "The file " + shortName + " has been modifed.\n" +
                            "Do you want to save the changes?",
                    this.getTitle(),
                    OptionDialog.YES_NO_CANCEL_OPTION,
                    OptionDialog.WARNING_MESSAGE);
            if (option == OptionDialog.CANCEL_OPTION ||
                    option == OptionDialog.CLOSED_OPTION) {
                return false;
            } else if (option == OptionDialog.YES_OPTION) {
                boolean success = doSave();
                if (!success) {
                    return false;
                }
            }
        }

        // Disable relevant menu items
        this.saveItem.setEnabled(false);
        this.closeItem.setEnabled(false);

        // Close current save
        this.saveGame = null;
        this.saveGameModified = false;
        this.populateActorList();
        return true;
    }

    public boolean doOpen() {
        // Close any existing save
        boolean success = this.doClose();
        if (!success) {
            return false;
        }

        // Create open file dialog
        FileDialog fileDialog = new FileDialog(this, "Open", FileDialog.LOAD);
        if (this.currentDir != null) {
            fileDialog.setDirectory(this.currentDir);
        }

        // Show dialog
        fileDialog.setFile("*.sav");
        fileDialog.setVisible(true);

        // What did the user choose?
        String directory = fileDialog.getDirectory();
        String name = fileDialog.getFile();
        if (name == null) {
            return false;
        }
        this.currentDir = directory;
        String filename = new File(directory, name).toString();

        // Open the selected file
        SaveGame saveGame = new SaveGame();
        try {
            saveGame.load(filename);
        } catch (EOFException e) {
            // Display error message
            OptionDialog.showMessageDialog(
                    this,
                    "Unable to load file.\n" +
                            "Unexpected end of file reached.",
                    this.getTitle(),
                    OptionDialog.ERROR_MESSAGE);
            return false;
        } catch (FileNotFoundException e) {
            // Display error message
            OptionDialog.showMessageDialog(
                    this,
                    "Unable to load file.\n" +
                            "The system cannot find the file specified.",
                    this.getTitle(),
                    OptionDialog.ERROR_MESSAGE);
            return false;
        } catch (IOException e) {
            // Display error message
            String errorMessage = e.getMessage();
            if (e.getMessage() == null) {
                errorMessage = "An unknown error occurred.";
            }
            OptionDialog.showMessageDialog(
                    this,
                    "Unable to load file.\n" +
                            errorMessage,
                    this.getTitle(),
                    OptionDialog.ERROR_MESSAGE);
            return false;
        } catch (SaveGame.FormatException e) {
            // Display error message
            //e.printStackTrace(System.err);
            OptionDialog.showMessageDialog(
                    this,
                    "Unable to load file.\n" +
                            "The save game format was not recognized.\n" +
                            e.getMessage(),
                    this.getTitle(),
                    OptionDialog.ERROR_MESSAGE);
            return false;
        }

        // Enable relevant menu items
        this.saveItem.setEnabled(true);
        this.closeItem.setEnabled(true);

        // Display the file
        this.saveGame = saveGame;

        // Select the first actor
        this.populateActorList();
        this.actorList.select(0);
        this.doSelectActor();
        this.saveGameModified = false;

        return true;
    }

    public boolean doSave() {
        // Do we have a save game open
        if (this.saveGame == null) {
            return true;
        }

        // Save the game
        try {
            this.saveGame.save();
        } catch (IOException e) {
            // Display error message
            String errorMessage = e.getMessage();
            if (e.getMessage() == null) {
                errorMessage = "An unknown error occurred.";
            }
            OptionDialog.showMessageDialog(
                    this,
                    "Unable to save file.\n" +
                            errorMessage,
                    this.getTitle(),
                    OptionDialog.ERROR_MESSAGE);
            return false;
        }

        // Reset modified flag
        this.saveGameModified = false;
        this.statPanel.setModified(false);
        this.itemPanel.setModified(false);

        return true;
    }

    public boolean doQuit() {
        // Close any existing save
        boolean success = this.doClose();
        if (!success) {
            return false;
        }

        // Close window
        this.setVisible(false);
        this.dispose();
        System.exit(0);
        return true;
    }

    private boolean doSelectActor() {
        // Which name did the user select
        String nickname = this.actorList.getSelectedItem();

        // Lookup that actor in the save
        Actor actor = null;
        Mercenary merc = null;
        if (!nickname.isEmpty()) {
            // Get actor and/or merc
            String cleanNickname = nickname.replace("*", "");
            actor = this.saveGame.getActor(cleanNickname);
            merc = this.saveGame.getMercByNick(cleanNickname);
        }

        // Do it
        this.doSetActor(actor, merc);
        return true;
    }

    private void doSetActor(Actor actor, Mercenary merc) {
        this.currentActor = actor;
        this.currentMerc = merc;

        // Redisplay the gui
        this.statPanel.setActor(this.currentActor, this.currentMerc);
        this.itemPanel.setActor(this.currentActor, this.currentMerc);
    }

    private void populateActorList() {
        // Clear all entries
        this.actorList.removeAll();
        this.doSetActor(null, null);

        // Game loaded?
        if (this.saveGame == null) {
            return;
        }
        TreeSet<String> treeSet = new TreeSet<>();

        // Iterator over actors in the save game
        for (int idx = 0; idx < this.saveGame.actorCount; ++idx) {
            // Get actor
            Actor actor = this.saveGame.getActor(idx);
            String nickname = actor.get("Nickname");

            // Is there an associated merc record?
            Mercenary merc = this.saveGame.getMercByNick(nickname);
            if (merc != null) {
                nickname = "*" + nickname;
            }
            treeSet.add(nickname);
        }

        // Add actor nickname to list
        treeSet.forEach(this.actorList::add);
    }

    public void dataChanged(DataChangeEvent event) {
        this.saveGameModified = true;
    }
}
