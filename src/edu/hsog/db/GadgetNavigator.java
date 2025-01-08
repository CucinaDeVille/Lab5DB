package edu.hsog.db;

import java.util.ArrayList;
import java.util.List;

public class GadgetNavigator {
    List<DTO> gadgetsEcht;
    private int currentIndex = 0;

    // Konstruktor
    public GadgetNavigator() {
        this.gadgetsEcht = new ArrayList<>();
        this.currentIndex = 0;
    }

    public void loadGadgets(){

        //Liste gadgetsEcht befüllen mit Inhalt
        gadgetsEcht = DBQueries.getBestRatedGadgets();
        System.out.println("Hat geklappt");
    }

    //Methode zum Abrufen des bestbewertetsten Gadgets
    public DTO getBestRatedGadget() {
        int besteBewertung = 0;

        //Gadgets sortiert nach URL → Beste Bewertung suchen
        for (int i = 0; i < gadgetsEcht.size(); i++){
            if (gadgetsEcht.get(i).getAverageRating() > gadgetsEcht.get(besteBewertung).getAverageRating()){
                besteBewertung = i;
                currentIndex = besteBewertung;
            }
        }
        return gadgetsEcht.get(besteBewertung);
    }

    //Zum nächsten Gadget
    public DTO next() {
        if (currentIndex < gadgetsEcht.size() - 1) {
            currentIndex++;
            return gadgetsEcht.get(currentIndex);
        }
        return gadgetsEcht.get(currentIndex);
    }

    //Zum vorherigen Gadget
    public DTO previous() {
        if (currentIndex > 0) {
            currentIndex--;
            return gadgetsEcht.get(currentIndex);
        }
        return gadgetsEcht.get(currentIndex);
    }

    //Erstes Gadget zurückgeben
    public DTO getFirst() {
        currentIndex = 0;
        return gadgetsEcht.getFirst();
    }

    //Letztes Gadget zurückgeben
    public DTO getLastGadget() {
        currentIndex = gadgetsEcht.size() - 1;
        return gadgetsEcht.getLast();
    }

    //aktuelles Gadget laden
    public DTO getCurrentGadget(){
        return gadgetsEcht.get(currentIndex);
    }
}
