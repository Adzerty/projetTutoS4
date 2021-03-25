package metier;

import ihm.*;


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

        this.randVitesse = MIN_VITESSE + (MAX_VITESSE - MIN_VITESSE) * r.nextDouble();
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

        this.mass = Math.PI * Math.pow(this.taille/2,2);

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
            while (true)
            {
                if(debug)
                {
                    System.out.println(angleRotation + " : " + vitesse);
                }
                //On calcul le decalage de temps avec la derniere iteration de boucle
                tpsDebut = System.currentTimeMillis();
                deltaT = tpsDebut - tpsFin;

                vitesse.setvX(vitesse.getvX() + acceleration * Math.cos(Math.toRadians(angleRotation)));
                vitesse.setvY(vitesse.getvY() + acceleration * Math.sin(Math.toRadians(angleRotation)));

                //On calcul la nouvelle position
                posX = posX + (vitesse.getvX() * deltaT);
                posY = posY + (vitesse.getvY() * deltaT);

                //Calcul d'un nouveau vecteur permettant la deceleration
                /*Vecteur vitInit = new Vecteur(vitesse.getvX(), vitesse.getvY());
                double vitFinal = vitInit.getNorme() - (deltaT * decelerarion);

                // On reduit la vitesse
                f(vitInit.getNorme() != 0)
                {
                    vitesse.setvX(vitesse.getvX() * (vitFinal / vitInit.getNorme()));
                    vitesse.setvY(vitesse.getvY() * (vitFinal / vitInit.getNorme()));
                }

                //On stop la fusee quand elle arrive a < min_vitesse
                if(vitesse.getNorme() < MIN_VITESSE)
                {
                    vitesse.setvX(0);
                    vitesse.setvY(0);
                }*/

                //System.out.println((int)posX + " : " + (int)posY);
                //On repeint la toile

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
        return mass;
    }

    public double getRandVitesse() {
        return randVitesse;
    }
}
