import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;
import java.awt.*;

public abstract class Information {

    // Okno dialogowe, pomoc do filtrowania
    public static JDialog filterHelp(JFrame frame) {
        JDialog dialog = new JDialog(frame, "Filtering help");
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.setPreferredSize(new Dimension(300, 300));
        JTextPane textArea = new JTextPane();
        String info = "ENTERING PRICE\n"
                + "Left box is for minimum price, rigth for maximum. If you leave text boxes with zeros, " +
                "app will not filter price.\n\n"
                + "INTENDED USE\n"
                + "Default type is universal. Choosing type of use will have no effect unless you pick practicality" +
                " trait in the next phase.\n\n"
                + "FUEL AND GEARBOX\n"
                + "If not specified, app will not filter it.";
        StyledDocument doc = textArea.getStyledDocument();
        doc.setParagraphAttributes(0, doc.getLength(), new SimpleAttributeSet(), true);
        textArea.setText(info);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.setResizable(false);
        dialog.pack();
        return dialog;
    }

    // Okno z pomocą dotyczącą cech
    public static JDialog traitHelp(JFrame frame) {
        JDialog dialog = new JDialog(frame, "Traits guide");
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.setPreferredSize(new Dimension(300, 300));
        JTextPane textArea = new JTextPane();
        String info = "GENERAL\n"
                + "The higher the trait is placed, the more it counts. Only checked traits are taken into" +
                " account. If none selected, cars are sorted alphabetically. " +
                "Following data is taken into account for each trait:\n\n"
                + "DYNAMICS\n"
                + "Power | Weight | Torque\n\n"
                + "PRACTICALITY\n"
                + "Depends on what you choose in \"Intended use\" field, but generally:\n"
                + "Dimensions | Body type | Consumption\n\n"
                + "SAFETY\n"
                + "Dimensions | Safety equpiment\n\n"
                + "SPORT CHARACTER\n"
                + "Body type | Power | Torque\n\n"
                + "EFFICIENCY\n"
                + "Fuel consumption\n\n"
                + "PRICE\n"
                + "Price\n\n"
                + "OFF-ROAD CAPABILITIES\n"
                + "Body type | Clearance\n\n";
        StyledDocument doc = textArea.getStyledDocument();
        doc.setParagraphAttributes(0, doc.getLength(), new SimpleAttributeSet(), true);
        textArea.setText(info);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.setResizable(false);
        dialog.pack();
        SwingUtilities.invokeLater(() -> scrollPane.getViewport().setViewPosition( new Point(0, 0) ));
        return dialog;
    }

    // Okno z informacjami o aplikacji
    public static JDialog appInfo(JFrame frame) {
        JDialog dialog = new JDialog(frame, "About app");
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.setPreferredSize(new Dimension(300, 300));
        JTextPane textArea = new JTextPane();
        String info = "GENERAL\n"
                + "Data and photos acquired from www.ccarprices.com and thoroughly cleaned. Data contains " +
                "over 2500 cars from years 2022 and 2023. May be updated in the future.\n\n"
                + "WARNING\n"
                + "Technical specifications and prices may be inacurate. To check current prices and availability" +
                " in your area it is recommended to go to the official website of the car manufacturer. Our " +
                "\"search web\" button will help you with that.";
        StyledDocument doc = textArea.getStyledDocument();
        doc.setParagraphAttributes(0, doc.getLength(), new SimpleAttributeSet(), true);
        textArea.setText(info);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.setResizable(false);
        dialog.pack();
        SwingUtilities.invokeLater(() -> scrollPane.getViewport().setViewPosition( new Point(0, 0) ));
        return dialog;
    }

    // Okno z informacjami o projekcie
    public static JDialog projectInfo(JFrame frame) {
        JDialog dialog = new JDialog(frame, "About project");
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.setPreferredSize(new Dimension(300, 300));
        JTextPane textArea = new JTextPane();
        String info = "GENERAL\n"
                + "Welcome to OptiCar! This project was created for the \"Advanced Object-oriented and Functional Programming\" course" +
                " at the Warsaw University of Technology. \n\n"
                + "CONTACT\n"
                + "In case of any questions or problems contact us at tymekurban@gmail.com.\n\n"
                + "AUTHORS\n"
                + "Tymoteusz Urban\nSebastian Trojan\nWiktor Woźniak";
        StyledDocument doc = textArea.getStyledDocument();
        doc.setParagraphAttributes(0, doc.getLength(), new SimpleAttributeSet(), true);
        textArea.setText(info);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.setResizable(false);
        dialog.pack();
        SwingUtilities.invokeLater(() -> scrollPane.getViewport().setViewPosition( new Point(0, 0) ));
        return dialog;
    }

}
