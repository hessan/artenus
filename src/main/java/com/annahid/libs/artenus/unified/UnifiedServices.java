package com.annahid.libs.artenus.unified;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.annahid.libs.artenus.Artenus;
import com.annahid.libs.artenus.internal.unified.BazaarUnifiedServices;
import com.annahid.libs.artenus.internal.unified.CandoUnifiedServices;
import com.annahid.libs.artenus.internal.unified.DummyUnifiedServices;
import com.annahid.libs.artenus.internal.unified.GoogleUnifiedServices;
import com.annahid.libs.artenus.security.LoginManager;

/**
 * Provides store-specific game functionality in a unified way. In-app billing, ad management, and
 * game services are handled by this class.
 */
@SuppressWarnings("UnusedDeclaration")
public abstract class UnifiedServices {
    /**
     * The Store defines the app store the unified services operate in
     */
    public enum Store {
        /**
         * Undefined app-store
         */
        NONE,
        /**
         * Google Play
         */
        GOOGLE,
        /**
         * Amazon Appstore
         */
        AMAZON,

        /**
         * Cafe Bazaar (local Iranian app-store)
         */
        BAZAAR,

        /**
         * Samsung Apps
         */
        SAMSUNG,

        /**
         * Cando (local Iranian app-store)
         */
        CANDO
    }

    /**
     * Service flag for in-app billing
     *
     * @see #getInstance
     */
    public static final int SERVICE_BILLING = 1;

    /**
     * Service flag for game services
     *
     * @see #getInstance()
     */
    public static final int SERVICE_GAMES = 2;

    /**
     * Service flag for ads
     *
     * @see #getInstance()
     */
    public static final int SERVICE_ADS = 32;

    /**
     * Gets the current instance of {@code UnifiedServices}. If no such instance exists, a new
     * instance will be created, supporting services specified.
     *
     * @param services The bit-masked list of services.
     * @return Current instance
     */
    public static UnifiedServices getInstance(int services) {
        if (instance == null) {
            switch (Artenus.getManifestAppStore()) {
                case GOOGLE:
                    instance = new GoogleUnifiedServices();
                    break;
                case AMAZON:
                    try {
                        instance = (UnifiedServices) Class.forName(
                                "com.annahid.libs.artenus.internal.unified.AmazonUnifiedServices"
                        ).getConstructor().newInstance();
                    } catch (Exception ex) {
                        throw new RuntimeException("Amazon unified services not found.");
                    }
                    break;
                case BAZAAR:
                    instance = new BazaarUnifiedServices();
                    break;
                case CANDO:
                    instance = new CandoUnifiedServices();
                    break;
                default:
                    instance = new DummyUnifiedServices();
                    break;
            }

            instance.services = services;
            instance.services = instance.init(services);
        }

        return instance;
    }

    /**
     * Gets the current instance of {@code UnifiedServices}.
     *
     * @return Current instance, or {@code null} if it is not yet created
     */
    public static UnifiedServices getInstance() {
        return instance;
    }

    /**
     * This method is internally called as part of {@link Artenus#onCreate(Bundle)}.
     *
     * @param context Artenus context
     */
    public abstract void onCreate(Context context);

    /**
     * This method is internally called as part of {@link Artenus#onCreate(Bundle)}.
     *
     * @param context Artenus context
     */
    public abstract void onDestroy(Context context);

    /**
     * This method is internally called as part of {@link Artenus#onPause()}.
     */
    public void onPause() {
    }

    /**
     * This method is internally called as part of {@link Artenus#onResume()}.
     */
    public void onResume() {
    }

    /**
     * This method is internally called as part of {@link Artenus#onStart()}.
     */
    public void onStart() {
    }

    /**
     * This method is internally called as part of {@link Artenus#onStop()}.
     */
    public void onStop() {
    }

    /**
     * This method is internally called by {@link Artenus#onActivityResult(int, int, Intent)}.
     * Unified services implementations direct it to their sub-services to handle their own
     * activity requests.
     *
     * @param requestCode The integer request code
     * @param resultCode  The integer result code returned by the child activity
     * @param data        An Intent, which can return result data to the caller
     * @return {@code true} if handled, {@code false} otherwise
     */
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        return false;
    }

    /**
     * Checks if this instance supports specified services. These might be different than those
     * provided in {@link #getInstance(int)}, as implementations mask out services they don't
     * support.
     *
     * @param servicesMask Bit-masked list of services
     * @return {@code true} if specified services are available, {@code false} otherwise
     */
    public final boolean hasServices(int servicesMask) {
        return (services & servicesMask) != 0;
    }

    /**
     * Gets the app-store this instance operates in
     *
     * @return The store
     * @see com.annahid.libs.artenus.unified.UnifiedServices.Store
     */
    public abstract Store getStore();

    /**
     * Gets an instance of {@link AdManager} provided by this implementation.
     *
     * @return Ad manager instance, or {@code null} if ads are not supported
     */
    public abstract AdManager getAdManager();

    /**
     * Gets an instance of {@link GameServices} provided by this implementation.
     *
     * @return Game services instance, or {@code null} if not supported
     */
    public abstract GameServices getGameServices();

    /**
     * Gets an instance of {@link LoginManager} provided by this implementation. The returned value
     * is never {@code null}.
     *
     * @return Login manager instance
     */
    public abstract LoginManager getLoginManager();

    /**
     * Gets an instance of {@link InventoryManager} provided by this implementation.
     *
     * @return In-app billing manager instance, or {@code null} if not supported
     */
    public abstract InventoryManager getInventoryManager();

    /**
     * Called on sub-classes to initialize the services, and mask out those they don't support.
     *
     * @param inputServices Bit-masked list of requested services
     * @return Bit-masked list of requested services supported by the specific implementation
     */
    protected abstract int init(int inputServices);

    /**
     * Called by subclasses to construct an empty instance of {@code UnifiedServices}.
     */
    protected UnifiedServices() {
    }

    private int services;
    private static UnifiedServices instance = null;
}
