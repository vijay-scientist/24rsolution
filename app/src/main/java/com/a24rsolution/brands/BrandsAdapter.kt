package com.a24rsolution.brands

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.recyclerview.widget.RecyclerView
import com.a24rsolution.R
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth


class BrandsAdapter(private val brandsList: ArrayList<Brand>): RecyclerView.Adapter<BrandsAdapter.BrandsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrandsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.brandview, parent, false)
        return BrandsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BrandsViewHolder, position: Int) {
        val currentItem = brandsList[position]
        Glide.with(holder.context).load(currentItem.logo).into(holder.logoView)
        holder.titleView.text = currentItem.title
        currentItem.description?.let { holder.descriptionView.loadUrl(it) }
        holder.shareButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.putExtra(Intent.EXTRA_TEXT, currentItem.link + FirebaseAuth.getInstance().currentUser?.uid)
            shareIntent.type = "text/plain"
            holder.context.startActivity(Intent.createChooser(shareIntent, "Send To"))
        }

        holder.visitButton.setOnClickListener {
            val browserIntent = Intent(ACTION_VIEW, Uri.parse(currentItem.link + FirebaseAuth.getInstance().currentUser?.uid))
            holder.context.startActivity(browserIntent)
        }
    }

    override fun getItemCount(): Int = brandsList.size

    class BrandsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val titleView: MaterialTextView =  itemView.findViewById(R.id.brandTitle)
        val descriptionView: WebView =  itemView.findViewById(R.id.brandDescription)
        val logoView: ShapeableImageView =  itemView.findViewById(R.id.brandLogo)
        val visitButton: ShapeableImageView =  itemView.findViewById(R.id.visitButton)
        val shareButton: ShapeableImageView =  itemView.findViewById(R.id.shareButton)
        val context: Context = itemView.context
    }
}