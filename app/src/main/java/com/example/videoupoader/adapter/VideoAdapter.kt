package com.example.videoupoader.adapter

import android.content.Context
import android.net.Uri
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.videoupoader.databinding.ItemPostBinding
import com.example.videoupoader.models.videourl

class VideoAdapter(val context: Context,val videos:List<videourl>) :RecyclerView.Adapter<VideoAdapter.viewHolder>(){

    class viewHolder(itemVideo:ItemPostBinding):RecyclerView.ViewHolder(itemVideo.root){
        val videoView = itemVideo.rvVideoView
        val tag = itemVideo.tvTag
        val description = itemVideo.tvDescription
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        return viewHolder(ItemPostBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val currentPost = videos[position]
        holder.tag.text = currentPost.tag
        holder.description.text = currentPost.description
        holder.videoView.setVideoURI(Uri.parse(currentPost.url))
        holder.videoView.start()
    }

    override fun getItemCount(): Int {
        return videos.size
    }

}

//class Adapters(val context: Context, val posts:List<Posts>): RecyclerView.Adapter<Adapters.viewHolder>() {
//
//    class viewHolder(itemView:ItemPostBinding): RecyclerView.ViewHolder(itemView.root) {
//        val name = itemView.tvUserName
//        val image = itemView.image
//        val time = itemView.tvTimestamp
//        val description = itemView.tvDescription
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
//        return viewHolder(ItemPostBinding.inflate(LayoutInflater.from(parent.context),parent,false))
//    }
//
//    override fun onBindViewHolder(holder: viewHolder, position: Int) {
//        val currentPost = posts[position]
//        holder.name.text = currentPost.user!!.userName
//        holder.description.text = currentPost.description
//        holder.time.text = DateUtils.getRelativeTimeSpanString(currentPost.timeMs)
//        Glide.with(context).load(currentPost.imageUrl).into(holder.image)
//    }
//
//    override fun getItemCount(): Int {
//        return posts.size
//    }
//}