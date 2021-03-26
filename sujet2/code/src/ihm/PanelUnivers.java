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
        for (int i=0; i<=nbPlanetes; i++){
            if (i == 0){
                planetes.add(new Planete(true, this));
            }
            else {
                planetes.add(new Planete(false, this));
                planetes.get(i).startDeplacementPlanete();
            }
        }

        this.addKeyListener(this);

        //On commence le thread de deplacement du vaisseau
        this.vaisseau.startDeplacement();

        //On commence le thread de check de collisions
        this.checkCollisions();
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

    public ArrayList<Planete> getPlanetes(){
        return this.planetes;
    }
    public Vaisseau getVaisseau(){
        return this.vaisseau;
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




}
