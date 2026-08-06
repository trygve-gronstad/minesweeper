import java.util.*;

class Rutenett {

    private final Punkt[] allePunkter;
    private final int maksX, maksY;

    public Rutenett(PunktDistribusjon modell) {
        allePunkter = modell.finnPunkter();
        this.maksX = modell.hentBredde();
        this.maksY = modell.hentHøyde();
    }

    private Trekant newSuperTrekant() {
        return new Trekant(new Punkt(-3 * maksX, -maksY), new Punkt(3 * maksX, -maksY), new Punkt(0, 3 * maksY));
    }

    public Collection<Trekant> finnTrekanter() {
        return finnTrekanter(newSuperTrekant());
    }

    private Collection<Trekant> finnTrekanter(Trekant superTrekant) {
        Collection<Trekant> trekanter = new HashSet<>();
        trekanter.add(superTrekant);

        for (Punkt p: allePunkter) {
            Collection<Trekant> dårligeTrekanter = new HashSet<>();

            for (Trekant t: trekanter) {
                if (t.innenforSirkel(p)) {
                    dårligeTrekanter.add(t);
                }
            }

            Collection<Linje> hull = finnHullKant(dårligeTrekanter);
            trekanter.removeAll(dårligeTrekanter);

            for (Linje l: hull) {
                trekanter.add(new Trekant(l, p));
            }
        }

        //fjernSuperTrekant(trekanter, superTrekant);
        return trekanter;
    }

    private Collection<Trekant> finnTrekanter2(Trekant superTrekant) {
        Collection<Trekant> aktivTrekanter = new HashSet<>();
        Collection<Trekant> ferdigTrekanter = new HashSet<>();
        aktivTrekanter.add(superTrekant);

        for (Punkt p: allePunkter) {
            Collection<Trekant> dårligeTrekanter = new HashSet<>();

            Iterator<Trekant> it = aktivTrekanter.iterator();
            while (it.hasNext()) {
                Trekant t = it.next();

                if (t.hentSentrum() == null) {
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

            Collection<Linje> hull = finnHullKant(dårligeTrekanter);
            aktivTrekanter.removeAll(dårligeTrekanter);

            for (Linje l: hull) {
                aktivTrekanter.add(new Trekant(l, p));
            }
        }

        return ferdigTrekanter;
    }

    private Collection<Linje> finnHullKant(Collection<Trekant> trekanter) {
        Collection<Linje> kanter = new HashSet<>();

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

    private void fjernSuperTrekant(Collection<Trekant> trekanter, Trekant fjern) {
        for (Punkt p: fjern.hentPunkter()) {
            Collection<Trekant> åFjerne = new HashSet<>();
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

    public Punkt[] punkterIIntervall(double x1, double y1, double x2, double y2) {
        double minX = Math.min(x1, x2);
        double maxX = Math.max(x1, x2);
        double minY = Math.min(y1, y2);
        double maxY = Math.max(y1, y2);

        return Arrays.stream(allePunkter)
            .filter(p -> p.x >= minX && p.x <= maxX && p.y >= minY && p.y <= maxY)
            .toArray(Punkt[]::new);
    }

    
    private Map<Punkt, Collection<Trekant>> sammenhengendeTreknaterPunkt(Collection<Trekant> trekanter, Trekant superTrekant) {
        Map<Punkt, Collection<Trekant>> linjer = new HashMap<>();
        for (Trekant t: trekanter) {
            for (Punkt p: t.hentPunkter()) {
                Collection<Trekant> liste = linjer.computeIfAbsent(p, k -> new HashSet<>());
                liste.add(t);
            }
        }
        for (Punkt p: superTrekant.hentPunkter()) {
            linjer.remove(p);
        }
        return linjer;
    }

    public Collection<MangeKant> finnMangeKant(Collection<Trekant> trekanter, Trekant superTrekant) {
        Map<Punkt, Collection<Trekant>> map = sammenhengendeTreknaterPunkt(trekanter, superTrekant);
        Collection<MangeKant> figur = new HashSet<>();
        for (Punkt p: map.keySet()) {
            MangeKant m = new MangeKant(p, map.get(p));
            if (m != null) {
                figur.add(m.clip(0, 0, maksX, maksY));
            }
        }
        return figur;
    }

    public Collection<MangeKant> finnMangeKant() {
        Trekant superTrekant = newSuperTrekant();
        return finnMangeKant(finnTrekanter(superTrekant), superTrekant);
    }

}
