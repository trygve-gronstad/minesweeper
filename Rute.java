import java.util.*;
import java.awt.Polygon;

class Rute {

    private static final Map<Punkt, Collection<Rute>> SAMMENHENGENDE = new HashMap<>();
    private final MangeKant FIGUR;
    private boolean bombe, flagget, sjekket;
    private int tall = -1;
    
    public Rute(MangeKant poly) {
        FIGUR = poly;
        leggTil(poly);
        bombe = true;
        flagget = sjekket = false;
    }

    private void leggTil(MangeKant poly) {
        for (Punkt p: poly.hentPunkter()) {
            SAMMENHENGENDE.computeIfAbsent(p, k -> new HashSet<>()).add(this);
        }
    }

    public static void tellNaboer(Collection<Rute> ruter) {
        for (Rute r: ruter) {
            r.tellNaboer();
        }
    }

    public int tellNaboer() {
        tall = 0;
        for (Punkt p: FIGUR.hentPunkter()) {
            for (Rute nabo: SAMMENHENGENDE.get(p)) {
                if (nabo.harBombe()) {
                    tall++;
                }
            }
        }
        return tall;
    }

    public boolean harBombe() {
        return bombe;
    }

    public boolean flagg() {
        flagget = !flagget;
        return harFlagg();
    }

    public boolean harFlagg() {
        return flagget;
    }

    public int hentTall() {
        return tall;
    }

    public Polygon hentPolygon() {
        return new Polygon(FIGUR.xArr(), FIGUR.yArr(), FIGUR.size());
    }

    public Collection<Rute> sjekk() {
        Collection<Rute> r = new HashSet<>();
        sjekk(r);
        return r;
    }

    private boolean sjekk(Collection<Rute> retur) {
        if (flagget || sjekket) {
            return false;
        }

        sjekket = true;
        retur.add(this);

        if (bombe) {
            return true;
        }

        if (tall == 0) {
            for (Punkt p: FIGUR.hentPunkter()) {
                for (Rute nabo: SAMMENHENGENDE.get(p)) {
                    if (!nabo.sjekket) {
                        nabo.sjekk(retur);
                    }
                }
            }
        }

        return true;
    }

}
