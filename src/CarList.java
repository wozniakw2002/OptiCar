import tech.tablesaw.api.Table;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class CarList extends JPanel implements ActionListener {

    static int listCount = 0; // liczba stron
    static int listNumber = 0; // obecna strona
    static AppGui frame; // główne okno
    static Table Cars; // Przefiltrowane i posortowane samochody
    private JPanel listPanel, buttonPanel;
    private JButton backButton, forwardButton, menuButton;
    private JPanel loadingPanel;
    private static final ImageIcon loading = new ImageIcon("res/loadgif.gif");

    // Panel do wyświetlania samochodów
    public CarList() {

        // Ustawienia panelu
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        listPanel = new JPanel();
        listPanel.setMaximumSize(new Dimension(1000, 3200));
        BoxLayout boxLayout = new BoxLayout(listPanel, BoxLayout.PAGE_AXIS);
        listPanel.setLayout(boxLayout);
        boolean maxCars = false;
        listPanel.add(Box.createRigidArea(new Dimension(5, 5)));

        // Dodawanie kolejnych paneli samochodów
        for (int j = listCount * 20; j < listCount * 20 + 20; j++) {
            try {
                Car car = new Car(Cars.rows(j));
                listPanel.add(car.carInfo());
                listPanel.add(Box.createRigidArea(new Dimension(5, 5)));
                maxCars = false;
            } catch (IndexOutOfBoundsException e){
                maxCars = true;
                break;
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        // Skrolowanie
        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, Color.LIGHT_GRAY));

        // Panel ładowania
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(new OverlayLayout(layeredPane));
        layeredPane.add(scrollPane, new Integer(0));
        loadingPanel = new JPanel();
        loadingPanel.setLayout(new BorderLayout());
        JLabel loadingGif = new JLabel(loading);
        loadingPanel.add(loadingGif);
        loadingPanel.setVisible(false);
        layeredPane.add(loadingPanel, new Integer(1));
        add(Box.createRigidArea(new Dimension(8, 5)), BorderLayout.WEST);
        add(layeredPane);


        // Panel przycisków
        buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        menuButton = new JButton("Menu");
        menuButton.setActionCommand("menu");
        menuButton.setPreferredSize(new Dimension(90, 30));
        menuButton.addActionListener(this);
        buttonPanel.add(menuButton);
        backButton = new JButton("Back");
        backButton.setActionCommand("back");
        backButton.setPreferredSize(new Dimension(90, 30));
        backButton.addActionListener(this);
        buttonPanel.add(backButton);
        forwardButton = new JButton("Next");
        if (maxCars){
            forwardButton.setEnabled(false);
        }
        forwardButton.setActionCommand("next");
        forwardButton.setPreferredSize(new Dimension(90, 30));
        forwardButton.addActionListener(this);
        buttonPanel.add(forwardButton);
        add(buttonPanel, BorderLayout.SOUTH);

        listCount++;
        listNumber++;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        // Powrót do poprzedniej strony
        if (e.getActionCommand().equals("back")) {
            if (listNumber == 1) {
                frame.mainLayout.first(frame.getContentPane());
            } else {
                frame.listLayout.previous(frame.thirdPage);
                listNumber -= 1;
            }
        }
        // Przejście do kolejnej strony
        if (e.getActionCommand().equals("next")) {
            // Jeśli przed nami są jakieś wygenerowane strony (czyli jeśli się cofaliśmy) przechodzimy dalej,
            // wpp tworzymy nową stronę z kolejnymi samochodami
            if (listNumber < listCount) {
                frame.listLayout.next(frame.thirdPage);
                listNumber += 1;
            } else {
                loadingPanel.setVisible(true);
                new Thread(() -> {
                    CarList carsList = new CarList();
                    frame.thirdPage.add(carsList);
                    frame.listLayout.next(frame.thirdPage);
                    loadingPanel.setVisible(false);
                }).start();
            }
        }
        // Powrót do menu
        if (e.getActionCommand().equals("menu")) {
            frame.mainLayout.first(frame.getContentPane());
        }
    }
}
