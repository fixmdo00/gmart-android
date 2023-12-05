package com.example.gmart

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Property
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.example.gmart.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    var string = "ini string dari mainActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val homeFragment = HomeFragment()
        val bundle = Bundle()
        val loginSP = LoginSP.getSP()
        bundle.putString("string", string)
        homeFragment.arguments = bundle

        RQ.initRQ(this)

        HomeInfo.getHomeInfoData2()

        Products.getFromDB()

//        val fragmentToStart = ""



        Log.d("pzn","ini main act")

//        CoroutineScope(Dispatchers.IO).launch {
//            delay(5000)
//            Products.getFromDB()
//            Log.d("pzn coroutine","coroiti")
//        }


        when (loginSP.getBoolean("status", false)){
            true -> {
                LoggedInUser.getUserInfo(loginSP.getString("userId", "null").toString())
                KeranjangItems.getItemsFromDB(loginSP.getString("userId", "null").toString())
                Toast.makeText(this, "Anda sudah Login", Toast.LENGTH_LONG).show()
            }
            false -> {
                Toast.makeText(this, "Anda belum Login", Toast.LENGTH_LONG).show()
            }
        }

//        LoggedInUser.livaLoggedInUser.observe(this, {
//            KeranjangItems.getItemsFromDB(it.id.toString())
//            })

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val fragmentToStart = intent.getStringExtra("fragmentToStart")
        if (fragmentToStart !== null){
            when (fragmentToStart){
                "KeranjangFragment" -> replaceFragment(KeranjangFragment())
                "ProductsFragment" -> replaceFragment(ProductsFragment())
            }
        } else {
            replaceFragment(HomeFragment.newInstance("ini param 1", "ini param 2"))
        }

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home-> replaceFragment(HomeFragment.newInstance("ini param 1","ini param 2"))
                R.id.products-> replaceFragment(ProductsFragment())
                R.id.profile-> {
                    when (LoginSP.getLoginStatus()){
                        true -> {
                            replaceFragment(ProfileFragment())
                        }
                        false -> {
                            replaceFragment(ProfileNotLoginFragment())
                        }
                    }
                }
                R.id.keranjang-> replaceFragment(KeranjangFragment())

                else -> {

                }
            }
            true
        }


    }



    fun replaceFragment (fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }
}