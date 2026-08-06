import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

class View {

    private final JFrame vindu = new JFrame();

    private final JPanel rutePanel = new JPanel();

    private final JComboBox<String> vansklighetValg = new JComboBox<>();
    private final JComboBox<String> modellValg = new JComboBox<>();

    private final JPopupMenu popUp = new JPopupMenu();

    private final JLabel antFlagg = new JLabel();
    private final JLabel tid = new JLabel();
    private final ResetKnapp restart = new ResetKnapp();

    private final Controller CON;
    private final List<KantKnapp> alleKnapper = new ArrayList<>();
    private Dimension overflateStørrelse = new Dimension(1000, 800);
    private int flagg = 0;
    private final VenteTråd sekRunneble = new VenteTråd(1000);
    private Thread sekKlokke;
    private boolean kanSpille, kanFlagge;
    private final static Color GRÅ = new Color(170, 170, 170);

    public View(Controller c) {
        CON = c;

        init(1, 0);
    }

    private void init(int valg1, int valg2) {

        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        }
        catch (Exception e) {
            System.exit(1);
        }

        setTekst();
        vindu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(overflateStørrelse);

        JPanel toppPanel = new JPanel();
        toppPanel.setLayout(new BoxLayout(toppPanel, BoxLayout.PAGE_AXIS));

