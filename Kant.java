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

    public Linje[] kanter() {
        int n = size();
        Linje[] l = new Linje[n];

        l[0] = new Linje(P[0], P[n-1]);
        for (int i = 1; i < n; i++) {
            l[i] = new Linje(P[i-1], P[i]);
        }
        return l;
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

    @Override
    public Linje[] kanter() {
        return new Linje[] {new Linje(P[0], P[1]), new Linje(P[0], P[2]), new Linje(P[1], P[2])};
    }

    public boolean erITrekant(Linje l) {
        return erIKant(l.P[0]) && erIKant(l.P[1]);
    }

    public Punkt hentSirkelSentrum() {
        return S;
    }

}

class Linje extends Kant {

    public Linje(Punkt A, Punkt B) {
        Punkt[] arr = {A, B};
        Arrays.sort(arr);
        super(arr);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Linje) {
            Linje annen = (Linje) o;
            return annen.P[0].equals(P[0]) && annen.P[1].equals(P[1]);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return P[0].hashCode() * 13 + P[1].hashCode();
    }

    public double kvadratLengde() {
        return P[1].subtraksjon(P[0]).square();
    }

    public Punkt fritsåtendePunkt(Trekant t) {
        for (Punkt trekantPunkt : t.hentPunkter()) {
            if (!trekantPunkt.equals(P[0]) && !trekantPunkt.equals(P[1])) {
                return trekantPunkt;
            }
        }
        throw new IllegalArgumentException("Linjen tilhører ikke denne trekanten");
    }

    public static Punkt skjæringsPunkt(Punkt A, Punkt B, int kant, boolean xAkse) {
        if (xAkse) {
            double t = (kant - A.x) / (B.x - A.x);
            double y = A.y + t * (B.y - A.y);
            return new Punkt(kant, y);
        }
        double t = (kant - A.y) / (B.y - A.y);
        double x = A.x + t * (B.x - A.x);
        return new Punkt(x, kant);
    }


}

class MangeKant extends Kant {

    protected final Punkt S;

    protected MangeKant(Punkt S, Punkt... p) {
        this.S = S;
        super(p);
    }

    public MangeKant(Punkt p, Collection<Trekant> trekanter) {
        this(p, slåSammen(p, trekanter));
    }

    private static Punkt[] slåSammen(Punkt p, Collection<Trekant> trekanter) {
        Punkt[] punkter = new Punkt[trekanter.size()];

        int i = 0;
        for (Trekant t: trekanter) {
            if (t.S != null) {
                punkter[i++] = t.S;
            }
            else {
                punkter = Arrays.copyOf(punkter, punkter.length - 1);
            }
        }
        sorter(p, punkter);

        return punkter;
    }

    private static void sorter(Punkt sentrum, Punkt[] punkter) {
        Arrays.sort(punkter, (p1, p2) -> { //ai
            double vinkel1 = Math.atan2(p1.y - sentrum.y, p1.x - sentrum.x);
            double vinkel2 = Math.atan2(p2.y - sentrum.y, p2.x - sentrum.x);
            return Double.compare(vinkel1, vinkel2);
        });
    }

    public Punkt hentSentrum() {
        double x = 0, y = 0;
        for (Punkt p: P) {
            x += p.x;
            y += p.y;
        }
        return new Punkt(x/size(), y/size());
    }

    public MangeKant clip(int x0, int y0, int x1, int y1) {

        List<Punkt> punkter = clip(Arrays.asList(P), x0, true, true);
        punkter = clip(punkter, x1, false, true);
        punkter = clip(punkter, y0, true, false);
        punkter = clip(punkter, y1, false, false);

        Punkt[] p = new Punkt[punkter.size()];
        punkter.toArray(p);
        return new MangeKant(S, p);
    }

    private List<Punkt> clip(List<Punkt> punkter, int kant, boolean større, boolean xAkse) {
        List<Punkt> nyePunkter = new ArrayList<>();

        for (int i = 0; i < punkter.size(); i++) {
            int j = (i + 1) % punkter.size();

            Punkt A = punkter.get(i);
            Punkt B = punkter.get(j);

            double varA = xAkse ? punkter.get(i).x : punkter.get(i).y;
            double varB = xAkse ? punkter.get(j).x : punkter.get(j).y;

            boolean aInni = større ? (varA >= kant) : (varA <= kant);
            boolean bInni = større ? (varB >= kant) : (varB <= kant);

            if (bInni) {
                if (!aInni) {
                    nyePunkter.add(Linje.skjæringsPunkt(A, B, kant, xAkse));
                }
                nyePunkter.add(B);
            } else if (aInni) {
                nyePunkter.add(Linje.skjæringsPunkt(A, B, kant, xAkse));
            }
        }
        return nyePunkter;
    }


}

