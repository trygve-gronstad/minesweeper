class Punkt {
    
    public final int x;
    public final int y;

    public Punkt(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int square() {
        return x*x + y*y;
    }

    @Override
    public String toString() {
        return String.format("(%d, %d)", x, y);
    }

    public Punkt addisjon(int i) {
        return new Punkt(x + i, y + i);
    }


}