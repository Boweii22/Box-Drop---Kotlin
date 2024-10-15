# Box Drop Game

## Overview

**Box Drop** is an interactive Android game where players tap the screen to drop colored boxes. The objective is to manage the falling boxes while ensuring they do not exceed the screen boundaries. As the boxes accumulate, the ground opens to allow the boxes to pass through, creating a dynamic gameplay experience.

## Features

- Tap to drop boxes from the top of the screen.
- Colored boxes with random colors.
- Physics simulation using the JBox2D library.

## Prerequisites

Before running the project, ensure you have the following installed:

- Android Studio
- Java Development Kit (JDK) 1.8 or higher
- Gradle

## Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/Boweii22/Box-Drop---Kotlin.git
   cd box-drop
   ```

2. **Open the project in Android Studio**:
   - Launch Android Studio.
   - Select "Open an existing Android Studio project" and navigate to the cloned repository.

3. **Build the project**:
   - In Android Studio, click on `Build` > `Rebuild Project` to ensure all dependencies are properly configured.

4. **Run the project**:
   - Connect your Android device or start an emulator.
   - Click on the `Run` button or press `Shift + F10` to launch the application.

## Usage

- Tap anywhere on the screen to drop a new box.
- Boxes will fall due to gravity and stack on each other.
- **Note**: You need to click the screen quickly and multiple times to effectively open the ground, allowing the boxes to pass through when they reach 20% of the screen height.
- The ground will close when the boxes drop below 80% of the screen height.
- The game continues until you close the application.

## Code Overview

The main code for the Box Drop game is implemented in the `BoxDropView` class. Here’s a brief overview of its functionality:

- **Box Creation**: Boxes are created dynamically when the screen is tapped.
- **Physics Simulation**: Utilizes the JBox2D library for realistic box behavior.
- **Ground Management**: The ground opens and closes based on the height of the stacked boxes.

### Key Methods

- `onDraw(Canvas canvas)`: Handles the rendering of the boxes and the ground.
- `createBox(float x, float y)`: Creates a new box with a random color.
- `removeGround()` / `restoreGround()`: Manages the opening and closing of the ground based on box height.

## Contributing

Contributions are welcome! If you have suggestions or improvements, please fork the repository and submit a pull request.


## Acknowledgments

- [JBox2D](https://github.com/jbox2d/jbox2d): A 2D physics engine for Java that powers the game's physics simulation.
- Thanks to all the contributors and community members who provide valuable feedback.

  ![box_drop_logo](https://github.com/user-attachments/assets/a9ebf9c5-b4a3-4c33-9267-67a60dabc771)
