package com.complete.convo.activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.complete.convo.R
import com.complete.convo.databinding.ActivityEditProfileBinding
import com.complete.convo.databinding.ActivityProfileBinding
import com.complete.convo.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class EditProfileActivity : AppCompatActivity() {
    private lateinit var imageUri: Uri
    private var _binding : ActivityEditProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var mAuth : FirebaseAuth
    private lateinit var dbReference : DatabaseReference

    var name :String? = null
    var emailorphone :String? = null
    private lateinit var storage : FirebaseStorage

    private val pickImage: Int = 100


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val actionBar = supportActionBar
        // showing the back button in action bar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
        actionBar?.setHomeAsUpIndicator(R.drawable.back_24px)

        mAuth = FirebaseAuth.getInstance()

        storage = FirebaseStorage.getInstance()
        val storagRef = storage.reference.child(mAuth.currentUser!!.uid).child(mAuth.currentUser!!.uid+"profile")
        val localFile = File.createTempFile("temp","jpg")
        storagRef.getFile(localFile).addOnSuccessListener {
            val bitMap = BitmapFactory.decodeFile(localFile.absolutePath)
            binding.imageprofile.setImageBitmap(bitMap)
        }
        name = intent.getStringExtra("name")
        emailorphone = intent.getStringExtra("emailorphone")
        binding.nameprofile.text = name
        binding.detailprofile.text = emailorphone
        val uid = intent.getStringExtra("uid")

        /*binding.nameprofile.setOnClickListener {
            binding.nameprofile.visibility = View.GONE
        }
        binding.editnameProfile.setEndIconOnClickListener {
            binding.editnameProfile.editText.toString()
            if(!TextUtils.isEmpty(binding.editnameProfile.editText.toString())){
                dbReference.child("user").addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for(snap in snapshot.children){
                            val currentUser = snap.getValue(User::class.java)
                            if(currentUser!!.uid == uid){
                                if(currentUser.email != null){
                                    dbReference.child("user").setValue(User(currentUser.name,currentUser.email,uid))
                                }else{
                                    dbReference.child("user").setValue(User(currentUser.name,currentUser.phoneNumber,uid,currentUser.phoneNumber))
                                }
                                dbReference.child("user").child(currentUser.uid.toString()).removeValue()

                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
            }else{
                Toast.makeText(this,"enter Valid Name", Toast.LENGTH_SHORT).show()
            }

        }*/


        binding.fabprofile.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK)
            galleryIntent.type = "image/*, video/*"
            if (galleryIntent.resolveActivity(packageManager) != null) {
                startActivityForResult(
                    Intent.createChooser(galleryIntent, "Select File"),pickImage
                )
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            if(data != null){
                imageUri = data.data!!
                storage = FirebaseStorage.getInstance()
                val storageRefer = storage.reference
                storageRefer.child(mAuth.currentUser!!.uid).child(mAuth.currentUser!!.uid+"profile")
                    .putFile(data.data!!).addOnCompleteListener {
                        Toast.makeText(this,"saved", Toast.LENGTH_LONG).show()
                    }
                binding.imageprofile.setImageURI(imageUri)
            }else{
                Toast.makeText(this,"Empty Data", Toast.LENGTH_SHORT).show()
            }

        }
    }

}