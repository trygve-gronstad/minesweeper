import java.util.*;

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

    public float maksX() {
        return maks.x;
    }

    public float maksY() {
        return maks.y;
    }

    private Trekant newSuperTrekant() {
        return new Trekant(new Punkt(-3 * maks.x, -maks.y), new Punkt(3 * maks.x, -maks.y), new Punkt(0, 3 * maks.y));
    }

    public Set<Trekant> finnTrekanter() {
        Trekant superTrekant = newSuperTrekant();
        return finnTrekanter(superTrekant);
    }

    private Set<Trekant> finnTrekanter(Trekant superTrekant) {
        Set<Trekant> trekanter = new HashSet<>();
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

    private Set<Trekant> finnTrekanter2(Trekant superTrekant) {
        Set<Trekant> aktivTrekanter = new HashSet<>();
        Set<Trekant> ferdigTrekanter = new HashSet<>();
        aktivTrekanter.add(superTrekant);

        for (Punkt p: allePunkter) {
            Set<Trekant> dårligeTrekanter = new HashSet<>();

            Iterator<Trekant> it = aktivTrekanter.iterator();
            while (it.hasNext()) {
                Trekant t = it.next();

                if (t.hentSirkelSentrum() == null) {
                    continue;
                }

                if (t.sirkelTilHøyreForPunkt(p)) {
                    ferdigTrekanter.add(t);
                    it.remove();
                }
                else if (t.innenforSirkel(p)) {
                    dårligeTrekanter.add(t);
                }
            }

            Set<Linje> hull = finnHullKant(dårligeTrekanter);
            aktivTrekanter.removeAll(dårligeTrekanter);

            for (Linje l: hull) {
                aktivTrekanter.add(new Trekant(l, p));
            }
        }

        return ferdigTrekanter;
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

    public Punkt[] punkterIIntervall(float x1, float y1, float x2, float y2) {
        float minX = Math.min(x1, x2);
        float maxX = Math.max(x1, x2);
        float minY = Math.min(y1, y2);
        float maxY = Math.max(y1, y2);

        return Arrays.stream(allePunkter)
            .filter(p -> p.x >= minX && p.x <= maxX && p.y >= minY && p.y <= maxY)
            .toArray(Punkt[]::new);
    }

    
    private Map<Punkt, Set<Trekant>> sammenhengendeTreknaterPunkt(Set<Trekant> trekanter, Trekant superTrekant) {
        Map<Punkt, Set<Trekant>> linjer = new HashMap<>();
        for (Trekant t: trekanter) {
            for (Punkt p: t.hentPunkter()) {
                Set<Trekant> liste = linjer.computeIfAbsent(p, k -> new HashSet<>());
                liste.add(t);
            }
        }
        for (Punkt p: superTrekant.hentPunkter()) {
            linjer.remove(p);
        }
        return linjer;
    }

    public Set<Polygon> finnPolygon(Set<Trekant> trekanter, Trekant superTrekant) {
        Map<Punkt, Set<Trekant>> map = sammenhengendeTreknaterPunkt(trekanter, superTrekant);
        Set<Polygon> figur = new HashSet<>();
        for (Punkt p: map.keySet()) {
            figur.add(Polygon.newPolygon(p, map.get(p)));
        }
        return figur;
    }

    public Set<Polygon> finnPolygon() {
        Trekant superTrekant = newSuperTrekant();
        return finnPolygon(finnTrekanter(superTrekant), superTrekant);
    }

}
