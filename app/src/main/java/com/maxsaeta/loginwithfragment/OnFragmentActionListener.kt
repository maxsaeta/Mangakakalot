package com.maxsaeta.loginwithfragment

import android.util.Log
import android.view.View
import java.io.*

interface OnFragmentActionListener {
    fun onWebSaved()
    fun onFileEmpty()
    fun onWebNotAvailable()
    fun onUpdateProgress(newProgress: Int){}
    fun onLastCapClicked(lasturl: String){}

    suspend fun htmlfromwebviewsaved(direccion : String, infile: String, outfile: String): Boolean {
        var iniciado = false
        var terminado = false
        val archivo = File(direccion + File.separator + infile)
        lateinit var fin: BufferedReader
        lateinit var temp: BufferedWriter


        if (!archivo.exists() || !archivo.canRead()) {
            println("El archivo de entrada no se puede leer")
            return false
        }
        else {
            val contenido = archivo.readText()

            val borrar = File(direccion + File.separator + outfile)
            if (borrar.exists()) {
                borrar.delete()
            }
            try {
                fin = BufferedReader(InputStreamReader(FileInputStream(archivo.absoluteFile)))
                temp =
                    BufferedWriter(OutputStreamWriter(FileOutputStream(direccion + File.separator + outfile)))

                fin.forEachLine {
                    var m = it.lastIndexOf("<!DOCTYPE html>")
                    if (m != -1) {
                        iniciado = true
                    }

                    if (iniciado && !terminado) {
                        var linea = it

                        m = linea.lastIndexOf("3D")
                        if (m != -1) {
                            //Log.e("3D", "localidado ")
                            linea = it.replace("3D", "")
                        }
                        temp.append(linea)
                    }
                    m = it.lastIndexOf("</html>")
                    if (m != -1) {
                        terminado = true
                    }
                }
                fin.close()
                temp.close()
            } catch (e: Exception) {
                Log.e("error: ", e.toString())
            }

            Log.d("Pagina Recuperada", "Recuperacion de datos guardados del WebView")
            imagefromwebviewdsaved(direccion, infile)
            return true
        }
        return false
    }

    suspend fun imagefromwebviewdsaved( direccion: String,infile: String) {
        try {
            val archivo = File(direccion + File.separator + infile)
            var temp =
                BufferedWriter(OutputStreamWriter(FileOutputStream(direccion + File.separator + "test.txt")))
            val separador = "MultipartBoundary"
            val contenttype = "Content-Type: image/"
            val contentname = "Content-Location: "
            var type = "png"
            var location = "midireccion"
            var data = "dataimage"
            while (!archivo.exists() || !archivo.canRead()) {
                Log.e("No leible", "Archivo no se puede leer")
            }
            val contenido = archivo.readText()
            var bloques = contenido.split(separador)

            for (bloque in bloques) {
                val presente = bloque.lastIndexOf(contenttype)
                var conteo = 0
                if (presente != -1) {
                    val lineas = bloque.split("\n")
                    location = lineas[3].replace(contentname, "")
                    conteo = location.lastIndexOf("/")
                    val conteo2 = location.lastIndexOf(".")
                    location = location.substring(conteo + 1, conteo2)+ ".txt"
                    type = lineas[1].replace(contenttype, "")
                    if (!File(direccion + File.separator + location).exists()) {
                        data = ""
                        for (linea in lineas) {
                            data += linea
                        }
                        for (i in 0..4) {
                            data = data.replace(lineas[i], "")
                        }
                        data = data.replace("------", "")

                        conteo = location.lastIndexOf("/")
                        val conteo2 = location.lastIndexOf(".")
                        location = location.substring(conteo + 1, conteo2)+ ".txt"
                        temp =
                            BufferedWriter(OutputStreamWriter(FileOutputStream(direccion + File.separator + location)))
                        temp.append(data)
                        temp.close()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("error 2: ", e.toString())
        }
    }

}