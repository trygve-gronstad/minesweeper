import java.util.*;
import java.awt.Polygon;
import java.io.FileNotFoundException;
import java.io.File;

public class Controller {
    
    private Map<String, String[]> tekst = lesFil("assets/english.txt");
    private final List<Rute> alleRuter = new ArrayList<>();
    private int antMiner, antallRuter, antallSjekket;
    private final View DISPLAY = new View(this);
    private boolean sattUtBomber = false;
    private static final Random RAND = new Random();
    private final Collection<Rute> ikkeSjekketNaboer = new HashSet<>();

    public Controller() {
        lagRuter(DISPLAY.hentVansklighetNr(), DISPLAY.hentModellNr());
        lagGuiRuter();
    }

    public void lagRuter(int vansklighetNr, int modellNr) {
        sattUtBomber = false;
        antallRuter = antallSjekket = 0;
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
        PunktDistribusjon m = finnModell(vansklighetNr, modellNr);
        lagRuter(m);
        DISPLAY.setAvstand(m.estimertAvstand());
        lagGuiRuter();
    }

    private PunktDistribusjon finnModell(int vansklighetNr, int modellNr) {
        switch (vansklighetNr) {
            case 0:
                return finnModell(modellNr, 25, 50);
            case 1:
                return finnModell(modellNr, 100, 50);
            case 2: 
                return finnModell(modellNr, 250, 50);
        }
        throw new IllegalArgumentException("Ikke gyldig modell, eller vansklighet");
    }

    private PunktDistribusjon finnModell(int modellNr, int... args) {
        switch (modellNr) {
            case 0:
                return new RandomModell(args[0], hentLengde(), hentHøyde(), args[1]);
            case 1:
                return new SpiralModell(args[0], hentLengde(), hentHøyde(), args[1]);
            case 2: 
                return new GridModell(args[0], hentLengde(), hentHøyde(), args[1]);
            case 3:
                return new HexModell(args[0], hentLengde(), hentHøyde(), args[1]);
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

    private void lagGuiRuter() {
        for (int i = 0; i < alleRuter.size(); i++) {
            Rute r = alleRuter.get(i);
            DISPLAY.leggTilKnapp(hentPolygon(r), r.hentSenterX(hentLengde()), r.hentSenterY(hentHøyde()));
        }
        DISPLAY.lagtTilKnapper();
        DISPLAY.restart(antMiner);
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
            DISPLAY.start();

        }

        Collection<Rute> sjekket = new HashSet<>();
        boolean tapt = alleRuter.get(nr).utvidetSjekk(sjekket);
        antallSjekket += sjekket.size();
        if (tapt) {
            DISPLAY.slutt(false);
        }
        else if (vunnet()) {
            DISPLAY.slutt(true);
        }
        for (Rute r: sjekket) {
            DISPLAY.vis(r.hentIndeks());
        }
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
        for (Rute nabo: ikkeSjekketNaboer) {
            DISPLAY.markerKnapp(nabo.hentIndeks(), x);
        }
    }

    public boolean erSjekket(int nr) {
        return alleRuter.get(nr).erSjekket();
    }

    public boolean erFlagget(int nr) {
        return alleRuter.get(nr).flagg();
    }

    public int hentLengde() {
        return DISPLAY.hentLengde();
    }

    public int hentHøyde() {
        return DISPLAY.hentHøyde();
    }

    public boolean vunnet() {
        return antallRuter - antallSjekket == antMiner;
    }

    public void sjekkFeil() {
        for (Rute r: alleRuter) {
            if (r.harBombe() && !r.harFlagg()) {
                DISPLAY.ikkeFlagget(r.hentIndeks());
            }
            else if (!r.harBombe() && r.harFlagg()) {
                DISPLAY.feilFlagget(r.hentIndeks());
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i: new int[] {hentLengde(), hentHøyde(), DISPLAY.hentVansklighetNr(), DISPLAY.hentModellNr(), DISPLAY.hentTid()}) {
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
    
}
