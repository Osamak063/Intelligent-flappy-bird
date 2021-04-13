/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flappybird;

import java.awt.Graphics;
import javax.swing.JPanel;

/**
 *
 * @author Osama 3.1 gpa
 */
public class Renderer extends JPanel{

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); //To change body of generated methods, choose Tools | Templates.
    
//        calling in Flapppy bird
    FlappyBird.flappyBird.repaint(g);
    }
    
    
}
