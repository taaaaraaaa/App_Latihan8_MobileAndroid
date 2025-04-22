package id.genta.ramadhan.latihan8;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PICK_IMAGE = 2;
    private static final int REQUEST_PERMISSIONS = 3;

    private ImageView photoImageView;
    private VideoView videoView;
    private Button takePhotoButton, selectPhotoButton, playAudioButton, playVideoButton;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        photoImageView = findViewById(R.id.photoImageView);
        videoView = findViewById(R.id.videoView);
        takePhotoButton = findViewById(R.id.takePhotoButton);
        selectPhotoButton = findViewById(R.id.selectPhotoButton);
        playAudioButton = findViewById(R.id.playAudioButton);
        playVideoButton = findViewById(R.id.playVideoButton);

        // Meminta izin jika belum diberikan
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSIONS);
        }

        takePhotoButton.setOnClickListener(v -> dispatchTakePictureIntent());
        selectPhotoButton.setOnClickListener(v -> openGallery());
        playAudioButton.setOnClickListener(v -> playAudio());
        playVideoButton.setOnClickListener(v -> playVideo());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Izin diberikan", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Izin ditolak", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void openGallery() {
        Intent pickImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickImageIntent, REQUEST_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK && data != null) {
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            photoImageView.setImageBitmap(imageBitmap);
        } else if (requestCode == REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            photoImageView.setImageURI(selectedImageUri);
        }
    }

    private void playAudio() {
        if (videoView.isPlaying()) {
            videoView.stopPlayback();
        }

        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.reset();
            }

            Uri audioUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sample_audio);
            mediaPlayer = MediaPlayer.create(this, audioUri);
            mediaPlayer.start();

            mediaPlayer.setOnCompletionListener(mp ->
                    Toast.makeText(this, "Audio selesai diputar", Toast.LENGTH_SHORT).show());

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal memutar audio", Toast.LENGTH_SHORT).show();
        }
    }

    private void playVideo() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }

        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sample_video4);
        videoView.setVideoURI(videoUri);
        videoView.setMediaController(new android.widget.MediaController(this));
        videoView.start();

        videoView.setOnCompletionListener(mp ->
                Toast.makeText(this, "Video selesai diputar", Toast.LENGTH_SHORT).show());
    }
}
