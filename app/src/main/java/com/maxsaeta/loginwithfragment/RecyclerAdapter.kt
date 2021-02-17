package com.maxsaeta.mangakakalot

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.maxsaeta.loginwithfragment.BookMarkFragment
import com.maxsaeta.loginwithfragment.R
import com.maxsaeta.loginwithfragment.databinding.BookmarkBinding
import java.io.File

class RecyclerAdapter(
    val Mangas: ArrayList<Manga>,
    val direccion: String,
    val bookMarkFragment: BookMarkFragment
) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {


    lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val bookmark = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.bookmark,
                parent,
                false
            )
        return ViewHolder(bookmark, Mangas,direccion,bookMarkFragment)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val manga = Mangas[position]
        holder.bind(manga)

    }

    override fun getItemCount(): Int = Mangas.size

    class ViewHolder(
        val vista: View,
        val Mangas: ArrayList<Manga>,
        val direccion: String,
        val bookMarkFragment: BookMarkFragment
    ) : RecyclerView.ViewHolder(vista) {

        val binding = BookmarkBinding.bind(vista)
        fun bind(manga: Manga) {
            binding.titulo.text = manga.titulo
            binding.tvviewed.text = manga.viewed.replace("Viewed :","")
            binding.tvcurrent.text = manga.current.replace("Current :","")
            val archivo = File(direccion + File.separator + manga.cover)
            if(archivo.exists()){
                val tempcontent = archivo.absoluteFile.readText()
                Glide.with(binding.cover.context).load(Base64Util.convertStringToBitmap(tempcontent)).into(binding.cover)
            }
            if(manga.pagina != "Bookmark - Manganelo"){
                binding.tvlast.text = ""
                binding.tvcurrent.text = ""
            }
        }

        private val boton = binding.button.setOnClickListener(( { vista : View ->
            val lasturl = Mangas.get(adapterPosition).urlViewed
            if(lasturl == "")
                println( "No hay direccion Valida")
            else {
                bookMarkFragment.paginacargada?.onLastCapClicked(lasturl)
            }
        }))
   }
}

