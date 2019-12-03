package com.mckrpk.animatedprogressbar.sample

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mckrpk.animatedprogressbar.AnimatedProgressBar

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var animProgress: AnimatedProgressBar
    lateinit var animProgress2: AnimatedProgressBar
    lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        animProgress = findViewById(R.id.animatedProgressBar)
        animProgress2 = findViewById(R.id.animatedProgressBar2)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.setHasFixedSize(true)

        fab.setOnClickListener { view ->
            animProgress.setProgress(1f)
            animProgress2.setProgress(1f)
//            recyclerView.adapter?.notifyDataSetChanged()
        }

        recyclerView.adapter = MyAdapter(50)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_simple -> {
                true
            }
            R.id.action_waves -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    class MyAdapter(private val itemSize: Int) :
            RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

        class MyViewHolder(val progressBar: AnimatedProgressBar, var number: Int) :
                RecyclerView.ViewHolder(progressBar)


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val textView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.my_progress_item, parent, false) as AnimatedProgressBar
            return MyViewHolder(textView, 0)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.number = position
            holder.progressBar.setProgress(Math.random().toFloat())
            Log.i("customView", "Adapter:onBindViewHolder")
        }

        override fun onViewAttachedToWindow(holder: MyViewHolder) {
            super.onViewAttachedToWindow(holder)
            Log.i(
                    "customView",
                    "Adapter:onViewAttachedToWindow"
            )
        }


        override fun getItemCount() = itemSize
    }
}
