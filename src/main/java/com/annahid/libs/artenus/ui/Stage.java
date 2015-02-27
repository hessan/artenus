package com.annahid.libs.artenus.ui;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLES10;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.annahid.libs.artenus.Artenus;
import com.annahid.libs.artenus.R;
import com.annahid.libs.artenus.data.Point2D;
import com.annahid.libs.artenus.data.RGB;
import com.annahid.libs.artenus.entities.sprites.ImageSprite;
import com.annahid.libs.artenus.entities.sprites.LineSprite;
import com.annahid.libs.artenus.graphics.TextureManager;
import com.annahid.libs.artenus.input.InputListener;
import com.annahid.libs.artenus.input.InputManager;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.opengles.GL10;

/**
 * <p>This class is one of the key components of this framework. {@code Stage} is
 * where everything actually happens. Each game has a stage on which different scenes appear.
 * Its instance is passed to the application through {@link com.annahid.libs.artenus.Artenus#init}.
 * The application needs to assign a {@link com.annahid.libs.artenus.ui.StageManager} to the
 * stage in order to handle its important event.</p>
 * 
 * @author Hessan Feghhi
 * @see StageManager
 */
@SuppressWarnings("UnusedDeclaration")
public final class Stage extends GLSurfaceView {
	private final class StageAdvanceTask extends Thread {
		private long mLastTime;
		private int tid;

		private StageAdvanceTask() {
			mLastTime = System.nanoTime();
			tid = ++advanceThreadId;
		}

		@Override
		public void run() {
			while (true) {
				if (advanceThread == null || tid != advanceThreadId)
					return;

				final long time = System.nanoTime();
				final long diff = time - mLastTime;

				if (diff < 64000000) {
					if (nextScene != null) {
						stPhase = Math.min(1, stPhase + diff / 250000000.0f);

						if (stPhase == 1) {
							TextureManager.unloadLocal();
							nextScene.onLocalLoad();
							currentScene = nextScene;
							nextScene = null;
							loadingSprite = null;
						}
					} else if (stPhase > 0)
						stPhase = Math.max(0, stPhase - diff / 250000000.0f);

					if (currentScene != null)
						if (currentScene.isLoaded())
							currentScene.advance(diff / 1000000000.0f);

					requestRender();
				}

				mLastTime = time;

				if (diff < 17000000)
					try {
						Thread.sleep(20 - diff / 1000000);
					} catch (InterruptedException e) {
						// Do nothing
					}
			}
		}
	}

	private final class MyRenderer implements Renderer {
		public final void onDrawFrame(GL10 gl) {
			if (advanceThread == null ||
					(((EGL10) EGLContext.getEGL()).eglGetCurrentContext().equals(EGL10.EGL_NO_CONTEXT))) {

				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// Do nothing
				}

				return;
			}

			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glOrthof(0, w, h, 0, -1f, 1f);
			gl.glMatrixMode(GL10.GL_MODELVIEW);

			final int ts = TextureManager.getCurrentState();

			if (ts != TextureManager.STATE_LOADED) {
				if (IntroScene.introShown)
					drawLoading(gl);

				if (lll == 0)
					lll = System.currentTimeMillis();

				if (ts == TextureManager.STATE_FRESH && System.currentTimeMillis() - lll > 200)
					TextureManager.loadTextures();

				return;
			}

			lll = 0;

			if (supportsEs2) {
				try {
					if (backFrameBuffer == null) {
						// Setup back-buffer
						backFrameBuffer = new int[1];
						backTexBuffer = new int[1];
						GLES20.glGenFramebuffers(1, backFrameBuffer, 0);
						GLES20.glGenTextures(1, backTexBuffer, 0);
						GLES20.glBindTexture(GLES10.GL_TEXTURE_2D, backTexBuffer[0]);
						GLES20.glTexParameteri(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_MAG_FILTER, GLES10.GL_LINEAR);
						GLES20.glTexParameteri(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_MIN_FILTER, GLES10.GL_LINEAR);
						final IntBuffer backTextureBuffer = ByteBuffer.allocateDirect(screenWidth * screenHeight * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
						GLES10.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB, screenWidth, screenHeight, 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_SHORT_5_6_5, backTextureBuffer);
						GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_RGB565, screenWidth, screenHeight);

						if (backFrameBuffer[0] == 0)
							supportsEs2 = false;
					}

					GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, backFrameBuffer[0]);
					GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, backTexBuffer[0], 0);

