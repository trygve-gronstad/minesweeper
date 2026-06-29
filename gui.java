import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

class View {

    private final JFrame VINDU = new JFrame("Minesweeper");
    private final JPanel RUTE_PANEL = new JPanel();
    private final Controller CON;

    public View(Controller c) {
        CON = c;

        init();
    }

    private void init() {

        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        }
        catch (Exception e) {
            System.exit(1);
        }

        VINDU.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        RUTE_PANEL.setLayout(null);

        VINDU.add(RUTE_PANEL);

        VINDU.pack();
        VINDU.setLocationRelativeTo(null);
        VINDU.setVisible(true);
    }

    public void leggTilKnapp(Polygon p, int nr) {
        KantKnapp k = new KantKnapp(p, nr);
        k.setBounds(p.getBounds());
        RUTE_PANEL.add(k);
    }

    public void lagtTilKnapper() {
        RUTE_PANEL.revalidate();
        RUTE_PANEL.repaint();

        VINDU.revalidate();
        VINDU.repaint();
    }

    private class KantKnapp extends JButton {

        private Polygon p;
        private int nr;

        public KantKnapp(Polygon p, int nr) {
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            this.p = p;
            this.nr = nr;

            addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println(1);
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (getModel().isArmed()) {
                g2.setColor(Color.GRAY);
            } else {
                g2.setColor(Color.ORANGE);
            }
            g2.translate(-getX(), -getY());
            g2.fillPolygon(p);

            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.DARK_GRAY);
        }

        @Override
        public boolean contains(int x, int y) {
            return p.contains(x, y);
        }
    }

}

