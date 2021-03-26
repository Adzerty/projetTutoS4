package metier;

import ihm.FrameUnivers;

import java.awt.*;

public class IA {

    private Planete pandora;
    private Vaisseau vaisseau;

    private Coordonnees coordBary;
    private Coordonnees coordPic;
    private Coordonnees coordPand;

    public IA(FrameUnivers frameUnivers){

        this.vaisseau = frameUnivers.getPanelUniv().getVaisseau();


        for (Planete p: frameUnivers.getPanelUniv().getPlanetes()) {
            if (p.estPandora()){
               this.pandora = p;
            }

        }

        this.coordBary = new Coordonnees((vaisseau.getPosX()+ vaisseau.getxBarycentre()),(vaisseau.getPosX()+ vaisseau.getyBarycentre()));
        this.coordPic  = new Coordonnees((vaisseau.getPosX()+ vaisseau.getxBarycentre()),((int)this.pandora.getPosY()+(this.pandora.getTaille()/2)));
        this.coordPand = new Coordonnees(((int)this.pandora.getPosX()+(this.pandora.getTaille()/2)),((int)this.pandora.getPosY()+(this.pandora.getTaille()/2)));


        //double angle = Math.atan(Math.tan((this.coordPand.getY()-this.coordBary.getY())/(this.coordPand.getX()-this.coordBary.getX())));


        //Bary
        double a = Math.sqrt(Math.pow((this.coordPand.getX()-this.coordPic.getX()),2)+Math.pow((this.coordPand.getY()-this.coordPic.getY()),2));
        //Pique
        double b = Math.sqrt(Math.pow((this.coordBary.getX()-this.coordPand.getX()),2)+Math.pow((this.coordBary.getY()-this.coordPand.getY()),2));
        //Pandora
        double c = Math.sqrt(Math.pow((this.coordBary.getX()-this.coordPic.getX()),2)+Math.pow((this.coordBary.getY()-this.coordPic.getY()),2));

        double angle = Math.cos(((Math.pow(a,2)+Math.pow(c,2))-Math.pow(c,2))/(2*b*c));

        System.out.println(Math.toDegrees(angle));
        System.out.println(Math.toDegrees(Math.abs(angle)));
        System.out.println(Math.toDegrees(Math.cos(angle)));
        System.out.println(Math.toDegrees(Math.acos(angle)));

        System.out.println(this.coordPand.getX());
        System.out.println(this.coordPic.getX());
        System.out.println(a);

        if (this.coordBary.getY()  > this.coordPand.getY())
        {
            this.vaisseau.addAngleRot(-Math.toDegrees(angle));
        }
        else{
            this.vaisseau.addAngleRot(Math.toDegrees(angle));
        }


       

        /*
        System.out.println("Vaisseau X : "+(this.vaisseau.getPosX())+" Y : "+this.vaisseau.getPosY());
        System.out.println("BarryCentre X : "+this.vaisseau.getxBarycentre()+" Y : "+this.vaisseau.getyBarycentre());
        System.out.println("Pandora X : "+this.pandora.getPosX()+" Y : "+this.pandora.getPosY());


        this.fusee.setPosX((int)this.pandora.getPosX());
        this.fusee.setPosY((int)this.pandora.getPosY());

         */

    }
}
