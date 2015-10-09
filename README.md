# Artenus 2D Framework for Android Games

This library fascilitates game development for the Android platform. It includes various
features, including graphics and animation, physics, store services, and ad management.

If you have any questions regarding this library, or if you want to participate in the development of the project, please feel free to contact me.

## Installation

Adding Artenus to your proeject is very simple. All you need to do is the following (with Android Studio):

1. Clone this project somewhere on your hard drive.
2. Create a new project with a blank activity.
3. Chose File > New > Import Module...
4. Choose the source diretory to the `artenus' directory (that you cloned in step 1).
5. Click Finish.
6. After the module is added to your project, modify the build.gradle of your main module and add the following to the dependencies:
    `compile project(':artenus')`

Congratulations! You have now empowered your project with Artenus. Next step is to start developing your game!

## Getting Started
After you add Artenus to your project, you need to have your main activity extend Arteuns. The simplest form of an Artenus application activity looks like this:

```
public class MainActivity extends Artenus implements StageManager {
    @Override
    protected void init(Stage stage) {
        stage.setManager(this);
    }

    @Override
    public void onLoadStage(Stage stage) {
        // These textures will be available throughout the game.
        TextureManager.add(
                R.drawable.my_texture,
                R.raw.my_svg_texture
        );
    }

    @Override
    public Scene createInitialScene(Stage stage) {
        // Make a scene by extending Scene!
        return new MyAwesomeScene(stage);
    }

    @Override
    public void onEvent(Stage stage, int eventId) {
        // Not interested!
    }
}
```

The game flow starts from your initial scene, and will continue from one scene to another. Don't forget to handle the back button on the initial scene, or any scene that lets the user exit your game! The stage automatically receives the back button event and passes it to the active scene. It is the scene's responsibility to exit the application when needed, by calling `Artenus.exit()`.

## Reference

[Artenus 2D Framework Reference](http://annahid.com/artenus-docs/)

