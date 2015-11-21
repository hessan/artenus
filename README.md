# Artenus 2D Framework for Android Games

This library facilitates game development for the Android platform. It includes various
features, including graphics and animation, physics, store services, and ad management.

If you have any questions regarding this library, or if you want to participate in the development
of the project, please feel free to contact me.

## Installation

Adding Artenus to your project is very simple. All you need to do is the following (with Android
Studio):

1. Create a new project with a blank activity (you will replace the code for the activity).
2. Clone Artenus into your project directory (next to your main app module).
3. Edit "settings.gradle" and add this line: `include ':artenus'`
4. After the module is added to your project, modify "build.gradle" of your app module and add the
   following to the dependencies: `compile project(':artenus')`
5. Sync gradle

Congratulations! You have now empowered your project with Artenus. Next step is to start developing
your game!

## Getting Started
After you add Artenus to your project, you need to have your main activity extend Arteuns. The
simplest form of an Artenus application activity looks like this:

```java
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
    public void onEvent(Stage stage, StageEvents event) {
        // Not interested!
    }
}
```

The game flow starts from your initial scene, and will continue from one scene to another. Inside
each scene, you may use `stage.setScene(newScene)` to change to another scene. It is recommended not
to recycle scenes or keep them in memory for a later use, as this is not what the framework is
designed to handle.

Don't forget to handle the back button on the initial scene, or any scene that lets the user exit
your game! The stage automatically receives back button events and passes them to the active scene.
It is the scene's responsibility to exit the application when needed, by calling `Artenus.exit()`.

If at any point you need to access the Android application context (such as when loading local scene
textures), use `Artenus.getInstance()` instead of manually keeping an instance variable or using
complicated logic. Always let the framework do the heavy lifting for you where it can.

## Reference

[Artenus 2D Framework Reference](http://annahid.com/artenus-docs/)

## Developed By

Hessan Feghhi - hessan@annahid.com