        JPanel innstillingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        innstillingPanel.setBackground(Color.WHITE);
        innstillingPanel.setPreferredSize(new Dimension(overflateStørrelse.width, 35));
        vansklighetValg.setSelectedIndex(valg1);
        ActionListener resetFunksjonalitet = new ResetAction();
        vansklighetValg.addActionListener(resetFunksjonalitet);
        modellValg.setSelectedIndex(valg2);
        modellValg.addActionListener(resetFunksjonalitet);
        innstillingPanel.add(vansklighetValg);
        innstillingPanel.add(modellValg);
        JButton info = new JButton("ⓘ");
        popUp.setPreferredSize(new Dimension(100, 100));
        info.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                popUp.show(info, 0, 50);
            }
        });
        innstillingPanel.add(info);
        toppPanel.add(innstillingPanel);

        JPanel forklaringPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.weighty = 1;
        forklaringPanel.setBackground(GRÅ);
        forklaringPanel.setPreferredSize(new Dimension(overflateStørrelse.width, 50));
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
        forklaringPanel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
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

    private void setTekst() {
        vindu.setTitle(CON.hentTekst("vindu")[0]);
        setTekst(vansklighetValg, "vansklighetValg");
        setTekst(modellValg, "modellValg");
    }

    private void setTekst(JComboBox<String> box, String varNavn) {
        box.removeAllItems();
        for (String s: CON.hentTekst(varNavn)) {
            box.addItem(s);
        }
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
        kanFlagge = true;
    }

    public void restart(int antBomber) {
        antFlagg.setText(String.format(" %d 🚩", antBomber));
        tid.setText("tid: 0 ");
        sekRunneble.restart();
        sekKlokke = new Thread(sekRunneble);
        flagg = antBomber;
        restart.settVanlig();
        kanFlagge = false;
        kanSpille = true;
    }

    public void slutt(boolean vunnet) {
        kanSpille = false;
        if (vunnet) {
            restart.settVunnet();
        }
        else {
            restart.settTap();
            CON.sjekkFeil();
        }
        sekKlokke.interrupt();
    }

    public void vis(int i) {
        alleKnapper.get(i).vis();
    }

    public Dimension hentDimensjon() {
        return overflateStørrelse;
    }

    public void markerKnapp(int indeks, boolean marker) {
        alleKnapper.get(indeks).marker(marker);;
    }

    public int hentLengde() {
        return rutePanel.getWidth();
    }

    public int hentHøyde() {
        return rutePanel.getHeight();
    }

    public int hentVansklighetNr() {
        return vansklighetValg.getSelectedIndex();
    }

    public int hentModellNr() {
        return modellValg.getSelectedIndex();
    }

    public void feilFlagget(int i) {
        alleKnapper.get(i).feilFlagget();
    }

    public void ikkeFlagget(int i) {
        alleKnapper.get(i).ikkeFlagget();
    }

    public int hentTid() {
        return sekRunneble.t;
    }

    public void setAvstand(int i) {
        KantKnapp.avstand = (int) Math.round(i / 10.0);
    }

    private class KantKnapp extends JButton {

        private static int ANTALL = 0;
        private static final Font FONT = new Font("Display", Font.BOLD, 15);
        private static final Color[] FARGER = new Color[] {Color.BLUE, new Color(0, 126, 0), Color.RED, new Color(178, 0, 255), new Color(255, 216, 0), new Color(3, 127, 127), new Color(137, 82, 0), Color.WHITE};
        private static int avstand;

        private final Polygon polygon;
        //private final Area omeråde;
        private final int indeks, tekstX, tekstY;
        private boolean sjult = true;
        private String tekst;
        private boolean visOmkrets = false;

        public KantKnapp(Polygon polygon, int tekstX, int tekstY) {
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setBackground(Color.LIGHT_GRAY);
            setForeground(Color.DARK_GRAY);

            Rectangle omkrets = polygon.getBounds();
            setBounds(
                omkrets.x - 50,
                omkrets.y - 50,
                omkrets.width + 100,
                omkrets.height + 100
            );
            //setFont(FONT);

            this.polygon = polygon;
            // omeråde = new Area(polygon);
            // omeråde.intersect(new Area(RECT));
            indeks = ANTALL++;
            this.tekstX = tekstX;
            this.tekstY = tekstY;

            MouseAdapter mus = new MouseAdapter(){
                KantKnapp forrige;
                boolean flagger = false;

                @Override
                public void mousePressed(MouseEvent e) {
                    if (!kanSpille) return;

                    if (SwingUtilities.isLeftMouseButton(e)) {
                        if (sjult) marker(true);
                        restart.settAventende();
                    } 
                    else if (SwingUtilities.isRightMouseButton(e)) {
                        if (sjult) {
                            if (kanFlagge) {
                                if (CON.byttFlagg(indeks)) {
                                    setText("🚩");
                                    flagg--;
                                }
                                else {
                                    setText("");
                                    flagg++;
                                }
                                antFlagg.setText(String.format(" %d 🚩", flagg));
                                repaint();
                            }
                            flagger = true;
                        }
                        else if (!flagger) {
                            CON.markerNaboer(indeks);
                        }
                    }
                    forrige = finnKnappUnderPeker(e);
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    if (!kanSpille) return;
                    
                    KantKnapp ny = finnKnappUnderPeker(e);
                    if (ny == null || ny == forrige) return;

                    if (SwingUtilities.isLeftMouseButton(e)) {
                        if (forrige.sjult) forrige.marker(false);
                        if (ny.sjult) ny.marker(true);
                    } 
                    else if (SwingUtilities.isRightMouseButton(e) && !flagger) {
                        CON.fjernMarkering();
                        CON.markerNaboer(ny.indeks);
                    }
                    forrige = ny;
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (!kanSpille) return;

                    if (SwingUtilities.isLeftMouseButton(e)) {
                        restart.settVanlig();
                        if (forrige.sjult) forrige.marker(false);
                        CON.trykkKnapp(forrige.indeks);
                    } 
                    else if (SwingUtilities.isRightMouseButton(e)) {
                        CON.fjernMarkering();
                    }
                    flagger = false;
                }
            };
            addMouseListener(mus);
            addMouseMotionListener(mus);
        }

        public void marker(boolean visOmkrets) {
            this.visOmkrets = visOmkrets;
            repaint();
        }

        private KantKnapp finnKnappUnderPeker(MouseEvent e) {
            Component startKilde = e.getComponent();
            Container rutenett = startKilde.getParent();
            Point punktIRutenett = SwingUtilities.convertPoint(startKilde, e.getPoint(), rutenett);
            Component treff = rutenett.getComponentAt(punktIRutenett);

            if (treff instanceof KantKnapp) {
                return (KantKnapp) treff;
            }
            return null;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.translate(-getX(), -getY());

            g2.setColor(getBackground());
            g2.fillPolygon(polygon); //g2.fill(omeråde);

            if (visOmkrets) {
                Shape omeråde = g2.getClip();
                g2.clip(polygon);

                g2.setStroke(new BasicStroke(Math.max(3, avstand)));
                g2.setColor(getForeground());
                g2.drawPolygon(polygon);

                g2.setStroke(new BasicStroke(2));
                g2.clip(omeråde);
            }
            g2.setColor(Color.DARK_GRAY);
            g2.drawPolygon(polygon);

            if (tekst != null) {
                g2.setColor(getForeground());
                g2.drawString(tekst, tekstX, tekstY);
            }

            g2.translate(getX(), getY());
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
                visOmkrets = true;
                return String.valueOf(x);
            }

            return "";

        }

        public void vis() {
            sjult = false;
            setText(hentTekst());

            if (CON.erMine(indeks)) {
                marker(true);
                setBackground(Color.RED);
            }
            else {
                setBackground(GRÅ);
            }

            repaint();
        }

        @Override
        public void setText(String s) {
            tekst = s;
        }

        @Override
        public String getText() {
            return tekst;
        }

        @Override
        public void setBackground(Color bg) {
            if (bg == null) {
                if (!sjult) {
                    super.setBackground(GRÅ);
                } else {
                    super.setBackground(Color.LIGHT_GRAY);
                }
            }
            else {
                super.setBackground(bg);
            }
        }

        public void feilFlagget() {
            setText("X");
            setForeground(Color.RED);
        }

        public void ikkeFlagget() {
            vis();
            setBackground(GRÅ);
        }
    }

    private class VenteTråd implements Runnable {
        private final int dt;
        private int t = 0;

        public VenteTråd(int dt) {
            this.dt = dt;
        }

        public void restart() {
            t = 0;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    Thread.sleep(dt);
                    tid.setText(String.format("tid: %d ", ++t));
                }
            }
            catch (InterruptedException e) {}
        }
    }

    private class ResetAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            restart.resett();
        }
    }

    private class ResetKnapp extends JButton {
        private static String vanlig = "😊";
        private static String overrasket = "😮";
        private static String dø = "😵";
        private static String glad = "😎";

        public ResetKnapp() {
            setFont(new Font("Dialog", Font.BOLD, 30));
            setPreferredSize(new Dimension(45, 45));
            setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    resett();
                }
            });
        }

        public void settVanlig() {
            markerRestert(vanlig, null);
        }

        public void settAventende() {
            markerRestert(overrasket, Color.YELLOW);
        }

        public void settTap() {
            markerRestert(dø, Color.RED);
        }

        public void settVunnet() {
            markerRestert(glad, Color.GREEN);
        }

        public void resett() {
            kanSpille = false;
            sekKlokke.interrupt();
            alleKnapper.clear();
            KantKnapp.ANTALL = 0;
            rutePanel.removeAll();
            CON.lagRuter(vansklighetValg.getSelectedIndex(), modellValg.getSelectedIndex());
        }

        private void markerRestert(String emoji, Color c) {
            restart.setText(emoji);
            restart.setBackground(c);
        }

    }

}

