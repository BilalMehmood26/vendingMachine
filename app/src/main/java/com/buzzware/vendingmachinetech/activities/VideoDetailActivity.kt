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

class VideoDetailActivity : BaseActivity() {

    private val binding : ActivityVideoDetailBinding by lazy {
        ActivityVideoDetailBinding.inflate(layoutInflater)
    }


    private lateinit var player: ExoPlayer
    private lateinit var videoLink : Videos
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setStatusBarColor(R.color.dark_bg)

        videoLink = intent.getParcelableExtra<Videos>("videoLink")!!

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
            durationTv.text = videoLink.duration.toString()
        }

        binding.backIV.setOnClickListener { onBackPressed() }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in, androidx.appcompat.R.anim.abc_fade_out)
    }
}