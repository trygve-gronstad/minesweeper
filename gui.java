import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.geom.Area;
import java.util.List;
import java.util.ArrayList;

class View {

    private final JFrame vindu = new JFrame("Minesweeper");

    private final JPanel rutePanel = new JPanel();
    private final JPanel panel = new JPanel(new BorderLayout());

    private final JComboBox<String> vansklighetValg = new JComboBox<>(new String[] {"Easy", "Medium", "Diffucalt"});
    private final JComboBox<String> modellValg = new JComboBox<>(new String[] {"Random", "Spiral", "Grid", "Hex"});

    private final JLabel antFlagg = new JLabel();
    private final JLabel tid = new JLabel();
    private final JButton restart = new JButton("😊");

    private final Controller CON;
    private final List<KantKnapp> alleKnapper = new ArrayList<>();
    private Dimension overflateStørrelse = new Dimension(1000, 800);
    private int flagg = 0;
    private Thread sekKlokke;
    private boolean kanSpille = false;

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

        vindu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel.setPreferredSize(overflateStørrelse);

        JPanel toppPanel = new JPanel();
        toppPanel.setLayout(new BoxLayout(toppPanel, BoxLayout.PAGE_AXIS));

        JPanel innstillingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        innstillingPanel.setBackground(Color.WHITE);
        innstillingPanel.setPreferredSize(new Dimension(overflateStørrelse.width, 35));
        vansklighetValg.setSelectedIndex(1);
        vansklighetValg.addActionListener(new ResetAction());
        modellValg.setSelectedIndex(1);
        modellValg.addActionListener(new ResetAction());
        innstillingPanel.add(vansklighetValg);
        innstillingPanel.add(modellValg);
        toppPanel.add(innstillingPanel);

        JPanel forklaringPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.weighty = 1;
        forklaringPanel.setBackground(Color.GRAY);
        forklaringPanel.setPreferredSize(new Dimension(overflateStørrelse.width, 50));
        restart.setPreferredSize(new Dimension(45, 45));
        restart.addActionListener(new ResetAction());
        restart.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        forklaringPanel.add(antFlagg, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        forklaringPanel.add(restart, gbc);
        gbc.gridx = 2;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        forklaringPanel.add(tid, gbc);
        forklaringPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        toppPanel.add(forklaringPanel);

        panel.add(toppPanel, BorderLayout.NORTH);

        rutePanel.setLayout(null);
        panel.add(rutePanel, BorderLayout.CENTER);

        vindu.add(panel);
        vindu.setPreferredSize(overflateStørrelse);

        vindu.pack();
        vindu.setLocationRelativeTo(null);
        vindu.setVisible(true);
    }

    public void leggTilKnapp(Polygon polygon, int tekstX, int tekstY) {
        KantKnapp k = new KantKnapp(polygon, tekstX, tekstY);
        alleKnapper.add(k);
        rutePanel.add(k);
    }

    public void lagtTilKnapper() {
        rutePanel.revalidate();
        rutePanel.repaint();

        vindu.revalidate();
        vindu.repaint();
    }

    public void start() {
        sekKlokke.start();
    }

    public void restart(int antBomber) {
        antFlagg.setText(String.format("%d 🚩", antBomber));
        tid.setText("tid: 0");
        sekKlokke = new Thread(new VenteTråd(1000));
        flagg = antBomber;
        restart.setText("😊");
        restart.setBackground(null);
        kanSpille = true;
    }

    public void slutt(boolean vunnet) {
        kanSpille = false;
        if (vunnet) {
            restart.setText("😎");
            restart.setBackground(Color.GREEN);
        }
        else {
            restart.setText("😵");
            restart.setBackground(Color.RED);
        }
        sekKlokke.interrupt();
    }

    public void vis(int i) {
        alleKnapper.get(i).vis();
    }

    public Dimension hentDimensjon() {
        return overflateStørrelse;
    }

    private class KantKnapp extends JButton {

        private static int ANTALL = 0;
        private static final Font FONT = new Font("Display", Font.BOLD, 15);
        private static final Color[] FARGER = new Color[] {Color.BLUE, Color.GREEN, Color.RED, Color.MAGENTA, Color.ORANGE, Color.CYAN, Color.BLACK, Color.WHITE};

        private final Polygon polygon;
        //private final Area omeråde;
        private final int indeks, tekstX, tekstY;
        private boolean sjult = true;

        public KantKnapp(Polygon polygon, int tekstX, int tekstY) {
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setBounds(polygon.getBounds());
            //setFont(FONT);

            this.polygon = polygon;
            // omeråde = new Area(polygon);
            // omeråde.intersect(new Area(RECT));
            indeks = ANTALL++;
            this.tekstX = tekstX;
            this.tekstY = tekstY;

            addMouseListener(new MouseAdapter(){
                @Override
                public void mousePressed(MouseEvent e) {
                    if (kanSpille) {
                        if (SwingUtilities.isLeftMouseButton(e)) {
                            CON.trykkKnapp(indeks);
                        } 
                        else if (SwingUtilities.isRightMouseButton(e)) {
                            if (sjult) {
                                if (CON.byttFlagg(indeks)) {
                                    setText("🚩");
                                    flagg--;
                                }
                                else {
                                    setText("");
                                    flagg++;
                                }
                                antFlagg.setText(String.format("%d 🚩", flagg));
                                repaint();
                            }
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
            g2.fillPolygon(polygon); //g2.fill(omeråde);

            g2.setColor(Color.BLACK);
            g2.drawPolygon(polygon); //g2.draw(omeråde);

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
            return polygon.contains(x + getX(), y + getY());
        }

        private String hentTekst() {
            if (CON.erMine(indeks)) {
                return "💣";
            }
            int x = CON.hentForklaring(indeks);

            if (x > 0) {
                int i = Math.min(7, x-1);
                setForeground(FARGER[i]);
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

    private class VenteTråd implements Runnable {
        private final int dt;

        public VenteTråd(int dt) {
            this.dt = dt;
        }

        @Override
        public void run() {
            int t = 0;
            try {
                while (true) {
                    Thread.sleep(dt);
                    tid.setText(String.format("tid: %d", ++t));
                }
            }
            catch (InterruptedException e) {}
        }
    }

    private class ResetAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            kanSpille = false;
            sekKlokke.interrupt();
            alleKnapper.clear();
            KantKnapp.ANTALL = 0;
            rutePanel.removeAll();
            //CON.lagRuter(vansklighetValg.getSelectedItem(), modellValg.getSelectedItem());
            CON.lagRuter();
        }
    }

}

