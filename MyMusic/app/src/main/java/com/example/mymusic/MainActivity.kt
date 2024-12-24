package com.example.mymusic

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.res.AssetFileDescriptor
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mymusic.adapter.MusicAdapter
import com.example.mymusic.data.MusicModel
import com.example.mymusic.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), MusicAdapter.SongClick,MusicService.PlaybackStateListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var musicAdapter: MusicAdapter
    var musicService: MusicService? = null
    private var currentSongIndex = 0
    private var isMusicPlaying = false
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val musicBinder = binder as MusicService.MusicBinder
            musicService = musicBinder.getService()
            musicService?.addPlaybackStateListener(this@MainActivity)
            musicService?.play()
            musicService?.intialiseSeekBar(binding.seekBar)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            musicService?.removePlaybackStateListener(this@MainActivity)
            musicService = null
        }


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val list: List<MusicModel> = getSongList()
        binding.rv.layoutManager = LinearLayoutManager(this)

        musicAdapter = MusicAdapter(this, list, this)
        binding.rv.adapter = musicAdapter
        binding.play.setOnClickListener {
            if (isMusicPlaying) {
                pause()
            } else {
                play()
            }
        }

        binding.prev.setOnClickListener {
            playBack()
        }
        binding.next.setOnClickListener {
            playNext()
        }

    }

    private fun playBack() {
        val list = getSongList()
        currentSongIndex = (currentSongIndex - 1).takeIf { it >= 0 } ?: (list.size - 1)
        onSongClick(list[currentSongIndex], currentSongIndex)
    }

    private fun playNext() {
        val list = getSongList()
        currentSongIndex = (currentSongIndex + 1).takeIf { it < list.size } ?: 0
        onSongClick(list[currentSongIndex], currentSongIndex)
    }

    private fun play() {
        musicService?.play()
        updatePlayPauseButton()
    }

    private fun pause() {
        musicService?.pause()
        updatePlayPauseButton()
    }


    private fun getSongList(): List<MusicModel> {
        return listOf(

            MusicModel(R.raw.one, getSongTitle(R.raw.one), getMp3FileLength(R.raw.one)),
            MusicModel(R.raw.two, getSongTitle(R.raw.two), getMp3FileLength(R.raw.two)),
            MusicModel(R.raw.three, getSongTitle(R.raw.three), getMp3FileLength(R.raw.three)),
            MusicModel(R.raw.four, getSongTitle(R.raw.four), getMp3FileLength(R.raw.four)),
            MusicModel(R.raw.four, getSongTitle(R.raw.four), getMp3FileLength(R.raw.four)),
            MusicModel(R.raw.five, getSongTitle(R.raw.five), getMp3FileLength(R.raw.five)),
            MusicModel(R.raw.six, getSongTitle(R.raw.six), getMp3FileLength(R.raw.six)),
            MusicModel(R.raw.seven, getSongTitle(R.raw.seven), getMp3FileLength(R.raw.seven)),
            MusicModel(R.raw.eight, getSongTitle(R.raw.eight), getMp3FileLength(R.raw.eight)),
            MusicModel(R.raw.nine, getSongTitle(R.raw.nine), getMp3FileLength(R.raw.nine)),
            MusicModel(R.raw.ten, getSongTitle(R.raw.ten), getMp3FileLength(R.raw.ten)),
            MusicModel(R.raw.eleven, getSongTitle(R.raw.eleven), getMp3FileLength(R.raw.eleven)),

        )
    }

    override fun onSongClick(music: MusicModel, position: Int) {
        val musicIntent = Intent(this, MusicService::class.java)
        musicIntent.putExtra(
            "music_file_path",
            "android.resource://" + packageName + "/" + music.resourceID // Removed null check
        )
        startService(musicIntent) // Removed null check
        bindService(musicIntent, connection, Context.BIND_AUTO_CREATE)
    }

    private fun updatePlayPauseButton() {
        if (isMusicPlaying) {
            binding.play.setImageResource(android.R.drawable.ic_media_pause)
        } else {
            binding.play.setImageResource(android.R.drawable.ic_media_play)
        }
        musicAdapter.notifyDataSetChanged()
    }

    private fun getSongTitle(mp3R: Int): String {
        val retriever = MediaMetadataRetriever()
        val filesDes: AssetFileDescriptor = this.resources.openRawResourceFd(mp3R)
        retriever.setDataSource(
            filesDes.fileDescriptor, filesDes.startOffset, filesDes.length
        )
        val title: String? = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
        retriever.release()
        return title ?: ""


    }


    private fun getMp3FileLength(mp3ResourceID: Int): String {
        val retriever = MediaMetadataRetriever()
        val filesDes: AssetFileDescriptor = this.resources.openRawResourceFd(mp3ResourceID)
        retriever.setDataSource(
            filesDes.fileDescriptor, filesDes.startOffset, filesDes.length
        )
        val duration: Long =
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0

        retriever.release()
        val min = (duration / 1000) / 60
        val sec = (duration / 1000) % 60
        return "$min:${String.format("%02d", sec)}"


    }

    override fun onPlaybackStateChanged(isPlaying: Boolean) {
        isMusicPlaying = isPlaying
        updatePlayPauseButton()
    }


}