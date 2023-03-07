import Exceptions.EmptyDataException;
import Exceptions.MaxPriceException;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class AppGui extends JFrame implements ActionListener {

    CardLayout mainLayout = new CardLayout(); // Layout aplikacji
    CardLayout listLayout = new CardLayout(); // Layout list samochodów
    JPanel firstPage, secondPage, thirdPage;
    // Połączenie
    private static boolean isConnected;
    private JDialog connectionDialog;
    private JTextPane connectText;
    // Pierwsza strona
    private JPanel dataPanel, buttonPanel1, practicalityPanel, fuelPanel, errorPanel, gearBoxPanel;
    private JTextField minPrice, maxPrice;
    private JButton forwardButton;
    private JLabel price, practicalityLabel, fuelLabel, errorlabel, gearBoxLabel;
    private JComboBox<String> practicalityCombo;
    private JCheckBox petrolCheck, hybridCheck, electricCheck, dieselCheck, cvtCheck, automaticCheck, manualCheck;
    // Druga strona
    private JPanel buttonPanel2, dragPanel, loadingPanel, infoPanel;
    private JButton backButton, searchButton;
    private JLayeredPane layeredPane;
    private JLabel loadingGif, listInfo;
    // Menu
    private JMenuBar menuBar;
    private JMenu aboutMenu, helpMenu;
    private JMenuItem aboutApp, aboutProject, filterHelp, traitsHelp;

    public AppGui() {

        // Ustawienia głównego okna aplikacji
        super("OptiCar");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1000, 600));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((int) (screenSize.getWidth() / 2 - 500), (int) (screenSize.getHeight() / 10));
        setLayout(mainLayout);
        ImageIcon icon = new ImageIcon("res/car-icon.png");
        setIconImage(icon.getImage());
        setFont(new Font("Segoe UI Semilight",  Font.PLAIN, 18));

        // Pasek menu
        menuBar = new JMenuBar();
        menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        aboutMenu = new JMenu("About");
        menuBar.add(aboutMenu);
        aboutApp = new JMenuItem("App");
        aboutApp.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                JDialog dialog = Information.appInfo(AppGui.this);
                dialog.setVisible(true);
                dialog.setLocationRelativeTo(AppGui.this);
            }
        });
        aboutMenu.add(aboutApp);
        aboutMenu.addSeparator();
        aboutProject = new JMenuItem("Project");
        aboutProject.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                JDialog dialog = Information.projectInfo(AppGui.this);
                dialog.setVisible(true);
                dialog.setLocationRelativeTo(AppGui.this);
            }
        });
        aboutMenu.add(aboutProject);

        helpMenu = new JMenu("Help");
        menuBar.add(helpMenu);
        filterHelp = new JMenuItem("Filtering");
        filterHelp.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                JDialog dialog = Information.filterHelp(AppGui.this);
                dialog.setVisible(true);
                dialog.setLocationRelativeTo(AppGui.this);

            }
        });
        helpMenu.add(filterHelp);
        helpMenu.addSeparator();
        traitsHelp = new JMenuItem("Traits");
        traitsHelp.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                JDialog dialog = Information.traitHelp(AppGui.this);
                dialog.setVisible(true);
                dialog.setLocationRelativeTo(AppGui.this);
            }
        });
        helpMenu.add(traitsHelp);
        setJMenuBar(menuBar);

        // Pierwsza strona
        firstPage = new JPanel();
        firstPage.setPreferredSize(new Dimension(1000, 600));
        firstPage.setLayout(new BoxLayout(firstPage, BoxLayout.PAGE_AXIS));


        // Panel informacyjny
        JPanel startPanel = new JPanel();
        JLabel startInfo = new JLabel("Find a perfect car for yourself!");
        startInfo.setFont(new Font("Segoe UI Semilight",  Font.PLAIN, 18));
        startInfo.setBorder(new EmptyBorder(20, 10, 30, 10));
        startPanel.add(startInfo);
        startPanel.setMaximumSize(new Dimension(400, 100));
        firstPage.add(startPanel);

        // Panel cena
        dataPanel = new JPanel();
        minPrice = new JTextField("0");
        minPrice.setFont(new Font("Segoe UI Semilight",  Font.PLAIN, 16));
        minPrice.setPreferredSize(new Dimension(90, 30));
        maxPrice = new JTextField("0");
        maxPrice.setFont(new Font("Segoe UI Semilight",  Font.PLAIN, 16));
        maxPrice.setPreferredSize(new Dimension(90, 30));
        price = new JLabel("Enter price ($): ");
        price.setFont(new Font("Segoe UI Semilight",  Font.PLAIN, 18));
        dataPanel.add(price);
        dataPanel.add(minPrice);
        dataPanel.add(maxPrice);
        dataPanel.setMaximumSize(new Dimension(400,100));
        firstPage.add(dataPanel);
        firstPage.add(Box.createRigidArea(new Dimension(0,30)));

        // Panel przeznaczenie
        practicalityCombo = new JComboBox<>();
        practicalityCombo.setFont(new Font("Segoe UI Semilight",  Font.PLAIN, 16));
        practicalityCombo.addItem("Universal");
        practicalityCombo.addItem("City");
        practicalityCombo.addItem("Route");
        practicalityCombo.addItem("Family");
        practicalityCombo.setPreferredSize(new Dimension(160,50));
        practicalityCombo.addActionListener(this);
        practicalityLabel = new JLabel("Intended use: ");
        practicalityLabel.setFont(new Font("Segoe UI Semilight",  Font.PLAIN, 18));
        practicalityPanel = new JPanel();
        practicalityPanel.add(practicalityLabel);
        practicalityPanel.add(practicalityCombo);
        practicalityPanel.setMaximumSize(new Dimension(300,100));
        firstPage.add(practicalityPanel);
        firstPage.add(Box.createRigidArea(new Dimension(0,30)));


        // Panel paliwo
        fuelPanel = new JPanel();
        fuelLabel = new JLabel("Fuel type: ");
        fuelLabel.setFont(new Font("Segoe UI Semilight",  Font.PLAIN, 18));
        fuelPanel.add(fuelLabel);

        petrolCheck = new JCheckBox("Petrol",false);
        hybridCheck = new JCheckBox("Hybrid",false);
        electricCheck = new JCheckBox("Electric",false);
        dieselCheck = new JCheckBox("Diesel",false);

        petrolCheck.setPreferredSize(new Dimension(70,40));
        hybridCheck.setPreferredSize(new Dimension(70,40));
        electricCheck.setPreferredSize(new Dimension(70,40));
        dieselCheck.setPreferredSize(new Dimension(70,40));

        petrolCheck.addActionListener(this);
        hybridCheck.addActionListener(this);
        electricCheck.addActionListener(this);
        dieselCheck.addActionListener(this);

        fuelPanel.add(petrolCheck);
        fuelPanel.add(hybridCheck);
        fuelPanel.add(electricCheck);
        fuelPanel.add(dieselCheck);
        fuelPanel.setMaximumSize(new Dimension(700,30));

        firstPage.add(fuelPanel);
        firstPage.add(Box.createRigidArea(new Dimension(0,30)));


        // Panel skrzynia biegów
        gearBoxPanel = new JPanel();

        gearBoxLabel = new JLabel("Gearbox: ");
        gearBoxLabel.setPreferredSize(new Dimension(80,20));
        gearBoxLabel.setFont(new Font("Segoe UI Semilight",  Font.PLAIN, 18));

        cvtCheck = new JCheckBox("CVT", false);
        automaticCheck = new JCheckBox("Automatic", false);
        manualCheck = new JCheckBox("Manual", false);

        cvtCheck.setPreferredSize(new Dimension(50,20));
        automaticCheck.setPreferredSize(new Dimension(85,20));
        manualCheck.setPreferredSize(new Dimension(85,20));

        cvtCheck.addActionListener(this);
        automaticCheck.addActionListener(this);
        manualCheck.addActionListener(this);

        gearBoxPanel.add(gearBoxLabel);
        gearBoxPanel.add(cvtCheck);
        gearBoxPanel.add(automaticCheck);
        gearBoxPanel.add(manualCheck);
        gearBoxPanel.setMaximumSize(new Dimension(700,30));

        firstPage.add(gearBoxPanel);
        firstPage.add(Box.createVerticalGlue());


        // Panel błędów
        errorlabel = new JLabel("");
        errorlabel.setFont(new Font("Verdana", Font.BOLD, 15));
        errorlabel.setForeground(Color.red);
        errorPanel = new JPanel();
        errorPanel.setMaximumSize(new Dimension(600,20));
        errorPanel.add(errorlabel);

        firstPage.add(errorPanel);

        // Panel przycisków
        forwardButton = new JButton("Next");
        forwardButton.setActionCommand("forward");
        forwardButton.setPreferredSize(new Dimension(90, 30));
        forwardButton.addActionListener(this);
        buttonPanel1 = new JPanel();
        buttonPanel1.add(forwardButton);
        buttonPanel1.setMaximumSize(new Dimension(3000,30));

        firstPage.add(buttonPanel1);


        add(firstPage);
        // Koniec pierwszej strony

        // Druga strona
        secondPage = new JPanel();
        secondPage.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));
        secondPage.setLayout(new BorderLayout());

        // Panel informacyjny
        infoPanel = new JPanel();
        listInfo = new JLabel("Choose traits and sort them by importance:");
        listInfo.setFont(new Font("Segoe UI Semilight",  Font.PLAIN, 18));
        listInfo.setBorder(new EmptyBorder(10, 10, 30, 10));
        infoPanel.add(listInfo);
        secondPage.add(infoPanel, BorderLayout.NORTH);

        // Panel Drag and drop
        dragPanel = new DragAndDropList();
        secondPage.add(dragPanel, BorderLayout.CENTER);

        // Panel przycisków
        backButton = new JButton("Back");
        backButton.setActionCommand("back");
        backButton.setPreferredSize(new Dimension(90, 30));
        backButton.addActionListener(this);
        buttonPanel2 = new JPanel();
        buttonPanel2.add(backButton);
        searchButton = new JButton("Search");
        searchButton.setActionCommand("search");
        searchButton.setPreferredSize(new Dimension(90, 30));
        searchButton.addActionListener(this);
        buttonPanel2.add(searchButton);
        secondPage.add(buttonPanel2, BorderLayout.SOUTH);

        // Panel ładowania
        layeredPane = new JLayeredPane();
        layeredPane.setLayout(new OverlayLayout(layeredPane));
        layeredPane.add(secondPage, new Integer(0));

        loadingPanel = new JPanel();
        loadingPanel.setLayout(new BorderLayout());
        ImageIcon loading = new ImageIcon("res/loadgif.gif");
        loadingGif = new JLabel(loading);
        loadingPanel.add(loadingGif);
        loadingPanel.setVisible(false);
        layeredPane.add(loadingPanel, new Integer(1));
        add(layeredPane);
        // Koniec drugiej strony

        // Trzecia strona
        thirdPage = new JPanel();
        thirdPage.setLayout(listLayout);
        add(thirdPage);
        // Koniec trzeciej strony

        pack();

        // Panel połaczenia
        if (!isConnected) {
            this.setEnabled(false);
            connectionDialog = new JDialog(this, "Connection error");
            connectionDialog.setPreferredSize(new Dimension(250, 200));
            JPanel connectionPanel = new JPanel(new BorderLayout());
            connectText = new JTextPane();
            String connectInfo = "\nThere is a problem with loading data. " +
                    "Check your internet connection and try again.";
            StyledDocument doc = connectText.getStyledDocument();
            SimpleAttributeSet center = new SimpleAttributeSet();
            StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
            doc.setParagraphAttributes(0, doc.getLength(), center, true);
            connectText.setText(connectInfo);
            connectText.setEditable(false);
            connectionPanel.add(connectText, BorderLayout.CENTER);
            JButton reloadButton = new JButton("Reload");
            reloadButton.addActionListener(this);
            reloadButton.setActionCommand("reload");
            reloadButton.setPreferredSize(new Dimension(80, 30));
            JPanel reloadPane = new JPanel();
            reloadPane.add(reloadButton);
            connectionPanel.add(reloadPane, BorderLayout.SOUTH);
            connectionPanel.setMaximumSize(new Dimension(200, 180));
            connectionDialog.add(connectionPanel, BorderLayout.CENTER);
            connectionDialog.setResizable(false);
            connectionDialog.pack();
            connectionDialog.setLocationRelativeTo(this.getContentPane());
            connectionDialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });
            connectionDialog.setVisible(true);
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Przejście z pierwszej strony na drugą wiąże się z przefiltrowaniem danych
        if (e.getActionCommand().equals("forward")) {
            try {
                CarData.setMinPrice(Integer.parseInt(minPrice.getText()));
                CarData.setMaxPrice(Integer.parseInt(maxPrice.getText()));
                if (CarData.minPrice < 5000) {
                    CarData.minPrice = 5000;
                }
                if (CarData.maxPrice == 0) {
                    CarData.maxPrice = 15000000;
                }
                else if (CarData.maxPrice < 5000 | CarData.minPrice > 15000000){
                    throw new MaxPriceException();
                }

                CarData.practicality = (String) practicalityCombo.getSelectedItem();
                boolean fuelFlag = false;
                boolean gearFlag = false;
                if (CarData.fuel.size() == 0){
                    CarData.fuel.add("Petrol");
                    CarData.fuel.add("Hybrid");
                    CarData.fuel.add("Electric");
                    CarData.fuel.add("Diesel");
                    fuelFlag = true;
                }
                if (CarData.gearBox.size() == 0){
                    CarData.gearBox.add("CVT");
                    CarData.gearBox.add("Automatic");
                    CarData.gearBox.add("Manual");
                    gearFlag = true;
                }
                CarData.filterData();
                if (CarData.filteredCars.isEmpty()){
                    throw new EmptyDataException();
                }
                if (fuelFlag) {
                    CarData.fuel.removeAll(CarData.fuel);
                    fuelFlag = false;
                }
                if (gearFlag) {
                    CarData.gearBox.removeAll(CarData.gearBox);
                    gearFlag = false;
                }
                mainLayout.next(this.getContentPane());
                errorlabel.setText("");
            }
            catch (NumberFormatException p) {
                errorlabel.setText("Please enter a correct price!");
            }
            catch (MaxPriceException p){
                errorlabel.setText("There are no cars at such a price!");
            }

            catch (EmptyDataException p){
                errorlabel.setText("There are no such cars!");
            }


        }
        // Ponawianie próby połączenia, dopóki dane się nie załadują, nie można używać aplikacji
        if (e.getActionCommand().equals("reload")) {
            connectText.setText("\n\nLoading...");
            new Thread(() -> {
                try {
                    connectText.setText("\n\nLoading...");
                    TimeUnit.SECONDS.sleep(1);
                    CarData.loadData();
                    isConnected = true;
                    this.setEnabled(true);
                    connectionDialog.setVisible(false);

                } catch (IOException ex) {
                    String connectInfo = "\nThere is a problem with loading data. " +
                            "Check your internet connection and try again.";
                    connectText.setText(connectInfo);
                } catch (InterruptedException ignored) {

                }
            }).start();

        }
        // Powrót do poprzedniej strony
        if (e.getActionCommand().equals("back")) {
            mainLayout.previous(this.getContentPane());
        }
        // Przejście z drugiej strony do listy samochodów
        if (e.getActionCommand().equals("search")) {
            loadingPanel.setVisible(true);
            Thread listingCars = new Thread(() -> {
                thirdPage.removeAll();
                CarData.traits = new ArrayList<>(DragAndDropList.dndList.getSelectedValuesList());
                CarData.findCars();
                Car.frame = AppGui.this;
                CarList.frame = AppGui.this;
                CarList carsList = new CarList();
                thirdPage.add(carsList);
                mainLayout.next(AppGui.this.getContentPane());
                loadingPanel.setVisible(false);
            });
            listingCars.start();
        }
        // Zmiana zaznaczenia filtrów
        if (e.getSource().equals(petrolCheck)){
            if (petrolCheck.isSelected()){
                CarData.fuel.add(petrolCheck.getText());
            }else {
                CarData.fuel.remove(petrolCheck.getText());
            }

        }
        if (e.getSource().equals(hybridCheck)){
            if (hybridCheck.isSelected()){
                CarData.fuel.add(hybridCheck.getText());
            }else {
                CarData.fuel.remove(hybridCheck.getText());
            }

        }
        if (e.getSource().equals(dieselCheck)){
            if (dieselCheck.isSelected()){
                CarData.fuel.add(dieselCheck.getText());
            }else {
                CarData.fuel.remove(dieselCheck.getText());
            }

        }
        if (e.getSource().equals(electricCheck)){
            if (electricCheck.isSelected()){
                CarData.fuel.add(electricCheck.getText());
            }else {
                CarData.fuel.remove(electricCheck.getText());
            }

        }
        if (e.getSource().equals(cvtCheck)){
            if (cvtCheck.isSelected()){
                CarData.gearBox.add(cvtCheck.getText());
            }else {
                CarData.gearBox.remove(cvtCheck.getText());
            }
        }
        if (e.getSource().equals(automaticCheck)){
            if (automaticCheck.isSelected()){
                CarData.gearBox.add(automaticCheck.getText());
            }else {
                CarData.gearBox.remove(automaticCheck.getText());
            }
        }
        if (e.getSource().equals(manualCheck)){
            if (manualCheck.isSelected()){
                CarData.gearBox.add(manualCheck.getText());
            }else {
                CarData.gearBox.remove(manualCheck.getText());
            }
        }

    }

    private void showGui() {
        this.setVisible(true);
    }

    // Main
    public static void main(String[] args) {
        try {
            CarData.loadData();
            isConnected = true;
        } catch (IOException e) {
            isConnected = false;
        }
        FlatLightLaf.setup();
        AppGui app = new AppGui();
        app.showGui();

    }
}