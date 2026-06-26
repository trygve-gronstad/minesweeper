import java.util.Random;


interface PunktDistribusjon{
    Punkt[] finnPunkter();
}

class RandomModell implements PunktDistribusjon {

    private static final Random RAND = new Random();
    private final int HØYDE, BREDDE, ANT;

    public RandomModell(int antall, int bredde, int høyde) {
        HØYDE = høyde;
        BREDDE = bredde;
        ANT = antall;
    }

    @Override
    public Punkt[] finnPunkter() {
        Punkt[] p = new Punkt[ANT];
        for (int i = 0; i < ANT; i++) {
            p[i] = new Punkt(RAND.nextInt(BREDDE), RAND.nextInt(HØYDE));
        }
        return p;
    }
}

class HexModell implements PunktDistribusjon {


    private final int DELTA, RAD, KOL;

    public HexModell(int antX, int antY, int dx) {
        KOL = antX;
        RAD = antY;
        DELTA = dx;
    }

    @Override
    public Punkt[] finnPunkter() {
        Punkt[] p = new Punkt[RAD*KOL];
        int halveis = DELTA / 2;

        for (int x = 0; x < KOL; x++) {
            for (int y = 0; y < RAD; y++) {
                Punkt punkt;
                if (x % 2 == 0) {
                    punkt = new Punkt(DELTA * x, DELTA * y);
                }
                else {
                    punkt = new Punkt(DELTA * x, DELTA * y + halveis);
                }
                p[x*RAD + y] = punkt;
            }
        }
        return p;
    }
}

//fungerer ikke
class TriangelModell implements PunktDistribusjon {


    private final int DELTA, RAD, KOL;

    public TriangelModell(int antX, int antY, int dx) {
        KOL = antX;
        RAD = antY;
        DELTA = dx;
    }

    @Override
    public Punkt[] finnPunkter() {
        Punkt[] p = new Punkt[RAD*KOL];
        int halveis = DELTA / 2;
        int fjeredel = DELTA / 4;

        int i = 0;
        for (int x = 0; x < KOL; x++) {
            for (int y = 0; y < RAD; y++) {
                Punkt punkt;
                if (i == 0) {
                    punkt = new Punkt(DELTA * x, DELTA * y);
                }
                else if (i == 3) {
                    punkt = new Punkt(DELTA * x + halveis, DELTA * y);
                }
                else {
                    punkt = new Punkt(DELTA * x + fjeredel, DELTA * y);
                }
                p[x*RAD + y] = punkt;
            }
            if (++i == 4) {
                i = 0;
            }
        }
        return p;
    }
}

class GridModell implements PunktDistribusjon {


    private final int DELTA, RAD, KOL;

    public GridModell(int antX, int antY, int dx) {
        KOL = antX;
        RAD = antY;
        DELTA = dx;
    }

    @Override
    public Punkt[] finnPunkter() {
        Punkt[] p = new Punkt[RAD*KOL];

        for (int x = 0; x < KOL; x++) {
            for (int y = 0; y < RAD; y++) {
                p[x*RAD + y] = new Punkt(DELTA * x, DELTA * y);
            }
        }
        return p;
    }
}