package com.example.gmart

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.example.gmart.databinding.ActivityOrderConfirmationBinding
import org.json.JSONArray
import org.json.JSONObject
import kotlin.random.Random

class OrderConfirmationActivity : AppCompatActivity() {
    lateinit var binding : ActivityOrderConfirmationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnKonfirmasiPesanan.setOnClickListener {
            if (binding.inputNama.text contentEquals  "" ||
                binding.inputAlamat.text contentEquals  "" ||
                binding.inputTelepon.text contentEquals  ""
            ){
                Toast.makeText(this, "Data Tidak Lengkap", Toast.LENGTH_LONG).show()
            }else {
            val radioId = binding.radioGroup.checkedRadioButtonId
            val radio : RadioButton = findViewById(radioId)
            addOrderToDB(radio.text.toString())
            }
        }

    }

    fun addOrderToDB(metodePembayaran : String){
        val alamatUrl = "https://wifi-map.my.id/api/gmart_add_alamat_pengiriman.php"
//        val hapusUrl = "https://gmartxxx.requestcatcher.com/test"
        val orderUrl = "https://wifi-map.my.id/api/gmart_add_order.php"
        val hapusUrl = "https://wifi-map.my.id/api/gmart_delete_keranjang_item.php"

        val orderParams = JSONArray()
        val alamatParams = JSONObject()
        val hapusParams = JSONObject()

        val orderId = Random.nextInt(900000) + 100000
        val alamatId = Random.nextInt(900000) + 100000
        for (i in 0 until KeranjangItems._kerItems.length()){
            val list = KeranjangItems._kerItems.getJSONObject(i)
            val item = JSONObject()

            item.put("ord_id", orderId)
            item.put("prd_id", list.get("ker_prd_id"))
            item.put("user_id", LoggedInUser.loggedInUser.id)
            item.put("ord_jumlah", list.get("ker_prd_jumlah"))
            item.put("ord_status", 1)
            item.put("ord_alamat_id", alamatId)
            item.put("metode_pembayaran", metodePembayaran)
            orderParams.put(item)
        }

        alamatParams.put("id",alamatId)
        alamatParams.put("nama",binding.inputNama.text)
        alamatParams.put("alamat", binding.inputAlamat.text)
        alamatParams.put("telepon", binding.inputTelepon.text)

        hapusParams.put("user_id", LoggedInUser.loggedInUser.id)

        Log.d("pzn order",orderParams.toString())
        Log.d("pzn order",alamatParams.toString())

        val hapusKeranjangRequest = JsonObjectRequest(
            Request.Method.POST, hapusUrl, hapusParams,
            { response ->
                val status = response.getString("status")
                Log.d("pzn hapus keranjang",status)
                if (status == "berhasil"){
                    KeranjangItems.getItemsFromDB(LoggedInUser.loggedInUser.id.toString())
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("fragmentToStart", "ProductsFragment")
                    startActivity(intent)
                }
                else {
                }
            },
            { error ->
                Log.d("pzn hapus keranjang err",error.toString())
            })

        val orderRequest = JsonArrayRequest(
            Request.Method.POST, orderUrl, orderParams,
            { response ->
                val _status : JSONObject = response.getJSONObject(0)
                val status = _status.getString("status")
                Log.d("pzn add order",status)
                if (status == "berhasil"){
                    RQ.getRQ().add(hapusKeranjangRequest)
                }
                else {
                }
            },
            { error ->
                Log.d("pzn add order err",error.toString())
            })

        val alamatRequest = JsonObjectRequest(
                Request.Method.POST, alamatUrl, alamatParams,
            { response ->
                val status = response.getString("status")
                Log.d("pzn alamat",status)
                if (status == "berhasil"){
                    RQ.getRQ().add(orderRequest)
                }
                else {
                }
            },
            { error ->
                Log.d("pzn alamat err",error.toString())
            })

        RQ.getRQ().add(alamatRequest)

    }
}