package metier;

public class Vecteur
{
    private double vX;
    private double vY;

    public Vecteur(double vX, double vY) {
        this.vX = vX;
        this.vY = vY;
    }

    public Vecteur(double norme, double angle, boolean b)
    {
        System.out.println("");
        this.vX = Math.cos(angle)*norme;
        this.vY = Math.sin(angle)*norme;
    }

    public Vecteur multiplication(double k)
    {
        return new Vecteur(k*vX, k*vY);
    }

    public Vecteur addition(Vecteur v2)
    {
        return new Vecteur(vX + v2.vX, vY + v2.vY);
    }

    public double getvX() {
        return vX;
    }

    public void setvX(double vX) {
        this.vX = vX;
    }

    public double getvY() {
        return vY;
    }

    public void setvY(double vY) {
        this.vY = vY;
    }

    public double getNorme()
    {
        return Math.sqrt( (vX*vX) + (vY*vY) );
    }

    public double calculNormeAvecDeuxValeurs(double x, double y){
        return Math.sqrt( (x*x) + (y*y) );
    }

    @Override
    public String toString() {
        return "Vecteur{" +
                "vX=" + vX +
                ", vY=" + vY +
                '}';
    }
}
