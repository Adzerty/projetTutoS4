package metier;

import ihm.*;


import java.util.Random;

public class Planete
{
    private Coordonnees coord;
    private int taille;
    private int imgP;
    private  static final  int TAILLE_FRAME = FrameUnivers.HEIGHT;
    private static final int MIN = 25;
    private static final int MAX = 75;

    /*-----
    ATTRIBUTS POUR LE MOUVEMENT
    -----*/

    private Thread threadDep;

    //rotation
    public double angleRotation;

    //position
    private double posX;
    private double posY;

    //vitesse
    private final double MAX_VITESSE = 0.20;
    private final double MIN_VITESSE = 0.01;
    private Vecteur vitesse;

    //acceleration
    private double acceleration = 0;
    private double decelerarion = 0;

    //temps
    private double deltaT;
    private double tpsDebut;
    private double tpsFin;

    /*--------------------------------*/

    private PanelUnivers panelUnivers;

    public Planete(PanelUnivers p)
    {
        this.panelUnivers = p;

        this.imgP = (int)(Math.random()*5+1);
        Random r = new Random();
        int low = -180;
        int high = 180;
        this.angleRotation = r.nextInt(high-low) + low;

        double randVitesse = MIN_VITESSE + (MAX_VITESSE - MIN_VITESSE) * r.nextDouble();
        this.vitesse = new Vecteur(randVitesse*Math.cos(angleRotation), randVitesse*Math.sin(angleRotation));

        int y = (int)(Math.random()*TAILLE_FRAME);
        int x;

        if(y <= 350 && y >= 150)
        {
            do{
                x = (int)(Math.random()*TAILLE_FRAME);
            }while(x <= 350 && x >= 150);
        }else
            x = (int)(Math.random()*TAILLE_FRAME);

        this.coord = new Coordonnees(x,y);

        this.posX = coord.getX();
        this.posY = coord.getY();

        this.taille = (int)(MIN + (Math.random() * (MAX - MIN)));
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
                //On calcul le decalage de temps avec la derniere iteration de boucle
                tpsDebut = System.currentTimeMillis();
                deltaT = tpsDebut - tpsFin;

                if(vitesse.calculNormeAvecDeuxValeurs( //On verifie que la vitesse ne depassera pas la vitesse max
                        vitesse.getvX() + acceleration * Math.cos(Math.toRadians(angleRotation)),
                        vitesse.getvY()) + acceleration * Math.sin(Math.toRadians(angleRotation))
                        < MAX_VITESSE )
                {
                    vitesse.setvX(vitesse.getvX() + acceleration * Math.cos(Math.toRadians(angleRotation)));
                    vitesse.setvY(vitesse.getvY() + acceleration * Math.sin(Math.toRadians(angleRotation)));
                }

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
}
