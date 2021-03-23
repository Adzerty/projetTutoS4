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

    private double angleOffset = (3*Math.PI)/2;

    public PanelUnivers(int nbPlanetes)
    {

        this.vaisseau = Vaisseau.getInstance();
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

        for (Planete p : planetes) g.fillOval((int)p.getCoord().getX(),(int)p.getCoord().getY(),p.getTaille(),p.getTaille());

        //g2.rotate(Math.toRadians(vaisseau.getAngleRot())+angleOffset, vaisseau.getxBarycentre()+vaisseau.getPosX(), vaisseau.getyBarycentre()+vaisseau.getPosY());
        //g2.drawImage(vaisseau.getImage(), vaisseau.getPosX(), vaisseau.getPosY(),this);

        g2.setColor(Color.RED);
        for(Coordonnees c : this.vaisseau.getEnsCoord())
        {
            g2.drawRect((int) (c.getX()), (int)(c.getY()),1,1 );
        }
        g2.setColor(Color.YELLOW);
        g2.fillRect((int)vaisseau.getxBarycentre()+ vaisseau.getPosX(), (int)vaisseau.getyBarycentre()+ vaisseau.getPosY(), 5,5);
        g2.dispose();

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

    public ArrayList<Planete> getPlanetes() {
        return planetes;
    }
}
