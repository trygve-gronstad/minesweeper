import java.util.*;

class Kant {
    protected final Punkt[] P;

    public Kant(Punkt... p) {
        P = p;
    }

    public Punkt[] hentPunkter() {
        return P;
    }

    public boolean erIKant(Punkt p) {
        for (Punkt punkt: P) {
            if (p == punkt) {
                return true;
            }
        }
        return false;
    }

}

class Trekant extends Kant {

    public static final Map<Linje, List<Trekant>> TREKANTER = new HashMap<>();
    
    public Trekant(Punkt A, Punkt B, Punkt C) {
        super(A, B, C);
        leggTil();
    }

    private void leggTil() {
        leggTil(0, 1);
        leggTil(0, 2);
        leggTil(1, 2);
    }

    private void leggTil(int i, int j) {
        Linje l = Linje.hentLinje(P[i], P[j]);
        List<Trekant> liste = TREKANTER.computeIfAbsent(l, k -> new ArrayList<>());
        liste.add(this);
    }

    public Trekant[] newTrekant(Punkt p) {
        return new Trekant[] {new Trekant(P[0], P[1], p), new Trekant(P[0], p, P[2]), new Trekant(p, P[1], P[2])};
    }

    public boolean innenforTrekant(Punkt p) {
        int d1 = p.sign(P[0], P[1]);
        int d2 = p.sign(P[1], P[2]);
        int d3 = p.sign(P[2], P[0]);

        return !((d1 < 0 || d2 < 0 || d3 < 0) && (d1 > 0 || d2 > 0 || d3 > 0));
    } 

    public Punkt sørVestHjørne() {
        return Punkt.min(P);
    }

    public Punkt nordØstHjørne() {
        return Punkt.max(P);
    }

}

class Linje extends Kant {

    private static final Map<LinjeNøkkel, Linje> LINJER = new HashMap<>();

    private record LinjeNøkkel(Punkt A, Punkt B) {}; 

    private Linje(Punkt A, Punkt B) {
        super(A, B);
    }

    public static Linje hentLinje(Punkt A, Punkt B) {
        Punkt p1 = A.compareTo(B) <= 0 ? A : B;
        Punkt p2 = A.compareTo(B) <= 0 ? B : A;

        LinjeNøkkel nøkkel = new LinjeNøkkel(p1, p2);
        
        return LINJER.computeIfAbsent(nøkkel, k -> new Linje(k.A, k.B));
    }

    public int kvadratLengde() {
        return P[1].addisjon(P[0].neg()).square();
    }
}
