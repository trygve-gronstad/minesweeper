import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

class Rutenett {

    private final Punkt[] allePunkter;
    private final Punkt maks;
    
    public Rutenett(PunktDistribusjon modell) {
        allePunkter = modell.finnPunkter();
        maks = Punkt.max(allePunkter);
    }

    public Rutenett(int antall, int bredde, int høyde) {
        this(new RandomModell(antall, bredde, høyde));
    }

    public Set<Trekant> finnTrekanter() {
        Set<Trekant> trekanter = new HashSet<>();
        Trekant superTrekant = new Trekant(new Punkt(-3 * maks.x, -maks.y), new Punkt(3 * maks.x, -maks.y), new Punkt(0, 3 * maks.y));
        trekanter.add(superTrekant);

        for (Punkt p: allePunkter) {
            Set<Trekant> dårligeTrekanter = new HashSet<>();

            for (Trekant t: trekanter) {
                if (t.innenforSirkel(p)) {
                    dårligeTrekanter.add(t);
                }
            }

            Set<Linje> hull = finnHullKant(dårligeTrekanter);
            trekanter.removeAll(dårligeTrekanter);

            for (Linje l: hull) {
                trekanter.add(new Trekant(l, p));
            }
        }

        //fjernSuperTrekant(trekanter, superTrekant);
        return trekanter;
    }

    private Set<Linje> finnHullKant(Set<Trekant> trekanter) {
        Set<Linje> kanter = new HashSet<>();

        for (Trekant t: trekanter) {
            for (Linje l: t.kanter()) {
                if (kanter.contains(l)) {
                    kanter.remove(l);
                }
                else {
                    kanter.add(l);
                }
            }
        }

        return kanter;
    }

    private void fjernSuperTrekant(Set<Trekant> trekanter, Trekant fjern) {
        for (Punkt p: fjern.hentPunkter()) {
            Set<Trekant> åFjerne = new HashSet<>();
            for (Trekant t: trekanter) {
                if (t.erIKant(p)) {
                    åFjerne.add(t);
                }
            }
            trekanter.removeAll(åFjerne);
        }
    }

    @Override
    public String toString() {
        return Arrays.toString(allePunkter);
    }

    public Punkt[] hentPunkter() {
        return allePunkter;
    }

    public Punkt[] punkterIIntervall(Punkt A, Punkt B) {
        return punkterIIntervall(A.x, A.y, B.x, B.y);
    }

    public Punkt[] punkterIIntervall(int x1, int y1, int x2, int y2) {
        int minX = Math.min(x1, x2);
        int maxX = Math.max(x1, x2);
        int minY = Math.min(y1, y2);
        int maxY = Math.max(y1, y2);

        return Arrays.stream(allePunkter)
            .filter(p -> p.x >= minX && p.x <= maxX && p.y >= minY && p.y <= maxY)
            .toArray(Punkt[]::new);
    }

}
