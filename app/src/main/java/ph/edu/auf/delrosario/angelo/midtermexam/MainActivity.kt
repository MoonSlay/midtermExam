package ph.edu.auf.delrosario.angelo.midtermexam

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import ph.edu.auf.delrosario.angelo.midtermexam.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var taskAdapter: TaskAdapter
    private val tasks = mutableListOf<Pair<String, String>>()
    private val removedTaskNumbers = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        taskAdapter = TaskAdapter(tasks)
        binding.recyclerView.adapter = taskAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val task = tasks[position]
                val taskNumber = task.first.split(" ")[1].toInt()
                removedTaskNumbers.add(taskNumber)
                taskAdapter.removeItem(position)
                Snackbar.make(binding.recyclerView, "Task deleted", Snackbar.LENGTH_LONG)
                    .setAction("UNDO") {
                        taskAdapter.addItem(task)
                        removedTaskNumbers.remove(taskNumber)
                    }.show()
            }
        })
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)

        binding.buttonAddTask.setOnClickListener {
            val taskName = binding.editTextTask.text.toString()
            if (taskName.isBlank()) {
                binding.editTextTask.error = "This field is required"
            } else {
                binding.editTextTask.error = null
                showConfirmationDialog(taskName)
            }
        }
    }

    private fun showConfirmationDialog(taskName: String) {
        AlertDialog.Builder(this)
            .setTitle("CONFIRMATION")
            .setMessage("Are you sure you want to add \"$taskName\" as your new task?")
            .setPositiveButton("Yes") { _, _ ->
                val taskNumber = if (removedTaskNumbers.isNotEmpty()) {
                    removedTaskNumbers.minOrNull()!!
                } else {
                    tasks.size + 1
                }
                val task = "Task # $taskNumber" to taskName
                taskAdapter.addItem(task)
                removedTaskNumbers.remove(taskNumber)
                binding.editTextTask.text.clear()
            }
            .setNegativeButton("No", null)
            .show()
    }
}