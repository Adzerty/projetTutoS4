package ihm;

import metier.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FrameNombrePlanetes extends JFrame implements ActionListener
{
    private JTextField nombre;
    private JButton valider;
    private Univers univ;


    public FrameNombrePlanetes(Univers univ)
    {
        this.setTitle("Sujet 1 - Equipe 1");;
        this.setLocation(500,500);
        this.setSize(200,200);

        this.univ = univ;
        this.setLayout(new GridLayout(3,1));

        this.valider = new JButton("valider");

        this.nombre = new JTextField();

        this.add(new JLabel("Nombre de planetes"));
        this.add(this.nombre);
        this.add(this.valider);
        this.valider.addActionListener(this);

        this.setTitle("Sujet 2 - Equipe 1");;
        this.setLocation(500,500);
        this.setSize(200,200);
        this.setVisible(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private boolean test(String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource() == this.valider && test(this.nombre.getText()))
        {
            this.univ.setNbPlanete(Integer.parseInt(this.nombre.getText()));
            this.univ.openFrameUniv();
            this.dispose();
        }

    }
}
