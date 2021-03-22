import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;


public class PanelUnivers extends JPanel implements KeyListener
{
    private ArrayList<Planete> planetes;
    private Vaisseau vaisseau;
    private Coordonnees coods;

    public PanelUnivers(int nbPlanetes)
    {

        this.vaisseau = Vaisseau.getInstanceVaisseau();
        this.vaisseau.setPanelUnivers(this);

        this.planetes = new ArrayList<>();

        for (int i=0; i<nbPlanetes; i++) planetes.add(new Planete());

        this.addKeyListener(this);

        this.vaisseau.startDeplacement();
    }

    public void setCoods(Coordonnees coods) {
        this.coods = coods;
    }

    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g;

        for (Planete p : planetes) g.fillOval(p.getCoord().getX(),p.getCoord().getY(),p.getTaille(),p.getTaille());

        g2.rotate(vaisseau.getAngleRot(), vaisseau.getxBarycentre()+vaisseau.getCoords().getX(), vaisseau.getyBarycentre()+vaisseau.getCoords().getY());
        g2.drawImage(vaisseau.getImage(), vaisseau.getCoords().getX(), vaisseau.getCoords().getY(),this);

    }
    public void rotateImage(double angle)
    {
        vaisseau.setAngleRot(vaisseau.getAngleRot()+ Math.toRadians(angle));
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

        if (e.getKeyCode()==KeyEvent.VK_RIGHT)
        {
            rotateImage(5);
        }

        else if (e.getKeyCode()==KeyEvent.VK_LEFT)
        {
            rotateImage(-5);
        }
        else if (e.getKeyCode()==KeyEvent.VK_UP)
        {
            if(this.vaisseau.getAcceleration()+0.001<Vaisseau.getVitesseMax()) this.vaisseau.setAcceleration(this.vaisseau.getAcceleration()+0.001);
        }
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        if (e.getKeyCode()==KeyEvent.VK_UP) this.vaisseau.setAcceleration(0);
    }


}
