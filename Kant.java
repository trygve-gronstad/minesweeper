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

    @Override
    public String toString() {
        return String.format(Arrays.toString(P));
    }

}

class Trekant extends Kant {
    
    public Trekant(Punkt A, Punkt B, Punkt C) {
        super(A, B, C);
    }

    public Trekant[] splitTrekant(Punkt p) {
        return new Trekant[] {new Trekant(P[0], P[1], p), new Trekant(P[0], p, P[2]), new Trekant(p, P[1], P[2])};
    }

    public static Trekant[] flipTrekant(Linje felles, Linje ny) {
        return new Trekant[] {new Trekant(felles.hentPunkter()[0], ny.hentPunkter()[0], ny.hentPunkter()[1]), new Trekant(felles.hentPunkter()[1], ny.hentPunkter()[0], ny.hentPunkter()[1])};
    }

    public static Linje kortere (Linje l, Trekant t1, Trekant t2) {
        Linje ny = Linje.hentLinje(l.fritsåtendePunkt(t1), l.fritsåtendePunkt(t2));
        if (ny.kvadratLengde() < l.kvadratLengde()) {
            return ny;
        }
        return null;
    }

    public Linje[] hentLinjer() {
        return new Linje[] {Linje.hentLinje(P[0], P[1]), Linje.hentLinje(P[0], P[2]), Linje.hentLinje(P[1], P[2])};
    }

    public Linje[] hentLinjer(Linje ekskludertLinje) {
        Punkt p = ekskludertLinje.fritsåtendePunkt(this);
        return new Linje[] {Linje.hentLinje(ekskludertLinje.hentPunkter()[0], p), Linje.hentLinje(ekskludertLinje.hentPunkter()[1], p)};
    }

    public boolean innenforTrekant(Punkt p) {
        int d1 = p.sign(P[0], P[1]);
        int d2 = p.sign(P[1], P[2]);
        int d3 = p.sign(P[2], P[0]);

        return ((d1 > 0 && d2 > 0 && d3 > 0) || (d1 < 0 && d2 < 0 && d3 < 0));
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

    public Punkt fritsåtendePunkt(Trekant t) {
        for (Punkt trekantPunkt : t.hentPunkter()) {
            if (trekantPunkt != P[0] && trekantPunkt != P[1]) {
                return trekantPunkt;
            }
        }
    throw new IllegalArgumentException("Linjen tilhører ikke denne trekanten");
}
}
