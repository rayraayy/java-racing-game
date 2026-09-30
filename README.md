# java-racing-game
# 2D Java Racing Game

A simple 2D racing game developed in Java using Swing. The player races against an AI-controlled opponent around a track over three laps.

![Gameplay Screenshot](screenshots/game-screenshot.png)

## Features

- Player-controlled racing car
- Acceleration, braking and reversing
- Steering using the arrow keys
- Track-boundary collision detection
- Three-lap race system
- Race timer
- AI-controlled opponent
- Easy, Medium and Hard difficulty levels
- Restart functionality
- On-screen controls and race information

## Controls

**Up Arrow** - Accelerate  
**Down Arrow** - Brake / Reverse  
**Left Arrow** - Turn left  
**Right Arrow** - Turn right  
**R** - Restart the race  
**D** - Change difficulty

## Technologies Used

- Java
- Java Swing
- Object-Oriented Programming

## How It Works

The game uses a Java Swing Timer as the main game loop. During each update, the program checks the player's keyboard input, updates the car's speed and direction, checks the track boundaries, updates the AI opponent and checks the progress of each lap.

The player's position is calculated using its current speed and steering angle. Track-boundary collision detection prevents the player from driving outside the racing area.

The AI opponent follows a series of waypoints around the track. The selected difficulty changes the speed of the AI car.

The race ends when either the player or AI completes three laps. The race timer stops when a winner is determined.

## Running the Game

Java must be installed on the computer.

Compile the program:

```bash
javac RacingGame.java
```

Run the game:

```bash
java RacingGame
```

## Project Purpose

This project was created to practise Java programming and apply concepts including object-oriented programming, event handling, game loops, collision detection, user input and simple AI behaviour.
