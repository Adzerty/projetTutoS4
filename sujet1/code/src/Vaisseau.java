import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class Vaisseau {
    private static Vaisseau instance = new Vaisseau();

    private static final String IMAGE_PATH = "/rocket-2.png";

    private final int POS_X_INIT = 0;
    private final int POS_Y_INIT = 0;


    private BufferedImage image;
    private BufferedImage maskAlpha;
    private BufferedImage contour;

    private double xBarycentre;
    private double yBarycentre;

    //ATTRIBUTS POUR LE DEPLACEMENT DU VAISSEAU
    // ----------------------------------------
    //rotation
    private double angleRot = 0;
    private double angleRotContour = -1;
    private double angleOffset = (3*Math.PI)/2;

    //position
    private double posX;
    private double posY;

    //vitesse
    private final double MAX_VITESSE = 0.100;
    private final double MIN_VITESSE = 0.0001;
    private Vecteur vitesse = new Vecteur(0,0);

    //acceleration
    private double acceleration = 0;
    private double decelerarion = 0.00001;

    //temps
    private double deltaT;
    private double tpsDebut;
    private double tpsFin;
    // ----------------------------------------


    private ArrayList<Coordonnees> ensCoord = new ArrayList<Coordonnees>();
    private PanelUnivers panelUnivers;


    private Vaisseau()
    {
        this.posX = POS_X_INIT;
        this.posY = POS_Y_INIT;

        try {
            image = ImageIO.read(Vaisseau.class.getResourceAsStream(IMAGE_PATH));
        } catch (IOException ex) {
            System.out.println(image);
        }

        //On cree le masque alpha
        this.maskAlpha = new BufferedImage(image.getWidth(), image.getHeight(),BufferedImage.TYPE_INT_ARGB);

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
        Thread dep = new Thread(() ->
        {
            while (true)
            {
                Vaisseau v = Vaisseau.getInstance();

                v.tpsDebut = System.currentTimeMillis();
                v.deltaT = v.tpsDebut - v.tpsFin;

                if(vitesse.calculNormeAvecDeuxValeurs(
                        v.vitesse.getvX() + v.acceleration * Math.cos(Math.toRadians(v.angleRot)),
                        v.vitesse.getvY()) + v.acceleration * Math.sin(Math.toRadians(v.angleRot))
                        < MAX_VITESSE )
                {
                    v.vitesse.setvX(v.vitesse.getvX() + v.acceleration * Math.cos(Math.toRadians(v.angleRot)));
                    v.vitesse.setvY(v.vitesse.getvY() + v.acceleration * Math.sin(Math.toRadians(v.angleRot)));
                }

                v.posX = v.posX + (v.vitesse.getvX() * deltaT);
                v.posY = v.posY + (v.vitesse.getvY() * deltaT);

                //ensCoord.clear();
                findBarycentre();
                for(Coordonnees c : v.ensCoord)
                {
                    c.setX(c.getX() + (v.vitesse.getvX() * deltaT));
                    c.setY(c.getY() + (v.vitesse.getvY() * deltaT));


                    if(angleRotContour != angleRot)
                    {
                        this.rotateCoord(c, v.angleRot);
                    }
                }

                if(angleRotContour != angleRot)
                    angleRotContour = angleRot;



                Vecteur vitInit = new Vecteur(v.vitesse.getvX(), v.vitesse.getvY());
                double vitFinal = vitInit.getNorme() - (deltaT * decelerarion);

                if(vitInit.getNorme() != 0)
                {
                    v.vitesse.setvX(v.vitesse.getvX() * (vitFinal / vitInit.getNorme()));
                    v.vitesse.setvY(v.vitesse.getvY() * (vitFinal / vitInit.getNorme()));
                }

                if(v.vitesse.getNorme() < MIN_VITESSE)
                {
                    v.vitesse.setvX(0);
                    v.vitesse.setvY(0);
                }

                Planete pTmp = v.panelUnivers.getPlanetes().get(0);
                Coordonnees cTmp = v.ensCoord.get(0);

                /*
                for(Planete p : v.panelUnivers.getPlanetes())
                {
                    Coordonnees coordPlanet = p.getCoord();
                    for(Coordonnees coordContour : v.ensCoord)
                    {
                        double distance = Math.sqrt( Math.pow(coordContour.getX() - coordPlanet.getX() ,2) + Math.pow(coordContour.getY() - coordPlanet.getY(),2)  );
                        if(distance <= p.getTaille()/2)
                        {
                            System.out.println("BOOM");
                        }
                    }

                }*/
                //System.out.println(Math.sqrt( Math.pow(cTmp.getX() - pTmp.getCoord().getX(),2) +  Math.pow(cTmp.getY() - pTmp.getCoord().getY(),2)));
                v.panelUnivers.repaint();

                v.tpsFin = System.currentTimeMillis();
                try {Thread.sleep(10);} catch(InterruptedException e){e.printStackTrace();}
            }
        });
        dep.start();
    }

    public static BufferedImage copyImage(BufferedImage source){
        BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        Graphics g = b.getGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return b;
    }

    public static Vaisseau getInstance() {
        return instance;
    }


    private BufferedImage generateContour(BufferedImage mask)
    {
        BufferedImage imageToReturn = new BufferedImage(mask.getWidth(), mask.getHeight(),BufferedImage.TYPE_INT_ARGB);

        for(int i = 0; i<mask.getHeight(); i++)
        {
            for (int j = 0; j < mask.getWidth(); j++)
            {
                if(mask.getRGB(j,i) != 0xFF000000)
                {
                    for(int y = -1; y<2; y++)
                    {
                        for(int x = -1; x<2; x++)
                        {
                            try{
                                if(mask.getRGB(j+x, i+y) == 0xFF000000)
                                {
                                    Coordonnees coordTmp = new Coordonnees(j+x, i+y);
                                    ensCoord.add(coordTmp);
                                }
                            }catch(Exception e) {}

                        }
                    }

                }else
                {
                    if( j == mask.getWidth()-1         ||
                            j == 0                         ||
                            i == mask.getHeight()-1    ||
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
            imageToReturn.setRGB((int)coordTmp.getX(), (int)coordTmp.getY(), 0xFF000000);
        }

        return imageToReturn;
    }


    private void findBarycentre() {
        double sommeX = 0;
        double sommeY = 0;

        for (Coordonnees cCoord : ensCoord) {
            sommeX += cCoord.getX();
            sommeY += cCoord.getY();
        }

        this.xBarycentre = sommeX / ensCoord.size();
        this.yBarycentre = sommeY / ensCoord.size();

    }

    private void rotateCoord(Coordonnees c, double angle)
    {

        double realAngle = Math.toRadians(angle)+angleOffset;
        System.out.println(realAngle);

        //On translate
        double x = c.getX();
        double y = c.getY();

        double xPrime = x - (xBarycentre);
        double yPrime = y - (yBarycentre);


        //On rotate
        double xSeconde = ( xPrime * Math.cos(realAngle) ) - ( yPrime * Math.sin(realAngle) );
        double ySeconde = ( yPrime * Math.cos(realAngle) ) + ( xPrime * Math.sin(realAngle) );

        //On remet
        double xMinute = xSeconde + (xBarycentre);
        double yMinute = ySeconde + (yBarycentre);

        c.setX(xMinute);
        c.setY(yMinute);
    }

    public BufferedImage getImage() {
        return image;
    }

    public double getxBarycentre() {
        return xBarycentre;
    }

    public double getyBarycentre() {
        return yBarycentre;
    }

    public static String getImagePath() {
        return IMAGE_PATH;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public BufferedImage getMaskAlpha() {
        return maskAlpha;
    }

    public void setMaskAlpha(BufferedImage maskAlpha) {
        this.maskAlpha = maskAlpha;
    }

    public BufferedImage getContour() {
        return contour;
    }

    public void setContour(BufferedImage contour) {
        this.contour = contour;
    }

    public void setxBarycentre(int xBarycentre) {
        this.xBarycentre = xBarycentre;
    }

    public void setyBarycentre(int yBarycentre) {
        this.yBarycentre = yBarycentre;
    }


    public ArrayList<Coordonnees> getEnsCoord() {
        return ensCoord;
    }

    public void setEnsCoord(ArrayList<Coordonnees> ensCoord) {
        this.ensCoord = ensCoord;
    }

    public int getPosX() {
        return (int)posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return (int)posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public double getAngleRot() {
        return angleRot;
    }

    public void addAngleRot(double angleRot) {
        this.angleRot += angleRot;
    }
    public double getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(double acceleration) {
        this.acceleration = acceleration;
    }
}
