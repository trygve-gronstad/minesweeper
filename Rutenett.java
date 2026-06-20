import java.util.Arrays;
import java.util.Comparator;

class Rutenett {

    private final Punkt[] sortertX;
    private final Punkt[] sortertY;

    
    public Rutenett(int antall, PunktDistribusjon modell) {
        Punkt[] p = finnPunkter(antall, modell);

        sortertX = Arrays.copyOf(p, p.length);
        sortertY = p;

        Arrays.sort(sortertX, Comparator.comparingInt(punkt -> punkt.x));
        Arrays.sort(sortertY, Comparator.comparingInt(punkt -> punkt.y));

    }

    public Rutenett(int antall, int bredde, int høyde) {
        this(antall, new RandomModell(bredde, høyde));
    }

    private Punkt[] finnPunkter(int antall, PunktDistribusjon modell) {
        Punkt[] punkter = new Punkt[antall];

        for (int i = 0; i < antall; i++) {
            punkter[i] = modell.neste();
        }

        return punkter;
    }

    @Override
    public String toString() {
        return String.format("x: %s, y: %s", Arrays.toString(sortertX), Arrays.toString(sortertY));
    }

    public Punkt[] punkterIIntervall(Punkt A, Punkt B) {
        int xStart = finnIndeks(sortertX, A);
        int xSlutt = finnIndeks(sortertX, B) + 1; //plusser på 1 for å få inklusiv

        Punkt[] punkter = Arrays.copyOfRange(sortertX, xStart, xSlutt);
        
        return Arrays.stream(punkter)
            .filter(p -> p.y >= A.y && p.y <= B.y)
            .toArray(Punkt[]::new);
    }

    public Punkt[] punkterIIntervall(int x1, int y1, int x2, int y2) {
        return punkterIIntervall(new Punkt(x1, y1), new Punkt(x2, y2));
    }

    private int finnIndeks(Punkt[] arr, Punkt p) {
        Comparator<Punkt> x = Comparator.comparingInt(punkt -> punkt.x);
        int i = Arrays.binarySearch(arr, p, x);
        if (i < 0) {
            return -(i + 1);
        }
        return i;
    }

}


