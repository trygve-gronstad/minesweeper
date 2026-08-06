import java.util.*;

class Rute {

    private final Collection<Rute> naboer = new HashSet<>();
    private final MangeKant FIGUR;
    private boolean bombe, flagget, sjekket;
    private int tall = -1;
    private final int indeks;
    
    public Rute(MangeKant poly, int indeks) {
        FIGUR = poly;
        bombe = false;
        flagget = sjekket = false;
        this.indeks = indeks;
    }

    public static void settNaboer(Collection<Rute> ruter) {
        Map<Punkt, Collection<Rute>> map = new HashMap<>();
        for (Rute r: ruter) {
            leggTil(r, map);
        }

        for (Punkt p: map.keySet()) {
            Collection<Rute> sammenhengendeRuter = map.get(p);
            for (Rute nabo: sammenhengendeRuter) {
                nabo.leggTilNabo(sammenhengendeRuter);
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

    private static void leggTil(Rute r, Map<Punkt, Collection<Rute>> map) {
        for (Punkt p: r.FIGUR.hentPunkter()) {
            map.computeIfAbsent(p, k -> new HashSet<>()).add(r);
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

    public int hentSenterX(int lengde) {
        return (int) FIGUR.hentSentrum(0, 0, lengde, 0).x;
    }

    public int hentSenterY(int høyde) {
        return (int) FIGUR.hentSentrum(0, 0, 0, høyde).y;
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

    public boolean erSjekket() {
        return sjekket;
    }

    @Override
    public String toString() {
        int i = 0;
        if (bombe) i |= 4;
        if (flagget) i |= 2;
        if (sjekket) i |= 1;

        return String.format("{%d,%s}", i, FIGUR.hentSentrum());
    }

}
