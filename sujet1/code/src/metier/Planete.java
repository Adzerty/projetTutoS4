package metier;

import ihm.FrameUnivers;

public class Planete
{
    private Coordonnees coord;
    private int taille;
    private  static final  int TAILLE_FRAME = FrameUnivers.HEIGHT;
    private static final int MIN = 25;
    private static final int MAX = 75;

    public Planete()
    {
        int y = (int)(Math.random()*TAILLE_FRAME);
        int x;

        if(y <= 350 && y >= 150)
        {
            do{
                x = (int)(Math.random()*TAILLE_FRAME);
            }while(x <= 350 && x >= 150);
        }else
            x = (int)(Math.random()*TAILLE_FRAME);

        this.coord = new Coordonnees(x,y);
        this.taille = (int)(MIN + (Math.random() * (MAX - MIN)));
    }

    public void setCoord(Coordonnees coord) {
        this.coord = coord;
    }

    public Coordonnees getCoord() {
        return coord;
    }

    public int getTaille() {
        return taille;
    }
}
