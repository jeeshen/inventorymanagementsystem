import javax.swing.*;
import java.awt.*;

public class StockLocation extends JPanel {
    private final Image image;
    private int selection;

    public StockLocation(String imagePath, int selection) {
        ImageIcon icon = new ImageIcon(imagePath);
        image = icon.getImage();
        this.selection = selection;
    }

    public int getSelection() {
        return this.selection;
    }

    public void setSelection(int selection) {
        this.selection = selection;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), this);

        g.setColor(Color.RED);

        int y;
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(3));

        for (Stock p:Main.stockList) {
            if (p != null && p.getQuantity() > 0 && p.getProduct().getName().equals(Main.productList[selection-1].getName())) {
                if (p.getLocation().getRowID().equals("A")) {
                    y = 115;
                    switch (p.getLocation().getRackID()) {
                        case 1:
                            g2d.drawOval(65,y,40,40);
                            break;
                        case 2:
                            g2d.drawOval(105,y,40,40);
                            break;
                        case 3:
                            g2d.drawOval(145,y,40,40);
                            break;
                        case 4:
                            g2d.drawOval(190,y,40,40);
                            break;
                        case 5:
                            g2d.drawOval(230,y,40,40);
                            break;
                        case 6:
                            g2d.drawOval(270,y,40,40);
                            break;
                        case 7:
                            g2d.drawOval(310,y,40,40);
                            break;
                        case 8:
                            g2d.drawOval(355,y,40,40);
                            break;
                        case 9:
                            g2d.drawOval(395,y,40,40);
                            break;
                        case 10:
                            g2d.drawOval(435,y,40,40);
                            break;
                    }
                }
                else if (p.getLocation().getRowID().equals("B")) {
                    y = 185;
                    switch (p.getLocation().getRackID()) {
                        case 1:
                            g2d.drawOval(65,y,40,40);
                            break;
                        case 2:
                            g2d.drawOval(105,y,40,40);
                            break;
                        case 3:
                            g2d.drawOval(145,y,40,40);
                            break;
                        case 4:
                            g2d.drawOval(190,y,40,40);
                            break;
                        case 5:
                            g2d.drawOval(230,y,40,40);
                            break;
                        case 6:
                            g2d.drawOval(270,y,40,40);
                            break;
                        case 7:
                            g2d.drawOval(310,y,40,40);
                            break;
                        case 8:
                            g2d.drawOval(355,y,40,40);
                            break;
                        case 9:
                            g2d.drawOval(395,y,40,40);
                            break;
                        case 10:
                            g2d.drawOval(435,y,40,40);
                            break;
                    }
                }
                else if (p.getLocation().getRowID().equals("C")) {
                    y = 260;
                    switch (p.getLocation().getRackID()) {
                        case 1:
                            g2d.drawOval(65,y,40,40);
                            break;
                        case 2:
                            g2d.drawOval(105,y,40,40);
                            break;
                        case 3:
                            g2d.drawOval(145,y,40,40);
                            break;
                        case 4:
                            g2d.drawOval(190,y,40,40);
                            break;
                        case 5:
                            g2d.drawOval(230,y,40,40);
                            break;
                        case 6:
                            g2d.drawOval(270,y,40,40);
                            break;
                        case 7:
                            g2d.drawOval(310,y,40,40);
                            break;
                        case 8:
                            g2d.drawOval(355,y,40,40);
                            break;
                        case 9:
                            g2d.drawOval(395,y,40,40);
                            break;
                        case 10:
                            g2d.drawOval(435,y,40,40);
                            break;
                    }
                }
                else if (p.getLocation().getRowID().equals("D")) {
                    y = 330;
                    switch (p.getLocation().getRackID()) {
                        case 1:
                            g2d.drawOval(65,y,40,40);
                            break;
                        case 2:
                            g2d.drawOval(105,y,40,40);
                            break;
                        case 3:
                            g2d.drawOval(145,y,40,40);
                            break;
                        case 4:
                            g2d.drawOval(190,y,40,40);
                            break;
                        case 5:
                            g2d.drawOval(230,y,40,40);
                            break;
                        case 6:
                            g2d.drawOval(270,y,40,40);
                            break;
                        case 7:
                            g2d.drawOval(310,y,40,40);
                            break;
                        case 8:
                            g2d.drawOval(355,y,40,40);
                            break;
                        case 9:
                            g2d.drawOval(395,y,40,40);
                            break;
                        case 10:
                            g2d.drawOval(435,y,40,40);
                            break;
                    }
                }
                else if (p.getLocation().getRowID().equals("E")) {
                    y = 405;
                    switch (p.getLocation().getRackID()) {
                        case 1:
                            g2d.drawOval(65,y,40,40);
                            break;
                        case 2:
                            g2d.drawOval(105,y,40,40);
                            break;
                        case 3:
                            g2d.drawOval(145,y,40,40);
                            break;
                        case 4:
                            g2d.drawOval(190,y,40,40);
                            break;
                        case 5:
                            g2d.drawOval(230,y,40,40);
                            break;
                        case 6:
                            g2d.drawOval(270,y,40,40);
                            break;
                        case 7:
                            g2d.drawOval(310,y,40,40);
                            break;
                        case 8:
                            g2d.drawOval(355,y,40,40);
                            break;
                        case 9:
                            g2d.drawOval(395,y,40,40);
                            break;
                        case 10:
                            g2d.drawOval(435,y,40,40);
                            break;
                    }
                }
                else if (p.getLocation().getRowID().equals("F")) {
                    g2d.drawOval(600,440,40,40);
                }
                else if (p.getLocation().getRowID().equals("G")) {
                    g2d.drawOval(600,250,40,40);
                }
                else if (p.getLocation().getRowID().equals("H")) {
                    g2d.drawOval(600,70,40,40);
                }
            }
        }
    }
}
