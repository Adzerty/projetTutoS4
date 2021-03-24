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
                Vecteur vitInit = new Vecteur(vitesse.getvX(), vitesse.getvY());
                double vitFinal = vitInit.getNorme() - (deltaT * decelerarion);

                // On reduit la vitesse
                if(vitInit.getNorme() != 0)
                {
                    vitesse.setvX(vitesse.getvX() * (vitFinal / vitInit.getNorme()));
                    vitesse.setvY(vitesse.getvY() * (vitFinal / vitInit.getNorme()));
                }

                //On stop la fusee quand elle arrive a < min_vitesse
                if(vitesse.getNorme() < MIN_VITESSE)
                {
                    vitesse.setvX(0);
                    vitesse.setvY(0);
                }

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

    public void checkCollisionsPlanetes()
    {
        this.threadCol = new Thread(() ->
        {
            while (true)
            {
                ArrayList<Planete> ensPlanete = panelUnivers.getPlanetes();

                for(Planete p : ensPlanete)
                {
                    if(p != this && !p.estPandora())
                    {
                        //On calcul la distance entre la planete et chaque pixel du vaisseau
                        double distance = Math.sqrt( Math.pow(posX - p.posX, 2) + Math.pow(posY - p.posY, 2) );

                        if(distance <= ((p.getTaille()/2) + (getTaille()/2)))
                        {
                            if(!calculCollisionFait)
                            {
                                calculCollisionFait = true;
                                //QDM
                                Vecteur p1 = vitesse.multiplication(mass);
                                Vecteur p2 = p.vitesse.multiplication(p.mass);
                                Vecteur p0 = p1.addition(p2);

                                //Energie cinetique
                                double Ec = ((Math.pow(p1.getvX(),2) + (Math.pow(p1.getvY(),2))) / (2*mass)) + ((Math.pow(p2.getvX(),2) + (Math.pow(p2.getvY(),2))) / (2*p.mass));

                                //pPrimeY
                                double p1PrimeY = p1.getvY();
                                double p2PrimeY = p2.getvY();

                                //pPrimeX
                                double p1PrimeX = (mass*p0.getvX() - Math.pow(((mass + p.mass)*(2*mass*p.mass*Ec - p.mass*Math.pow(p1.getvY(),2) - mass*Math.pow(p2.getvY(),2)) - mass*p.mass*Math.pow(p0.getvX(),2)),1/2) / (mass + p.mass));
                                double p2PrimeX = (p.mass*p0.getvX() - Math.pow(((mass + p.mass)*(2*mass*p.mass*Ec - p.mass*Math.pow(p1.getvY(),2) - mass*Math.pow(p2.getvY(),2)) - mass*p.mass*Math.pow(p0.getvX(),2)),1/2) / (mass + p.mass));

                                //angles
                                double thetaPrime1 = Math.atan(p1PrimeY / p1PrimeX);
                                //double thetaPrime2 = Math.toDegrees(Math.atan(p2PrimeY / p2PrimeX));

                                this.angleRotation = thetaPrime1;
                                //p.angleRotation = thetaPrime2;

                                //vitesses
                                vitesse = new Vecteur(randVitesse*Math.cos(Math.toRadians(angleRotation)), randVitesse*Math.sin(Math.toRadians(angleRotation)));
                                //p.vitesse = new Vecteur(p.randVitesse*Math.cos(Math.toRadians(p.angleRotation)), p.randVitesse*Math.sin(Math.toRadians(p.angleRotation)));



                            }

                        }
                        else
                        {
                            calculCollisionFait = false;
                        }
                    }

                }
            }
        });
        this.threadCol.start();
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
}
