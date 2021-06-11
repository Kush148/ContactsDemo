package kush.example.contactsdemo

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.list_contacts.view.*
import kush.example.contactsdemo.model.Contact

class ContactsListAdapter(
    private var contactList: List<Contact>,
    val context: Activity,
    val click: OnDeleteClick
) :
    RecyclerView.Adapter<ContactsListAdapter.ContactsListViewHolder>() {


    private val filteredList = ArrayList<Contact>()

    init {
        filteredList.addAll(contactList)
    }

    class ContactsListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profile: CircleImageView = itemView.civ_profile
        val firstName = itemView.tv_fname
        val lastName = itemView.tv_lname
        val phoneNo = itemView.tv_phone
        val email = itemView.tv_email
        val ivDelete = itemView.ivDelete
        val ivShare = itemView.ivShare
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsListViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_contacts, parent, false)
        return ContactsListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ContactsListViewHolder, position: Int) {
        val currentItem = filteredList[position]

        Picasso.get().load("http://192.168.2.24/address_book/${currentItem.image}")
            .into(holder.profile)
        holder.firstName.text = currentItem.first_name
        holder.lastName.text = currentItem.last_name
        holder.phoneNo.text = currentItem.phone_no
        holder.email.text = currentItem.email

        holder.itemView.setOnClickListener(View.OnClickListener {
            val newContactActivity = Intent(context, NewContactActivity::class.java)
            newContactActivity.putExtra("id", currentItem.id)
            newContactActivity.putExtra("profile", currentItem.image)
            newContactActivity.putExtra("fname", currentItem.first_name)
            newContactActivity.putExtra("lname", currentItem.last_name)
            newContactActivity.putExtra("phoneNo", currentItem.phone_no)
            newContactActivity.putExtra("email", currentItem.email)

            context.startActivityForResult(newContactActivity, 100)
        })

        holder.ivDelete.setOnClickListener {
            click.onClick(currentItem.id)
        }
        holder.ivShare.setOnClickListener({
            val shareIntent = Intent(Intent.ACTION_SEND)
            var shareData =
                "FirstName: ${currentItem.first_name} \nLastName: ${currentItem.last_name} \nPhoneNo: ${currentItem.phone_no} \nEmail: ${currentItem.email}"

            shareIntent.putExtra(
                Intent.EXTRA_TEXT,
                shareData
            )

            context.startActivity(Intent.createChooser(shareIntent, "Share with"))
        })
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    interface OnDeleteClick {
        public fun onClick(id: String)
    }

    fun filter(text: String) {
        filteredList.clear()
        if (text.isEmpty()) {
            filteredList.addAll(contactList)
        } else {
            for (item in contactList) {

                if (item.first_name.contains(text)
                    || item.last_name.contains(text)
                    || item.phone_no.contains(text) || item.email.contains(text)
                ) {
                    filteredList.add(item)
                }
            }
        }
        notifyDataSetChanged()
    }
}