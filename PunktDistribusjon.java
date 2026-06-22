import java.util.Random;

interface PunktDistribusjon {
    Punkt neste();
}

class RandomModell implements PunktDistribusjon {

    private static final Random RAND = new Random();
    private final int HØYDE;
    private final int BREDDE;

    public RandomModell(int bredde, int høyde) {
        HØYDE = høyde;
        BREDDE = bredde;
    }

    @Override
    public Punkt neste() {
        return new Punkt(RAND.nextInt(BREDDE), RAND.nextInt(HØYDE));
    }
}

class GridModell implements PunktDistribusjon {

    private final int BREDDE, PER_RAD, DELTA;
    private int x, y;

    public GridModell(int bredde, int perRad) {
        BREDDE = bredde;
        PER_RAD = perRad;
        x = y = 0;
        DELTA = BREDDE / PER_RAD;
    }

    @Override
    public Punkt neste() {
        if (x > PER_RAD) {
            x = 0;
            y++;
        }

        return new Punkt(DELTA * x++, DELTA * y);
    }
}