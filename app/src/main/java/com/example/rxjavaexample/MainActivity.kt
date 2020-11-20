package com.example.rxjavaexample

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    //падает при быстром удалении строки в ноль


private lateinit var tvCounter: TextView
    private lateinit var subscription: Disposable

    private val text = exampleText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val t3vText: TextView = findViewById(R.id.tvText1)
        t3vText.text = text

        val rxSearch: EditText= findViewById(R.id.etSearch)

        tvCounter = findViewById(R.id.tvCounter)

        subscription = RxTextView.textChanges(rxSearch)  //можно было заюзать TextWatcher и подписаться на CharSequence, но я сделал через RxBinding
            .filter{ it.isNotEmpty() }
            .debounce(700, TimeUnit.MILLISECONDS)
            .map {
                  count(text, it.toString()) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Log.d("TREAD", "${Thread.currentThread()}")
                tvCounter.text = it.toString()
            }


    }

    fun count(str: String, target: String?): Int {
        if (target == null) { return 0}

        return (str.length - str.replace(target, "").length) / target.length
    }

    override fun onStop() {
        super.onStop()
        subscription.dispose()

    }

}


