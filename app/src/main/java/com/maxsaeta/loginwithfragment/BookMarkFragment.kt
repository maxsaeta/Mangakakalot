package com.maxsaeta.loginwithfragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.maxsaeta.loginwithfragment.databinding.ActivityLeemangaBinding
import com.maxsaeta.mangakakalot.Manga
import com.maxsaeta.mangakakalot.RecyclerAdapter
import org.jsoup.Jsoup
import java.io.File


private const val DIRECCION = "param1"
private const val ARCHIVO = "param2"


class BookMarkFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var direccion2: String
    private var direccion: String? = null
    private var archivo: String? = null
    private lateinit var _bindingManga : ActivityLeemangaBinding
    private val binding get() =  _bindingManga
    private var listademangas : ArrayList<Manga> = arrayListOf<Manga>()
    lateinit var paginacargada: OnFragmentActionListener


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentActionListener) {
            paginacargada = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            direccion = it.getString(DIRECCION)
            archivo = it.getString(ARCHIVO)
        }
        direccion2 = direccion.toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _bindingManga = ActivityLeemangaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        leerPaginaPrincipal()
        val linearLayoutManager = LinearLayoutManager(requireContext())
        val recyclerViewAdapter = RecyclerAdapter(listademangas, direccion.toString(),this )
        binding.recycler.layoutManager = linearLayoutManager
        binding.recycler.adapter = recyclerViewAdapter
        binding.recycler.layoutManager = linearLayoutManager
    }
    private fun leerPaginaPrincipal() {
        val paginaprincipal = "https://manganelo.com/bookmark"

        var imagentest: String

        try {
            val archivo = File(direccion.toString() + File.separator + "bookmark.html")
            val pagina = Jsoup.parseBodyFragment(archivo.readText(), paginaprincipal)
            val titulopagina = pagina.title()
            val mangas = pagina.getElementsByClass("bookmark-item")

            for (manga in mangas) {
                val titulo = manga.getElementsByClass("item-story-name text-nowrap color-red")[0].text().toString().replace("=","")
                val imagen = manga.select("img[src]")[0].absUrl("src").toString().replace("=","")
                imagentest = imagen.substring(imagen.lastIndexOf("/")+1,imagen.lastIndexOf(".")).replace("=","") + ".txt"
                var viewedURL = ""
                var viewed = ""
                var current = ""
                val vistas = manga.getElementsByClass("a-h")
                if (vistas.size > 0) {
                    if (manga.getElementsByClass("a-h")[0].getElementsByTag("a").size > 0) {
                        viewedURL =
                            manga.getElementsByClass("a-h")[0].getElementsByTag("a")[0].absUrl(
                                "href").replace("https://manganelo.com/htt=ps","https")
                        viewedURL=viewedURL.replace("=","")
                    }
                    viewed = manga.getElementsByClass("a-h")[0].text().replace("=","")

                    if (vistas.size > 1)
                        current = manga.getElementsByClass("a-h")[1].text().replace("=","")
                }

                listademangas.add(
                    Manga(
                        titulo,
                        imagentest,
                        viewed,
                        current,
                        viewedURL,
                        titulopagina
                    )
                )
            }
        } catch (e: Exception) {
            Log.e("error LeerPagina: ", e.toString())
        }
    }
    companion object {

        @JvmStatic
        fun newInstance(direccion: String, archivo: String) =
            BookMarkFragment().apply {
                arguments = Bundle().apply {
                    putString(DIRECCION, direccion)
                    putString(ARCHIVO, archivo)
                }
            }
    }
}