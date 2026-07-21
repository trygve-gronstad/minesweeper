import java.util.*;
import java.awt.Polygon;

public class Controller {
    
    private List<Rute> alleRuter = new ArrayList<>();
    private int antMiner;
    private final View DISPLAY = new View(this);
    private boolean sattUtBomber = false;
    private final Random RAND = new Random();

    public Controller(PunktDistribusjon m, int antMiner) {
        this.antMiner = antMiner;
        lagRuter(m);
        lagGuiRuter();
    }

    private void lagRuter(PunktDistribusjon m) {
        Rutenett fg = new Rutenett(m);
        Collection<MangeKant> poly = fg.finnMangeKant();
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
