package kush.example.contactsdemo

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Patterns
import java.io.ByteArrayOutputStream

object Utils {


    fun isValidEmail(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target!!).matches()
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path: String =
            MediaStore.Images.Media.insertImage(
                inContext.contentResolver,
                inImage,
                "IMG_" + System.currentTimeMillis(),
                null
            )
        return Uri.parse(path)
    }

    fun getRealPathFromURI(context: Context, uri: Uri?): String {
        var path = ""
        if (context.contentResolver != null) {
            val cursor: Cursor? = context.contentResolver.query(uri!!, null, null, null, null)
            if (cursor != null) {
                cursor.moveToFirst()
                val idx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                path = cursor.getString(idx)
                cursor.close()
            }
        }
        return path
    }
}