import java.util.Random;


interface PunktDistribusjon{
    Punkt[] finnPunkter();
}

class RandomModell implements PunktDistribusjon {

    private static final Random RAND = new Random();
    protected final int høyde, bredde, ant, avstand;

    public RandomModell(int antall, int bredde, int høyde, int avstand) {
        ant = antall;
        this.bredde = bredde - avstand;
        this.høyde = høyde - avstand;
        this.avstand = avstand;
    }

    @Override
    public Punkt[] finnPunkter() {
        Punkt[] p = new Punkt[ant];

        for (int i = 0; i < ant; i++) {
            p[i] = new Punkt(RAND.nextInt(avstand, bredde), RAND.nextInt(avstand, høyde));
        }
        return p;
    }
}

class HexModell extends GridModell {

    public HexModell(int antall, int bredde, int høyde, int avstand) {
        super(antall, bredde, høyde, avstand);
    }

    @Override
    public Punkt[] finnPunkter() {
        Punkt[] p = new Punkt[rad*kol];
        int halveis = delta / 2;

        for (int x = 0; x < kol; x++) {
            for (int y = 0; y < rad; y++) {
                Punkt punkt;
                if (x % 2 == 0) {
                    punkt = new Punkt(delta * x, delta * y);
                }
                else {
                    punkt = new Punkt(delta * x, delta * y + halveis);
                }
                p[x*rad + y] = punkt.addisjon(avstand);
            }
        }
        return p;
    }
}

class GridModell implements PunktDistribusjon {

    protected final int delta, rad, kol, avstand;

    public GridModell(int antall, int bredde, int høyde, int avstand) {
        int avstand2 = avstand*2;
        int tilgjenligBredde = bredde - avstand2;
        double forhold = ((double) tilgjenligBredde)/(høyde - avstand2);
        double rot = Math.sqrt(antall / forhold);
        rad = Math.max(1, (int) Math.round(rot));
        kol = (int) Math.round((double) antall / this.rad);
        delta = tilgjenligBredde / kol;
        this.avstand = avstand;
    }

    @Override
    public Punkt[] finnPunkter() {
        Punkt[] p = new Punkt[rad*kol];

        for (int x = 0; x < kol; x++) {
            for (int y = 0; y < rad; y++) {
                p[x*rad + y] = new Punkt(delta * x + avstand, delta * y + avstand);
            }
        }
        return p;
    }
}

class SpiralModell extends RandomModell {

    private final double vinkel;
    public static final double GYLENDESNITT = (1 + Math.sqrt(5)) / 2 ;

    public SpiralModell(int antall, int lengde, int bredde, int avstand) {
        this(antall, lengde, bredde, avstand, GYLENDESNITT);
    }

    public SpiralModell(int antall, int bredde, int høyde, int avstand, double snitt) {
        super(antall, bredde, høyde, avstand);
        vinkel = 2 * Math.PI * (1 - 1 / snitt);
    }

    @Override
    public Punkt[] finnPunkter() {
        Punkt[] p = new Punkt[ant];
        double c = (Math.min(bredde, høyde) - avstand) / (2 * Math.sqrt(ant - 1));
        int x = bredde / 2;
        int y = høyde / 2;

        for (int i = 0; i < ant; i++) {
            double r = c * Math.sqrt(i);
            double theta = i * vinkel;
            p[i] = new Punkt(r * Math.cos(theta) + x, r * Math.sin(theta) + y);
        }
        return p;
    }

}