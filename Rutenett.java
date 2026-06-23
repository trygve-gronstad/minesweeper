import java.util.*;

/*
Jeg bruker bare den sorterete x arrayen, så jeg kan fjerne sortert Y, da kan jeg også implementere compareble i Punkt og gjøre koden litt lettere
*/

class Rutenett {

    private final Punkt[] sortertX;
    private final Punkt[] sortertY;

    
    public Rutenett(int antall, PunktDistribusjon modell) {
        Punkt[] p = finnPunkter(antall, modell);

        sortertX = Arrays.copyOf(p, p.length);
        sortertY = p;

        Arrays.sort(sortertX);
        Arrays.sort(sortertY, Comparator.comparingInt(punkt -> punkt.y));

    }

    public Rutenett(int antall, int bredde, int høyde) {
        this(antall, new RandomModell(bredde, høyde));
    }

    public int maksX() {
        return sortertX[sortertX.length - 1].x;
    }

    public int maksY() {
        return sortertY[sortertY.length - 1].y;
    }

    private boolean finnTrekanter(List<Trekant> alle, Trekant t) {
        Punkt[] punkter = punkterIIntervall(t.sørVestHjørne(), t.nordØstHjørne());
        for (Punkt p: punkter) {
            if (!t.erIKant(p) && t.innenforTrekant(p)) {
                Trekant[] nye = t.splitTrekant(p);
                for (Trekant nyT: nye) {
                    if (finnTrekanter(alle, nyT)) {
                        alle.add(nyT);
                    }
                }
                return false;
            }
        }
        return true;
    }

    public Set<Linje> finnTrekanter() {
        List<Trekant> trekanter = new ArrayList<>();
        finnTrekanter(trekanter, new Trekant(new Punkt(-maksX(), -1), new Punkt(2*maksX(), -1), new Punkt(maksX()/2, 2*maksY())));
        
        Map<Linje, Trekant[]> alle = oppdel(trekanter);
        Set<Linje> linjer = new HashSet<>(alle.keySet());
        while (linjer.size() > 0) {
            linjer = forenkl(alle, linjer);
        }

        return alle.keySet();
    }

    private Map<Linje, Trekant[]> oppdel(List<Trekant> liste) {
        Map<Linje, Trekant[]> retur = new HashMap<>();
        
        for (Trekant t: liste) {
            leggTil(retur, t, 0, 1);
            leggTil(retur, t, 0, 2);
            leggTil(retur, t, 1, 2);
        }
        
        return retur;
    }

    private void leggTil(Map<Linje, Trekant[]> map, Trekant t, int i, int j) {
        Linje l = Linje.hentLinje(t.hentPunkter()[i], t.hentPunkter()[j]);
        Trekant[] liste = map.computeIfAbsent(l, k -> new Trekant[] {t, null});
        
        if (liste[0] != t) {
            liste[1] = t;
        }
    }

    private Set<Linje> forenkl(Map<Linje, Trekant[]> map, Set<Linje> nøkkeler) {
        Set<Linje> retur = new HashSet<>();
        boolean fyll = false;

        for (Linje nøkkel: nøkkeler) {
            Trekant[] gamle = map.get(nøkkel);
            if (gamle == null) {
                continue;
            }

            if (fyll) {
                retur.add(nøkkel);
            }
            else if (gamle[1] != null) {
                Linje l = Trekant.kortere(nøkkel, gamle[0], gamle[1]); //retunerer null, hvis den nåverende er kortere
                if (l != null) {
                    Trekant[] nye = Trekant.flipTrekant(nøkkel, l);
                    
                    map.put(l, nye);
                    retur.add(l);

                    for (Trekant t: nye) {
                        for (Linje oppdater: t.hentLinjer(l)) {
                            Trekant[] arr = map.get(oppdater);
                            if (arr != null) { //dette er hvis linjen er fra A til A, ingen trekant tilknyttet da, ikke helt sikker på hvorfor slik linjer lages
                                if (arr[0] == gamle[1] || arr[0] == gamle[0]) {
                                    arr[0] = t;
                                }
                                else {
                                    arr[1] = t;
                                }

                                retur.add(oppdater);
                            }
                        }
                    }

                    //fyll = true;
                    map.remove(nøkkel);
                }
            }
        }

        return retur;
    }

    private Punkt[] finnPunkter(int antall, PunktDistribusjon modell) {
        Punkt[] punkter = new Punkt[antall];

        for (int i = 0; i < antall; i++) {
            punkter[i] = modell.neste();
        }

        return punkter;
    }

    @Override
    public String toString() {
        return String.format("x: %s, y: %s", Arrays.toString(sortertX), Arrays.toString(sortertY));
    }

    

    public Punkt[] hentPunkter() {
        return sortertX;
    }

    /*
    må kanskje gjøre om denne metoden, men det fungerer ok for nå
    feks, så må for øyeblikket A alltid være det minste, og dereter B
    og sikkert bedre måter å gjøre det min/max / +-1 på
    */
    
    public Punkt[] punkterIIntervall(Punkt A, Punkt B) {
        int minX = Math.min(A.x, B.x);
        int maxX = Math.max(A.x, B.x);
        int minY = Math.min(A.y, B.y);
        int maxY = Math.max(A.y, B.y);

        return Arrays.stream(sortertX)
            .filter(p -> p.x >= minX && p.x <= maxX && p.y >= minY && p.y <= maxY)
            .toArray(Punkt[]::new);
    }

    public Punkt[] punkterIIntervall(int x1, int y1, int x2, int y2) {
        return punkterIIntervall(new Punkt(x1, y1), new Punkt(x2, y2));
    }

}
