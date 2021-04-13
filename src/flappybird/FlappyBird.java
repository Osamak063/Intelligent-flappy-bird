/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flappybird;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.Timer;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

/**
 *
 * @author Osama 3.1 gpa
 */
public class FlappyBird implements ActionListener {

    public static FlappyBird flappyBird;

    public static final int N = 300;
    public static final int M = 160;

    public static int GENERATION = 0;

    public final int WIDTH = 700, HEIGHT = 700;
    int score;

    boolean isScoreCounted;

    int deadBirdsCount;

    public final int TIMER_DELAY_IN_MILI_S = 20;
    public final int PIPE_SPEED = 10;

    Random rand;

    static ArrayList<Bird> birds;
    Rectangle bird;
    Renderer renderer;
    ArrayList<Rectangle> pipes;

//    for bird motion
    int ticks;
    int yMotion = 0;

    Timer timer;

    boolean  isStarted;

    FlappyBird() {

//        initializing JFrame
        JFrame frame = new JFrame();

        timer = new Timer(TIMER_DELAY_IN_MILI_S, this);

//        initializing Jpanel
        renderer = new Renderer();

//        setting JFrame attributes
        frame.setSize(WIDTH, HEIGHT);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Flappy Bird");
        frame.setVisible(true);

        //     adding renderer to JFRAme
        frame.add(renderer);

//        setting all birds in the center of the screen x,y,width,height
        for (Bird bird : birds) {
            bird.setRectangle(new Rectangle(WIDTH / 2, HEIGHT / 2, 20, 20));

        }

        pipes = new ArrayList<>();

        rand = new Random();

        addPipe(true);
        addPipe(true);
        addPipe(true);
        addPipe(true);

//        starting timer
        timer.start();

    }

    public void addPipe(boolean isStart) {

//        vertical space bw pipes
        int verticalSpace = 300;
        int horizontalSpace = 600;
        int width = 100;
        int height = 50 + rand.nextInt(200);

        if (isStart) {

//            addding lower pipe ( horizontal space 600          , -120 for grass and land, ,                            
            pipes.add(new Rectangle(WIDTH + width + pipes.size() * 300, HEIGHT - height - 120, width, height));
//            upper pipe            x of prev lower pipe
            pipes.add(new Rectangle(pipes.get(pipes.size() - 1).x, 0, width, HEIGHT - height - verticalSpace));
        } else {
//            lower pipe            (add 600 horizontal to prev pipe
            pipes.add(new Rectangle(pipes.get(pipes.size() - 1).x + horizontalSpace, HEIGHT - height - 120, width, height));
//            upper pipe
            pipes.add(new Rectangle(pipes.get(pipes.size() - 1).x, 0, width, HEIGHT - height - verticalSpace));
        }

    }

    public void paintPipe(Graphics graphics, Rectangle pipe) {

        graphics.setColor(Color.green.darker());
        graphics.fillRect(pipe.x, pipe.y, pipe.width, pipe.height);
    }

