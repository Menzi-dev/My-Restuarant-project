package com.example.urbanstylerestaurant;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class LandingPageActivity extends AppCompatActivity {

    private ImageView landingImageView, logoImageView;
    private Button getStartedButton;

    private final int[] images = {
            R.drawable.image1,
            R.drawable.image2,
            R.drawable.image3,
            R.drawable.image4,
            R.drawable.image5
    };

    private int currentIndex = 0;
    private final Handler handler = new Handler();
    private Runnable slideshowRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        landingImageView = findViewById(R.id.landingImageView);
        logoImageView = findViewById(R.id.logoImageView);
        getStartedButton = findViewById(R.id.getStartedButton);

        startSmoothSlideshow();

        getStartedButton.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_button));
            startActivity(new Intent(LandingPageActivity.this, LoginActivity.class));
            overridePendingTransition(R.anim.fade_slide, R.anim.fade_slide_exit);
        });
    }

    private void startSmoothSlideshow() {
        landingImageView.setImageResource(images[currentIndex]);

        slideshowRunnable = new Runnable() {
            @Override
            public void run() {
                // Fade out animation
                Animation fadeOut = new AlphaAnimation(1, 0);
                fadeOut.setDuration(300);
                fadeOut.setFillAfter(true);

                fadeOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // Change image after fade out
                        currentIndex = (currentIndex + 1) % images.length;
                        landingImageView.setImageResource(images[currentIndex]);

                        // Fade in animation
                        Animation fadeIn = new AlphaAnimation(0, 1);
                        fadeIn.setDuration(300);
                        fadeIn.setFillAfter(true);
                        landingImageView.startAnimation(fadeIn);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });

                landingImageView.startAnimation(fadeOut);

                handler.postDelayed(this, 6000); // Delay before next transition
            }
        };

        handler.postDelayed(slideshowRunnable, 6000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(slideshowRunnable);
    }
}
