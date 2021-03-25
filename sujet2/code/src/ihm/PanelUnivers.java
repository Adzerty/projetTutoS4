package ihm;

import metier.*;

import javax.imageio.ImageIO;
import metier.Coordonnees;
import metier.Planete;
import metier.Vaisseau;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;


public class PanelUnivers extends JPanel implements KeyListener
{
    private ArrayList<Planete> planetes;
    private Vaisseau vaisseau;
    private Coordonnees coods;

    private boolean aTouche;
    private boolean gameOver;

    private int posXpandora;
    private int posYpandora;

    private BufferedImage explosion;

    private int width;
    private int height;

    private final int ANGLE_ROTATION = 5;



    private double angleOffset = (3*Math.PI)/2;

    public PanelUnivers(int nbPlanetes)
    {
        this.width = FrameUnivers.WIDTH;
        this.height = FrameUnivers.HEIGHT;

        this.vaisseau = Vaisseau.getInstance();
        this.vaisseau.setPanelUnivers(this);

        this.setBackground(new Color(4,30,60));

        this.planetes = new ArrayList<>();

        //On ajoute les planetes au panel
        /*for (int i=0; i<nbPlanetes; i++) {
            planetes.add(new Planete(false, this));
            planetes.get(i).startDeplacementPlanete();
        }*/

        planetes.add(new Planete(false, this));
        planetes.add(new Planete(false, this));

        planetes.get(0).angleRotation = -180;
        planetes.get(1).angleRotation = 0;

        planetes.get(0).setPosX(0);
        planetes.get(1).setPosX(300);

        planetes.get(0).setPosY(300);
        planetes.get(1).setPosY(300);

        planetes.get(0).setVitesse( new Vecteur(planetes.get(0).getRandVitesse()*Math.cos(Math.toRadians(planetes.get(0).angleRotation)), planetes.get(0).getRandVitesse()*Math.sin(Math.toRadians(planetes.get(0).angleRotation))));
        planetes.get(1).setVitesse( new Vecteur(planetes.get(1).getRandVitesse()*Math.cos(Math.toRadians(planetes.get(1).angleRotation)), planetes.get(1).getRandVitesse()*Math.sin(Math.toRadians(planetes.get(1).angleRotation))));

        planetes.get(0).startDeplacementPlanete();
        planetes.get(1).startDeplacementPlanete();



        //planetes.add(new Planete(true, this));

        //planetes.get(1).debug = true;


        this.addKeyListener(this);

        //On commence le thread de deplacement du vaisseau
        this.vaisseau.startDeplacement();

        //On commence le thread de check de collisions
        //this.checkCollisions();
        this.checkCollisionsPlanetes();
    }

    public void checkCollisions()
    {
        Thread check = new Thread(() ->
        {
            while (!this.gameOver) // Tant que la partie n'est pas finie
            {
                for(Planete p : planetes)//Pour chaque planetes de l'univers du panel
                {
                    Coordonnees coordPlanet = p.getCoord();
                    for(Coordonnees c : this.vaisseau.getEnsCoord()) //On calcul la position du contour du vaisseau
                    {
                        double planX = p.getPosX()+ p.getTaille()/2;
                        double planY = p.getPosY() + p.getTaille()/2;

                        double sin = Math.sin(Math.toRadians(vaisseau.getAngleRot())-Math.PI/2);
                        double cos = Math.cos(Math.toRadians(vaisseau.getAngleRot())-Math.PI/2);

                        double xp = c.getX()-this.vaisseau.getxBarycentre();
                        double yp = c.getY()-this.vaisseau.getyBarycentre();

                        double xpn = xp * cos - yp * sin;
                        double ypn = xp * sin + yp * cos;

                        xp = xpn + this.vaisseau.getPosX() + this.vaisseau.getxBarycentre();
                        yp = ypn + this.vaisseau.getPosY() + this.vaisseau.getyBarycentre();

                        //On calcul la distance entre la planete et chaque pixel du vaisseau
                        double distance = Math.sqrt( Math.pow(planX - xp, 2) + Math.pow(planY - yp, 2) );


                        if(distance <= p.getTaille()/2)//Si le vaisseau touche on met "aTouche"
                        {
                            if (p.estPandora())
                            {
                                this.gameOver = true;
                                this.vaisseau.stopDeplacement();
                            }else {
                                this.aTouche = true;
                            }

                        }
                    }

                }
            }

        });
        check.start();
    }

