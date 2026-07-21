import java.util.*;

class Rute {

    private static final Map<Punkt, Collection<Rute>> SAMMENHENGENDE = new HashMap<>();
    private static int antall = 0;
    private static int antSjekket = 0;

    private final Collection<Rute> naboer = new HashSet<>();
    private final MangeKant FIGUR;
    private boolean bombe, flagget, sjekket;
    private int tall = -1;
    private final int indeks = antall++;
    
    public Rute(MangeKant poly) {
        FIGUR = poly;
        leggTil(poly);
        bombe = false;
        flagget = sjekket = false;
    }

    public static void reset() {
        SAMMENHENGENDE.clear();
        antall = antSjekket = 0;
    }

    public static boolean vunnet(int antBomber) {
        return antall - antSjekket == antBomber;
    }

    public static void settNaboer() {
        for (Punkt p: SAMMENHENGENDE.keySet()) {
            Collection<Rute> ruter = SAMMENHENGENDE.get(p);
            for (Rute nabo: ruter) {
                nabo.leggTilNabo(ruter);
            }
        }
    }

    private void leggTilNabo(Collection<Rute> ruter) {
        naboer.addAll(ruter);
        naboer.remove(this);
    }

    public int hentIndeks() {
        return indeks;
    }

    public Collection<Rute> hentNaboer() {
        return naboer;
    }

    public void settMine() {
        bombe = true;
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
        for (Rute nabo: naboer) {
            if (nabo.harBombe()) {
                tall++;
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

    public int[] hentPolygonX() {
        return FIGUR.xArr();
    }

    public int[] hentPolygonY() {
        return FIGUR.yArr();
    }

    public int hentPolygonS() {
        return FIGUR.size();
    }

    public Collection<Rute> sjekk() {
        Collection<Rute> r = new HashSet<>();
        sjekk(r);
        return r;
    }

    public int hentSenterX() {
        return (int) FIGUR.hentSentrum().x;
    }

    public int hentSenterY() {
        return (int) FIGUR.hentSentrum().y;
    }

    public boolean utvidetSjekk(Collection<Rute> retur) {
        if (sjekket) {
            boolean r = false;
            int ant = tall;
            for (Rute nabo: naboer) {
                if (nabo.flagget) {
                    ant--;
                }
            }
            if (ant == 0) {
                for (Rute nabo: naboer) {
                    if (!(nabo.sjekket || nabo.flagget)) {
                        r = r || nabo.sjekk(retur);
                    }
                }
            }
            return r;
        }

        return sjekk(retur);
    }

    public boolean sjekk(Collection<Rute> retur) {
        if (flagget || sjekket) {
            return false;
        }

        sjekket = true;
        retur.add(this);
        antSjekket++;

        if (bombe) {
            return true;
        }

        if (tall == 0) {
            for (Rute nabo: naboer) {
                if (!nabo.sjekket) {
                    nabo.sjekk(retur);
                }
            }
        }

        return false;
    }

}
