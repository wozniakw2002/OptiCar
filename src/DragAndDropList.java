import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;


public class DragAndDropList extends JPanel {

    static JList<String> dndList; // Lista kafelków
    private static DefaultListModel<String> traitsList;
    private String[] traits = new String[] { "Dynamics", "Practicality", "Safety", "Sport character", "Equipment", "Efficiency", "Price", "Off-road capabilities"};

    // Panel z kafelkami
    public DragAndDropList() {
        traitsList = new DefaultListModel<>();
        for (String trait : traits) {
            traitsList.addElement(trait);
        }
        dndList = new JList<>(traitsList);
        dndList.setOpaque(false);
        dndList.setCellRenderer(new buttonCellRenderer());
        DnDAdapter mouseAdapter = new DnDAdapter();
        dndList.addMouseListener(mouseAdapter);
        dndList.addMouseMotionListener(mouseAdapter);
        ListSelectionModel noSelection = new DefaultListSelectionModel() {
            @Override
            public void setSelectionInterval(int index0, int index1) {}
        };
        dndList.setSelectionModel(noSelection);
        this.add(dndList);
    }

    // Interaktywna lista przycisków
    private static class DnDAdapter extends MouseInputAdapter {

        private boolean mouseDragging = false;
        private int dragSourceIndex; // zmienia się wraz z przenoszeniem kafelka
        private int sourceIndex; // pierwotne położenie kafelka
        private boolean wasDragged;

        // Przyciśnięcie kafelka
        @Override
        public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                dragSourceIndex = dndList.locationToIndex(e.getPoint());
                sourceIndex = dndList.locationToIndex(e.getPoint());
                mouseDragging = true;
                wasDragged = false;
            }
        }

        // Skończenie przenoszenia
        @Override
        public void mouseReleased(MouseEvent e) {
            mouseDragging = false;
            int endIndex = dndList.locationToIndex(e.getPoint());
            // Zmień zaznaczenie kafelka tylko jeśli kafelek nie zmienił miejsca na liście
            if (endIndex == sourceIndex & !wasDragged) {
                if (dndList.isSelectedIndex(sourceIndex)) {
                    dndList.removeSelectionInterval(sourceIndex, sourceIndex);
                } else {
                    dndList.addSelectionInterval(sourceIndex, sourceIndex);
                }
            }
        }

        // Przenoszenie kafelka
        @Override
        public void mouseDragged(MouseEvent e) {
            if (mouseDragging) {
                int currentIndex = dndList.locationToIndex(e.getPoint());
                // Jeśli kursor zmienił położenie i wskazuje na inny kafelek
                if (currentIndex != dragSourceIndex) {
                    wasDragged = true;
                    String dragElement = traitsList.get(dragSourceIndex);
                    boolean isSrc = dndList.isSelectedIndex(dragSourceIndex);
                    boolean isCur = dndList.isSelectedIndex(currentIndex);
                    // Zamień kafelki
                    traitsList.remove(dragSourceIndex);
                    traitsList.add(currentIndex, dragElement);
                    // Zaznaczenie jest po indeksach, zatem jeśli zamienimy zaznaczony kafelek z niezaznaczonym,
                    // musimy odwrócić zaznaczenie kafelków
                    if (isSrc) {
                        dndList.addSelectionInterval(currentIndex, currentIndex);
                    } else {
                        dndList.removeSelectionInterval(currentIndex, currentIndex);
                    }
                    if (isCur) {
                        dndList.addSelectionInterval(dragSourceIndex, dragSourceIndex);
                    } else {
                        dndList.removeSelectionInterval(dragSourceIndex, dragSourceIndex);
                    }
                    dragSourceIndex = currentIndex;
                }
            }
        }
    }

    // Wygląd kafelków
    private static class buttonCellRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JToggleButton button = new JToggleButton(value.toString(), isSelected);
            button.setFont(new Font("Segoe UI Semilight",  Font.PLAIN, 20));
            button.setSize(new Dimension(100, 30));
            return button;
        }
    }

}