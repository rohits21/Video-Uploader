package com.example.videoupoader.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.videoupoader.R
import com.example.videoupoader.adapter.VideoAdapter
import com.example.videoupoader.databinding.ActivityMainBinding
import com.example.videoupoader.models.videourl
import com.example.videoupoader.util.Constant
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseDb : FirebaseFirestore
    private lateinit var videoList :MutableList<videourl>
    private lateinit var videoAdapter: VideoAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseDb = FirebaseFirestore.getInstance()
        videoList = mutableListOf()
        videoAdapter = VideoAdapter(this,videoList)

        var videoRef = firebaseDb.collection("posts")
        videoRef.addSnapshotListener { value, error ->
            if(error != null || value == null){
                Log.e("snapshot",error.toString())
                return@addSnapshotListener
            }

            val video = value.toObjects(videourl::class.java)
            videoList.clear()
            videoList.addAll(video)
            videoAdapter.notifyDataSetChanged()

        }



//        val videoAdapter = VideoAdapter(this,Constant.videoList)
//        videoAdapter.notifyDataSetChanged()
        binding.addBtn.setOnClickListener {
            val intent = Intent(this, AddActivity::class.java)
            startActivity(intent)

        }

        binding.rvPost.adapter = videoAdapter
        binding.rvPost.layoutManager = LinearLayoutManager(this)


    }
}