package kush.example.contactsdemo

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kush.example.contactsdemo.api.RetrofitClient
import kush.example.contactsdemo.model.CommonModel
import kush.example.contactsdemo.model.ContactModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MainActivity : AppCompatActivity(), ContactsListAdapter.OnDeleteClick {

    private var contactsAdapter: ContactsListAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getContacts()
        fab.setOnClickListener({
            val newContact = Intent(this, NewContactActivity::class.java)
            startActivityForResult(newContact, 100)
        })

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (contactsAdapter != null) {
                    contactsAdapter!!.filter(s.toString().toLowerCase(Locale.getDefault()))
                }
            }
        })

    }

    private fun getContacts() {
        ProgressUtil.showProgressDialog(this)
        val call =
            RetrofitClient.getClient().getContacts()

        call.enqueue(object : Callback<ContactModel> {
            override fun onResponse(
                call: Call<ContactModel>,
                response: Response<ContactModel>
            ) {
                recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
                if (response.body() != null) {
                    ProgressUtil.dismissDialog()
                    contactsAdapter =
                        ContactsListAdapter(
                            response.body()!!.data,
                            this@MainActivity,
                            this@MainActivity
                        )
                    recyclerView.adapter = contactsAdapter
                }
            }

            override fun onFailure(call: Call<ContactModel>, t: Throwable) {
                ProgressUtil.dismissDialog()
                t.printStackTrace()
                Log.d("Error", t.message.toString())
            }
        })
    }

    private fun deleteContact(id: String) {
        ProgressUtil.showProgressDialog(this)
        val call =
            RetrofitClient.getClient().deleteContact(id)

        call.enqueue(object : Callback<CommonModel> {
            override fun onResponse(
                call: Call<CommonModel>,
                response: Response<CommonModel>
            ) {
                recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
                if (response.code() == 200) {
                    getContacts()
                }
            }

            override fun onFailure(call: Call<CommonModel>, t: Throwable) {
                ProgressUtil.dismissDialog()
                t.printStackTrace()
                Log.d("Error", t.message.toString())
            }
        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100) {
            if (resultCode == Activity.RESULT_OK) {
                getContacts()
            }
        }
    }

    override fun onClick(id: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Contact")
        builder.setMessage("Are you sure?")
        builder.setPositiveButton("Yes") { dialogInterface, which ->
            deleteContact(id)
            dialogInterface.dismiss()
        }
        builder.setNegativeButton("No") { dialogInterface, which ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

}



