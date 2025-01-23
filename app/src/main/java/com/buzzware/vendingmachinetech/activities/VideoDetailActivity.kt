package com.buzzware.vendingmachinetech.activities

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.buzzware.vendingmachinetech.R
import com.buzzware.vendingmachinetech.databinding.ActivityVideoDetailBinding
import com.buzzware.vendingmachinetech.model.Videos
import com.buzzware.vendingmachinetech.utils.UserSession
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class VideoDetailActivity : BaseActivity() {

    private val binding : ActivityVideoDetailBinding by lazy {
        ActivityVideoDetailBinding.inflate(layoutInflater)
    }


    private lateinit var player: ExoPlayer
    private lateinit var videoLink : Videos

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setStatusBarColor(R.color.dark_bg)

        videoLink = intent.getParcelableExtra("videoLink")!!

        player = ExoPlayer.Builder(this).build()
        val uri: Uri = Uri.parse(videoLink.videoLink)

        binding.apply {
            exoPlayerView.player = player

            val mediaItem = MediaItem.fromUri(uri)
            player.apply {
                setMediaItem(mediaItem)
                prepare()
                play()
            }
            player.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    if (isPlaying) {
                        mDialog.hide()
                    }
                }

                override fun onPlaybackStateChanged(state: Int) {
                }

                override fun onPlayerError(error: PlaybackException) {
                    Toast.makeText(this@VideoDetailActivity, error.message, Toast.LENGTH_SHORT)
                        .show()
                }
            })

        }

        setListener()

    }

    private fun setListener() {
        binding.apply {
            titleTv.text = videoLink.title
            descriptionTv.text = videoLink.description
            durationTv.text = convertMillisecondsToTimeFormat(videoLink.duration)

            videoLink.favorites?.forEach {
                if (it.equals(Firebase.auth.currentUser!!.uid)) {
                    heartIV.setImageResource(R.drawable.ic_heart_fill)
                } else {
                    heartIV.setImageResource(R.drawable.ic_heart)
                }
            }

            heartIV.setOnClickListener {
                videoLink.apply {
                    if(favorites!!.isNotEmpty()){
                        favorites?.forEach {
                            if (it.equals(Firebase.auth.currentUser!!.uid)) {
                                db.collection("Videos").document(postId)
                                    .update(
                                        "favorites",
                                        FieldValue.arrayRemove(Firebase.auth.currentUser!!.uid)
                                    )
                                db.collection("Users").document(Firebase.auth.currentUser!!.uid)
                                    .update(
                                        "favorites",
                                        FieldValue.arrayRemove(postId)
                                    )
                                UserSession.user.favorites.remove(postId)
                                heartIV.setImageResource(R.drawable.ic_heart_post)
                            } else {
                                db.collection("Videos").document(postId)
                                    .update(
                                        "favorites",
                                        FieldValue.arrayUnion(Firebase.auth.currentUser!!.uid)
                                    )
                                db.collection("Users").document(Firebase.auth.currentUser!!.uid)
                                    .update(
                                        "favorites",
                                        FieldValue.arrayUnion(postId)
                                    )
                                UserSession.user.favorites.add(postId)
                                heartIV.setImageResource(R.drawable.ic_heart_post_fill)
                            }
                        }
                    }else{
                        db.collection("Videos").document(postId)
                            .update(
                                "favorites",
                                FieldValue.arrayUnion(Firebase.auth.currentUser!!.uid)
                            )
                        db.collection("Users").document(Firebase.auth.currentUser!!.uid)
                            .update(
                                "favorites",
                                FieldValue.arrayUnion(postId)
                            )
                        UserSession.user.favorites.add(postId)
                        heartIV.setImageResource(R.drawable.ic_heart_post_fill)
                    }
                }
            }
        }

        binding.backIV.setOnClickListener { onBackPressed() }
    }

    private fun convertMillisecondsToTimeFormat(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val remainingSeconds = seconds % 60

        return if (hours > 0) {
            String.format("%02d:%02d", hours, minutes)
        } else if (minutes > 0) {
            String.format("%02d:%02d", minutes, remainingSeconds)
        } else {
            String.format("00:%02d", remainingSeconds)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in, androidx.appcompat.R.anim.abc_fade_out)
    }

    override fun onDestroy() {
        super.onDestroy()
        player.stop()
    }
}