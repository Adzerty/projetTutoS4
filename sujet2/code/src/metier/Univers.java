package metier;

import ihm.FrameNombrePlanetes;
import ihm.FrameUnivers;

import java.awt.*;

public class Univers
{
    private int nbPlanete = 0;
    private FrameUnivers FrameUnivers;

    public Univers()
    {
        FrameNombrePlanetes choixNombre = new FrameNombrePlanetes(this);
    }

    public void openFrameUniv()
    {
        this.FrameUnivers = new FrameUnivers(nbPlanete);
        new IA(this.FrameUnivers);
    }
    public void setNbPlanete(int nbPlanete) {
        this.nbPlanete = nbPlanete;
    }


    public static void main(String[] args) {
        new Univers();
    }
}
