class Trekant {
    
    private final Punkt A, B, C, S;
    private final int r, r2;

    public Trekant(Punkt A, Punkt B, Punkt C) {
        this.A = A;
        this.B = B;
        this.C = C;
        S = sirkelSentrum();
        r2 = finnRadiusKvadrat();
        r = finnRaduis();
    }

    public Trekant[] newTrekant(Punkt p) {
        return new Trekant[] {new Trekant(A, B, p), new Trekant(A, p, C), new Trekant(p, B, C)};
    }

    private Punkt sirkelSentrum() {
        int d = 2 * (A.x * (B.y - C.y) + B.x * (C.y - A.y) + C.x * (A.y - B.y));
        int h = (A.square()*(B.y - C.y) + B.square()*(C.y - A.y) + C.square()*(A.y - B.y)) / d;
        int k = (A.square()*(C.x - B.x) + B.square()*(A.x - C.x) + C.square()*(B.x - A.x)) / d;

        return new Punkt(h, k);
    } 

    private int finnRadiusKvadrat() {
        return pow2(A.x - S.x) + pow2(A.y - S.y);
    }

    private int finnRaduis() {
        return (int) Math.round(Math.sqrt(r2));
    }

    public Punkt sørVestHjørne() {
        return S.addisjon(-r);
    }

    public Punkt nordØstHjørne() {
        return S.addisjon(r);
    }

    public boolean innenforSirkel(Punkt p) {
        return pow2(p.x - S.x) + pow2(p.y - S.y) <= r2;
    }

    private static int pow2(int tall) {
        return tall*tall;
    }

}
