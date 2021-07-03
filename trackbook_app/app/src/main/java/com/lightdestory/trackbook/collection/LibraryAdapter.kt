package com.lightdestory.trackbook.collection


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import com.lightdestory.trackbook.R

class LibraryAdapter private constructor() : RecyclerView.Adapter<LibraryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ShapeableImageView = view.findViewById(R.id.bookshelfListItemImage)
        val title: MaterialTextView = view.findViewById(R.id.bookshelfListItemTitle)
        val isbn: MaterialTextView = view.findViewById(R.id.bookshelfListItemSubtitle)
        val date: MaterialTextView = view.findViewById(R.id.bookshelfListItemDate)
        val count: MaterialTextView = view.findViewById(R.id.bookshelfListItemCount)
        init {
            view.setOnClickListener {

            }
            view.setOnLongClickListener {
                MaterialAlertDialogBuilder(it.context)
                    .setIcon(R.drawable.icon_warning)
                    .setTitle(R.string.bookshelf_DeleteBookTitle)
                    .setMessage(it.context.applicationContext.getString(R.string.bookshelf_DeleteBookDesc).replace("$1", title.text.toString()).replace("$2", isbn.text.toString()))
                    .setCancelable(false)
                    .setNegativeButton(R.string.dialog_No) { dialog, _ -> dialog.dismiss()}
                    .setPositiveButton(R.string.dialog_Yes) {dialog, _ ->
                        val position: Int = adapterPosition
                        if(position != RecyclerView.NO_POSITION){
                            Library.instance.deleteBook(it.context.applicationContext, adapterPosition)
                            instance.notifyItemRemoved(position)
                        }
                        dialog.dismiss()
                    }.show()
                return@setOnLongClickListener true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.component_list_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return Library.instance.books.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = Library.instance.books[position]
        holder.title.text = currentItem.title
        holder.isbn.text = currentItem.isbn
        holder.date.text = currentItem.start_read
        holder.count.text = currentItem.page_read.toString()
        val color : Int = holder.itemView.context.applicationContext.resources.getIntArray(R.array.book_colors)[currentItem.color.toInt()]
        holder.image.drawable.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(color, BlendModeCompat.SRC_ATOP)
    }

    companion object {
        val instance: LibraryAdapter by lazy { LibraryAdapter() }
    }
}