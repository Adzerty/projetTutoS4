package metier;

import ihm.PanelUnivers;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class Vaisseau {
    private static Vaisseau instance = new Vaisseau();

    private static final String IMAGE_PATH = "/rocket-1.png";
    private static final String IMAGE_PROP_PATH = "/rocket-1-propeling.png";

    private Thread threadDep;


    private BufferedImage image;
    private BufferedImage maskAlpha;
    private BufferedImage contour;
    private BufferedImage propeling;

    private int xBarycentre;
    private int yBarycentre;

    //ATTRIBUTS POUR LE DEPLACEMENT DU VAISSEAU
    // ----------------------------------------
    //rotation
    private double angleRot = Math.PI/2;

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
        this.posX = 250;
        this.posY = 250;

        try {
            image = ImageIO.read(Vaisseau.class.getResourceAsStream(IMAGE_PATH)); //On recupere chaque image
            this.maskAlpha = ImageIO.read(Vaisseau.class.getResourceAsStream(IMAGE_PATH));
            this.propeling = ImageIO.read(Vaisseau.class.getResourceAsStream(IMAGE_PROP_PATH));

        } catch (IOException ex) {
            System.out.println(image);
        }

        for (int i = 0; i < image.getHeight(); i++) { //On fait le masque alpha de l'image
            for (int j = 0; j < image.getWidth(); j++) {
                Color c = new Color(image.getRGB(j, i), true);
                if (c.getAlpha() > 0) {
                    maskAlpha.setRGB(j, i, 0xFF000000);
                }
            }
        }

        this.contour = generateContour(maskAlpha); //on genere le contour
        findBarycentre();
    }

    public void setPanelUnivers(PanelUnivers panelUnivers) {
        this.panelUnivers = panelUnivers;
    }


    public void startDeplacement()
    {

        /* ----------------------
            THREAD DE DEPLACEMENT
           ----------------------
         */
        this.threadDep = new Thread(() ->
        {
            while (true)
            {
                Vaisseau v = Vaisseau.getInstance();

                //On calcul le decalage de temps avec la derniere iteration de boucle
                v.tpsDebut = System.currentTimeMillis();
                v.deltaT = v.tpsDebut - v.tpsFin;

                if(vitesse.calculNormeAvecDeuxValeurs( //On verifie que la vitesse ne depassera pas la vitesse max
                        v.vitesse.getvX() + v.acceleration * Math.cos(Math.toRadians(v.angleRot)),
                        v.vitesse.getvY()) + v.acceleration * Math.sin(Math.toRadians(v.angleRot))
                        < MAX_VITESSE )
                {
                    v.vitesse.setvX(v.vitesse.getvX() + v.acceleration * Math.cos(Math.toRadians(v.angleRot)));
                    v.vitesse.setvY(v.vitesse.getvY() + v.acceleration * Math.sin(Math.toRadians(v.angleRot)));
                }

                //On calcul la nouvelle position
                v.posX = v.posX + (v.vitesse.getvX() * deltaT);
                v.posY = v.posY + (v.vitesse.getvY() * deltaT);

                //Calcul d'un nouveau vecteur permettant la deceleration
                Vecteur vitInit = new Vecteur(v.vitesse.getvX(), v.vitesse.getvY());
                double vitFinal = vitInit.getNorme() - (deltaT * decelerarion);

                // On reduit la vitesse
                if(vitInit.getNorme() != 0)
                {
                    v.vitesse.setvX(v.vitesse.getvX() * (vitFinal / vitInit.getNorme()));
                    v.vitesse.setvY(v.vitesse.getvY() * (vitFinal / vitInit.getNorme()));
                }

                //On stop la fusee quand elle arrive a < min_vitesse
                if(v.vitesse.getNorme() < MIN_VITESSE)
                {
                    v.vitesse.setvX(0);
                    v.vitesse.setvY(0);
                }

                //On repeint la toile
                v.panelUnivers.repaint();

                v.tpsFin = System.currentTimeMillis();
                try {Thread.sleep(10);} catch(InterruptedException e){e.printStackTrace();}
            }
        });
        this.threadDep.start();

    }

    public void stopDeplacement(){
        this.threadDep.stop();
    }

    public static Vaisseau getInstance() {
        return instance;
    }


    private BufferedImage generateContour(BufferedImage mask) {
        this.ensCoord = new ArrayList<>();
        BufferedImage imageToReturn = new BufferedImage(maskAlpha.getWidth(), maskAlpha.getHeight(), BufferedImage.TYPE_INT_ARGB);

        for (int i = 0; i < maskAlpha.getHeight(); i++) {//Pour chaque pixel du masque alpha
            for (int j = 0; j < maskAlpha.getWidth(); j++) {
                if (maskAlpha.getRGB(j, i) != 0xFF000000) {
                    for (int y = -1; y < 2; y++) {//On regarde les 8 pixels autour
                        for (int x = -1; x < 2; x++) {
                            try {
                                if (maskAlpha.getRGB(j + x, i + y) == 0xFF000000) { //Si le pixel est du contour on lajoute
                                    Coordonnees coordTmp = new Coordonnees(j + x, i + y);
                                    ensCoord.add(coordTmp);
                                }
                            } catch (Exception e) {
                            }

                        }
                    }

                } else {
                    if (j == maskAlpha.getWidth() - 1 || // calcul du contour pour les contours de l'image
                            j == 0 ||
                            i == maskAlpha.getHeight() - 1 ||
                            i == 0) {
                        Coordonnees coordTmp = new Coordonnees(j, i);
                        ensCoord.add(coordTmp);
                    }
                }
            }
        }

        for (Coordonnees coordTmp : ensCoord) { //On dessine la nouvelle image (simplement le contour)
            imageToReturn.setRGB(coordTmp.getX(), coordTmp.getY(), 0xFF000000);
        }

        return imageToReturn;
    }


    private void findBarycentre() { //Calcul du barycentre
        int sommeX = 0;
        int sommeY = 0;

        for (Coordonnees cCoord : ensCoord) {
            sommeX += cCoord.getX();
            sommeY += cCoord.getY();
        }

        this.xBarycentre = sommeX / ensCoord.size();
        this.yBarycentre = sommeY / ensCoord.size();

    }

    public BufferedImage getImage() {
        return image;
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

    public void setEnsCoord(BufferedImage mask) {
        this.generateContour(mask);
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

    public BufferedImage getPropeling() {
        return propeling;
    }

    public Vecteur getVitesse() {
        return vitesse;
    }
}
