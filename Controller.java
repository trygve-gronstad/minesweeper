import java.util.*;

import javax.imageio.ImageIO;

import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.awt.Font;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.awt.GraphicsEnvironment;

public class Controller {

    private static final Random RAND = new Random();

    public static final Font RUTE_FONT = lastInnFont("assets/ruteFont.ttf");
    public static final Font ANTALL_FONT = lastInnFont("assets/7segmentDisplay.ttf");
    public static final BufferedImage FLAGG = lesBilde("assets/flagg.png");
    public static final BufferedImage BOMBE = lesBilde("assets/bombe.png");
    public static final BufferedImage FEIL_FLAGG = lesBilde("assets/feilFlagg.png");
    public static final BufferedImage KUL = lesBilde("assets/kul.png");
    public static final BufferedImage VANLIG = lesBilde("assets/vanlig.png");
    public static final BufferedImage OVERRASKET = lesBilde("assets/overrasket.png");
    public static final BufferedImage DØ = lesBilde("assets/dø.png");

    private final List<Rute> alleRuter = new ArrayList<>();
    private final Collection<Rute> ikkeSjekketNaboer = new HashSet<>();
    private final View display;
    
    private Map<String, String[]> tekst = lesFil("assets/english.txt");
    private int antMiner, antallRuter, antallSjekket;
    private boolean sattUtBomber = false;

    public Controller() {
        display = new View(this);
        lagRuter();
        display.setBakgrunnsfarge();
    }

    public void lagRuter() {
        sattUtBomber = false;
        antallRuter = antallSjekket = 0;
        int vansklighetNr = display.hentVansklighetNr();
        switch (vansklighetNr) {
            case 0:
                antMiner = 5;
                break;
            case 1:
                antMiner = 20;
                break;
            case 2: 
                antMiner = 100;
                break;
            default:
                break;
        }
        PunktDistribusjon m = finnModell(vansklighetNr, display.hentModellNr());
        lagRuter(m);
        lagGuiRuter(m.estimertAvstand());
    }

    private PunktDistribusjon finnModell(int vansklighetNr, int modellNr) {
        switch (vansklighetNr) {
            case 0:
                return finnModellAntall(modellNr, 25);
            case 1:
                return finnModellAntall(modellNr, 100);
            case 2: 
                return finnModellAntall(modellNr, 250);
        }
        throw new IllegalArgumentException("Ikke gyldig modell, eller vansklighet");
    }

    private PunktDistribusjon finnModellAntall(int modellNr, int antall) {
        switch (modellNr) {
            case 0:
                return new RandomModell(antall, hentLengde(), hentHøyde());
            case 1:
                return new SpiralModell(antall, hentLengde(), hentHøyde());
            case 2: 
                return new GridModell(antall, hentLengde(), hentHøyde());
            case 3:
                return new HexModell(antall, hentLengde(), hentHøyde());
        }
        throw new IllegalArgumentException("Ikke gyldig modell");
    } 

    private void lagRuter(PunktDistribusjon m) {
        Rutenett fg = new Rutenett(m);
        Collection<MangeKant> poly = fg.finnMangeKant();
        alleRuter.clear();
        for (MangeKant p: poly) {
            alleRuter.add(new Rute(p, antallRuter++));
        }

        Rute.settNaboer(alleRuter);
        sattUtBomber = false;
    }

    private Polygon hentPolygon(Rute r) {
        return new Polygon(r.hentPolygonX(), r.hentPolygonY(), r.hentPolygonS());
    }

    private void lagGuiRuter(int avstand) {
        display.setAvstand(avstand);
        display.setVisible(false);
        for (int i = 0; i < alleRuter.size(); i++) {
            Rute r = alleRuter.get(i);
            display.leggTilKnapp(hentPolygon(r), r.hentSenterX(hentLengde()), r.hentSenterY(hentHøyde()));
        }
        display.setVisible(true);
        display.lagtTilKnapper();
        display.restart(antMiner);
    }

    public String hentTegn(int nr) {
        Rute r = alleRuter.get(nr);
        return String.valueOf(r.hentTall());
    }

    public boolean byttFlagg(int nr) {
        Rute r = alleRuter.get(nr);
        return r.flagg();
    }

