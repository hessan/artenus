package com.annahid.libs.artenus.unified.impl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;

import com.annahid.libs.artenus.Artenus;
import com.annahid.libs.artenus.unified.UnifiedServices;
import com.annahid.libs.artenus.security.LoginManager;
import com.annahid.libs.artenus.security.LoginStatusListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

import java.lang.ref.WeakReference;

final class GoogleLoginManager implements LoginManager,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
	private static final int LOGIN_MASK = UnifiedServices.SERVICE_GAMES;
	private static int RC_SIGN_IN = 9011;

	private boolean mResolvingConnectionFailure = false;
	private boolean mSignInClicked = false;
	private GoogleApiClient mGoogleApiClient = null;
	private WeakReference<LoginStatusListener> listener;
	private Context context;

	public void onCreate(Context context) {
		this.context = context;
	}

	public void onStart() {
		if (mGoogleApiClient != null && !mGoogleApiClient.isConnecting())
			mGoogleApiClient.connect();
	}

	public void onStop() {
		if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}

	@SuppressWarnings("UnusedDeclaration")
	public boolean onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == RC_SIGN_IN) {
			mSignInClicked = false;
			mResolvingConnectionFailure = false;

			if (resultCode == Activity.RESULT_OK) {
				if (!mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting())
					mGoogleApiClient.connect();
			} else {
				mSignInClicked = false;
				// Sign in dialog failed (canceled, etc)

				if (listener != null && listener.get() != null) {
					listener.get().onStatusChanged(false, LOGIN_MASK);
				}
			}

			return true;
		}

		return false;
	}

	public void setGoogleApiClient(GoogleApiClient apiClient) {
		if (mGoogleApiClient != null) {
			mGoogleApiClient.unregisterConnectionCallbacks(this);
			mGoogleApiClient.unregisterConnectionFailedListener(this);
		}

		mGoogleApiClient = apiClient;
		mGoogleApiClient.registerConnectionCallbacks(this);
		mGoogleApiClient.registerConnectionFailedListener(this);
	}

	@Override
	public boolean isLoginRequired(int services) {
		return (services & LOGIN_MASK) != 0;
	}

	@Override
	public boolean isLoggedIn(int services) {
		return !isLoginRequired(services) ||
				(mGoogleApiClient != null && mGoogleApiClient.isConnected());
	}

	@Override
	public void setLoginStatusListener(LoginStatusListener listener) {
		this.listener = new WeakReference<>(listener);
	}

	@Override
	public void launchLogin(int services) {
		final int available = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);

		if(available != ConnectionResult.SUCCESS) {
			GooglePlayServicesUtil.getErrorDialog(available, Artenus.getInstance(), 1111).show();
		}

		if (mGoogleApiClient == null || available != ConnectionResult.SUCCESS) {
			if (listener != null && listener.get() != null) {
				listener.get().onStatusChanged(false, LOGIN_MASK);
			}
			return;
		}

		mSignInClicked = true;

		mGoogleApiClient.connect();
	}

	@Override
	public void logout(int services) {
		mSignInClicked = false;

		if (listener != null && listener.get() != null) {
			listener.get().onStatusChanged(false, LOGIN_MASK);
		}

		if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
			Games.signOut(mGoogleApiClient);
			mGoogleApiClient.reconnect();
		}
	}

	@Override
	public void onConnected(Bundle bundle) {
		if (listener != null && listener.get() != null) {
			listener.get().onStatusChanged(true, LOGIN_MASK);
		}

		mSignInClicked = false;
	}

	@Override
	public void onConnectionSuspended(int i) {
		// Attempt to reconnect
		mGoogleApiClient.connect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		if (mResolvingConnectionFailure) {
			// already resolving
			return;
		}

		// if the sign-in button was clicked or if auto sign-in is enabled,
		// launch the sign-in flow
		if (mSignInClicked) {
			mSignInClicked = false;

			// Attempt to resolve the connection failure.
			if (!resolveConnectionFailure(connectionResult, RC_SIGN_IN)) {
				mResolvingConnectionFailure = false;
			}
		}
	}

	public boolean resolveConnectionFailure(ConnectionResult result, int requestCode) {
		if (result.hasResolution()) {
			try {
				mResolvingConnectionFailure = true;
				result.startResolutionForResult((Activity) context, requestCode);
				return true;
			} catch (IntentSender.SendIntentException e) {
				// The intent was canceled before it was sent.  Return to the default
				// state and attempt to connect to get an updated ConnectionResult.
				mResolvingConnectionFailure = false;
				mGoogleApiClient.connect();
				return false;
			}
		} else {
			if (listener != null && listener.get() != null) {
				listener.get().onStatusChanged(false, LOGIN_MASK);
			}

			return false;
		}
	}
}
