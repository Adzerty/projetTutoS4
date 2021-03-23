package ihm;

import javax.swing.*;
import java.awt.*;

public class FrameUnivers extends JFrame
{
    private int nbPlanete;
    private PanelUnivers panelUniv;

    public static final int WIDTH = 500;
    public static final int HEIGHT = 500;
    public FrameUnivers(int nbPlanete)
    {
       
        this.nbPlanete = nbPlanete;
        this.panelUniv = new PanelUnivers(nbPlanete);

        this.addKeyListener(panelUniv);
        this.add(panelUniv);

        this.setTitle("Sujet 1 - Equipe 1");
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        //this.setLocation(50,50);
        this.setLocationRelativeTo(null);
        this.setSize(WIDTH,HEIGHT);

        this.setVisible(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}
