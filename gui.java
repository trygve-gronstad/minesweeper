import java.awt.*;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.Border;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.ArrayList;

class View {

    private final static int KANT_AVSTAND = 10;
    private final static int INFO_AVSTAND = 5;
    private final static Dimension MIN_STØRRELSE = new Dimension(250, 250);
    private final static Dimension OVERFLATE_STØRRELSE = finnOverflateDimensjon();
    private final static Color GRÅ = new Color(170, 170, 170);

    private final JFrame vindu = new JFrame();
    private final JPanel rutePanel = new JPanel();

    private final JComboBox<String> vansklighetValg = new JComboBox<>();
    private final JComboBox<String> modellValg = new JComboBox<>();

    private final TallDisplay antFlagg = new TallDisplay(2);
    private final TallDisplay tid = new TallDisplay(3);
    private final ResetKnapp restart = new ResetKnapp();

    private final Controller controller;
    private final List<KantKnapp> alleKnapper = new ArrayList<>();
    private final VenteTråd sekRunneble = new VenteTråd(1000);

    private int flagg = 0;
    private Thread sekKlokke;
    private boolean kanSpille, kanFlagge;

    public View(Controller c) {
        controller = c;

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
        panel.setPreferredSize(OVERFLATE_STØRRELSE);

        JPanel toppPanel = new JPanel();
        toppPanel.setLayout(new BoxLayout(toppPanel, BoxLayout.PAGE_AXIS));

        JPanel innstillingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        innstillingPanel.setBackground(Color.WHITE);
        vansklighetValg.setSelectedIndex(valg1);
        ActionListener resetFunksjonalitet = new ResetAction();
        vansklighetValg.addActionListener(resetFunksjonalitet);
        modellValg.setSelectedIndex(valg2);
        modellValg.addActionListener(resetFunksjonalitet);
        innstillingPanel.add(vansklighetValg);
        innstillingPanel.add(modellValg);
        JButton info = new JButton("ⓘ");
        info.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "tekst", "info", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        innstillingPanel.add(info);
        toppPanel.add(innstillingPanel);

        JPanel forklaringPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.weighty = 1;
        forklaringPanel.setBackground(GRÅ);
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
        Border senket = new BevelBorder(BevelBorder.LOWERED);
        Border b = BorderFactory.createCompoundBorder(senket, new EmptyBorder(INFO_AVSTAND - 1, INFO_AVSTAND, INFO_AVSTAND - 1, INFO_AVSTAND));
        b = BorderFactory.createCompoundBorder(new EmptyBorder(KANT_AVSTAND, KANT_AVSTAND, KANT_AVSTAND, KANT_AVSTAND), b);
        forklaringPanel.setBorder(b);
        toppPanel.add(forklaringPanel);

        panel.add(toppPanel, BorderLayout.NORTH);

        rutePanel.setLayout(null);
        rutePanel.setBackground(GRÅ);
        JPanel hovedPanel = new JPanel(new BorderLayout());
        hovedPanel.add(rutePanel, BorderLayout.CENTER);
        hovedPanel.setBackground(GRÅ);
        hovedPanel.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(0, KANT_AVSTAND, KANT_AVSTAND, KANT_AVSTAND), senket));
        panel.add(hovedPanel, BorderLayout.CENTER);

        vindu.add(panel);
        vindu.setMinimumSize(MIN_STØRRELSE);
        vindu.setIconImage(Controller.BOMBE);

        vindu.pack();
        vindu.setLocationRelativeTo(null);
        //vindu.setVisible(true);
    }

    private void setTekst() {
        vindu.setTitle(controller.hentTekst("vindu")[0]);
        setTekst(vansklighetValg, "vansklighetValg");
        setTekst(modellValg, "modellValg");
    }

    private void setTekst(JComboBox<String> box, String varNavn) {
        box.removeAllItems();
        for (String s: controller.hentTekst(varNavn)) {
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
    }

    public void start() {
        sekKlokke.start();
        kanFlagge = true;
    }

    public void restart(int antBomber) {
        antFlagg.setSiffer(String.valueOf(antBomber).length());
        antFlagg.setText(antBomber);
        tid.setText(0);
        sekRunneble.restart();
        sekKlokke = new Thread(sekRunneble);
        flagg = antBomber;
        restart.settVanlig();
        kanFlagge = false;
        kanSpille = true;
    }

    public void slutt(boolean vunnet) {
        kanSpille = false;
        sekKlokke.interrupt();
        if (vunnet) {
            restart.settVunnet();
        }
        else {
            restart.settTap();
            controller.sjekkFeil();
        }
    }

    public void vis(int i) {
        alleKnapper.get(i).vis();
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

    private static Dimension finnOverflateDimensjon() {
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) (Math.min(d.width, d.height) * 0.8);
        return new Dimension(Math.max(x, MIN_STØRRELSE.width), Math.max(x, MIN_STØRRELSE.height));
    }

    public void setBakgrunnsfarge() {
        rutePanel.setBackground(Color.GRAY);
        vindu.setVisible(true);
    }

    public void setVisible(boolean b) {
        rutePanel.setVisible(b);
    }

    public void setAvstand(int i) {
        KantKnapp.tykkPensel = new BasicStroke(Math.max(3, i/5f));
        KantKnapp.font = Controller.RUTE_FONT.deriveFont(Font.BOLD, Math.max(10, i/3f));
        LineMetrics lm = KantKnapp.font.getLineMetrics("1", new FontRenderContext(null, true, true));
        KantKnapp.yOffset = Math.round((lm.getAscent() - lm.getDescent()) / 2f);
    }

    private class KantKnapp extends JButton {

        private static final Color[] FARGER = new Color[] {Color.BLUE, new Color(0, 126, 0), Color.RED, new Color(178, 0, 255), new Color(255, 216, 0), new Color(3, 127, 127), new Color(137, 82, 0), Color.WHITE};
        private static final BasicStroke TYNN_PENSEL = new BasicStroke(2);

        private static int antall = 0;
        private static Font font;
        private static BasicStroke tykkPensel;
        private static int yOffset;

        private final Polygon polygon;
        //private final Area omeråde;
        private final int indeks, tekstX, tekstY;
        private boolean sjult = true;
        private String tekst;
        private BufferedImage bilde;
        private boolean visOmkrets = false;

        public KantKnapp(Polygon polygon, int tekstX, int tekstY) {
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setBackground(Color.LIGHT_GRAY);
            setForeground(Color.BLACK);
            setFont(font);

            Rectangle omkrets = polygon.getBounds();
            setBounds(
                omkrets.x - 50,
                omkrets.y - 50,
                omkrets.width + 100,
                omkrets.height + 100
            );

            this.polygon = polygon;
            indeks = antall++;
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
                                if (controller.byttFlagg(indeks)) {
                                    flagg--;
                                    setBilde(Controller.FLAGG);
                                }
                                else {
                                    flagg++;
                                    setBilde(null);
                                }
                                antFlagg.setText(flagg);
                                repaint();
                            }
                            flagger = true;
                        }
                        else if (!flagger) {
                            controller.markerNaboer(indeks);
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
                        controller.fjernMarkering();
                        controller.markerNaboer(ny.indeks);
                    }
                    forrige = ny;
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (!kanSpille) return;

                    if (SwingUtilities.isLeftMouseButton(e)) {
                        restart.settVanlig();
                        if (forrige.sjult) forrige.marker(false);
                        controller.trykkKnapp(forrige.indeks);
                    } 
                    else if (SwingUtilities.isRightMouseButton(e)) {
                        controller.fjernMarkering();
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

                g2.setStroke(tykkPensel);
                g2.setColor(getForeground().darker());
                g2.drawPolygon(polygon);

                g2.setStroke(TYNN_PENSEL);
                g2.clip(omeråde);
            }
            g2.setColor(Color.DARK_GRAY);
            g2.drawPolygon(polygon);

            if (tekst != null) {
                g2.setColor(getForeground());
                FontMetrics m = g2.getFontMetrics(font);
                g2.drawString(tekst, tekstX - (m.stringWidth(tekst) / 2), tekstY + yOffset);
            }
            else if (bilde != null) {
                int dx = yOffset * 2;
                g2.drawImage(bilde, tekstX - yOffset, tekstY - yOffset, dx, dx, this);
            }

            g2.translate(getX(), getY());
            g2.dispose();
        }

        @Override
        public boolean contains(int x, int y) {
            return polygon.contains(x + getX(), y + getY());
        }

        private String hentTekst() {
            if (controller.erMine(indeks)) {
                setBilde(Controller.BOMBE);
                return null;
            }
            int x = controller.hentForklaring(indeks);

            if (x > 0) {
                int i = Math.min(7, x-1);
                setForeground(FARGER[i]);
                visOmkrets = true;
                return String.valueOf(x);
            }

            return null;

        }

        public void vis() {
            sjult = false;
            setText(hentTekst());

            if (controller.erMine(indeks)) {
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

        public void setBilde(BufferedImage i) {
            bilde = i;
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
            setText(null);
            setBilde(Controller.FEIL_FLAGG);
            setBackground(GRÅ);
            marker(true);
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
                    tid.setText(++t);
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
        private static final ImageIcon VANLIG = finnIcon(Controller.VANLIG);
        private static final ImageIcon OVERRASKET = finnIcon(Controller.OVERRASKET);
        private static final ImageIcon DØ = finnIcon(Controller.DØ);
        private static final ImageIcon KUL = finnIcon(Controller.KUL);

        public ResetKnapp() {
            setPreferredSize(new Dimension(40, 40));
            setBackground(GRÅ);

            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    resett();
                }
            });
        }

        public void settVanlig() {
            setIcon(VANLIG);
        }

        public void settAventende() {
            setIcon(OVERRASKET);
        }

        public void settTap() {
            setIcon(DØ);
        }

        public void settVunnet() {
            setIcon(KUL);
        }

        public void resett() {
            kanSpille = false;
            sekKlokke.interrupt();
            alleKnapper.clear();
            KantKnapp.antall = 0;
            rutePanel.removeAll();
            controller.lagRuter();
        }

        private static ImageIcon finnIcon(BufferedImage bilde) {
            return new ImageIcon(bilde.getScaledInstance(36, 36, Image.SCALE_SMOOTH));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(getBackground());
            boolean hevet = !getModel().isPressed();
            g2.fill3DRect(0, 0, getWidth(), getHeight(), hevet);

            Icon ikon = getIcon();
            if (ikon != null) {
                int x = (getWidth() - ikon.getIconWidth()) / 2;
                int y = (getHeight() - ikon.getIconHeight()) / 2;
                if (!hevet) {
                    x++;
                    y++;
                }
                ikon.paintIcon(this, g2, x, y);
            }

            g2.dispose();
        }

    }

    class TallDisplay extends JLabel {
        private static final Color AV_FARGE = new Color(96, 0, 0);
        private static final Font FONT = Controller.ANTALL_FONT.deriveFont(Font.BOLD, 18);
        private static final Border KANT = new EmptyBorder(2, 3, 2, 1);

        private int antSiffer;
        private int maks;

        public TallDisplay(int antSiffer) {
            setSiffer(antSiffer);

            setFont(FONT);
            setForeground(AV_FARGE);
            setBackground(Color.BLACK);
            setOpaque(true);
            setBorder(KANT);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            FontMetrics m = g2.getFontMetrics(getFont());
            Insets i = getInsets();

            int x = i.left;
            int y = i.top + m.getAscent();

            g2.drawString(" ".repeat(antSiffer), x, y);
            if (getText() != null) {
                g2.setColor(Color.RED);
                g2.drawString(getText(), x, y);
            }

            g2.dispose();
        }

        public void setText(int i) {
            setText(String.format("%0" + antSiffer + "d", Math.min(maks, Math.max(0, i))));
        }

        public void setSiffer(int antSiffer) {
            this.antSiffer = antSiffer;
            maks = Integer.parseInt("9".repeat(antSiffer));
        } 

    }

}