					if (GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER) != GLES20.GL_FRAMEBUFFER_COMPLETE)
						return;
				} catch (Exception ex) {
					backFrameBuffer = null;
					supportsEs2 = false;
				}
			}

			final RGB clearColor = currentScene == null ? defClearColor : currentScene.bgColor;

			gl.glClearColor(clearColor.r, clearColor.g, clearColor.b, 1.0f);
			gl.glClear(GLES10.GL_COLOR_BUFFER_BIT);

			if (currentScene != null) {
				if (!currentScene.isLoaded())
					currentScene.onLoaded();

				currentScene.render();
			}

			if (tintColor[3] > 0.01 || stPhase > 0) {
				gl.glPushMatrix();
				gl.glTranslatef(w / 2, h / 2, 0);
				gl.glScalef(w, h, 0);
				gl.glDisable(GL10.GL_TEXTURE_2D);

				if (tintColor[3] > 0.01) {
					gl.glColor4f(tintColor[0], tintColor[1], tintColor[2], tintColor[3]);
					gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
				} else {
					gl.glColor4f(0, 0, 0, stPhase);
					gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
				}

				gl.glPopMatrix();
			}

			gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

			if (supportsEs2) {
				GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
				gl.glEnable(GL10.GL_TEXTURE_2D);
				gl.glBindTexture(GL10.GL_TEXTURE_2D, backTexBuffer[0]);
				gl.glPushMatrix();
				gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
				gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
				gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
				gl.glTranslatef(w / 2, h / 2, 0);
				gl.glScalef(w, -h, 0);
				gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

				if (blur > 0) {
					gl.glScalef(1.0f + blur, 1.0f + blur, 0);
					gl.glColor4f(0.5f, 0.5f, 0.5f, 0.5f);
					gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
				}

				gl.glPopMatrix();
			}
		}

		public final void onSurfaceChanged(GL10 gl, int width, int height) {
			final RGB clearColor = currentScene == null ? defClearColor : currentScene.bgColor;

			gl.glClearColor(clearColor.r, clearColor.g, clearColor.b, 1.0f);
			gl.glLoadIdentity();

			if (width > height) {
				h = 600f;
				w = width * h / height;
			} else {
				w = 600f;
				h = height * w / width;
			}

			if (backFrameBuffer != null) {
				GLES20.glDeleteFramebuffers(1, backFrameBuffer, 0);
				backFrameBuffer = null;
			}

			screenWidth = width;
			screenHeight = height;
			texScale = (float) Math.min(screenHeight, screenWidth) / 600.0f;

			gl.glViewport(0, 0, screenWidth, screenHeight);
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, Stage.vertexBuffer);
		}

		public final void onSurfaceCreated(GL10 gl, EGLConfig config) {
			gl.glEnable(GL10.GL_ALPHA_TEST);
			gl.glEnable(GL10.GL_BLEND);
			gl.glDisable(GL10.GL_DEPTH_TEST);
			gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

			if (backFrameBuffer != null) {
				GLES20.glDeleteFramebuffers(1, backFrameBuffer, 0);
				backFrameBuffer = null;
			}

			if (handler != null)
				handler.onEvent(Stage.this, StageManager.EVENT_DISPLAY);

			TextureManager.unloadTextures();
		}
	}

	private static float texScale = 1.0f;
	private static FloatBuffer vertexBuffer = null;
	private static int screenWidth, screenHeight;

	static FloatBuffer textureBuffer = null;

	/**
	 * Retrieves the texture scaling factor for this {@code Stage}. Normally all stages
	 * are assumed to have the smallest dimension of 600. The smallest dimension means
	 * the the width in portrait mode and height in landscape mode. If the real
	 * dimensions is larger that this, textures are scaled to maintain this perception
	 * from the developer's point of view. This method yields the scaling factor used.
	 *
	 * @return The texture scaling factor.
	 */
	public static float getTextureScalingFactor() {
		return texScale;
	}

	/**
	 * Constructs a new stage on the given application context and with the given set
	 * of XML attributes. This method will be automatically called if you put the
	 * {@code Stage} tag in your layout XML.
	 *
	 * @param context The application context
	 * @param attrs   The set of attributes
	 */
	public Stage(Context context, AttributeSet attrs) {
		super(context, attrs);
		TextureManager.setLoadingTexture(R.raw.loading);
		currentScene = null;
		nextScene = null;

		//setEGLContextClientVersion(2);

		@SuppressWarnings("deprecation")
		int px = Artenus.getInstance().getWindowManager().getDefaultDisplay().getPixelFormat();

		if (px == PixelFormat.RGB_565)
			setEGLConfigChooser(5, 6, 5, 0, 0, 0);
		else setEGLConfigChooser(8, 8, 8, 8, 0, 0);

		supportsEs2 = true;

		setRenderer(new MyRenderer());
		scheduleTimer();

		if (vertexBuffer == null) {
			final float vertices[] = {
					-0.5f, -0.5f, 0.0f,  // 0. left-bottom
					0.5f, -0.5f, 0.0f,  // 1. right-bottom
					-0.5f, 0.5f, 0.0f,  // 2. left-top
					0.5f, 0.5f, 0.0f   // 3. right-top
			};

			final float texture[] = {
					0.0f, 0.0f,
					1.0f, 0.0f,
					0.0f, 1.0f,
					1.0f, 1.0f,
			};

			final ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
			vbb.order(ByteOrder.nativeOrder());
			vertexBuffer = vbb.asFloatBuffer();
			vertexBuffer.put(vertices);
			vertexBuffer.position(0);

			final ByteBuffer ibb = ByteBuffer.allocateDirect(texture.length * 4);
			ibb.order(ByteOrder.nativeOrder());
			textureBuffer = ibb.asFloatBuffer();
			textureBuffer.put(texture);
			textureBuffer.position(0);
		}

		if (Build.VERSION.SDK_INT >= 11)
			setPreserveEGLContextOnPause(true);

		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		setScene(new IntroScene(this));
	}

	/**
	 * Sets the background color of the loading screen.
	 *
	 * @param bgColor	Background color
	 */
	public void setLoadingBackColor(RGB bgColor) {
		loadingBackground.r = bgColor.r;
		loadingBackground.g = bgColor.g;
		loadingBackground.b = bgColor.b;
	}

	/**
	 * Converts screen x to stage x coordination considering the scaling factor.
	 *
	 * @param x Screen x
	 * @return Stage x
	 */
	public final float screenToStageX(float x) {
		return x * w / screenWidth;
	}

	/**
	 * Converts screen y to stage x coordination considering the scaling factor.
	 *
	 * @param y Screen y
	 * @return Stage y
	 */
	public final float screenToStageY(float y) {
		return y * h / screenHeight;
	}

	/**
	 * Converts stage x to screen x coordination considering the scaling factor.
	 *
	 * @param x Stage x
	 * @return Screen x
	 */
	public final float stageToScreenX(float x) {
		return x * screenWidth / w;
	}

	/**
	 * Converts stage y to screen y coordination considering the scaling factor.
	 *
	 * @param y Screen y
	 * @return Stage y
	 */
	public final float stageToScreenY(float y) {
		return y * screenHeight / h;
	}

	/**
	 * Gets the currently assigned input manager.
	 *
	 * @return The input manager
	 */
	public final InputManager getInputManager() {
		return inputMan;
	}

	/**
	 * Appoints a new input manager to handle inputs for this {@code Stage}.
	 *
	 * @param inputManager The new input manager
	 */
	public void setInputManager(InputManager inputManager) {
		inputMan = inputManager;
		inputMan.register(getContext());
		inputMan.setListener(new InputListener() {
			@Override
			public void inputStatusChanged(InputManager inputManager) {
				if (currentScene != null)
					currentScene.handleInput(inputManager);
			}
		});
	}

	/**
	 * Sets the next scene that should be shown on this {@code Stage}. The
	 * scene will not be shown immediately and will go through a transition
	 * effect and possibly a local loading procedure.
	 *
	 * @param scene The new scene
	 */
	public final void setScene(Scene scene) {
		if (!(currentScene instanceof IntroScene)) {
			blur = 0;
			nextScene = scene;
		}
	}

	/**
	 * Gets the current scene. If a new scene has been set, but the transition
	 * is not yet complete, previous scene might be returned.
	 *
	 * @return The current scene
	 */
	public final Scene getScene() {
		return currentScene;
	}

	/**
	 * Gets the width of the stage. If the device is in portrait mode, this
	 * value is always 600 and otherwise it is the scaled version of screen
	 * width to match the height of 600.
	 *
	 * @return Stage width
	 */
	public final float getGLWidth() {
		return w;
	}

	/**
	 * Gets the width of the stage. If the device is in landscape mode, this
	 * value is always 600 and otherwise it is the scaled version of screen
	 * height to match the width of 600.
	 *
	 * @return Stage width
	 */
	public final float getGLHeight() {
		return h;
	}

	/**
	 * Gets the real screen width. This width is the width of the stage, not
	 * the whole device screen.
	 *
	 * @return Screen width
	 */
	public final int getScreenWidth() {
		return screenWidth;
	}

	/**
	 * Gets the real screen height. This height is the height of the stage,
	 * not the whole device screen.
	 *
	 * @return Screen height
	 */
	public final int getScreenHeight() {
		return screenHeight;
	}

	/**
	 * Sets the default background color for scenes that don't specify one.
	 *
	 * @param rgb The default color
	 */
	public void setDefaultBackColor(RGB rgb) {
		setDefaultBackColor(rgb.r, rgb.g, rgb.b);
	}

	/**
	 * Sets the default background color for scenes that don't specify one.
	 *
	 * @param r The red component of the color
	 * @param g The green component of the color
	 * @param b The blue component of the color
	 */
	public void setDefaultBackColor(float r, float g, float b) {
		defClearColor.r = r;
		defClearColor.g = g;
		defClearColor.b = b;
	}

	/**
	 * Sets screen tint. The color specified will cover the stage screen and
	 * all scenes that are displayed on it.
	 *
	 * @param r The red component of the tint
	 * @param g The green component of the tint
	 * @param b The blue component of the tint
	 * @param a The alpha transparency value of the tint
	 */
	public final void setTint(float r, float g, float b, float a) {
		tintColor[0] = r;
		tintColor[1] = g;
		tintColor[2] = b;
		tintColor[3] = a;
	}

	/**
	 * Renders the "loading" screen on the given OpenGL context. Like all the
	 * methods in the framework that involve the OpenGL context directly, this
	 * method is intended for internal use and manual invocation is not
	 * recommended.
	 *
	 * @param gl The OpenGL context
	 */
	public final void drawLoading(GL10 gl) {
		final RGB clearColor = loadingBackground;
		gl.glViewport(0, 0, screenWidth, screenHeight);
		gl.glClearColor(clearColor.r, clearColor.g, clearColor.b, 1.0f);
		gl.glClear(GLES10.GL_COLOR_BUFFER_BIT);

		if (!TextureManager.getLoadingTexture().isLoaded())
			TextureManager.getLoadingTexture().waitLoad();

		if (loadingSprite == null) {
			loadingSprite = new ImageSprite(-1, null);
			loadingSprite.setPosition(getGLWidth() / 2, getGLHeight() / 2);

			loadingBarSprite = new LineSprite(
					new Point2D(0, getGLHeight()),
					new Point2D(
							getGLWidth() * TextureManager.getLoadedCount() / TextureManager.getTextureCount(),
							getGLHeight()),
					3);
			loadingBarSprite.setAlpha(0.75f);
		}

		loadingSprite.render(0);
		loadingBarSprite.setEndPoint(
				getGLWidth() * TextureManager.getLoadedCount() / TextureManager.getTextureCount(),
				getGLHeight());
		loadingBarSprite.render(0);

		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	}

	/**
	 * Adds up to the value of blur on this stage. This method needs the {@code supportES2}
	 * attribute to be set for this {@code Stage} and the device to support OpenGLES 2.
	 *
	 * @param blur The amount to be added to the blur
	 */
	public final void addBlur(float blur) {
		this.blur = Math.max(0, this.blur + blur);
	}

	/**
	 * Sets the amount of blur on this stage. This method needs the {@code supportES2}
	 * attribute to be set for this {@code Stage} and the device to support OpenGLES 2.
	 *
	 * @param blur The value of a blur in the range 0 to 1
	 */
	public final void setBlur(float blur) {
		this.blur = Math.max(blur, 0);
	}

	/**
	 * Gets the current value of blur specified for this {@code Stage}.
	 *
	 * @return The blur value in the range 0 to 1
	 */
	public final float getBlur() {
		return blur;
	}

	/**
	 * Indicates whether this {@code Stage} supports OpenGLES 2 features. THe value
	 * returned by this method depends on the {@code supportES2} attribute setting for
	 * this {@code Stage} and also on device capabilities.
	 *
	 * @return    {@code true} if OpenGLES 2 is supported, and {@code false} otherwise
	 */
	public boolean supportsES2() {
		return supportsEs2;
	}

	/**
	 * This method handles the physical back button for this {@code Stage}.
	 *
	 * @return {@code true} if the back button has been handled by this {@code Stage} or the
	 * currently active {@code Scene}, {@code false} otherwise
	 * @see com.annahid.libs.artenus.ui.Scene
	 */
	public boolean onBackButton() {
		if (currentScene == null)
			return false;
		else {
			if (currentScene.dialog == null)
				return currentScene.onBackButton();
			else return currentScene.dialog.onBackButton();
		}
	}

	/**
	 * Gets the currently assigned stage manager. A stage manager handles basic events
	 * and functionality for this {@code Stage}.
	 *
	 * @return The stage manager for this stage
	 */
	public StageManager getStageManager() {
		return handler;
	}

	/**
	 * Appoints a stage manager to handle required functionality for this {@code Stage}.
	 * You MUST assign a stage manager before doing anything else with this {@code Stage}.
	 *
	 * @param stageManager The stage manager
	 */
	public void setStageManager(StageManager stageManager) {
		handler = stageManager;
	}

	/**
	 * Handles motion events for this {@code Stage} and passes them on to the input manager
	 * and current scene or dialog.
	 */
	@Override
	public boolean onTouchEvent(@NonNull MotionEvent event) {
		if (inputMan != null)
			inputMan.onTouchEvent(this, event);

		if (currentScene != null) {
			float x = screenToStageX(event.getX()), y = screenToStageY(event.getY());
			final int pointerIndex = event.getActionIndex();
			int action = event.getAction() & MotionEvent.ACTION_MASK;

			if (action == MotionEvent.ACTION_POINTER_UP)
				action = InputManager.EVENT_UP;
			else if (action == MotionEvent.ACTION_POINTER_DOWN)
				action = InputManager.EVENT_DOWN;

			if (currentScene.dialog != null) {
				if (currentScene.dialog.getResult() == Dialog.RESULT_NONE && currentScene.dialog.isLoaded()) {
					currentScene.dialog.handleTouch(pointerIndex, action, x, y);
					requestRender();
				}
			} else if (currentScene.isLoaded()) {
				currentScene.handleTouch(pointerIndex, action, x, y);
				requestRender();
			}

			return true;
		}

		return super.onTouchEvent(event);
	}

	/**
	 * This method is inherited from {@code GLSurfaceView}.
	 */
	@Override
	public void onPause() {
		super.onPause();
		TextureManager.getLoadingTexture().destroy();
		advanceThread = null;

		if (handler != null)
			handler.onEvent(this, StageManager.EVENT_PAUSE);
	}

	/**
	 * This method is inherited from {@code GLSurfaceView}.
	 */
	@Override
	public void onResume() {
		super.onResume();
		scheduleTimer();

		if (handler != null)
			handler.onEvent(this, StageManager.EVENT_RESUME);
	}

	/**
	 * Forces the new scene. This method is called from the intro scene.
	 *
	 * @param scene The new scene
	 */
	final void forceScene(Scene scene) {
		blur = 0;
		nextScene = scene;
	}

	/**
	 * Schedules the advance (frame) timer
	 */
	private void scheduleTimer() {
		if (advanceThread == null) {
			advanceThread = new StageAdvanceTask();
			advanceThread.start();
		}
	}

	private final RGB defClearColor = new RGB(0, 0, 0);
	private final RGB loadingBackground = new RGB(0, 0, 0);
	private final float[] tintColor = {0f, 0f, 0f, 0f};
	private Scene currentScene, nextScene;
	private float stPhase;
	private InputManager inputMan = null;
	private Thread advanceThread;
	private float w, h, blur = 0;
	private boolean supportsEs2 = false;
	private int[] backFrameBuffer, backTexBuffer;
	private int advanceThreadId = 1000;
	private StageManager handler = null;

	private ImageSprite loadingSprite;
	private LineSprite loadingBarSprite;

	/**
	 * This field is used to delay texture loading  a bit to let
	 * the loading screen appear before. It holds the start time
	 * for delay calculation.
	 */
	private long lll = 0;
}
