import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PanelContour extends JPanel
{
    private final String IMAGE_PATH = "../resources/robot1.png";
    private BufferedImage image;

    public PanelContour()
    {
        try {
            image = ImageIO.read(PanelContour.class.getResourceAsStream("/robot1.png"));
        } catch (IOException ex) {
            System.out.println(image);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
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
        g.drawImage(image, 0, 0, this);
    }
}
