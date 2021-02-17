package com.maxsaeta.loginwithfragment

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.NetworkOnMainThreadException
import android.util.Log
import android.view.KeyEvent
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.maxsaeta.loginwithfragment.databinding.ActivityMainBinding
import com.maxsaeta.loginwithfragment.databinding.NointernetBinding
import com.maxsaeta.loginwithfragment.databinding.FragmentWebviewloginBinding

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch



class MainActivity : AppCompatActivity(), OnFragmentActionListener {

    private lateinit var webView: Fragment
    private val fm = supportFragmentManager
    private lateinit var visibleWebView: Fragment
    private lateinit var BookMark: Fragment
    private lateinit var direccion : String
    private lateinit var binding: ActivityMainBinding
    private lateinit var bindingDialog : NointernetBinding
    private lateinit var bindingWebView : FragmentWebviewloginBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val myWebView = Dialog(this)
        bindingWebView = FragmentWebviewloginBinding.inflate(layoutInflater)
        myWebView.setContentView(bindingWebView.root)

        direccion = filesDir.absolutePath
        webView = webviewlogin.newInstance("https://manganelo.com/bookmark", direccion)
        BookMark = BookMarkFragment.newInstance(direccion, "bookmark.txt")
        visibleWebView = webView
        //setupPermissions(this)
        if(checkConnection()){
            bindingWebView.fragwebView.isVisible = false
            binding.mainprogressBar.visibility = ProgressBar.VISIBLE
            loadFragment(webView)
        }
    }

    override fun onWebSaved() {
        showToast("La Pagina ha sido guardada")
        GlobalScope.launch {
            delay(5000L)
            htmlfromwebviewsaved(direccion, "bookmark.txt", "bookmark.html")
            replaceFragment(BookMark)
            binding.mainprogressBar.visibility = ProgressBar.INVISIBLE
        }
    }


    override fun onFileEmpty() {
        GlobalScope.launch {
            delay(5000L)
            htmlfromwebviewsaved(direccion, "bookmark.txt", "bookmark.html")
        }
    }

    override fun onWebNotAvailable() {
        dialogoNoRed("Page Not Available. \n Try Later")
    }
    override fun onResumeFragments() {
        super.onResumeFragments()
    }

    override fun onUpdateProgress(progress: Int) {
        binding.mainprogressBar.progress = progress
    }

    override fun onLastCapClicked(lasturl: String) {
        println(lasturl)
        bindingWebView.fragwebView.isVisible = true
        webView = webviewlogin.newInstance(lasturl, direccion)
        replaceFragment(webView)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if(keyCode == KeyEvent.KEYCODE_BACK){
            val intent = Intent(visibleWebView.hashCode().toString())
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            true
        }else
            super.onKeyDown(keyCode, event)
    }

    private fun setupPermissions(context: Context) {

        val permission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (permission == PackageManager.PERMISSION_GRANTED) {
            Log.e("Permisos ", "Concedidos")
        } else {
            Log.e("Permisos ", "No Concedidos")
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                101
            )
        }
    }

    private fun checkConnection() : Boolean{
        val manager =
            applicationContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = manager.activeNetworkInfo
        if (null != networkInfo) {
            if (networkInfo.type == ConnectivityManager.TYPE_WIFI) {
                //showToast("Conectado a  WiFi")
            } else if (networkInfo.type == ConnectivityManager.TYPE_MOBILE) {
                //showToast("Conectado a Red Movil")
            }
            return true
        } else {
            dialogoNoRed(R.string.NoInternet.toString())
        }
        return false
    }

    private fun dialogoNoRed(mensaje: String){
        val dialog = Dialog(this)
        bindingDialog = NointernetBinding.inflate(layoutInflater)
        dialog.setContentView(bindingDialog.root)
        bindingDialog.tvmensaje.text = mensaje
        dialog.setCanceledOnTouchOutside(false)
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        bindingDialog.btnTryAgain.setOnClickListener {
            recreate()
        }
        dialog.show()
    }

    private fun loadFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.frag, fragment)
        fragmentTransaction.commit()
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frag, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    private fun showToast(mensaje: String){
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()

    }



}