package hr.fer.hom.project.cvrptw.operators;

import hr.fer.hom.project.cvrptw.dataClasses.Solution;

public class RelocateCustomerInterOp {

    private Solution solution;

    public RelocateCustomerInterOp(Solution solution){
        this.solution = solution;
    }

    /*
    1.izaberi neku rutu s malo korisnika
    2.izaberi korisnike iz te rute
    3.pokusaj ubaciti tog korisnika u neku drugu rutu s vecim brojem korisnika (for petlja)
    4.ako je ubacivanje uspjelo provjeri da li je ruta ostala prazna (samo 2 skladista)
    5.ako je ruta ostala prazna, izbrisati je, tj ne ubacivati novu
     */
    public Solution run(){
        return this.solution;
    }
}
