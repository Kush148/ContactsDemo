package kush.example.contactsdemo

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_new_contact.*
import kush.example.contactsdemo.api.RetrofitClient
import kush.example.contactsdemo.model.CommonModel
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class NewContactActivity : AppCompatActivity() {
    private val galleryRequestCode = 1
    private var mUri: Uri? = null
    private var contactId: String? = null
    private var img: String? = null
    private var fName: String? = null
    private var lName: String? = null
    private var phoneNo: String? = null
    private var email: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_contact)

        civ_profile.setOnClickListener {
            Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse) {
                        openGallery()
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse) {
                        Toast.makeText(
                            this@NewContactActivity,
                            "Can't Open Gallery",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permission: PermissionRequest?,
                        token: PermissionToken?
                    ) {
                        token?.continuePermissionRequest()
                    }
                }).check()
        }

        contactId = intent.getStringExtra("id")
        img = intent.getStringExtra("profile")
        fName = intent.getStringExtra("fname")
        lName = intent.getStringExtra("lname")
        phoneNo = intent.getStringExtra("phoneNo")
        email = intent.getStringExtra("email")

        if (contactId != null) {
            Picasso.get().load("http://192.168.2.24/address_book/$img").into(civ_profile)
            et_fname.setText(fName)
            et_lname.setText(lName)
            et_phone.setText(phoneNo)
            et_email.setText(email)
        }

        btnSave.setOnClickListener {
            if (isValidEmail() && isValidPhoneNumber()) this.updateContact()
        }

        btnCancel.setOnClickListener {
            finish()
        }

    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, galleryRequestCode)
        } else {
            Toast.makeText(this, "Can't Open Gallery", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == galleryRequestCode) {
            if (resultCode == Activity.RESULT_OK) {
                mUri = data?.data
                val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(mUri!!))
                civ_profile!!.setImageBitmap(bitmap)
                mUri = Utils.getImageUri(this, bitmap)
            }
        }
    }

    private fun updateContact() {
        ProgressUtil.showProgressDialog(this)

        var requestBodyImage: RequestBody? = null
        if (mUri != null) {
            val file = File(Utils.getRealPathFromURI(this, mUri))
            requestBodyImage = RequestBody.create(MediaType.parse("image/*"), file)
        }

        val requestBodyFirstName =
            RequestBody.create(MediaType.parse("text/plain"), et_fname.text.toString().trim())
        val requestBodyLastName =
            RequestBody.create(MediaType.parse("text/plain"), et_lname.text.toString().trim())
        val requestBodyEmail =
            RequestBody.create(MediaType.parse("text/plain"), et_email.text.toString().trim())
        val requestBodyPhoneNo =
            RequestBody.create(MediaType.parse("text/plain"), et_phone.text.toString().trim())

        val call =
            if (contactId != null) {
                val requestBodyId = RequestBody.create(MediaType.parse("text/plain"), contactId!!)

                RetrofitClient.getClient()
                    .updateContact(
                        requestBodyId,
                        requestBodyImage,
                        requestBodyFirstName,
                        requestBodyLastName,
                        requestBodyPhoneNo,
                        requestBodyEmail,
                    )
            } else {
                RetrofitClient.getClient()
                    .addContact(
                        requestBodyImage,
                        requestBodyFirstName,
                        requestBodyLastName,
                        requestBodyPhoneNo,
                        requestBodyEmail,
                    )
            }
        call.enqueue(object : Callback<CommonModel> {
            override fun onResponse(
                call: Call<CommonModel>,
                response: Response<CommonModel>
            ) {
                ProgressUtil.dismissDialog()
                if (response.code() == 200) {
                    Toast.makeText(applicationContext, "Updated", Toast.LENGTH_LONG).show()
                    setResult(Activity.RESULT_OK)
                    finish()
                } else {
                    Toast.makeText(applicationContext, "Something went wrong.", Toast.LENGTH_LONG)
                        .show()
                }
            }

            override fun onFailure(call: Call<CommonModel>, t: Throwable) {
                ProgressUtil.dismissDialog()
                t.printStackTrace()
                Log.d("Error", t.message.toString())
                Toast.makeText(applicationContext, "Something went wrong.", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }


    private fun isValidEmail(): Boolean {
        email = et_email.text.toString().trim { it <= ' ' }
        return if (email!!.isEmpty()) {
            et_email.requestFocus()
            et_email.error = "Email can't be empty"
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.toString()).matches()) {
            et_email.requestFocus()
            et_email.error = "Please enter a valid email address"
            false
        } else {
            et_email.error = null
            true
        }
    }

    private fun isValidPhoneNumber(): Boolean {
        phoneNo = et_phone.text.toString().trim { it <= ' ' }
        return if (phoneNo!!.isEmpty()) {
            et_phone.requestFocus()
            et_phone.error = "Email can't be empty"
            false
        } else if (!Patterns.PHONE.matcher(phoneNo.toString()).matches()) {
            et_phone.requestFocus()
            et_phone.error = "Please enter a valid email address"
            false
        } else {
            et_phone.error = null
            true
        }
    }

}
