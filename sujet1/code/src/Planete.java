public class Planete
{
    private Coordonnees coord;
    private int taille;
    private  static final  int TAILLE_FRAME = 500;
    private static final int MIN = 10;
    private static final int MAX = 50;

    public Planete()
    {
        this.coord = new Coordonnees((int)(Math.random()*TAILLE_FRAME),(int)(Math.random()*TAILLE_FRAME));
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
