package com.kiotech.tcc

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.Menu
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.type.DateTime

import com.kiotech.tcc.databinding.ActivityMainBinding
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var imageUriPhoto: Uri? = null
    private var imageUriBill: Uri? = null
    var wie = 0;
    private val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.ivPhoto.setImageDrawable(getDrawable(R.drawable.google))
        binding.ivBill.setImageDrawable(getDrawable(R.drawable.google))


        auth = FirebaseAuth.getInstance()
        val userM = auth.currentUser
        val userEmail: String? = userM?.email

        binding.btnSumit.setOnClickListener() {
            val mrr: Uri = imageUriPhoto!!
            if (imageUriPhoto != null) {
                val storage = Firebase.storage
                val storageRef = storage.reference
                val imagesRef = storageRef.child("images")
                val billRefStore = storageRef.child("bills")
                val photoRef = imagesRef.child("$userEmail/${imageUriPhoto?.lastPathSegment}")
                val uploadTask = photoRef.putFile(imageUriPhoto!!)
                val billRef = billRefStore.child("$userEmail/${imageUriBill?.lastPathSegment}")
                val uploadTaskBill = billRef.putFile(imageUriBill!!)

                val tickets = hashMapOf(
                    "Email" to userEmail,
                    "Date" to Timestamp.now(),
                    "Photo" to photoRef.toString(),
                    "Bill" to billRef.toString(),
                    "Description" to binding.tbDescription.text.toString()
                )
                db.collection("Tickets")
                    .add(tickets)
                    .addOnSuccessListener { documentReference ->
                        Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error adding document", e)
                    }
                binding.tbDescription.text.clear()
                recreate()
            }
        }

        binding.ivPhoto.setOnClickListener(){
            openImagePicker()
            wie = 1;
        }

        binding.ivBill.setOnClickListener(){
            openImagePicker()
            wie = 2;

        }

        binding.btnCB.setOnClickListener(){
            openCamera()
            wie = 3;
        }

        binding.btnCP.setOnClickListener(){
            openCamera()
            wie = 4;
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                PICK_IMAGE_REQUEST -> {
                    if(wie == 1){
                        imageUriPhoto = data.data
                        binding.ivPhoto.setImageURI(imageUriPhoto)
                    }
                    if(wie == 2){
                        imageUriBill = data.data
                        binding.ivBill.setImageURI(imageUriBill)
                    }
                }
                CAMERA_REQUEST -> {
                    if(wie == 3){
                        val photo = data.extras?.get("data") as Bitmap
                        binding.ivBill.setImageBitmap(photo)
                    }
                    if(wie == 4){
                        val photo = data.extras?.get("data") as Bitmap
                        binding.ivPhoto.setImageBitmap(photo)
                    }

                }
            }
        }
    }

    companion object {
        const val PICK_IMAGE_REQUEST = 1
        const val CAMERA_REQUEST = 2
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
}