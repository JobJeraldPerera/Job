package com.example.yogaapp
import DatabaseHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.content.Intent
import android.util.Log
import android.widget.Button


class ClassListAdapter(
    private var classList: MutableList<ClassModel>,
    private val onDeleteClickListener: (Long) -> Unit,
    private val dbHelper: DatabaseHelper,
    private val onItemClick: (ClassModel) -> Unit,
    private val onItemDoubleClick: (ClassModel) -> Unit
) : RecyclerView.Adapter<ClassListAdapter.ClassViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.itemclass, parent, false)
        return ClassViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
        val currentClass = classList[position]
        holder.bind(currentClass)

        holder.itemView.findViewById<Button>(R.id.buttonShowInstances)?.setOnClickListener {
            val intent = Intent(holder.itemView.context, ShowInstancesActivity::class.java)
            intent.putExtra("class_id", currentClass.id) // Pass necessary data if needed
            holder.itemView.context.startActivity(intent)
        }

        holder.itemView.setOnClickListener {
            // Call onItemClick when an item is clicked
            Log.d("ItemClick", "Single click occurred")
            onItemClick(currentClass)

            val intent = Intent(holder.itemView.context, DisplayClassInstancesActivity::class.java)
            // Add any extras or perform any necessary actions here
            holder.itemView.context.startActivity(intent)
        }

        val gestureDetector = GestureDetector(holder.itemView.context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                val adapterPosition = holder.adapterPosition
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemDoubleClick(classList[adapterPosition])
                    return true
                }
                return false
            }
        })

        holder.itemView.setOnTouchListener { v, event ->
            gestureDetector.onTouchEvent(event)
            true
        }




        holder.itemView.findViewById<TextView>(R.id.buttonDelete).setOnClickListener {
            onDeleteClickListener.invoke(currentClass.id.toLong())
            deleteFromClassListAndDatabase(currentClass.id.toLong())
        }
    }

    override fun getItemCount(): Int {
        return classList.size
    }

    fun removeItem(position: Int) {
        classList.removeAt(position)
        notifyItemRemoved(position)
    }

    private fun deleteFromClassListAndDatabase(itemId: Long) {
        val index = classList.indexOfFirst { it.id.toLong() == itemId }
        if (index != -1) {
            classList.removeAt(index)
            notifyItemRemoved(index)
        }
        dbHelper.deleteCourse(itemId)
    }

    fun deleteCourse(courseId: Long) {
        dbHelper.deleteCourse(courseId)
    }

    class ClassViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dayOfWeekTextView: TextView = itemView.findViewById(R.id.dayOfWeekTextView)
        private val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
        private val capacityTextView: TextView = itemView.findViewById(R.id.capacityTextView)
        private val durationTextView: TextView = itemView.findViewById(R.id.durationTextView)
        private val priceTextView: TextView = itemView.findViewById(R.id.priceTextView)
        private val typeTextView: TextView = itemView.findViewById(R.id.typeTextView)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)

        fun bind(classModel: ClassModel) {
            dayOfWeekTextView.text = "Day of Week: ${classModel.dayOfWeek}"
            timeTextView.text = "Time: ${classModel.time}"
            capacityTextView.text = "Capacity: ${classModel.capacity}"
            durationTextView.text = "Duration: ${classModel.duration}"
            priceTextView.text = "Price: ${classModel.price}"
            typeTextView.text = "Type: ${classModel.type}"
            descriptionTextView.text = "Description: ${classModel.description}"
        }
    }
}