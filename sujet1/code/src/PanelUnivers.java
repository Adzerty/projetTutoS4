import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.ArrayList;


public class PanelUnivers extends JPanel implements KeyListener
{
    private ArrayList<Planete> planetes;
    private Vaisseau vaisseau;
    private Coordonnees coods;



    private double angleOffset = (3*Math.PI)/2;

    public PanelUnivers(int nbPlanetes)
    {

        this.vaisseau = Vaisseau.getInstance();
        this.vaisseau.setPanelUnivers(this);

        System.out.println(this.getInsets().top);

        this.planetes = new ArrayList<>();

        for (int i=0; i<nbPlanetes; i++) planetes.add(new Planete());
        this.addKeyListener(this);

        this.vaisseau.startDeplacement();
        this.checkCollisions();
    }

    public void checkCollisions()
    {
        Thread check = new Thread(() ->
        {
            while (true)
            {
                for(Planete p : planetes)
                {
                    Coordonnees coordPlanet = p.getCoord();
                    for(Coordonnees c : this.vaisseau.getEnsCoord())
                    {
                        double planX = p.getCoord().getX() + p.getTaille()/2;
                        double planY = p.getCoord().getY() + p.getTaille()/2;

                        double sin = Math.sin(Math.toRadians(vaisseau.getAngleRot())-Math.PI/2);
                        double cos = Math.cos(Math.toRadians(vaisseau.getAngleRot())-Math.PI/2);

                        double xp = c.getX()-this.vaisseau.getxBarycentre();
                        double yp = c.getY()-this.vaisseau.getyBarycentre();

                        double xpn = xp * cos - yp * sin;
                        double ypn = xp * sin + yp * cos;

                        xp = xpn + this.vaisseau.getPosX() + this.vaisseau.getxBarycentre();
                        yp = ypn + this.vaisseau.getPosY() + this.vaisseau.getyBarycentre();
                        //double distance = Math.sqrt( Math.pow(((this.vaisseau.getPosX()) + (coordContour.getX() * Math.cos(Math.toRadians(this.vaisseau.getAngleRot())))) - planX,2) +  Math.pow(((this.vaisseau.getPosY()) + (coordContour.getY() * Math.sin(Math.toRadians(this.vaisseau.getAngleRot())))) - planY,2));

                        double distance = Math.sqrt( Math.pow(planX - xp, 2) + Math.pow(planY - yp, 2) );
                        if(distance <= p.getTaille()/2)
                        {
                            System.out.println("BOOM");
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

        for (Planete p : planetes) g.fillOval(p.getCoord().getX(),p.getCoord().getY(),p.getTaille(),p.getTaille());
    
        g2.rotate(Math.toRadians(vaisseau.getAngleRot())+angleOffset, vaisseau.getxBarycentre()+vaisseau.getPosX(), vaisseau.getyBarycentre()+vaisseau.getPosY());
        g2.drawImage(vaisseau.getImage(), vaisseau.getPosX(), vaisseau.getPosY(),this);

        float vitesse = (float) vaisseau.getVitesse().getNorme()*10;
        if(vitesse>1)vitesse=1;

        g2.setComposite(AlphaComposite.SrcOver.derive(vitesse));
        g2.drawImage(vaisseau.getPropeling(),vaisseau.getPosX(), vaisseau.getPosY(),this);


    }
    public void rotateImage(double angle)
    {
        vaisseau.addAngleRot(angle);
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode()==KeyEvent.VK_UP)
        {
            this.vaisseau.setAcceleration(this.vaisseau.getAcceleration()+0.001);
        }
        else if (e.getKeyCode()==KeyEvent.VK_RIGHT)
        {
            rotateImage(5);
        }

        else if (e.getKeyCode()==KeyEvent.VK_LEFT)
        {
            rotateImage(-5);
        }
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        if (e.getKeyCode()==KeyEvent.VK_UP) {
            this.vaisseau.setAcceleration(0);
        }
    }


}