    public void checkCollisionsPlanetes()
    {
        Thread threadColP = new Thread(() ->
        {
            while (true)
            {
                for(Planete p : planetes)
                {
                    for(Planete p2 : planetes )
                    {
                        if(p != p2 && !p.estPandora() && !p2.estPandora())
                        {

                            //System.out.println(p.getVitesse().toString() + " " + p2.getVitesse().toString());
                            //On calcul la distance entre la planete et chaque pixel du vaisseau
                            double distance = Math.sqrt( ((p.getPosX() - p2.getPosX()) * (p.getPosX() - p2.getPosX())) + ( (p.getPosY() - p2.getPosY()) * (p.getPosY() - p2.getPosY())));

                            if(distance <= (p.getTaille()/2  + p2.getTaille()/2))
                            {

                                //QDM
                                Vecteur pp1 = p.getVitesse().multiplication(p.getMass());
                                Vecteur pp2 = p2.getVitesse().multiplication(p2.getMass());
                                Vecteur pp0 = pp1.addition(pp2);

                                //System.out.println("----------- DEBUG ------------");

                                //System.out.println("p1 vitesse : " + p.getVitesse());
                                //System.out.println("p2 vitesse : " + p2.getVitesse());

                                //System.out.println("p1 angle : " + p.angleRotation);
                                //System.out.println("p2 angle : " + p2.angleRotation);

                                //System.out.println("p1 masse : " + p.getMass());
                                //System.out.println("p2 masse : " + p2.getMass());


                                //System.out.println(" --------------------------- ");

                                //System.out.println("pp1 : " + pp1 );
                                //System.out.println("pp2 : " + pp2 );
                                //System.out.println("pp0 : " + pp0 );


                                //Energie cinetique
                                double Ec = ((pp1.getvX()*pp1.getvX() + pp1.getvY()*pp1.getvY()) / (2*p.getMass())) + ((pp2.getvX()*pp2.getvX() + pp2.getvY()*pp2.getvY()) / (2*p2.getMass()));

                                //System.out.println(" --------------------------- ");

                                //System.out.println("Ec : " + Ec );

                                //pPrimeY
                                double p1PrimeY = pp1.getvY();
                                double p2PrimeY = pp2.getvY();

                                //pPrimeX

                                double etapePuissance1Demi = ((p.getMass() + p2.getMass())*(2*p.getMass()*p2.getMass()*Ec - p2.getMass()*(pp1.getvY()*pp1.getvY()) - p.getMass()*(pp2.getvY()*pp2.getvY())) - p.getMass()*p2.getMass()*(pp0.getvX()*pp0.getvX())) /
                                        ( ((p.getMass() + p2.getMass())*(2*p.getMass()*p2.getMass()*Ec - p2.getMass()*(pp1.getvY()*pp1.getvY()) - p.getMass()*(pp2.getvY()*pp2.getvY())) - p.getMass()*p2.getMass()*(pp0.getvX()*pp0.getvX())) * ((p.getMass() + p2.getMass())*(2*p.getMass()*p2.getMass()*Ec - p2.getMass()*(pp1.getvY()*pp1.getvY()) - p.getMass()*(pp2.getvY()*pp2.getvY())) - p.getMass()*p2.getMass()*(pp0.getvX()*pp0.getvX())));
                                double p1PrimeX = (p.getMass()*pp0.getvX() - etapePuissance1Demi )/ (p.getMass() +p2.getMass());
                                double p2PrimeX = (p2.getMass()*pp0.getvX() + etapePuissance1Demi)/ (p.getMass() + p2.getMass());

                                //System.out.println(" --------------------------- ");

                                //System.out.println("p1PrimeX : " + p1PrimeX );
                                //System.out.println("p2PrimeX : " + p2PrimeX );

                                //angles
                                double thetaPrime1 = Math.atan(p1PrimeY / p1PrimeX);
                                double thetaPrime2 = Math.atan(p2PrimeY / p2PrimeX);


                                if(p1PrimeX < 0)
                                    if(p1PrimeY > 0)
                                        thetaPrime1 += Math.PI/2;
                                    else
                                        thetaPrime1 -= Math.PI/2;

                                if(p2PrimeX< 0)
                                    if(p2PrimeY > 0)
                                        thetaPrime2 -= Math.PI/2;
                                    else
                                        thetaPrime2 += Math.PI/2;

                                //System.out.println(" --------------------------- ");

                                //System.out.println("tPrime1 : " + Math.toDegrees(thetaPrime1));
                                //System.out.println("tPrime2 : " + Math.toDegrees(thetaPrime2));

                                //System.out.println(p1PrimeX + " " + p1PrimeY);
                                //System.out.println(p2PrimeX + " " + p2PrimeY);
                                //System.out.println(Math.toDegrees(thetaPrime1) + " " + Math.toDegrees(thetaPrime2));

                                p.angleRotation = Math.toDegrees(thetaPrime1);
                                p2.angleRotation = Math.toDegrees(thetaPrime2);

                                //vitesses
                                p.setVitesse(  new Vecteur(p.getRandVitesse()*Math.cos(thetaPrime1), p.getRandVitesse()*Math.sin(thetaPrime1)));
                                p2.setVitesse( new Vecteur(p2.getRandVitesse()*Math.cos(thetaPrime2), p2.getRandVitesse()*Math.sin(thetaPrime2)));

                            }

                        }
                    }
                }
            }
        });
        threadColP.start();
    }

