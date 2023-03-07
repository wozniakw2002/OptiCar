import org.imgscalr.Scalr;
import tech.tablesaw.api.Table;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Map;

public class Car implements ActionListener{

    static JFrame frame; // główne okno
    private JPanel mainPanel;
    private JFrame carInfoDetailed;
    // Pola z danymi samochodu
    private String photo;
    private String brand;
    private String model;
    private String body;
    private int price;
    private String engineType;
    private String fuelType;
    private String torque;
    private String power;
    private String gearBox;
    private String driveType;
    private String cityConsumption;
    private String highwayConsumption;
    private String height;
    private String weight;
    private String length;
    private String width;
    private String doors;
    private String seats;

    // Konstruktor samochodu
    public Car(Table carRow) {
        mainPanel = new JPanel();
        carInfoDetailed = new JFrame();
        photo = carRow.get(0, "Photo");
        brand = carRow.get(0, "Brand");
        model = carRow.get(0, "Model");
        body = carRow.get(0, "Body");
        price = (int) Double.parseDouble(carRow.get(0, "Pricing"));
        engineType = carRow.get(0,"Engine");
        fuelType = carRow.get(0,"Fuel");
        torque = carRow.get(0,"Torque_lb_ft");
        power = carRow.get(0,"Power");
        gearBox = carRow.get(0,"GearBox");
        driveType = carRow.get(0,"Drivetrain");
        cityConsumption = carRow.get(0,"Consumption_city");
        highwayConsumption = carRow.get(0,"Consumption_highway");
        height = carRow.get(0,"Height");
        weight = carRow.get(0,"Weight_lbs");
        length = carRow.get(0,"Length");
        width = carRow.get(0,"Width");
        doors = carRow.get(0,"Doors");
        seats = carRow.get(0,"Seats");
    }

