import java.util.List;
import java.util.ArrayList;

class Punkt {
    
    public final int x;
    public final int y;
    private final List<Punkt> naboer = new ArrayList<>();

    public Punkt(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void leggTilNabo(Punkt p) {
        naboer.add(p);
    }

    public Punkt finnSenter() {
        if (naboer.size() != 2) {
            throw new IllegalArgumentException(this + " har " + naboer.size() + " naboer, det må være 2 naboer");
        }

        Punkt A = naboer.get(0);
        Punkt C = naboer.get(1);

        int d = 2 * (A.x * (this.y - C.y) + this.x * (C.y - A.y) + C.x * (A.y - this.y));
        int h = (A.square()*(this.y - C.y) + square()*(C.y - A.y) + C.square()*(A.y - this.y)) / d;
        int k = (A.square()*(C.x - this.x) + square()*(A.x - C.x) + C.square()*(this.x - A.x)) / d;

        return new Punkt(h, k);

    }

    private int square() {
        return x*x + y*y;
    }

    @Override
    public String toString() {
        return String.format("(%d, %d)", x, y);
    }


}