package com.example.rxjavaexample

import android.os.Bundle
import android.os.SystemClock
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {



private lateinit var tvCounter: TextView
    private lateinit var subscription: Disposable


    private val text = exampleText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val t3vText: TextView = findViewById(R.id.tvText1)
        t3vText.text = text
        val tt = Observable.fromIterable(text.split("\n"))


         var countWords: Int
        val rxSearch: EditText= findViewById(R.id.etSearch)

        tvCounter = findViewById(R.id.tvCounter)

        //можно было заюзать TextWatcher и подписаться на CharSequence в нужной функции,
        // но я сделал через RxBinding
        subscription = RxTextView.textChanges(rxSearch)
            .debounce(700, TimeUnit.MILLISECONDS)
            .filter{ it.isNotEmpty() }
            .flatMap {target ->
                    countWords = 0
                    tt.map { source ->
                        //SystemClock.sleep(100)
                        count(source, target.toString())

                    }

                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnNext {

                                countWords += it
                                tvCounter.text = countWords.toString()

                            }

            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                countWords = 0
            //    tvCounter.text = it.toString()
            }


    }

    private fun count(str: String, target: String): Int {
    if (target.isEmpty()) return 0
        return if (str.length < target.length) 0
        else
            (str.length - str.replace(target, "").length) / target.length
    }

    override fun onDestroy() {
        super.onDestroy()
        subscription.dispose()


    }

}


