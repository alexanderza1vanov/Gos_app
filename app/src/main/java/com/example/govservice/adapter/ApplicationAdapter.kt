package com.example.govservice.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.govservice.R
import com.example.govservice.dto.ApplicationResponse

class ApplicationAdapter(
    private var items: List<ApplicationResponse>,
    private val onClick: (ApplicationResponse) -> Unit
) : RecyclerView.Adapter<ApplicationAdapter.ApplicationViewHolder>() {

    class ApplicationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.applicationTitleTextView)
        val statusTextView: TextView = view.findViewById(R.id.applicationStatusTextView)
        val dateTextView: TextView = view.findViewById(R.id.applicationDateTextView)
        val applicantTextView: TextView = view.findViewById(R.id.applicationApplicantTextView)
        val commentTextView: TextView = view.findViewById(R.id.applicationCommentTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApplicationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_application, parent, false)

        return ApplicationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ApplicationViewHolder, position: Int) {
        val application = items[position]

        holder.titleTextView.text = "Заявление №${application.id}"
        holder.statusTextView.text = translateStatus(application.status)
        holder.dateTextView.text = "Дата подачи: ${formatDate(application.createdAt)}"
        holder.applicantTextView.text = application.serviceTitle
        holder.commentTextView.text = application.employeeComment ?: "Комментарий отсутствует"

        holder.itemView.setOnClickListener {
            onClick(application)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateItems(newItems: List<ApplicationResponse>) {
        items = newItems
        notifyDataSetChanged()
    }

    private fun translateStatus(status: String): String {
        return when (status) {
            "SUBMITTED" -> "Отправлено"
            "IN_PROGRESS" -> "На рассмотрении"
            "NEEDS_INFO" -> "Требует уточнения"
            "APPROVED" -> "Одобрено"
            "REJECTED" -> "Отклонено"
            else -> status
        }
    }

    private fun formatDate(date: String): String {
        return date.replace("T", " ")
    }
}