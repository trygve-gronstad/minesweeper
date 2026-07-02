class Punkt implements Comparable<Punkt> {
    
    public final double x;
    public final double y;

    public Punkt(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double square() {
        return x*x + y*y;
    }

    @Override
    public String toString() {
        return String.format("(%.0f, %.0f)", x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Punkt) {
            Punkt annen = (Punkt) o;
            return Math.abs(annen.x - x) < 0.000001 && Math.abs(annen.y - y) < 0.000001;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 31 * (int) Math.round(x * 100) + (int) Math.round(y * 100);
    }

    public Punkt addisjon(double i) {
        return new Punkt(x + i, y + i);
    }

    public Punkt addisjon(Punkt p) {
        return new Punkt(x + p.x, y + p.y);
    }

    public Punkt neg() {
        return new Punkt(-x, -y);
    }

    public double sign(Punkt A, Punkt B) {
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

        double x, y;
        x = y = Float.MAX_VALUE;
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

        double x, y;
        x = y = Float.MIN_VALUE;
        for (Punkt p: arr) {
            x = Math.max(x, p.x);
            y = Math.max(y, p.y);
        }
        return new Punkt(x, y);
    }

    public static Punkt[] shuffelSort(Punkt[] arr) {
        Punkt[] retur = new Punkt[arr.length];
        if (arr.length < 2) {
            if (arr.length == 0) {
                return retur;
            }
            retur[0] = arr[0];
            return retur;
        }

        int minstIndeks = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[minstIndeks].compareTo(arr[i]) > 0) {
                minstIndeks = i;
            }
        }

        int forrige = minstIndeks - 1 > 0 ? minstIndeks - 1 : arr.length - 1;
        int neste = minstIndeks + 1 < arr.length ? minstIndeks + 1 : 0;
        int nesteIndeks = arr[forrige].compareTo(arr[neste]) < 0 ? forrige : neste;

        int j = 0;
        if (nesteIndeks - minstIndeks > 0) {
            for (int i = minstIndeks; i < arr.length; i++) {
                retur[j] = arr[i];
                j++;
            }
            for (int i = 0; i < minstIndeks; i++) {
                retur[j] = arr[i];
                j++;
            }
        }
        else {
            for (int i = minstIndeks; i >= 0; i--) {
                retur[j] = arr[i];
                j++;
            }
            for (int i = arr.length - 1; i > minstIndeks; i--) {
                retur[j] = arr[i];
                j++;
            }
        }

        return retur;
    }

}