package com.example.mymusic.adapter

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.mymusic.R
import com.example.mymusic.data.MusicModel

class MusicAdapter(
    private val context: Context,
    private val musicList: List<MusicModel>,
    private val listener: SongClick
) : RecyclerView.Adapter<MusicAdapter.MusicViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent, false)
        return MusicViewHolder(view)
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        val currentMusic = musicList[position]

        getMp3Thumbnail(currentMusic.resourceID)?.let {
            holder.bind(
                currentMusic,
                listener,
                position,
                it
            )
        }



    }

    inner class MusicViewHolder(itemView: View) : ViewHolder(itemView) {
        private val songTitle: TextView = itemView.findViewById(R.id.textView)
        private val songImage: ImageView = itemView.findViewById(R.id.imageView)

        fun bind(mp3: MusicModel, listener: SongClick, position: Int, bm: Bitmap?) {
            songTitle.text = mp3.title
            if (bm != null) {
                songImage.setImageBitmap(bm)
            } else {
                songImage.setImageResource(R.drawable.icon_music)
            }
            songTitle.setOnClickListener {
                listener.onSongClick(mp3, position)
            }
        }
    }


    interface SongClick {
        fun onSongClick(music: MusicModel, position: Int)
    }

    private fun getMp3Thumbnail(mp3ResourceID: Int): Bitmap? {
        val retriever = MediaMetadataRetriever()
        val filesDes: AssetFileDescriptor = context.resources.openRawResourceFd(mp3ResourceID)
        retriever.setDataSource(
            filesDes.fileDescriptor, filesDes.startOffset, filesDes.length
        )
        val songPic: ByteArray? = retriever.embeddedPicture
        if (songPic != null) {
            return BitmapFactory.decodeByteArray(songPic, 0, songPic.size)
        }
        retriever.release()
        return null


    }



}

