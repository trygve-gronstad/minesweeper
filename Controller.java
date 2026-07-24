import java.util.*;
import java.awt.Polygon;

public class Controller {
    
    private final List<Rute> alleRuter = new ArrayList<>();
    private int antMiner;
    private final View DISPLAY = new View(this);
    private boolean sattUtBomber = false;
    private static final Random RAND = new Random();

    public Controller(PunktDistribusjon m, int antMiner) {
        this.antMiner = antMiner;
        lagRuter(m);
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
                switch (modellNr) {
                    case 0:
                        return new RandomModell(25, 500, 500);
                    case 1:
                        return new SpiralModell(25, 500, 500);
                    case 2: 
                        return new GridModell(5, 5, 100);
                    case 3:
                        return new HexModell(5, 5, 100);
                }
            case 1:
                switch (modellNr) {
                    case 0:
                        return new RandomModell(100, 500, 500);
                    case 1:
                        return new SpiralModell(100, 500, 500);
                    case 2: 
                        return new GridModell(10, 10, 50);
                    case 3:
                        return new HexModell(10, 10, 50);
                }
            case 2: 
                switch (modellNr) {
                    case 0:
                        return new RandomModell(250, 500, 500);
                    case 1:
                        return new SpiralModell(250, 500, 500);
                    case 2: 
                        return new GridModell(16, 16, 32);
                    case 3:
                        return new HexModell(16, 16, 32);
                }
        }
        throw new IllegalArgumentException("Ikke gyldig modell, eller vansklighet");
    }

    private PunktDistribusjon finnModell(int modellNr, int... args) {
        switch (modellNr) {
            case 0:
                return new RandomModell(args[0], args[1], args[2]);
            case 1:
                return new SpiralModell(args[0], args[1], args[2]);
            case 2: 
                return new GridModell(args[0], args[1], args[2]);
            case 3:
                return new HexModell(args[0], args[1], args[2]);
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
    
}
