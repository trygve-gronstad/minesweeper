import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

class View {

    private final JFrame VINDU = new JFrame("Minesweeper");
    private final JPanel RUTE_PANEL = new JPanel();
    private final Controller CON;
    private final List<KantKnapp> alleKnapper = new ArrayList<>();

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
        VINDU.setPreferredSize(new Dimension(1000, 800));

        VINDU.pack();
        VINDU.setLocationRelativeTo(null);
        VINDU.setVisible(true);
    }

    public void leggTilKnapp(Polygon p, int tekstX, int tekstY) {
        KantKnapp k = new KantKnapp(p, tekstX, tekstY);
        alleKnapper.add(k);
        RUTE_PANEL.add(k);
    }

    public void lagtTilKnapper() {
        RUTE_PANEL.revalidate();
        RUTE_PANEL.repaint();

        VINDU.revalidate();
        VINDU.repaint();
    }

    public void vis(int i) {
        alleKnapper.get(i).vis();
    }

    private class KantKnapp extends JButton {

        private static int antall = 0;
        private static final Font font = new Font("Display", Font.BOLD, 15);
        private static final Color[] farger = new Color[] {Color.BLUE, Color.GREEN, Color.RED, Color.MAGENTA, Color.ORANGE, Color.CYAN, Color.BLACK, Color.WHITE};

        private final Polygon p;
        private final int indeks, tekstX, tekstY;
        private boolean sjult = true;

        public KantKnapp(Polygon p, int tekstX, int tekstY) {
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setBounds(p.getBounds());
            setFont(font);

            this.p = p;
            indeks = antall++;
            this.tekstX = tekstX;
            this.tekstY = tekstY;

            addMouseListener(new MouseAdapter(){
                @Override
                public void mousePressed(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        CON.trykkKnapp(indeks);
                    } 
                    else if (SwingUtilities.isRightMouseButton(e)) {
                        if (sjult) {
                            if (CON.byttFlagg(indeks)) {
                                setText("🚩");
                            }
                            else {
                                setText("");
                            }
                            repaint();
                        }
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (!sjult) {
                g2.setColor(Color.GRAY);
            } else {
                g2.setColor(Color.LIGHT_GRAY);
            }
            g2.translate(-getX(), -getY());
            g2.fillPolygon(p);

            g2.setColor(Color.BLACK);
            g2.drawPolygon(p);

            g2.translate(getX(), getY());

            // String tekst = getText();
            // if (tekst != null) {
            //     g2.drawString(tekst, tekstX, tekstY);
            // }
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.BLACK);
        }

        @Override
        public boolean contains(int x, int y) {
            return p.contains(x + getX(), y + getY());
        }

        private String hentTekst() {
            if (CON.erMine(indeks)) {
                return "💣";
            }
            int x = CON.hentForklaring(indeks);

            if (x > 0) {
                int i = Math.min(7, x-1);
                setForeground(farger[i]);
                return String.valueOf(x);
            }

            return "";

        }

        public void vis() {
            sjult = false;
            setText(hentTekst());
            repaint();
        }
    }

}

