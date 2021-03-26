package metier;

import ihm.*;


import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Random;

public class Planete
{
    private Coordonnees coord;
    private int taille;
    private int imgP;
    private  static final  int TAILLE_FRAME = FrameUnivers.HEIGHT;
    private static final int MIN = 25;
    private static final int MAX = 75;
    private boolean pandora;
    private boolean enChoc = false;

    /*-----
    ATTRIBUTS POUR LE MOUVEMENT
    -----*/

    private Thread threadDep;
    private Thread threadCol;

    //rotation
    public double angleRotation;

    //position
    private double posX;
    private double posY;

    //vitesse
    private final double MAX_VITESSE = 0.20;
    private final double MIN_VITESSE = 0.01;
    private Vecteur vitesse;
    double  randVitesse;

    //acceleration
    private double acceleration = 0;
    private double decelerarion = 0;

    //temps
    private double deltaT;
    private double tpsDebut;
    private double tpsFin;

    private boolean calculCollisionFait;
    private double mass;

    /*--------------------------------*/

    private PanelUnivers panelUnivers;

    public boolean debug;

    public Planete(boolean pandora, PanelUnivers p)
    {
    	this.pandora = pandora;
        this.panelUnivers = p;

        this.imgP = (int)(Math.random()*5+1);
        Random r = new Random();
        int low = -180;
        int high = 180;
        this.angleRotation = r.nextInt(high-low) + low;

        this.randVitesse = 0.1;//MIN_VITESSE + (MAX_VITESSE - MIN_VITESSE) * r.nextDouble();
        this.vitesse = new Vecteur(randVitesse*Math.cos(Math.toRadians(angleRotation)), randVitesse*Math.sin(Math.toRadians(angleRotation)));

        int y;
        int x;

        if (this.estPandora()){

            x = 300+(int)(Math.random() * ( TAILLE_FRAME - 300));
            y = 200+(int)(Math.random() * ( TAILLE_FRAME - 200));
            this.coord = new Coordonnees(x,y);
            this.taille = 50;
        }
        else {

            y = (int)(Math.random()*TAILLE_FRAME);

            if(y <= 350 && y >= 150)
            {
                do{
                    x = (int)(Math.random()*TAILLE_FRAME);
                }while(x <= 350 && x >= 150);
            }else
                x = (int)(Math.random()*TAILLE_FRAME);
        }

        this.coord = new Coordonnees(x,y);

        this.posX = coord.getX();
        this.posY = coord.getY();

        this.taille = (int)(MIN + (Math.random() * (MAX - MIN)));
        this.coord = new Coordonnees(x,y);

        //System.out.println(taille);
        //this.mass = Math.PI * (this.taille/2)*(this.taille/2);

        }



    public void setCoord(Coordonnees coord) {
        this.coord = coord;
    }

    public Coordonnees getCoord() {
        return coord;
    }

    public int getTaille() {
        return taille;
    }


