public class Vecteur
{
    private double vX;
    private double vY;

    public Vecteur(double vX, double vY) {
        this.vX = vX;
        this.vY = vY;
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

}
