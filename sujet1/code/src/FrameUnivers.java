import javax.swing.*;
import java.awt.*;

public class FrameUnivers extends JFrame
{
    private int nbPlanete;
    private PanelUnivers panelUniv;

    public FrameUnivers(int nbPlanete)
    {

        this.nbPlanete = nbPlanete;
        this.panelUniv = new PanelUnivers(nbPlanete);

        this.addKeyListener(panelUniv);
        this.add(panelUniv);

        this.setTitle("SUJET 1");
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(0,0);
       // this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
        this.setSize(500,500);
        this.setUndecorated(true);
        this.setVisible(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

    }
}
