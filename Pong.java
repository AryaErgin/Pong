import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Color;
import java.awt.Rectangle;

public class Pong extends JPanel implements KeyListener {
    @Override
    public void keyTyped(KeyEvent e) {

    }



    @Override
    public void keyReleased(KeyEvent e) {

    }

    // Create necessary values for paddle
    public class Paddle {
        int x;
        int y;
        int height;
        int width;
        Color color;
        int velocity = 20;

        Paddle(int x, int y, int height, int width, Color color) {
            this.x = x;
            this.y = y;
            this.height = height;
            this.width = width;
            this.color = color;
        }

        public void MoveUp(){
            y -= velocity;
            if (y < textSize){
                y = textSize;
            }
        }

        public void MoveDown() {
            int newY = y + velocity;

            if (newY + height > totalHeight) {
                y = totalHeight - height;
            } else {
                y = newY;
            }
        }

        public void draw(Graphics g) {
            g.setColor(color);
            g.fillRect(x, y, width, height);
        }
    }

    public class Ball{
        int x;
        int y;
        int ballSize;
        Color color;
        int velocityX;
        int velocityY;

        Ball(int x, int y, int ballSize, Color color){
            this.x = x;
            this.y = y;
            this.ballSize = ballSize;
            this.color = color;
        }

        public void draw(Graphics g) {
            g.setColor(color);
            g.fillOval(x, y, ballSize, ballSize);
        }

        public void move(){
             x += velocityX;
             y += velocityY;
        }
    }

    int boardHeight = 720;
    int boardWidth = boardHeight;


    //scoreboard
    String scoreBoard = "Score: ";
    int score1 = 0;
    int score2 = 0;
    int textSize = 50;

    int totalHeight = boardWidth + textSize;

    JFrame frame = new JFrame("Pong");
    Paddle player1, player2;
    boolean upPressed, downPressed, upPressed2, downPressed2;
    Ball ball;
    Timer gameLoop;

    // Set paddle values
    int paddleHeight = 160;
    int paddleWidth = 20;
    int centerY = boardHeight / 2;
    int centerX = boardWidth / 2;
    int desiredDistance = 80;

    //scoring and game over
    boolean gameOver = false;
    boolean showScoreMessage = false;
    String scoreMessage = "";
    int winningScore = 5;

