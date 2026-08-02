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
            if (p.equals(punkt)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format(Arrays.toString(P));
    }

    public Punkt sørVestHjørne() {
        return Punkt.min(P);
    }

    public Punkt nordØstHjørne() {
        return Punkt.max(P);
    }

    public int size() {
        return P.length;
    }

    public int[] xArr() {
        return hentKodeArray(true);
    }

    public int[] yArr() {
        return hentKodeArray(false);
    }

    private int[] hentKodeArray(boolean x) {
        int[] n = new int[size()];
        for (int i = 0; i < size(); i++) {
            n[i] = (int) (x ? P[i].x : P[i].y);
        }
        return n;
    }
}


class Trekant extends MangeKant {

    private final double r2;
    
    public Trekant(Punkt A, Punkt B, Punkt C) {
        super(sirkelSentrum(A, B, C), A, B, C);
        r2 = finnRadiusKvadrat();
    }

    public Trekant(Linje l, Punkt A) {
        this(l.P[0], l.P[1], A);
    }

    private static Punkt sirkelSentrum(Punkt A, Punkt B, Punkt C) {
        double d = 2 * (A.x * (B.y - C.y) + B.x * (C.y - A.y) + C.x * (A.y - B.y));

        if (d == 0) { //punktene er på rett linje
            return null;
        }

        double h = (A.square()*(B.y - C.y) + B.square()*(C.y - A.y) + C.square()*(A.y - B.y)) / d;
        double k = (A.square()*(C.x - B.x) + B.square()*(A.x - C.x) + C.square()*(B.x - A.x)) / d;
        return new Punkt(h, k);
    } 

    private double finnRadiusKvadrat() {
        if (S == null) {
            return 0;
        }
        return pow2(P[0].x - S.x) + pow2(P[0].y - S.y);
    }

    private static double pow2(double tall) {
        return tall*tall;
    }

    public boolean innenforSirkel(Punkt p) {
        if (S == null) {
            return false;
        }
        return pow2(p.x - S.x) + pow2(p.y - S.y) <= r2;
    }

    public boolean sirkelTilHøyreForPunkt(Punkt p) {
        double dx = p.x - S.x;
        return dx > 0 && (dx * dx) > r2;
    }

    public Linje[] kanter() {
        return new Linje[] {Linje.hentLinje(P[0], P[1]), Linje.hentLinje(P[0], P[2]), Linje.hentLinje(P[1], P[2])};
    }

    public boolean erITrekant(Linje l) {
        return erIKant(l.P[0]) && erIKant(l.P[1]);
    }

    public Punkt hentSentrum(double minX, double minY, double maksX, double maksY) {
        return new Punkt(Math.min(maksX, Math.max(S.x, minX)), Math.min(maksY, Math.max(S.y, minY)));
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

    public double kvadratLengde() {
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

class MangeKant extends Kant {
    
    protected final Punkt S;

    protected MangeKant(Punkt S, Punkt... p) {
        this.S = S;
        super(p);
    }

    public static MangeKant newMangeKant(Punkt p, Collection<Trekant> trekanter) {
        return new MangeKant(p, slåSammen(p, trekanter));
    }

    private static Punkt[] slåSammen(Punkt p, Collection<Trekant> trekanter) {
        Punkt[] punkter = new Punkt[trekanter.size()];

        int i = 0;
        for (Trekant t: trekanter) {
            punkter[i++] = t.hentSentrum();
        }

        Arrays.sort(punkter, 0, i, (p1, p2) -> { //ai
            if (p == null) {System.out.println("null");}
            double vinkel1 = Math.atan2(p1.y - p.y, p1.x - p.x);
            double vinkel2 = Math.atan2(p2.y - p.y, p2.x - p.x);
            return Double.compare(vinkel1, vinkel2);
        });

        return punkter;
    }

    public Punkt hentSentrum() {
        return S;
    }

    public MangeKant trim(int x0, int y0, int x1, int y1) {
        
        return null;
    }

}
