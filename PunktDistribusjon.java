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