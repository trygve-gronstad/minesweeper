import java.util.*;

abstract class Kant {
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

    private final Punkt S;
    private final float r2;
    
    public Trekant(Punkt A, Punkt B, Punkt C) {
        super(A, B, C);
        S = sirkelSentrum();
        r2 = finnRadiusKvadrat();
    }

    public Trekant(Linje l, Punkt A) {
        this(l.P[0], l.P[1], A);
    }

    public Punkt sørVestHjørne() {
        return Punkt.min(P);
    }

    public Punkt nordØstHjørne() {
        return Punkt.max(P);
    }

    private Punkt sirkelSentrum() {
        float d = 2 * (P[0].x * (P[1].y - P[2].y) + P[1].x * (P[2].y - P[0].y) + P[2].x * (P[0].y - P[1].y));

        if (d == 0) { //punktene er på rett linje
            return null;
        }

        float h = (P[0].square()*(P[1].y - P[2].y) + P[1].square()*(P[2].y - P[0].y) + P[2].square()*(P[0].y - P[1].y)) / d;
        float k = (P[0].square()*(P[2].x - P[1].x) + P[1].square()*(P[0].x - P[2].x) + P[2].square()*(P[1].x - P[0].x)) / d;
        return new Punkt(h, k);
    } 

    private float finnRadiusKvadrat() {
        if (S == null) {
            return 0;
        }
        return pow2(P[0].x - S.x) + pow2(P[0].y - S.y);
    }

    private static float pow2(float tall) {
        return tall*tall;
    }

    public boolean innenforSirkel(Punkt p) {
        if (S == null) {
            return false;
        }
        return pow2(p.x - S.x) + pow2(p.y - S.y) <= r2;
    }

    public Linje[] kanter() {
        return new Linje[] {Linje.hentLinje(P[0], P[1]), Linje.hentLinje(P[0], P[2]), Linje.hentLinje(P[1], P[2])};
    }

    public boolean erITrekant(Linje l) {
        return erIKant(l.P[0]) && erIKant(l.P[1]);
    }

    public Punkt hentSirkelSentrum() {
        return S;
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

    public float kvadratLengde() {
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

class Polygon extends Kant {
    
    private final Punkt S;

    public Polygon(Punkt S, Punkt... p) {
        this.S = S;
        super(p);
    }

    public static Polygon newPolygon(Punkt p, Set<Trekant> trekanter) {
        return new Polygon(p, slåSammen(p, trekanter));
    }

    private static Punkt[] slåSammen(Punkt p, Set<Trekant> trekanter) {
        Punkt[] punkter = new Punkt[trekanter.size()];

        int i = 0;
        for (Trekant t: trekanter) {
            punkter[i++] = t.hentSirkelSentrum();
        }

        Arrays.sort(punkter, (p1, p2) -> { //ai
            double vinkel1 = Math.atan2(p1.y - p.y, p1.x - p.x);
            double vinkel2 = Math.atan2(p2.y - p.y, p2.x - p.x);
            return Double.compare(vinkel1, vinkel2);
        });

        return punkter;
    }


    
}