    public void trykkKnapp(int nr) {
        if (!sattUtBomber) {
            sattUtBomber = true;

            Rute r1 = alleRuter.get(nr);
            List<Rute> resterendeRuter = new ArrayList<>(alleRuter);
            resterendeRuter.remove(r1.hentIndeks());
            resterendeRuter.removeAll(r1.hentNaboer());

            for (int i = 0; i < antMiner; i++) {
                Rute r2 = resterendeRuter.remove(RAND.nextInt(resterendeRuter.size()));
                r2.settMine();
            }

            Rute.tellNaboer(alleRuter);
            display.start();

        }

        Collection<Rute> sjekket = new HashSet<>();
        boolean tapt = alleRuter.get(nr).utvidetSjekk(sjekket);
        antallSjekket += sjekket.size();
        if (tapt) {
            display.slutt(false);
        }
        else if (vunnet()) {
            display.slutt(true);
        }
        display.setVisible(false);
        for (Rute r: sjekket) {
            display.vis(r.hentIndeks());
        }
        display.setVisible(true);
    }

    public boolean erMine(int nr) {
        return alleRuter.get(nr).harBombe();
    }

    public int hentForklaring(int nr) {
        return alleRuter.get(nr).hentTall();
    }

    public void markerNaboer(int nr) {
        Rute r = alleRuter.get(nr);
        for (Rute nabo: r.hentNaboer()) {
            if (!nabo.erSjekket()) {
                ikkeSjekketNaboer.add(nabo);
            }
        }
        marker(true);
    }

    public void fjernMarkering() {
        marker(false);
        ikkeSjekketNaboer.clear();
    }

    private void marker(boolean x) {
        display.setVisible(false);
        for (Rute nabo: ikkeSjekketNaboer) {
            display.markerKnapp(nabo.hentIndeks(), x);
        }
        display.setVisible(true);
    }

    public boolean erSjekket(int nr) {
        return alleRuter.get(nr).erSjekket();
    }

    public boolean erFlagget(int nr) {
        return alleRuter.get(nr).flagg();
    }

    public int hentLengde() {
        return display.hentLengde();
    }

    public int hentHøyde() {
        return display.hentHøyde();
    }

    public boolean vunnet() {
        return antallRuter - antallSjekket == antMiner;
    }

    public void sjekkFeil() {
        display.setVisible(false);
        for (Rute r: alleRuter) {
            if (r.harBombe() && !r.harFlagg()) {
                display.ikkeFlagget(r.hentIndeks());
            }
            else if (!r.harBombe() && r.harFlagg()) {
                display.feilFlagget(r.hentIndeks());
            }
        }
        display.setVisible(true);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i: new int[] {hentLengde(), hentHøyde(), display.hentVansklighetNr(), display.hentModellNr(), display.hentTid()}) {
            sb.append(i);
            sb.append(",");
        }
        
        sb.append("[");
        for (Rute r: alleRuter) {
            sb.append(r);
            sb.append(",");
        }
        sb.delete(sb.length() - 1, sb.length());
        sb.append("]");


        return sb.toString();
    }

    private static Map<String, String[]> lesFil(String filnavn) {
        Map<String, String[]> map = new HashMap<>();

        try {
            Scanner sc = new Scanner(new File(filnavn));
            while(sc.hasNextLine()) {
                String linje = sc.nextLine();
                String[] args = linje.split("=");
                map.put(args[0], args[1].split(";"));
            }
            sc.close();
        }
        catch (FileNotFoundException e) {
            System.exit(1);
        }
        return map;
    }

    public String[] hentTekst(String s) {
        return tekst.get(s);
    }

    private static Font lastInnFont(String filSti) {
        try {
            InputStream is = Controller.class.getResourceAsStream(filSti);
            Font font = Font.createFont(Font.TRUETYPE_FONT, is);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
            return font.deriveFont(Font.PLAIN, 24f);
        }
        catch (Exception e) {
            return new Font("SansSerif", Font.PLAIN, 14);
        }
    }

    private static BufferedImage lesBilde(String relativSti) {
        try {
            return ImageIO.read(new File(relativSti));
        }
        catch (IOException e) {
            return null;
        }
    }
    
}
