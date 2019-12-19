package com.mckrpk.animatedprogressbar.sample

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.mckrpk.animatedprogressbar.AnimatedProgressBar
import com.mckrpk.animatedprogressbar.dpToPx
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    var style = AnimatedProgressBar.Style.WAVE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            addViewProgrammatically()
        }

        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = MyAdapter(50)
    }

    private fun addViewProgrammatically() {
        val progressBar = AnimatedProgressBar(this)
        progressBar.setMax(100)
        progressBar.setProgress((Math.random() * 100).toInt())
        progressBar.setTrackColor(getRandomColor())
        progressBar.setProgressColor(getRandomColor())
        progressBar.setProgressTipEnabled(Math.random() < 0.5)
        progressBar.setProgressTipColor(getRandomColor())
        progressBar.setAnimDuration(500 + (Math.random() * 2000).toInt())
        progressBar.setProgressStyle(if (Math.random() < 0.5) AnimatedProgressBar.Style.LINE else AnimatedProgressBar.Style.WAVE)
        progressBar.setLineWidth(
            dpToPx(3 + (Math.random() * 10).roundToInt(), this).roundToInt()
        )

        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val marginTopBottom = dpToPx(4, this).toInt()
        params.setMargins(0, marginTopBottom, 0, marginTopBottom)
        barsContainer.addView(progressBar, params)
    }

    private fun getRandomColor(): Int {
        return Color.rgb(
            (Math.random() * 255).toInt(),
            (Math.random() * 255).toInt(),
            (Math.random() * 255).toInt()
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_simple -> {
                style = AnimatedProgressBar.Style.LINE
                recyclerView.adapter?.notifyDataSetChanged()
                true
            }
            R.id.action_waves -> {
                style = AnimatedProgressBar.Style.WAVE
                recyclerView.adapter?.notifyDataSetChanged()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    inner class MyAdapter(private val itemSize: Int) :
        RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

        inner class MyViewHolder(val progressBar: AnimatedProgressBar, var number: Int) :
            RecyclerView.ViewHolder(progressBar)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val animatedProgressBar = LayoutInflater.from(parent.context)
                .inflate(R.layout.my_progress_item, parent, false) as AnimatedProgressBar
            animatedProgressBar.setProgressStyle(style)

            return MyViewHolder(animatedProgressBar, 0)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.number = position
            holder.progressBar.setProgressStyle(style)
            holder.progressBar.setProgress((Math.random() * 100).toInt())
        }

        override fun getItemCount() = itemSize
    }
}
