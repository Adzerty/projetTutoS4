import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class Vaisseau
{
    private static Vaisseau instanceVaisseau = new Vaisseau();

    private static final String IMAGE_PATH = "/rocket-1.png";
    private Coordonnees coords;
    private BufferedImage image;
    private BufferedImage maskAlpha;
    private BufferedImage contour;

    private int xBarycentre;
    private int yBarycentre;
    private double acceleration;
    private double vitesseX;
    private double vitesseY;
    private static final double VITESSE_MIN = 0.0001;
    private static final double VITESSE_MAX = 0.100;
    private double deltaT;
    private double lastTime;
    private double angleRot;

    private ArrayList<Coordonnees> ensCoord = new ArrayList<Coordonnees>();

    private PanelUnivers panelUnivers;


    private Vaisseau()
    {
        this.angleRot = Math.PI/2;
        this.deltaT = 0;
        this.lastTime =0;
        this.vitesseX =0;
        this.vitesseY =0;
        this.acceleration = 0;

        this.coords = new Coordonnees(250,250);
        try {
            image = ImageIO.read(Vaisseau.class.getResourceAsStream(IMAGE_PATH));
            this.maskAlpha = ImageIO.read(Vaisseau.class.getResourceAsStream(IMAGE_PATH));

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
                    maskAlpha.setRGB(j,i,0xFF000000);
                }
            }
        }
        this.contour = generateContour(maskAlpha);
        findBarycentre();
    }

    public void setPanelUnivers(PanelUnivers panelUnivers) {
        this.panelUnivers = panelUnivers;
    }

    public void startDeplacement()
    {
        Thread dep = new Thread(() -> {
            while (true) {
                instanceVaisseau.deltaT = System.currentTimeMillis() - instanceVaisseau.lastTime;
                instanceVaisseau.lastTime = System.currentTimeMillis();

                if (vitesse() < VITESSE_MAX) {
                    instanceVaisseau.vitesseX = instanceVaisseau.vitesseX + (instanceVaisseau.acceleration * Math.cos(instanceVaisseau.angleRot));
                    instanceVaisseau.vitesseY = instanceVaisseau.vitesseY + (instanceVaisseau.acceleration * Math.sin(instanceVaisseau.angleRot));
                    //System.out.println("vitesse : " +instanceVaisseau.vitesse());
                }

                instanceVaisseau.getCoords().setX((int) (instanceVaisseau.getCoords().getX() + (instanceVaisseau.vitesseX * instanceVaisseau.deltaT)));
                instanceVaisseau.getCoords().setY((int) (instanceVaisseau.getCoords().getY() + (instanceVaisseau.vitesseY * instanceVaisseau.deltaT)));

                if (instanceVaisseau.acceleration > 0) instanceVaisseau.acceleration -= 0.01;
                instanceVaisseau.panelUnivers.repaint();

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        dep.start();
    }
    public static Vaisseau getInstanceVaisseau() {
        return instanceVaisseau;
    }

    public static void setInstanceVaisseau(Vaisseau instanceVaisseau) {
        Vaisseau.instanceVaisseau = instanceVaisseau;
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

                }else
                {
                    if( j == maskAlpha.getWidth()-1         ||
                            j == 0                         ||
                            i == maskAlpha.getHeight()-1    ||
                            i == 0)
                    {
                        Coordonnees coordTmp = new Coordonnees(j, i);
                        ensCoord.add(coordTmp);
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

    public BufferedImage getImage() {
        return image;
    }

    public Coordonnees getCoords() {
        return coords;
    }

    public int getxBarycentre() {
        return xBarycentre;
    }

    public int getyBarycentre() {
        return yBarycentre;
    }

    public static String getImagePath() {
        return IMAGE_PATH;
    }

    public void setCoords(Coordonnees coords) {
        instanceVaisseau.coords = coords;
    }

    public void setImage(BufferedImage image) {
        instanceVaisseau.image = image;
    }

    public BufferedImage getMaskAlpha() {
        return maskAlpha;
    }

    public void setMaskAlpha(BufferedImage maskAlpha) {
        instanceVaisseau.maskAlpha = maskAlpha;
    }

    public BufferedImage getContour() {
        return contour;
    }

    public void setContour(BufferedImage contour) {
        instanceVaisseau.contour = contour;
    }

    public void setxBarycentre(int xBarycentre) {
        instanceVaisseau.xBarycentre = xBarycentre;
    }

    public void setyBarycentre(int yBarycentre) {
        instanceVaisseau.yBarycentre = yBarycentre;
    }

    public double getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(double acceleration) {
        instanceVaisseau.acceleration = acceleration;
    }

    public double getVitesseX() {
        return vitesseX;
    }

    public void setVitesseX(double vitesseX) {
        instanceVaisseau.vitesseX = vitesseX;
    }

    public double getVitesseY() {
        return vitesseY;
    }

    public void setVitesseY(double vitesseY) {
        instanceVaisseau.vitesseY = vitesseY;
    }

    public static double getVitesseMin() {
        return VITESSE_MIN;
    }

    public static double getVitesseMax() {
        return VITESSE_MAX;
    }

    public double getDeltaT() {
        return deltaT;
    }

    public void setDeltaT(double deltaT) {
        this.deltaT = deltaT;
    }

    public double getLastTime() {
        return lastTime;
    }

    public void setLastTime(double lastTime) {
        this.lastTime = lastTime;
    }

    public double getAngleRot() {
        return angleRot;
    }

    public void setAngleRot(double angleRot) { instanceVaisseau.angleRot = angleRot;
    }

    public ArrayList<Coordonnees> getEnsCoord() {
        return ensCoord;
    }

    public void setEnsCoord(ArrayList<Coordonnees> ensCoord) {
        this.ensCoord = ensCoord;
    }
    public double vitesse() { return  Math.sqrt(Math.pow(this.vitesseX,2)+Math.pow(this.vitesseY,2)); }
}
