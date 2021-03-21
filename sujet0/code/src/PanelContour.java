import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PanelContour extends JPanel implements KeyListener
{
    private final String IMAGE_PATH = "/robot1.png";
    private BufferedImage image;
    private BufferedImage maskAlpha;
    private BufferedImage contour;

    private int xBarycentre;
    private int yBarycentre;
    private double angle;

    private ArrayList<Coordonnees> ensCoord = new ArrayList<Coordonnees>();

    public PanelContour()
    {
        this.addKeyListener(this);
        try {
            image = ImageIO.read(PanelContour.class.getResourceAsStream(IMAGE_PATH));
        } catch (IOException ex) {
            System.out.println(image);
        }

        for(int i = 0; i<image.getHeight(); i++)
        {
            for(int j = 0; j<image.getWidth(); j++)
            {
                Color c = new Color(image.getRGB(j,i), true);
                if(c.getAlpha() > 0)
                {
                    image.setRGB(j,i,0xFF000000);
                }
            }
        }
        this.maskAlpha = image;
        this.contour = generateContour(maskAlpha);
        findBarycentre();
    }

    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g;
        g2.drawImage(contour, 0, 0, this);
        g2.fillOval(xBarycentre,yBarycentre,10,10);
        g2.rotate(angle, xBarycentre, yBarycentre);
    }

    private BufferedImage generateContour(BufferedImage mask)
    {
        BufferedImage imageToReturn = new BufferedImage(maskAlpha.getWidth(), maskAlpha.getHeight(),BufferedImage.TYPE_INT_ARGB);

        for(int i = 0; i<maskAlpha.getHeight(); i++)
        {
            for (int j = 0; j < maskAlpha.getWidth(); j++)
            {
                if(image.getRGB(j,i) != 0xFF000000)
                {
                    for(int y = -1; y<2; y++)
                    {
                        for(int x = -1; x<2; x++)
                        {
                            try{
                                if(maskAlpha.getRGB(j+x, i+y) == 0xFF000000)
                                {
                                    Coordonnees coordTmp = new Coordonnees(j+x, i+y);
                                    ensCoord.add(coordTmp);
                                }
                            }catch(Exception e) {}

                        }
                    }
                }
            }
        }

        for(Coordonnees coordTmp : ensCoord)
        {
            imageToReturn.setRGB(coordTmp.getX(), coordTmp.getY(), 0xFF000000);
        }

        return imageToReturn;
    }




    private void findBarycentre()
    {
        int sommeX = 0;
        int sommeY = 0;

        for(Coordonnees cCoord : ensCoord)
        {
            sommeX += cCoord.getX();
            sommeY += cCoord.getY();
        }

        this.xBarycentre = sommeX/ensCoord.size();
        this.yBarycentre = sommeY/ensCoord.size();

    }

    public void rotateImage(double angle)
    {
        this.angle += Math.toRadians(angle);
        System.out.println(angle);
        repaint();
    }
    @Override
    public void keyTyped(KeyEvent e)
    {

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
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