    public void setCoods(Coordonnees coods) {
        this.coods = coods;
    }

    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g;

        //Pour chaque planetes on cree un rond
        for (Planete p : planetes) {
            if (p.getPosX() < 0 - p.getTaille())
                p.setPosX(width);
            else if (p.getPosX() >= width + p.getTaille())
                p.setPosX(0);

            if (p.getPosY() < 0 - p.getTaille())
                p.setPosY(height);
            else if (p.getPosY() >= height + p.getTaille())
                p.setPosY(0);

            try {
                BufferedImage imgPlanete = ImageIO.read(PanelUnivers.class.getResourceAsStream("/planete/p" + p.getImgP() + ".png"));
                if (p.estPandora()) {
                    g2.setColor(Color.BLUE);
                    g2.fillOval(p.getCoord().getX(), p.getCoord().getY(), p.getTaille(), p.getTaille());
                    g2.setColor(Color.BLACK);
                } else
                    g.drawImage(imgPlanete, (int) p.getPosX(), (int) p.getPosY(), p.getTaille(), p.getTaille(), this);


            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

            //On tourne le vaisseau selon l'angle
            g2.rotate(Math.toRadians(vaisseau.getAngleRot()) + angleOffset, vaisseau.getxBarycentre() + vaisseau.getPosX(), vaisseau.getyBarycentre() + vaisseau.getPosY());

            //On calcul la position en mode torique
            if (vaisseau.getPosX() < 0 - vaisseau.getImage().getHeight())
                vaisseau.setPosX(width);
            else if (vaisseau.getPosX() >= width + vaisseau.getImage().getHeight())
                vaisseau.setPosX(0);

            if (vaisseau.getPosY() < 0 - vaisseau.getImage().getHeight())
                vaisseau.setPosY(height);
            else if (vaisseau.getPosY() >= height + vaisseau.getImage().getHeight())
                vaisseau.setPosY(0);


            //On dessine le vaisseau
            g2.drawImage(vaisseau.getImage(), vaisseau.getPosX(), vaisseau.getPosY(), this);


            if (this.aTouche) //Si le vaisseau touche
            {
                //On affiche le .gif d'explosion et on arrete tout le jeu
                Image icon2 = new ImageIcon(Vaisseau.class.getResource("/explosion.gif")).getImage();
                g2.drawImage(icon2, this.vaisseau.getPosX() - 75, this.vaisseau.getPosY() - 152, this);
                this.vaisseau.stopDeplacement();

                for (Planete p1 : planetes)
                {
                    if(p1.getThreadDep() != null)
                        p1.stopDeplacement();
                }
                this.gameOver = true;
            }

            if (!gameOver) //Si le jeu n'est pas game over on gere l'affichage en transparence de la flamme
            {
                float vitesse = (float) vaisseau.getVitesse().getNorme() * 10;
                if (vitesse > 1) vitesse = 1;

                g2.setComposite(AlphaComposite.SrcOver.derive(vitesse));
                g2.drawImage(vaisseau.getPropeling(), vaisseau.getPosX(), vaisseau.getPosY(), this);
            }
            else
            {
                if(! aTouche)
                {
                    Image icon2 = new ImageIcon(Vaisseau.class.getResource("/confetti.gif")).getImage();
                    g2.drawImage(icon2, this.vaisseau.getPosX() - 120, this.vaisseau.getPosY() - 152, this);

                    for (Planete p1 : planetes)
                    {
                        if(p1.getThreadDep() != null)
                            p1.stopDeplacement();
                    }
                }
            }

    }

    //Methode pour tourner le vaisseau
    public void rotateImage(double angle)
    {
        if (!this.gameOver){
            vaisseau.addAngleRot(angle);
            repaint();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode()==KeyEvent.VK_UP)//Fleche du haut += l'acceleration
        {
            this.vaisseau.setAcceleration(this.vaisseau.getAcceleration()+0.001);
        }
        else if (e.getKeyCode()==KeyEvent.VK_RIGHT) //On tourne l'image d'ANGLE_ROTATION
        {
            rotateImage(ANGLE_ROTATION);
        }

        else if (e.getKeyCode()==KeyEvent.VK_LEFT) //On tourne l'image d'ANGLE_ROTATION dans le sens inverse
        {
            rotateImage(-ANGLE_ROTATION);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) //On stop l'acceleration
    {
        if (e.getKeyCode()==KeyEvent.VK_UP) {
            this.vaisseau.setAcceleration(0);
        }
    }

    public ArrayList<Planete> getPlanetes() {
        return planetes;
    }
}
