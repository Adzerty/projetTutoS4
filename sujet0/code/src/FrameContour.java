import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class FrameContour extends JFrame
{
    private PanelContour panel;
    public FrameContour()
    {
        this.setTitle("FrameContour");
        this.setLocation(100,100);
        this.setSize(500,500);

        panel = new PanelContour();
        this.addKeyListener(panel);
        this.add(panel);


        this.setVisible(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        new FrameContour();
    }

}
