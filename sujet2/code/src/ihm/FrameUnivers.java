package ihm;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;

public class FrameUnivers extends JFrame
{
    private int nbPlanete;
    private PanelUnivers panelUniv;

    public static final int WIDTH = 750;
    public static final int HEIGHT = 750;

    public FrameUnivers(int nbPlanete)
    {
       
        this.nbPlanete = nbPlanete;
        this.panelUniv = new PanelUnivers(nbPlanete);

        this.addKeyListener(panelUniv);
        this.add(panelUniv);

        this.setTitle("Sujet 2 - Equipe 1");
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(50,50);
        this.setLocationRelativeTo(null);
        this.setSize(WIDTH,HEIGHT);
        this.setResizable(false);

        playMusic("/pandora.wav");


        this.setVisible(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public static void playMusic(String musicLocation)
    {
        InputStream music;
        try{
            //File musicPath = new File(FrameUnivers.class.getResource(musicLocation).toURI());
            InputStream audioSrc = FrameUnivers.class.getResourceAsStream(musicLocation);
            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(bufferedIn);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInput);

            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

            float volume = 0.5f;
            float range = gainControl.getMaximum() - gainControl.getMinimum();
            float gain = (range * volume) + gainControl.getMinimum();
            gainControl.setValue(gain);


            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        } catch(Exception e){ e.printStackTrace(); }
    }

}
