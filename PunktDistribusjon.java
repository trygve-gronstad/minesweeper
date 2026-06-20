import java.util.Random;

/*
kanskje dette burde implementere iterator
*/

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

    private final int PER_RAD;
    private final int BREDDE;
    private int x, y;
    private final int dx;

    public GridModell(int bredde, int perRad) {
        BREDDE = bredde;
        PER_RAD = perRad;
        x = y = 0;
        dx = (int) BREDDE / PER_RAD;

    }

    @Override
    public Punkt neste() {
        if (x > PER_RAD) {
            x = 0;
            y++;
        }
        x++;
                
        return new Punkt(x * dx, y * dx);
    }
}