    public void startDeplacementPlanete()
    {
        /* ----------------------
            THREAD DE DEPLACEMENT
           ----------------------
         */
        this.threadDep = new Thread(() ->
        {
            Planete coli = null;
            Boolean choque = false;
            while (true)
            {
                Planete p = this;
                /*if(coli != null && this.isEnChoc() && Math.sqrt( ((p.getPosX() - coli.getPosX()) * (p.getPosX() - coli.getPosX())) + ( (p.getPosY() - coli.getPosY()) * (p.getPosY() - coli.getPosY()))) > (p.getTaille() / 2 + coli.getTaille() / 2)) {
                    coli.setEnChoc(false);
                    this.setEnChoc(false);
                    coli = null;
                    System.out.println("Plus en choc connard");
                }
                else if(coli != null){
                    //System.out.println("aie fdp");
                }*/
                if(choque && Math.sqrt( ((p.getPosX() - coli.getPosX()) * (p.getPosX() - coli.getPosX())) + ( (p.getPosY() - coli.getPosY()) * (p.getPosY() - coli.getPosY()))) > (p.getTaille() / 2 + coli.getTaille() / 2))
                  choque = false;
                for(Planete p2 : panelUnivers.getPlanetes()) {
                    if(p != p2 && !p.estPandora() && !p2.estPandora()) {
                        double distance = Math.sqrt( ((p.getPosX() - p2.getPosX()) * (p.getPosX() - p2.getPosX())) + ( (p.getPosY() - p2.getPosY()) * (p.getPosY() - p2.getPosY())));

                        if (distance <= (p.getTaille() / 2 + p2.getTaille() / 2) && !choque/*&& coli != p2*/ /*&& !this.enChoc/*&& pco1 != p && p2 != pco2 && pco2 != p && pco1 != p2*/) {
                            //QDM

                            Vecteur pp1 = p.getVitesse().multiplication(p.getMass());
                            //System.out.println("pp1 : " + pp1);
                            Vecteur pp2 = p2.getVitesse().multiplication(p2.getMass());
                            Vecteur pp0 = pp1.addition(pp2);

                            //Energie cinetique
                            //double Ec = ((pp1.getvX()*pp1.getvX() + pp1.getvY()*pp1.getvY()) / (2*p.getMass())) + ((pp2.getvX()*pp2.getvX() + pp2.getvY()*pp2.getvY()) / (2*p2.getMass()));
                            //double Ec = ((Math.pow(pp1.getvX(), 2) + (Math.pow(pp1.getvY(), 2))) / (2 * p.getMass())) + ((Math.pow(pp2.getvX(), 2) + (Math.pow(pp2.getvY(), 2))) / (2 * p2.getMass()));
                            double Ec = (((pp1.getvX()*pp1.getvX() + pp1.getvY()*pp1.getvY()) / (2*p.getMass())) + ((pp2.getvX()*pp2.getvX() + pp2.getvY()*pp2.getvY()) / (2*p2.getMass())));

                            //pPrimeY
                            double p1PrimeY = pp1.getvY();
                            double p2PrimeY = pp2.getvY();

                            //pPrimeX
                            //double p1PrimeX = (p.getMass() * pp0.getvX() - Math.pow(((p.getMass() + p2.getMass()) * (2 * p.getMass() * p2.getMass() * Ec - p2.getMass() * Math.pow(pp1.getvY(), 2) - p.getMass() * Math.pow(pp2.getvY(), 2)) - p.getMass() * p2.getMass() * Math.pow(pp0.getvX(), 2)), 1 / 2)) / (p.getMass() + p2.getMass());
                            //double p2PrimeX = (p2.getMass() * pp0.getvX() + Math.pow(((p.getMass() + p2.getMass()) * (2 * p.getMass() * p2.getMass() * Ec - p2.getMass() * Math.pow(pp1.getvY(), 2) - p.getMass() * Math.pow(pp2.getvY(), 2)) - p.getMass() * p2.getMass() * Math.pow(pp0.getvX(), 2)), 1 / 2)) / (p.getMass() + p2.getMass());
                            /*double etapePuissance1Demi = Math.pow(((p.getMass() + p2.getMass())*(2*p.getMass()*p2.getMass()*Ec - p2.getMass()*(pp1.getvY()*pp1.getvY()) - p.getMass()*(pp2.getvY()*pp2.getvY())) - p.getMass()*p2.getMass()*(pp0.getvX()*pp0.getvX())),0.5);

                            double p1PrimeX = (p.getMass()*pp0.getvX() - etapePuissance1Demi )/ (p.getMass() +p2.getMass());
                            double p2PrimeX = (p2.getMass()*pp0.getvX() + etapePuissance1Demi)/ (p.getMass() + p2.getMass());*/

                            /*double etapePuissance1Demi =(long) Math.pow(((p.getMass() + p2.getMass())*(2*p.getMass()*p2.getMass()*Ec - p2.getMass()*(pp1.getvY()*pp1.getvY()) - p.getMass()*(pp2.getvY()*pp2.getvY())) - p.getMass()*p2.getMass()*(pp0.getvX()*pp0.getvX())),0.5);
                            double p1PrimeX =(long) ((p.getMass()*pp0.getvX() - etapePuissance1Demi )/ (p.getMass() +p2.getMass()));
                            double p2PrimeX =(long) ((p2.getMass()*pp0.getvX() + etapePuissance1Demi)/ (p.getMass() + p2.getMass()));*/
                            double etapePuissance1Demi = Math.pow(((p.getMass() + p2.getMass())*(2*p.getMass()*p2.getMass()*Ec - p2.getMass()*(pp1.getvY()*pp1.getvY()) - p.getMass()*(pp2.getvY()*pp2.getvY())) - p.getMass()*p2.getMass()*(pp0.getvX()*pp0.getvX())),0.5);
                            double p1PrimeX = ((p.getMass()*pp0.getvX() - etapePuissance1Demi )/ (p.getMass() +p2.getMass()));
                            double p2PrimeX = ((p2.getMass()*pp0.getvX() + etapePuissance1Demi)/ (p.getMass() + p2.getMass()));
                            //angles
                            double thetaPrime1 = Math.atan(p1PrimeY / p1PrimeX);
                            double thetaPrime2 = Math.atan(p2PrimeY / p2PrimeX);


                            if(p1PrimeX > -0 && !(p1PrimeY >-0) || !(p1PrimeX > -0)&& (p1PrimeY >-0)) thetaPrime1 += Math.PI;
                            /*if(p1PrimeX < -0 ) {
                                thetaPrime1 -= Math.PI/2;
                                if(p1PrimeY < -0) thetaPrime1 -= Math.PI/2;
                            }*/



                            p.angleRotation = Math.toDegrees(thetaPrime1);
                            p2.angleRotation = Math.toDegrees(thetaPrime2);

                            //vitesses
                            Vecteur vitesseTemp1=new Vecteur(p.getRandVitesse() * Math.cos(thetaPrime1), p.getRandVitesse() * Math.sin(thetaPrime1));
                            //Vecteur vitesseTemp2=new Vecteur(p2.getRandVitesse() * Math.cos(thetaPrime2), p2.getRandVitesse() * Math.sin(thetaPrime2));
                            p.setVitesse(vitesseTemp1);
                            //p2.setVitesse(vitesseTemp2);

                            coli = p2;
                            //System.out.println("energie cinetique : " + Ec);
                            //System.out.println("vitesse get :" + p2.getVitesse());
                            //System.out.println("vitesse calculé: " + vitesseTemp1);

                            //coli.setEnChoc(true);
                            //this.setEnChoc(true);

                            choque = true;
                        }
                    }
                }
                //On calcul le decalage de temps avec la derniere iteration de boucle
                tpsDebut = System.currentTimeMillis();
                deltaT = tpsDebut - tpsFin;

                if(!choque) {
                    vitesse.setvX(vitesse.getvX() + acceleration * Math.cos(Math.toRadians(angleRotation)));
                    vitesse.setvY(vitesse.getvY() + acceleration * Math.sin(Math.toRadians(angleRotation)));
                }

                //On calcul la nouvelle position
                posX = posX + (vitesse.getvX() * deltaT);
                posY = posY + (vitesse.getvY() * deltaT);

                panelUnivers.repaint();

                tpsFin = System.currentTimeMillis();
                try {Thread.sleep(10);} catch(InterruptedException e){e.printStackTrace();}
            }
        });
        this.threadDep.start();

    }


