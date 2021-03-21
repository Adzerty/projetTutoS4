import javax.swing.*;

public class FrameContour extends JFrame
{
    public FrameContour()
    {
        this.setTitle("FrameContour");
        this.setLocation(100,100);
        this.setSize(500,500);

        this.add(new PanelContour());

        this.setVisible(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        new FrameContour();
    }
}
