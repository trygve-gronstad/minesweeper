import java.util.*;

public class Controller {
    
    private List<Rute> alleRuter = new ArrayList<>();
    private final int ANT_MINER;
    private final View DISPLAY = new View(this);

    public Controller(PunktDistribusjon m, int antMiner) {
        ANT_MINER = antMiner;
        init(m);
        lagRuter();
    }

    private void init(PunktDistribusjon m) {
        Rutenett fg = new Rutenett(m);
        Collection<MangeKant> poly = fg.finnMangeKant();
        for (MangeKant p: poly) {
            alleRuter.add(new Rute(p));
        }
    }

    private void lagRuter() {
        for (int i = 0; i < alleRuter.size(); i++) {
            DISPLAY.leggTilKnapp(alleRuter.get(i).hentPolygon(), i);
        }
        DISPLAY.lagtTilKnapper();
    }

    
}
