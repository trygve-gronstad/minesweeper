class Punkt implements Comparable<Punkt> {
    
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

    public Punkt addisjon(Punkt p) {
        return new Punkt(x + p.x, y + p.y);
    }

    public Punkt neg() {
        return new Punkt(-x, -y);
    }

    public int sign(Punkt A, Punkt B) {
        return (x - B.x) * (A.y - B.y) - (A.x - B.x) * (y - B.y);
    }

    @Override
    public int compareTo(Punkt annet) {
        if (x < annet.x) {
            return -1;
        }
        if (x > annet.x) {
            return 1;
        }

        if (y < annet.y) {
            return -1;
        }
        if (y > annet.y) {
            return 1;
        }

        return 0;
    }

    public static Punkt min(Punkt... arr) {
        if (arr.length < 1) {
            throw new IllegalArgumentException();
        }

        int x, y;
        x = y = Integer.MAX_VALUE;
        for (Punkt p: arr) {
            x = Math.min(x, p.x);
            y = Math.min(y, p.y);
        }
        return new Punkt(x, y);
    }

    public static Punkt max(Punkt... arr) {
        if (arr.length < 1) {
            throw new IllegalArgumentException();
        }

        int x, y;
        x = y = Integer.MIN_VALUE;
        for (Punkt p: arr) {
            x = Math.max(x, p.x);
            y = Math.max(y, p.y);
        }
        return new Punkt(x, y);
    }

}