    public void stopDeplacement()
    {
        this.threadDep.stop();
    }


    public Thread getThreadDep() {
        return threadDep;
    }

    public double getPosX() {
        return posX;
    }

    public double getPosY() {
        return posY;
    }

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public void setPosY(double posY) {
        this.posY = posY;
    }

    public int getImgP() {
        return imgP;
    }
    public boolean estPandora(){return this.pandora;}

    public Vecteur getVitesse() {
        return vitesse;
    }

    public void setVitesse(Vecteur vitesse) {
        this.vitesse = vitesse;
    }

    public double getMass() {
        return Math.PI * (this.taille/2)*(this.taille/2);
    }

    public double getRandVitesse() {
        return randVitesse;
    }

    public boolean isEnChoc() {
        return enChoc;
    }

    public void setEnChoc(boolean enChoc) {
        this.enChoc = enChoc;
    }

    public void setTaille(int taille) {
        this.taille = taille;
    }

    @Override
    public String toString() {
        return "Planete{" +
                "coord=" + coord +
                ", taille=" + taille +
                ", imgP=" + imgP +
                ", pandora=" + pandora +
                ", enChoc=" + enChoc +
                ", threadDep=" + threadDep +
                ", threadCol=" + threadCol +
                ", angleRotation=" + angleRotation +
                ", posX=" + posX +
                ", posY=" + posY +
                ", MAX_VITESSE=" + MAX_VITESSE +
                ", MIN_VITESSE=" + MIN_VITESSE +
                ", vitesse=" + vitesse +
                ", randVitesse=" + randVitesse +
                ", acceleration=" + acceleration +
                ", decelerarion=" + decelerarion +
                ", deltaT=" + deltaT +
                ", tpsDebut=" + tpsDebut +
                ", tpsFin=" + tpsFin +
                ", calculCollisionFait=" + calculCollisionFait +
                ", mass=" + mass +
                ", panelUnivers=" + panelUnivers +
                ", debug=" + debug +
                '}';
    }
}
