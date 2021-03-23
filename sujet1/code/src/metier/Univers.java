package metier;

import ihm.FrameNombrePlanetes;
import ihm.FrameUnivers;

public class Univers
{
    private int nbPlanete = 0;
    public Univers()
    {
        FrameNombrePlanetes choixNombre = new FrameNombrePlanetes(this);
    }

    public void openFrameUniv()
    {
        new FrameUnivers(nbPlanete);
    }
    public void setNbPlanete(int nbPlanete) {
        this.nbPlanete = nbPlanete;
    }


    public static void main(String[] args) {
        new Univers();
    }
}
