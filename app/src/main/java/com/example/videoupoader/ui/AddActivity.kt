package com.example.videoupoader.ui

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.videoupoader.databinding.ActivityAddBinding
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import android.webkit.MimeTypeMap

import android.content.ContentResolver
import com.example.videoupoader.models.videourl
import com.example.videoupoader.util.Constant
import com.google.firebase.firestore.FirebaseFirestore

const val REQUEST_VIDEO_CAPTURE = 1
class AddActivity : AppCompatActivity() {
    private val PICK_VIDEO_CODE = 1
    private var videoUri: Uri?=null
    private lateinit var firebaseStorage : StorageReference
    private lateinit var firebaseDb : FirebaseFirestore
   // private var url : Uri?= null


    private lateinit var binding : ActivityAddBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseDb = FirebaseFirestore.getInstance()

        firebaseStorage = FirebaseStorage.getInstance().reference

        binding.tvAdd.setOnClickListener {
            prompt()
        }

        binding.submitBtn.setOnClickListener{
            if(binding.etTag.text.toString().isBlank() || binding.etDescription.toString().isBlank()){
                Toast.makeText(this, "Fields can not be empty!!", Toast.LENGTH_SHORT).show()
            }
            updateVideoToDatabase()
        }
    }

    private fun prompt(){

        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Select One")

        builder.setPositiveButton("Camera"){ dialogInterface,which->
            dispatchTakeVideoIntent()
            dialogInterface.dismiss()
        }

        builder.setNeutralButton("Storage"){dialogInterface, which->
            addVideo()
            dialogInterface.dismiss()
        }

        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun updateVideoToDatabase(){

        if(videoUri == null){
            Toast.makeText(this, "Please Select Video First", Toast.LENGTH_SHORT).show()
            return
        }

        val tag = binding.etTag.text.toString()
        val description = binding.etDescription.text.toString()

        binding.submitBtn.isEnabled = false

        val videoReference = firebaseStorage.child("videos/${System.currentTimeMillis()}-video." + getfiletype(videoUri!!) )
        videoReference.putFile(videoUri!!).continueWithTask { phototask->
            videoReference.downloadUrl
        }.continueWithTask { downloadUrlTask->

          //  val post = Posts(description,downloadUrlTask.result.toString(),System.currentTimeMillis(),signedInUser)

            val url = videourl(tag,downloadUrlTask.result.toString(),description)


            firebaseDb.collection("posts").add(url)

        }.addOnCompleteListener { task->


            if(!task.isSuccessful){
                Log.e("addactivity", "Exception during Firebase operations", task.exception)
                Toast.makeText(this, "Failed to save video", Toast.LENGTH_SHORT).show()
                binding.submitBtn.isEnabled = true
            }
            else{
               // url = task.result
               // Constant.videoList.add(url!!)
                Toast.makeText(this, "File Uploaded", Toast.LENGTH_SHORT).show()
                binding.submitBtn.isEnabled = true
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
                finishAffinity()
            }
        }
    }

    private fun dispatchTakeVideoIntent() {
        Intent(MediaStore.ACTION_VIDEO_CAPTURE).also { takeVideoIntent ->
            takeVideoIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE)
            }
        }
    }

    private fun getfiletype(videouri: Uri): String? {
        val r = contentResolver
        // get the file type ,in this case its mp4
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(r.getType(videouri))
    }

    private fun addVideo(){


        val imagePickerIntent = Intent(Intent.ACTION_GET_CONTENT)
        imagePickerIntent.type = "video/*"

        if(imagePickerIntent.resolveActivity(packageManager) !=null) {

            startActivityForResult(imagePickerIntent, PICK_VIDEO_CODE)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_VIDEO_CODE){
            if(resultCode == Activity.RESULT_OK) {
                videoUri = data?.data
                binding.addVideoView.setVideoURI(videoUri)
                binding.addVideoView.start()
            }else{
                Toast.makeText(this, "Action failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}