    // Panel samochodu do wyświetlania w liście
    public JPanel carInfo() throws IOException {

        Color myColor = Color.decode("#55acee");
        mainPanel.setPreferredSize(new Dimension(400, 150));
        mainPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setMaximumSize(new Dimension(1000,165));
        mainPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Kliknięcie i otworzenie okna ze szczegółowymi danymi
        mainPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                try {
                    carInfoDetailed = CarInfoDetailed();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                carInfoDetailed.setLocationRelativeTo(frame);
                carInfoDetailed.setVisible(true);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                mainPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, myColor));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                mainPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
            }
        });

        // Informacje
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.PAGE_AXIS));
        infoPanel.setPreferredSize(new Dimension(400, 150));
        JLabel carName = new JLabel(brand + " " + model);
        carName.setFont(new Font("Segoe UI Semibold",  Font.PLAIN, 20));
        infoPanel.add(carName, Component.LEFT_ALIGNMENT);
        JLabel carInfo = new JLabel(engineType + " | " + fuelType);
        infoPanel.add(carInfo, Component.LEFT_ALIGNMENT);
        JLabel carPrice = new JLabel(price + "$");
        carPrice.setForeground(myColor);
        carPrice.setFont(new Font("Segoe UI Semibold",  Font.PLAIN, 20));
        infoPanel.add(carPrice, Component.LEFT_ALIGNMENT);

        // Zdjęcie
        JPanel photoPanel = new JPanel();
        photoPanel.setSize(new Dimension(260, 140));
        URL url = new URL(photo);
        BufferedImage originalImage;
        try {
            originalImage = ImageIO.read(url);
        } catch (IOException e) {
            File file = new File("res/no_photo.jpg");
            originalImage = ImageIO.read(file);
        }
        originalImage = Scalr.resize(originalImage, Scalr.Method.AUTOMATIC, Scalr.Mode.AUTOMATIC, 260, 140, Scalr.OP_ANTIALIAS);
        if (originalImage.getHeight() > 140) {
            int h = (originalImage.getHeight() - 140) / 2;
            originalImage = originalImage.getSubimage(0, h, 260, 140);
        }
        BufferedImage proccessedImage = makeRoundedCorner(originalImage, 10);
        ImageIcon icon = new ImageIcon(proccessedImage);
        JLabel carPhoto = new JLabel(icon);
        photoPanel.add(carPhoto);
        mainPanel.add(photoPanel, BorderLayout.WEST);
        mainPanel.add(infoPanel, BorderLayout.CENTER);

        // Przycisk szukania
        JLabel searchWeb = new JLabel("Search web");
        searchWeb.setBorder(new EmptyBorder(10, 10, 10, 10));
        Font searchFont = searchWeb.getFont();
        searchWeb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchWeb.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                String string = "https://www.google.com/search?q=" + brand + " " + model;
                String uri = string.replaceAll(" ", "+").toLowerCase();
                openWebpage(URI.create(uri));
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                Map attributes = searchFont.getAttributes();
                attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
                searchWeb.setFont(searchFont.deriveFont(attributes));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                searchWeb.setFont(searchFont);
            }
        });
        JPanel morePanel = new JPanel();
        morePanel.setLayout(new BoxLayout(morePanel, BoxLayout.PAGE_AXIS));
        morePanel.add(Box.createRigidArea(new Dimension(5,0)), Component.RIGHT_ALIGNMENT);
        morePanel.add(searchWeb, Component.RIGHT_ALIGNMENT);
        mainPanel.add(morePanel, BorderLayout.EAST);

        return mainPanel;
    }

    // Okno ze szczegółowymi informacjami wyświetlonymi w tabeli
    public JFrame CarInfoDetailed() throws IOException{

        JFrame carInfoDetailed  = new JFrame("Technical specifications");
        carInfoDetailed.setMinimumSize(new Dimension(960, 500));
        carInfoDetailed.setResizable(false);
        ImageIcon icon = new ImageIcon("res/car-icon.png");
        carInfoDetailed.setIconImage(icon.getImage());

        String[][] dataBasic ={
                {"Model", brand + " " + model},
                {"Price", price + " $"},
                {"Body Type", body}};
        String[] columnBasic ={"Basic info", ""};
        JTable tableBasic = new JTable(dataBasic,columnBasic)  {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableBasic.getTableHeader().setReorderingAllowed(false);
        tableBasic.getTableHeader().setResizingAllowed(false);


        if (torque.length() > 0) {
            torque = String.valueOf((int) Double.parseDouble(torque));
        }
        if (power.length() > 0) {
            power = String.valueOf((int) Double.parseDouble(power));
        }

        String[][] dataEngine ={
                {"Engine type", engineType},
                {"Engine Power", power + " HP"},
                {"Torque", torque + " lb-ft"},
                {"Fuel Type", fuelType}};
        String[] columnEngine ={"Engine", ""};
        JTable tableEngine = new JTable(dataEngine,columnEngine) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableEngine.getTableHeader().setReorderingAllowed(false);
        tableEngine.getTableHeader().setResizingAllowed(false);

        String[][] dataTransmission ={
                {"Gear box", gearBox},
                {"Drive type", driveType.substring(0, 1).toUpperCase() + driveType.substring(1)}};
        String[] columnTransmission ={"Transmission", ""};
        JTable tableTransmission = new JTable(dataTransmission,columnTransmission) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableTransmission.getTableHeader().setReorderingAllowed(false);
        tableTransmission.getTableHeader().setResizingAllowed(false);

        String[][] dataConsuption ={
                {"Mileage in city", cityConsumption + " miles per gallon"},
                {"Mileage on highway", highwayConsumption + " miles per gallon"}};
        String[] columnConsuption ={"Mileage", ""};
        JTable tableConsuption = new JTable(dataConsuption,columnConsuption) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableConsuption.getTableHeader().setReorderingAllowed(false);
        tableConsuption.getTableHeader().setResizingAllowed(false);

        if (doors.length() > 0) {
            doors = String.valueOf((int) Double.parseDouble(doors));
        }
        if (seats.length() > 0) {
            seats = String.valueOf((int) Double.parseDouble(seats));
        }

        String[][] dataPhysical ={
                {"Number of doors", doors},
                {"Number of seats", seats},
                {"Weight", weight + " pounds"},
                {"Height", height + " inches"},
                {"Length", length + " inches"},
                {"Width", width + " inches"}};
        String[] columnPhysical ={"Physical parameters", ""};
        JTable tablePhysical = new JTable(dataPhysical,columnPhysical) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablePhysical.getTableHeader().setReorderingAllowed(false);
        tablePhysical.getTableHeader().setResizingAllowed(false);

        Container c = carInfoDetailed.getContentPane();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.add(tableBasic.getTableHeader());
        c.add(tableBasic);
        c.add(tableEngine.getTableHeader());
        c.add(tableEngine);
        c.add(tableTransmission.getTableHeader());
        c.add(tableTransmission);
        c.add(tableConsuption.getTableHeader());
        c.add(tableConsuption);
        c.add(tablePhysical.getTableHeader());
        c.add(tablePhysical);

        return carInfoDetailed;
    }

    // Zaokrąglenie rogów zdjęcia
    private static BufferedImage makeRoundedCorner(BufferedImage image, int cornerRadius) {

        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = output.createGraphics();
        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, cornerRadius, cornerRadius));
        g2.setComposite(AlphaComposite.SrcAtop);
        g2.drawImage(image, 0, 0, null);
        g2.dispose();

        return output;
    }

    // Otwieranie strony w przeglądarce
    private static void openWebpage(URI uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }
}