    public void repaint(Graphics graphics) {

//        setting full screen green screen color
        graphics.setColor(Color.cyan);
        graphics.fillRect(0, 0, WIDTH, HEIGHT);


        Image ground = Toolkit.getDefaultToolkit().getImage("assets/ground.png");
        graphics.drawImage(ground,0 , HEIGHT-120, null);

//        setting birds color
        for (int i = 0; i < N; i++) {
//            graphics.setColor(birds.get(i).getColor());
//            graphics.fillRect(birds.get(i).getRectangle().x, birds.get(i).getRectangle().y,
//                    birds.get(i).getRectangle().width, birds.get(i).getRectangle().height);

            Image img = Toolkit.getDefaultToolkit().getImage(birds.get(i).getImage());
            graphics.drawImage(img, (birds.get(i).getRectangle().x) - 9, birds.get(i).getRectangle().y - 2, null);
        }

//        painting pipes
        for (Rectangle pipe : pipes) {
            paintPipe(graphics, pipe);
        }

//        showing game over or click to start
        graphics.setColor(Color.WHITE);
        graphics.setFont(new Font("Arial", 1, 80));

        if (!isStarted) {
            graphics.drawString("Click to start!", 75, HEIGHT / 2 - 50);
        } else {
            graphics.drawString(String.valueOf(GENERATION), WIDTH / 2 - 25, 100);
        }

//        setting birds alive text
        graphics.setColor(Color.YELLOW);
        graphics.setFont(new Font("Arial", 1, 30));
        graphics.drawString("Birds Alive", 50, 50);

//        setting birds alive count
        graphics.setColor(Color.WHITE);
        graphics.setFont(new Font("Arial", 1, 30));
        graphics.drawString(String.valueOf(N - deadBirdsCount), 90, 90);

//        showing score text
        graphics.setColor(Color.YELLOW);
        graphics.setFont(new Font("Arial", 1, 30));
        graphics.drawString("Score", 550, 50);

        //        showing score 
        graphics.setColor(Color.WHITE);
        graphics.setFont(new Font("Arial", 1, 30));
        graphics.drawString(String.valueOf(score), 575, 90);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
//        timer call back, this will execute after every 20 mili seconds
        ticks++;

        if (isStarted) {

//        moving rectangles
            for (Rectangle pipe : pipes) {
                pipe.x -= PIPE_SPEED;
            }

//            checking is bird needs to jump
            for (int i = 0; i < N; i++) {
                if (birds.get(i).isAlive) {
//                        this method will decide if jump is needed
                    birds.get(i).jump(pipes);

//                    setting fitness
                    birds.get(i).setFitness(ticks);
                }
            }

//            calculating bird fall  motion
            for (int i = 0; i < N; i++) {
                if (birds.get(i).isAlive && ticks % 2 == 0 && birds.get(i).getyMotion() < 15) {
//                    yMotion += 2;
                    birds.get(i).setyMotion(birds.get(i).getyMotion() + 2);

                }

//                this will run eveytime for every bird alive
                if (birds.get(i).isAlive) {

//                    bird fall motion
//                    bird.y += yMotion;
                    birds.get(i).getRectangle().y += birds.get(i).getyMotion();
                }
            }

//        removing pipes that are gone from the screen
//        it was crashing with for each loop thats why using thi loop
            for (int i = 0; i < pipes.size(); i++) {

                Rectangle pipe = pipes.get(i);

//            if pipe is gone
                if (pipe.x + pipe.getWidth() <= 0) {

//                because we added lower pipe first so lower pipe will be removed and thn upper
                    pipes.remove(pipe);
                    /*   because we have to remove two pipes(upper and lower) simultaneously we will add new pipe
                     only when both(upper and lower) pipes are removed*/

//                upper pipe will have y coordinate==0
                    if (pipe.y == 0) {
                        addPipe(false);
                    }
                }
            }

//        checking game terminating condtiiton and collisions
            for (Rectangle pipe : pipes) {

//                checking collision of  pipes with all birds
                for (int i = 0; i < N; i++) {

                    if (birds.get(i).isAlive) {
//                calculating score checking for y=0 so only one time score is count not for two times for upper and lower pipes
                        if (!isScoreCounted && (pipe.y == 0 && pipe.x + pipe.width == birds.get(i).getRectangle().x)) {
                            score++;
                            isScoreCounted = true;

                        }

//                        checking collision
                        if (pipe.intersects(birds.get(i).getRectangle())) {
//                            gameOver = true;
                            birds.get(i).setIsAlive(false);
                            deadBirdsCount++;

//                            setting bird to bottom of the screen
                            birds.get(i).getRectangle().y = HEIGHT;

                        }

//                        checking collision
                        if (birds.get(i).isAlive && (birds.get(i).getRectangle().y > HEIGHT - 120 || birds.get(i).getRectangle().y <= 0)) {
//                            gameOver = true;
                            birds.get(i).setIsAlive(false);
                            deadBirdsCount++;

//                           setting bird to bottom of the screen
                            birds.get(i).getRectangle().y = HEIGHT  ;

                        }
                    }
                }
            }

            isScoreCounted = false;

//               calling repaing of renderer will call paint component in Renderer which eventually will call repaint of Flappy bird(this class)
            renderer.repaint();

            
            if (deadBirdsCount == N) {
                isStarted = false;

//                start next generation
                performEvolution();
            }
        }

    }
 public static void playMusicInBackground() {

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

        executorService.scheduleAtFixedRate(new Runnable() {
            public void run() {

                try {
                    FileInputStream fileInputStream = new FileInputStream("music.mp3");
                    Player player = new Player(fileInputStream);
                    player.play();

                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (JavaLayerException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }, 0, 141, TimeUnit.SECONDS);

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here

        playMusicInBackground();

        Color colors[] = {Color.GRAY, Color.BLUE, Color.YELLOW, Color.RED, Color.MAGENTA, Color.ORANGE, Color.black, Color.PINK, Color.darkGray, Color.LIGHT_GRAY,};
        String[] images = {"assets/flappy-bird.png", "assets/flappy-bird -brown.png", "assets/flappy-bird -green.png", "assets/flappy-bird -pink.png", "assets/flappy-bird -red.png"};

        birds = new ArrayList<>();

//        for images
        int j = 0;
        //initializing population
        for (int i = 0; i < N; i++) {

//            creating bird
            Bird bird = new Bird();
//            creating network
            bird.setNetwork(new EvolutionaryNeuralNetwork());
//            setting bird color
//            bird.setColor(colors[i]);
            bird.setImage(images[j]);

            bird.setIsAlive(true);

            birds.add(bird);
            //randomly initializing networks
            bird.initializeNetwork();

            //setting initial fitness to 0
            bird.setFitness(0);

            j++;
            if (j == 5) {
                j = 0;
            }
        }

//      starting game
        flappyBird = new FlappyBird();
        flappyBird.isStarted = true;
    }

    public void performEvolution() {

        GENERATION++;

        List<Bird> children = new ArrayList<>();

        for (int i = 0; i < M; i = i + 2) {

            //STEP #2.1 different parents
            Bird[] birdsArray = birds.toArray(new Bird[birds.size()]);

//            List<Bird> parent = EvolutionaryAlgorithm.rankBased(birdsArray, 2);
            List<Bird> parent = Arrays.asList(EvolutionaryAlgorithm.rankBased(birds.toArray(new Bird[birds.size()]), 2));
            System.out.println("parentttttttttttttt 1 " + parent.get(0).toString());
            System.out.println("parentttttttttttttt 2 " + parent.get(1).toString());
            //STEP #2.2 apply crossovers and produce 2 children
            Bird child1 = new Bird();
            Bird child2 = new Bird();

            child1.setNetwork(new EvolutionaryNeuralNetwork());
            child2.setNetwork(new EvolutionaryNeuralNetwork());
            //true for first child , false for second child

            child1.getNetwork().crossOver(parent, true);
            child2.getNetwork().crossOver(parent, false);

            child1.setImage(parent.get(1).getImage());
            child2.setImage(parent.get(0).getImage());

//            adding children
            children.add(child1);
            children.add(child2);

            //STEP #2.3 APPLY MUTATION ON TWO PRODUCED CHILDREN OF 0.25
//                   System.out.println("before "+children.get(children.size()-2).toString());
            children.get(children.size() - 2).getNetwork().mutate();
            children.get(children.size() - 1).getNetwork().mutate();
//                   System.out.println("afer "+children.get(children.size()-2).toString());

            //STEP # 2.4 SETTING FITNESS AS AVERAGE OF TWO PARENTS
            children.get(children.size() - 2).setFitness(((parent.get(0).getFitness() + parent.get(1).getFitness()) / 2) - 6 + new Random().nextInt(20));
            children.get(children.size() - 1).setFitness(((parent.get(0).getFitness() + parent.get(1).getFitness()) / 2) - 6 + new Random().nextInt(20));

//            System.out.println("CHILLDDDDDDDD 1 "+ child1.toString());
//            System.out.println("CHILDDDDDDDDD 2 "+child2.toString());
        }

        //STEP # 3 FROM m+n individuals select n fittest individuals
        birds.addAll(children);
        //population selection and resetting population
        birds = EvolutionaryAlgorithm.truncationSelection(birds, N);

        System.out.println("GENERATION " + GENERATION + " Best Fitness " + birds.get(0).getFitness());

        for (int i = 0; i < birds.size(); i++) {
            System.out.println(birds.get(i).toString());
            System.out.println("Fitness " + birds.get(i).getFitness());
        }
        //    start game again
        resetGame();
        flappyBird.isStarted = true;
    }

//    reset game for new generation
    public void resetGame() {

        // setting all birds in the center of the screen x,y,width,height
        for (Bird bird : birds) {
            bird.setRectangle(new Rectangle(WIDTH / 2, HEIGHT / 2, 20, 20));

//            setting y motion to 0
            bird.setyMotion(0);
            bird.setIsAlive(true);
            bird.setFitness(0);
        }

        deadBirdsCount = 0;
        ticks = 0;
        score = 0;

        pipes = new ArrayList<>();

        rand = new Random();

        addPipe(true);
        addPipe(true);
        addPipe(true);
        addPipe(true);

    }

}
