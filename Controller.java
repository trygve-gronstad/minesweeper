import java.util.*;
import java.awt.Polygon;

public class Controller {
    
    private final List<Rute> alleRuter = new ArrayList<>();
    private int antMiner;
    private final View DISPLAY = new View(this);
    private boolean sattUtBomber = false;
    private static final Random RAND = new Random();
    private final Collection<Rute> ikkeSjekketNaboer = new HashSet<>();

    public Controller(PunktDistribusjon m, int antMiner) {
        this.antMiner = antMiner;
        lagRuter(m);
        lagGuiRuter();
    }

    public Controller() {
        lagRuter(1, 1);
        lagGuiRuter();
    }

    public void lagRuter(int vansklighetNr, int modellNr) {
        sattUtBomber = false;
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
        Rute.reset();
        lagRuter(finnModell(vansklighetNr, modellNr));
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
                return new RandomModell(args[0], DISPLAY.hentLengde(), DISPLAY.hentHøyde(), args[1]);
            case 1:
                return new SpiralModell(args[0], DISPLAY.hentLengde(), DISPLAY.hentHøyde(), args[1]);
            case 2: 
                return new GridModell(args[0], DISPLAY.hentLengde(), DISPLAY.hentHøyde(), args[1]);
            case 3:
                return new HexModell(args[0], DISPLAY.hentLengde(), DISPLAY.hentHøyde(), args[1]);
        }
        throw new IllegalArgumentException("Ikke gyldig modell");
    } 

    private void lagRuter(PunktDistribusjon m) {
        Rutenett fg = new Rutenett(m);
        Collection<MangeKant> poly = fg.finnMangeKant();
        alleRuter.clear();
        for (MangeKant p: poly) {
            alleRuter.add(new Rute(p));
        }

        Rute.settNaboer();
        sattUtBomber = false;
    }

    private Polygon hentPolygon(Rute r) {
        return new Polygon(r.hentPolygonX(), r.hentPolygonY(), r.hentPolygonS());
    }

    private void lagGuiRuter() {
        for (int i = 0; i < alleRuter.size(); i++) {
            Rute r = alleRuter.get(i);
            DISPLAY.leggTilKnapp(hentPolygon(r), r.hentSenterX(), r.hentSenterY());
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
        if (alleRuter.get(nr).utvidetSjekk(sjekket)) {
            DISPLAY.slutt(false);
        }
        else if (Rute.vunnet(antMiner)) {
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
    
}
