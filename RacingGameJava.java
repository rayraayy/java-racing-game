import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RacingGame extends JPanel implements ActionListener, KeyListener {

    private final Timer timer = new Timer(16, this);

    private double playerX;
    private double playerY;
    private double playerAngle;
    private double playerSpeed;

    private double aiX;
    private double aiY;
    private double aiAngle;
    private double aiSpeed;

    private boolean up;
    private boolean down;
    private boolean left;
    private boolean right;

    private int playerLap;
    private int aiLap;

    private boolean playerPassedCheckpoint;
    private boolean aiPassedCheckpoint;

    private long raceStartTime;
    private long raceFinishTime;

    private boolean raceFinished;
    private String winnerText = "";

    private String difficulty = "Medium";

    private static final int CAR_WIDTH = 18;
    private static final int CAR_HEIGHT = 32;
    private static final int MAX_LAPS = 3;

    private static final double PLAYER_MAX_FORWARD_SPEED = 5.5;
    private static final double PLAYER_MAX_REVERSE_SPEED = -2.0;

    public RacingGame() {
        setPreferredSize(new Dimension(1100, 650));
        setFocusable(true);
        addKeyListener(this);

        chooseDifficulty();
        resetRace();

        timer.start();
    }

    private void chooseDifficulty() {
        String[] options = {
                "Easy",
                "Medium",
                "Hard"
        };

        int choice = JOptionPane.showOptionDialog(
                null,
                "Choose AI difficulty:",
                "Java Racing Game",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]
        );

        if (choice == 0) {
            difficulty = "Easy";
            aiSpeed = 2.0;

        } else if (choice == 2) {
            difficulty = "Hard";
            aiSpeed = 3.3;

        } else {
            difficulty = "Medium";
            aiSpeed = 2.6;
        }
    }

    private void resetRace() {
        playerX = 155;
        playerY = 365;
        playerAngle = 0;
        playerSpeed = 0;

        aiX = 125;
        aiY = 330;
        aiAngle = 0;

        setAISpeedFromDifficulty();

        up = false;
        down = false;
        left = false;
        right = false;

        playerLap = 0;
        aiLap = 0;

        playerPassedCheckpoint = false;
        aiPassedCheckpoint = false;

        raceFinished = false;
        winnerText = "";

        raceStartTime = System.currentTimeMillis();
        raceFinishTime = raceStartTime;

        requestFocusInWindow();
    }

    private void restartRace() {
        chooseDifficulty();
        resetRace();
    }

    private void setAISpeedFromDifficulty() {
        switch (difficulty) {
            case "Easy" -> aiSpeed = 2.0;
            case "Hard" -> aiSpeed = 3.3;
            default -> aiSpeed = 2.6;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        drawTrack(g2);

        drawCar(
                g2,
                playerX,
                playerY,
                playerAngle,
                Color.RED
        );

        drawCar(
                g2,
                aiX,
                aiY,
                aiAngle,
                Color.BLUE
        );

        drawHUD(g2);
        drawControlsPanel(g2);
    }

    private void drawTrack(Graphics2D g2) {

        // Grass background
        g2.setColor(
                new Color(
                        50,
                        150,
                        70
                )
        );

        g2.fillRect(
                0,
                0,
                900,
                650
        );

        // Main road
        g2.setColor(Color.DARK_GRAY);

        g2.fillRoundRect(
                100,
                100,
                700,
                450,
                180,
                180
        );

        // Inner grass
        g2.setColor(
                new Color(
                        50,
                        150,
                        70
                )
        );

        g2.fillRoundRect(
                250,
                220,
                400,
                210,
                120,
                120
        );

        // Dashed centre line
        g2.setColor(Color.WHITE);

        Stroke oldStroke = g2.getStroke();

        g2.setStroke(
                new BasicStroke(
                        3,
                        BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_BEVEL,
                        0,
                        new float[]{12},
                        0
                )
        );

        g2.drawRoundRect(
                175,
                160,
                550,
                330,
                150,
                150
        );

        g2.setStroke(oldStroke);

        // Start / finish line
        int startX = 150;
        int startY = 300;

        for (
                int y = startY;
                y < startY + 100;
                y += 10
        ) {

            for (
                    int x = startX;
                    x < startX + 30;
                    x += 10
            ) {

                boolean whiteSquare =
                        ((x + y) / 10) % 2 == 0;

                g2.setColor(
                        whiteSquare
                                ? Color.WHITE
                                : Color.BLACK
                );

                g2.fillRect(
                        x,
                        y,
                        10,
                        10
                );
            }
        }

        // Checkpoint used for lap tracking
        g2.setColor(
                new Color(
                        255,
                        255,
                        0,
                        110
                )
        );

        g2.fillRect(
                710,
                275,
                20,
                100
        );
    }

    private void drawCar(
            Graphics2D g2,
            double x,
            double y,
            double angle,
            Color color
    ) {

        Graphics2D car =
                (Graphics2D) g2.create();

        car.translate(x, y);
        car.rotate(angle);

        car.setColor(color);

        car.fillRoundRect(
                -CAR_WIDTH / 2,
                -CAR_HEIGHT / 2,
                CAR_WIDTH,
                CAR_HEIGHT,
                6,
                6
        );

        car.setColor(Color.BLACK);

        car.fillRect(
                -CAR_WIDTH / 2 - 3,
                -CAR_HEIGHT / 2 + 4,
                4,
                8
        );

        car.fillRect(
                CAR_WIDTH / 2 - 1,
                -CAR_HEIGHT / 2 + 4,
                4,
                8
        );

        car.fillRect(
                -CAR_WIDTH / 2 - 3,
                CAR_HEIGHT / 2 - 12,
                4,
                8
        );

        car.fillRect(
                CAR_WIDTH / 2 - 1,
                CAR_HEIGHT / 2 - 12,
                4,
                8
        );

        car.setColor(Color.CYAN);

        car.fillRect(
                -5,
                -11,
                10,
                7
        );

        car.dispose();
    }

    private void drawHUD(Graphics2D g2) {
        g2.setColor(Color.WHITE);

        g2.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        18
                )
        );

        // Freeze the displayed time once the race finishes
        long shownTime;

        if (raceFinished) {
            shownTime =
                    raceFinishTime
                            - raceStartTime;

        } else {
            shownTime =
                    System.currentTimeMillis()
                            - raceStartTime;
        }

        long totalSeconds =
                shownTime / 1000;

        long minutes =
                totalSeconds / 60;

        long seconds =
                totalSeconds % 60;

        g2.drawString(
                "Player Lap: "
                        + Math.min(
                        playerLap + 1,
                        MAX_LAPS
                )
                        + "/"
                        + MAX_LAPS,
                20,
                30
        );

        g2.drawString(
                "AI Lap: "
                        + Math.min(
                        aiLap + 1,
                        MAX_LAPS
                )
                        + "/"
                        + MAX_LAPS,
                20,
                55
        );

        g2.drawString(
                String.format(
                        "Time: %02d:%02d",
                        minutes,
                        seconds
                ),
                20,
                80
        );

        g2.drawString(
                "Difficulty: "
                        + difficulty,
                20,
                105
        );

        if (raceFinished) {

            g2.setFont(
                    new Font(
                            "SansSerif",
                            Font.BOLD,
                            42
                    )
            );

            g2.setColor(Color.YELLOW);

            int winnerWidth =
                    g2.getFontMetrics()
                            .stringWidth(
                                    winnerText
                            );

            g2.drawString(
                    winnerText,
                    (900 - winnerWidth) / 2,
                    70
            );

            g2.setFont(
                    new Font(
                            "SansSerif",
                            Font.BOLD,
                            20
                    )
            );

            String restartText =
                    "Press R to race again";

            int restartWidth =
                    g2.getFontMetrics()
                            .stringWidth(
                                    restartText
                            );

            g2.drawString(
                    restartText,
                    (900 - restartWidth) / 2,
                    100
            );
        }
    }

    private void drawControlsPanel(Graphics2D g2) {

        // Controls panel
        g2.setColor(
                new Color(
                        35,
                        35,
                        35
                )
        );

        g2.fillRect(
                900,
                0,
                200,
                650
        );

        g2.setColor(Color.LIGHT_GRAY);

        g2.drawLine(
                900,
                0,
                900,
                650
        );

        g2.setColor(Color.WHITE);

        g2.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        22
                )
        );

        g2.drawString(
                "CONTROLS",
                940,
                70
        );

        g2.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        17
                )
        );

        int y = 120;

        g2.drawString(
                "↑  Accelerate",
                930,
                y
        );

        y += 45;

        g2.drawString(
                "↓  Brake / Reverse",
                930,
                y
        );

        y += 45;

        g2.drawString(
                "←  Turn Left",
                930,
                y
        );

        y += 45;

        g2.drawString(
                "→  Turn Right",
                930,
                y
        );

        y += 60;

        g2.drawString(
                "R  Restart",
                930,
                y
        );

        y += 45;

        g2.drawString(
                "D  Difficulty",
                930,
                y
        );

        y += 80;

        g2.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        18
                )
        );

        g2.drawString(
                "Difficulty",
                930,
                y
        );

        y += 35;

        g2.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        16
                )
        );

        g2.drawString(
                difficulty,
                930,
                y
        );

        y += 80;

        g2.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        18
                )
        );

        g2.drawString(
                "Cars",
                930,
                y
        );

        y += 35;

        g2.setColor(Color.RED);

        g2.fillRect(
                930,
                y - 15,
                18,
                18
        );

        g2.setColor(Color.WHITE);

        g2.drawString(
                "Player",
                960,
                y
        );

        y += 35;

        g2.setColor(Color.BLUE);

        g2.fillRect(
                930,
                y - 15,
                18,
                18
        );

        g2.setColor(Color.WHITE);

        g2.drawString(
                "AI",
                960,
                y
        );
    }

    private void updatePlayer() {

        if (raceFinished) {
            return;
        }

        // Acceleration and braking
        if (up) {
            playerSpeed += 0.10;
        }

        if (down) {

            if (playerSpeed > 0.15) {
                playerSpeed -= 0.18;

            } else {
                playerSpeed -= 0.07;
            }
        }

        // Gradually slow the car when no pedal is pressed
        if (!up && !down) {
            playerSpeed *= 0.97;
        }

        playerSpeed =
                Math.max(
                        PLAYER_MAX_REVERSE_SPEED,
                        Math.min(
                                PLAYER_MAX_FORWARD_SPEED,
                                playerSpeed
                        )
                );

        if (
                Math.abs(playerSpeed)
                        < 0.02
        ) {
            playerSpeed = 0;
        }

        // Steering strength changes slightly depending on speed
        if (
                Math.abs(playerSpeed)
                        > 0.10
        ) {

            double speedRatio =
                    Math.min(
                            Math.abs(
                                    playerSpeed
                            )
                                    / PLAYER_MAX_FORWARD_SPEED,
                            1.0
                    );

            double steeringAmount =
                    0.025
                            + (0.025
                            * speedRatio);

            if (playerSpeed < 0) {
                steeringAmount *= -1;
            }

            if (left) {
                playerAngle -=
                        steeringAmount;
            }

            if (right) {
                playerAngle +=
                        steeringAmount;
            }
        }

        double oldX =
                playerX;

        double oldY =
                playerY;

        playerX +=
                Math.sin(
                        playerAngle
                )
                        * playerSpeed;

        playerY -=
                Math.cos(
                        playerAngle
                )
                        * playerSpeed;

        // Prevent the player from leaving the track
        if (
                !isOnRoad(
                        playerX,
                        playerY
                )
        ) {

            playerX = oldX;
            playerY = oldY;

            playerSpeed *= 0.35;
        }

        updateLap(
                playerX,
                playerY,
                true
        );
    }

    private void updateAI() {

        if (raceFinished) {
            return;
        }

        // Waypoints guide the AI around the track
        double[][] waypoints = {

                {160, 325},
                {180, 180},
                {450, 135},
                {720, 180},
                {755, 325},
                {720, 480},
                {450, 515},
                {180, 480}
        };

        int targetIndex =
                getNextWaypoint(
                        aiX,
                        aiY,
                        waypoints
                );

        double targetX =
                waypoints[targetIndex][0];

        double targetY =
                waypoints[targetIndex][1];

        double desiredAngle =
                Math.atan2(
                        targetX - aiX,
                        -(targetY - aiY)
                );

        aiAngle =
                rotateToward(
                        aiAngle,
                        desiredAngle,
                        0.035
                );

        aiX +=
                Math.sin(aiAngle)
                        * aiSpeed;

        aiY -=
                Math.cos(aiAngle)
                        * aiSpeed;

        updateLap(
                aiX,
                aiY,
                false
        );
    }

    private int getNextWaypoint(
            double x,
            double y,
            double[][] points
    ) {

        int nearest = 0;
        double bestDistance =
                Double.MAX_VALUE;

        for (
                int i = 0;
                i < points.length;
                i++
        ) {

            double dx =
                    points[i][0]
                            - x;

            double dy =
                    points[i][1]
                            - y;

            double distance =
                    dx * dx
                            + dy * dy;

            if (
                    distance
                            < bestDistance
            ) {

                bestDistance =
                        distance;

                nearest = i;
            }
        }

        return (
                nearest + 1
        )
                % points.length;
    }

    private double rotateToward(
            double current,
            double target,
            double maximumTurn
    ) {

        double difference =
                normalizeAngle(
                        target
                                - current
                );

        difference =
                Math.max(
                        -maximumTurn,
                        Math.min(
                                maximumTurn,
                                difference
                        )
                );

        return current
                + difference;
    }

    private double normalizeAngle(
            double angle
    ) {

        while (
                angle
                        > Math.PI
        ) {

            angle -=
                    Math.PI * 2;
        }

        while (
                angle
                        < -Math.PI
        ) {

            angle +=
                    Math.PI * 2;
        }

        return angle;
    }

    private boolean isOnRoad(
            double x,
            double y
    ) {

        boolean insideOuter =
                insideRoundedArea(
                        x,
                        y,
                        100,
                        100,
                        700,
                        450
                );

        boolean insideInner =
                insideRoundedArea(
                        x,
                        y,
                        250,
                        220,
                        400,
                        210
                );

        return insideOuter
                && !insideInner;
    }

    private boolean insideRoundedArea(
            double x,
            double y,
            int rectangleX,
            int rectangleY,
            int width,
            int height
    ) {

        double centerX =
                rectangleX
                        + width / 2.0;

        double centerY =
                rectangleY
                        + height / 2.0;

        double xDistance =
                Math.abs(
                        x - centerX
                );

        double yDistance =
                Math.abs(
                        y - centerY
                );

        double halfWidth =
                width / 2.0;

        double halfHeight =
                height / 2.0;

        if (
                xDistance
                        <= halfWidth - 75
                        &&
                        yDistance
                                <= halfHeight
        ) {

            return true;
        }

        if (
                yDistance
                        <= halfHeight - 75
                        &&
                        xDistance
                                <= halfWidth
        ) {

            return true;
        }

        double cornerX =
                Math.max(
                        0,
                        xDistance
                                - (halfWidth - 75)
                );

        double cornerY =
                Math.max(
                        0,
                        yDistance
                                - (halfHeight - 75)
                );

        return
                (cornerX * cornerX)
                        / (75.0 * 75.0)
                        +
                        (cornerY * cornerY)
                                / (75.0 * 75.0)
                        <= 1.0;
    }

    private void updateLap(
            double x,
            double y,
            boolean player
    ) {

        // A checkpoint must be reached before crossing the finish line
        if (
                x > 690
                        && x < 760
                        && y > 250
                        && y < 400
        ) {

            if (player) {
                playerPassedCheckpoint =
                        true;

            } else {
                aiPassedCheckpoint =
                        true;
            }
        }

        boolean atFinishLine =
                x > 135
                        && x < 205
                        && y > 285
                        && y < 415;

        if (!atFinishLine) {
            return;
        }

        if (
                player
                        && playerPassedCheckpoint
        ) {

            playerLap++;

            playerPassedCheckpoint =
                    false;

            if (
                    playerLap
                            >= MAX_LAPS
            ) {

                finishRace(
                        "YOU WIN!"
                );
            }
        }

        if (
                !player
                        && aiPassedCheckpoint
        ) {

            aiLap++;

            aiPassedCheckpoint =
                    false;

            if (
                    aiLap
                            >= MAX_LAPS
            ) {

                finishRace(
                        "AI WINS!"
                );
            }
        }
    }

    private void finishRace(
            String winner
    ) {

        if (raceFinished) {
            return;
        }

        raceFinished = true;

        // Store the finish time so the timer stops increasing
        raceFinishTime =
                System.currentTimeMillis();

        winnerText =
                winner;

        playerSpeed = 0;
    }

    @Override
    public void actionPerformed(
            ActionEvent event
    ) {

        updatePlayer();
        updateAI();
        repaint();
    }

    @Override
    public void keyPressed(
            KeyEvent event
    ) {

        switch (
                event.getKeyCode()
        ) {

            case KeyEvent.VK_UP ->
                    up = true;

            case KeyEvent.VK_DOWN ->
                    down = true;

            case KeyEvent.VK_LEFT ->
                    left = true;

            case KeyEvent.VK_RIGHT ->
                    right = true;

            case KeyEvent.VK_R ->
                    restartRace();

            case KeyEvent.VK_D -> {

                chooseDifficulty();
                resetRace();
            }
        }
    }

    @Override
    public void keyReleased(
            KeyEvent event
    ) {

        switch (
                event.getKeyCode()
        ) {

            case KeyEvent.VK_UP ->
                    up = false;

            case KeyEvent.VK_DOWN ->
                    down = false;

            case KeyEvent.VK_LEFT ->
                    left = false;

            case KeyEvent.VK_RIGHT ->
                    right = false;
        }
    }

    @Override
    public void keyTyped(
            KeyEvent event
    ) {
    }

    public static void main(
            String[] args
    ) {

        SwingUtilities.invokeLater(
                () -> {

                    JFrame frame =
                            new JFrame(
                                    "Java Racing Game"
                            );

                    RacingGame game =
                            new RacingGame();

                    frame.setDefaultCloseOperation(
                            JFrame.EXIT_ON_CLOSE
                    );

                    frame.setResizable(false);

                    frame.add(game);
                    frame.pack();

                    frame.setLocationRelativeTo(
                            null
                    );

                    frame.setVisible(true);

                    game.requestFocusInWindow();
                }
        );
    }
}
