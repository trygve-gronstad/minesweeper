import java.util.Random;


abstract class PunktDistribusjon{

    protected final int bredde, høyde;

    public PunktDistribusjon(int høyde, int bredde) {
        this.høyde = høyde;
        this.bredde = bredde;
    }

    abstract Punkt[] finnPunkter();

    public int hentBredde() {
        return bredde;
    }

    public int hentHøyde() {
        return høyde;
    }

    abstract int estimertAvstand();

}

class RandomModell extends PunktDistribusjon {

    protected static final Random RAND = new Random();
    private static final double AVSTAND = 0.1;

    protected final int ant;

    public RandomModell(int antall, int bredde, int høyde) {
        super(høyde, bredde);
        ant = antall;
    }

    protected int hentAvstand() {
        return Math.min((int) (bredde * AVSTAND), (int) (høyde * AVSTAND));
    }

    @Override
    public Punkt[] finnPunkter() {
        Punkt[] p = new Punkt[ant];
        int avstand = hentAvstand();
        int x = bredde - avstand;
        int y = høyde - avstand;

        for (int i = 0; i < ant; i++) {
            p[i] = new Punkt(RAND.nextInt(avstand, x), RAND.nextInt(avstand, y));
        }
        return p;
    }

    @Override
    public int estimertAvstand() {
        int a = hentAvstand() * 2;
        return estimertAvstand(bredde - a, høyde - a);
    }

    protected int estimertAvstand(int x, int y) {
        return (int) (1 / (Math.sqrt(ant / ((double) (x * y)))));
    }
}

class SpiralModell extends RandomModell {

    private final double vinkel;
    public static final double GYLENDESNITT = (1 + Math.sqrt(5)) / 2;
    public static final double GYLENDEVINKEL = 2 * Math.PI * (1 - 1 / GYLENDESNITT);
    private static final double[] FINE_VINKLER = {GYLENDEVINKEL, 0.3351, 0.3386, 0.3421, 0.3438, 0.3543, 0.3560, 0.3578, 0.3613, 0.3648, 0.3752, 0.3787, 0.3857, 0.3997, 0.4032, 0.4084, 0.4136, 0.4276, 0.4294, 0.4311, 0.4398, 0.4416, 0.4555, 0.4608, 0.4625, 0.4677, 0.4695, 0.4730, 0.4765, 0.4922, 0.4939, 0.4974, 0.4992, 0.5061, 0.5079, 0.5358, 0.5411, 0.5428, 0.5498, 0.5568, 0.5829, 0.5864, 0.5917, 0.5934, 0.6021, 0.6056, 0.6109, 0.6144, 0.6423, 0.6458, 0.6475, 0.6528, 0.6667, 0.6754, 0.6772, 0.7226, 0.7278, 0.7295, 0.7575, 0.7592, 0.7645, 0.8133, 0.8290, 0.8308, 0.8447, 0.8605, 0.8639, 0.8692, 0.8709, 0.9338, 0.9372, 0.9477, 0.9495, 0.9547, 0.9757, 0.9791, 0.9844, 0.9966, 0.9983, 1.0088, 1.0105, 1.0856, 1.0961, 1.0978, 1.1013, 1.1031, 1.1153, 1.1257, 1.1292, 1.1554, 1.1589, 1.1606, 1.1659, 1.1676, 1.1868, 1.1903, 1.2008, 1.2043, 1.2060, 1.2113, 1.2182, 1.3020, 1.3055, 1.3125, 1.3160, 1.3282, 1.3299, 1.3352, 1.3544, 1.3561, 1.3701, 1.3718, 1.4155, 1.4207, 1.4312, 1.4347, 1.4382, 1.4626, 1.4696, 1.4713, 1.4731};

    public SpiralModell(int antall, int lengde, int bredde) {
        this(antall, lengde, bredde, FINE_VINKLER[RAND.nextInt(FINE_VINKLER.length)]);
    }

    public SpiralModell(int antall, int bredde, int høyde, double vinkelRadian) {
        super(antall, bredde, høyde);
        this.vinkel = vinkelRadian;
    }

    @Override
    public Punkt[] finnPunkter() {
        Punkt[] p = new Punkt[ant];
        double c = (Math.min(bredde, høyde) - hentAvstand()) / (2 * Math.sqrt(ant - 1));
        int x = bredde / 2;
        int y = høyde / 2;

        for (int i = 0; i < ant; i++) {
            double r = c * Math.sqrt(i);
            double theta = i * vinkel;
            p[i] = new Punkt(r * Math.cos(theta) + x, r * Math.sin(theta) + y);
        }
        return p;
    }

    @Override
    public int estimertAvstand() {
        int min = Math.min(bredde, høyde);
        return estimertAvstand(min, min);
    }

}

class GridModell extends PunktDistribusjon {

    protected final int delta, rad, kol;

    public GridModell(int caAntall, int bredde, int høyde) {
        super(høyde, bredde);

        double forhold = ((double) bredde)/(høyde);
        double rot = Math.sqrt(caAntall / forhold);
        rad = Math.max(1, (int) Math.round(rot));
        kol = (int) Math.round((double) caAntall / this.rad);
        delta = Math.min(bredde / kol, høyde / rad);
    }

    @Override
    public Punkt[] finnPunkter() {
        Punkt[] p = new Punkt[rad*kol];

        for (int x = 0; x < kol; x++) {
            for (int y = 0; y < rad; y++) {
                p[x*rad + y] = new Punkt(delta * (x + 0.5), delta * (y + 0.5));
            }
        }
        return p;
    }

    @Override
    public int estimertAvstand() {
        return delta;
    }
}

class HexModell extends GridModell {

    public HexModell(int caAntall, int bredde, int høyde) {
        super(caAntall, bredde, høyde);
    }

    @Override
    public Punkt[] finnPunkter() {
        Punkt[] p = new Punkt[rad*kol];
        int halveis = delta / 2;

        for (double x = 0.5; x < kol; x++) {
            for (double y = 0.375; y < rad; y++) {
                Punkt punkt;
                if ((int) x % 2 == 0) {
                    punkt = new Punkt(delta * x, delta * y);
                }
                else {
                    punkt = new Punkt(delta * x, delta * y + halveis);
                }
                p[(int) x * rad + (int) y] = punkt;
            }
        }
        return p;
    }
}