    Pong() {

        addKeyListener(this);
        setFocusable(true);



        player1 = new Paddle(desiredDistance, centerY - paddleHeight/2, paddleHeight, paddleWidth, Color.white);
        player2 = new Paddle((boardWidth - desiredDistance) - paddleWidth * 2, centerY - paddleHeight/2, paddleHeight, paddleWidth, Color.white);

        //set ball values
        int ballSize = 30;
        ball = new Ball((boardWidth - ballSize) / 2, (boardHeight - ballSize) / 2, ballSize, Color.white);

        startGame();


        // Adjust the frame size to ensure the content pane is 800x800
        frame.pack();
        Insets insets = frame.getInsets(); // Get the insets of the frame (borders, title bar)
        int frameWidth = boardWidth + insets.left + insets.right; // Add insets to the width
        int frameHeight = totalHeight + insets.top + insets.bottom ; // Add insets to the height
        frame.setSize(frameWidth, frameHeight); // Set the frame size

        frame.add(this); // Add the current Pong panel to the frame
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gameLoop = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(upPressed){
                    player1.MoveUp();
                }

                if(downPressed){
                    player1.MoveDown();
                }

                if(upPressed2){
                    player2.MoveUp();
                }

                if (downPressed2){
                    player2.MoveDown();
                }

                checkCollision();
                ball.move();
                repaint();
            }
        });
        gameLoop.start();
    }

    //moves upon click
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_W) {
            upPressed = true;
            downPressed = false;
        }

        else if (key == KeyEvent.VK_S) {
            downPressed = true;
            upPressed = false;
        }

        else if (key == KeyEvent.VK_UP) {
            upPressed2 = true;
            downPressed2 = false;
        }

        else if (key == KeyEvent.VK_DOWN) {
            downPressed2 = true;
            upPressed2 = false;
        }

        if (gameOver && key == KeyEvent.VK_SPACE){
            score1 = 0;
            score2 = 0;
            gameOver = false;
            showScoreMessage = true;
            scoreMessage = "New Game Begins Soon";
            repaint();

            Timer startDelay = new Timer(500, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    checkGameOver();
                }

            });
            startDelay.setRepeats(false);
            startDelay.start();
        }
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, boardWidth, totalHeight);

        g.setColor(Color.GRAY);
        g.fillRect(0,0, boardWidth, textSize);

        // Draw paddles into the game
        player1.draw(g);
        player2.draw(g);
        ball.draw(g);

        g.setFont(new Font("Arial", Font.PLAIN, 30));
        g.setColor(Color.white);
        g.drawString(scoreBoard + score1, 10, 30);

        g.setFont(new Font("Arial", Font.PLAIN, 30));
        g.setColor(Color.white);
        g.drawString(scoreBoard + score2, 580, 30);

        if (showScoreMessage || gameOver) {
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.setColor(Color.YELLOW);
            FontMetrics metrics = g.getFontMetrics(g.getFont());
            int x = (boardWidth - metrics.stringWidth(scoreMessage)) / 2;
            g.drawString(scoreMessage, x, boardHeight/2 - 50);
        }
    }

    public void checkCollision(){
        //checks if ball has collided with left paddle
        if (ball.x - ball.ballSize / 2 <= player1.x + player1.width &&
                ball.x - ball.ballSize / 2 >= player1.x &&
                ball.y + ball.ballSize / 2 >= player1.y &&
                ball.y - ball.ballSize / 2 <= player1.y + player1.height){

            ball.x = player1.x + player1.width + ball.ballSize / 2;

            ball.velocityX = -ball.velocityX;
        }

        //checks if ball has collided with right paddle
        if (ball.x + ball.ballSize / 2 >= player2.x &&
                ball.x + ball.ballSize / 2 < player2.x + player2.width &&
                ball.y + ball.ballSize / 2 >= player2.y &&
                ball.y - ball.ballSize / 2 <= player2.y + player2.height){

            ball.x = player2.x - ball.ballSize / 2;

            ball.velocityX = -ball.velocityX;
        }

        //checks if ball has collided with walls
        if (ball.y - ball.ballSize / 2 <= textSize || ball.y + ball.ballSize / 2 >= totalHeight){
            ball.velocityY = -ball.velocityY;
        }

        //give score to player 2 if it goes out of bounds on left side
        if (ball.x - ball.ballSize/2 < 0){
            scoreMessage = "Player 2 Scores!";
            showScoreMessage = true;
            checkGameOver();

        }

        //give score to player 1 if it goes out of bounds on left side
        if (ball.x - ball.ballSize/2 > boardWidth){
            scoreMessage = "Player 1 Scores!";
            showScoreMessage = true;
            checkGameOver();

        }
    }

    public void startGame() {
        // You can add game start logic here
        ball.velocityX = 10;
        ball.velocityY = 10;
    }

    public void newRound(){
        if (gameOver) return;
        if (score1 >= winningScore || score2 >= winningScore) {
            checkGameOver();
            return;
        }

        if (ball.x - ball.ballSize/2 > boardWidth){
            score1 += 1;}

        if (ball.x - ball.ballSize/2 < 0){
            score2 += 1;}

        player1.y = centerY - paddleHeight/2;
        player2.y = centerY - paddleHeight/2;
        ball.y = centerY;
        ball.x = centerX;

        ball.velocityX = new Random().nextInt(2) == 0 ? ball.velocityX : -ball.velocityX;
        ball.velocityY = new Random().nextInt(2) == 0 ? ball.velocityY : -ball.velocityY;

        // Add a short delay before the ball starts moving
        Timer startDelay = new Timer(1000, new ActionListener() {  // 1-second delay
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gameOver) {  // Double-check if game is still ongoing before starting the loop
                    gameLoop.start();
                }
            }
        });
        startDelay.setRepeats(false);  // Ensure it only runs once
        startDelay.start();

        gameLoop.stop();  // Pause the game loop until the delay ends
        repaint();
    }

    public void checkGameOver() {
        if (score1 >= winningScore || score2 >= winningScore) {
            gameOver = true;
            scoreMessage = (score1 >= winningScore) ? "Player 1 Wins!" :
                    "Player 2 Wins!";
            repaint();
            gameLoop.stop();  // Stop the game loop
        } else {
            // Start a new round after a delay of 5 seconds
            Timer pauseTimer = new Timer(5000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    showScoreMessage = false;
                    newRound();
                    repaint();
                }
            });
            pauseTimer.setRepeats(false);  // Ensure it only runs once
            pauseTimer.start();
        }
    }
}
