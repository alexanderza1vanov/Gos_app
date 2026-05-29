package com.example.govservice.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.govservice.R
import com.example.govservice.dto.ApplicationResponse
import com.google.android.material.button.MaterialButton

class EmployeeApplicationAdapter(
    private var items: List<ApplicationResponse>,
    private val onStatusClick: (ApplicationResponse, String) -> Unit,
    private val onClick: (ApplicationResponse) -> Unit
) : RecyclerView.Adapter<EmployeeApplicationAdapter.EmployeeApplicationViewHolder>() {

    class EmployeeApplicationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.employeeApplicationTitleTextView)
        val applicantTextView: TextView = view.findViewById(R.id.employeeApplicantTextView)
        val statusTextView: TextView = view.findViewById(R.id.employeeStatusTextView)
        val detailsTextView: TextView = view.findViewById(R.id.employeeDetailsTextView)
        val inProgressButton: MaterialButton = view.findViewById(R.id.inProgressButton)
        val approveButton: MaterialButton = view.findViewById(R.id.approveButton)
        val rejectButton: MaterialButton = view.findViewById(R.id.rejectButton)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EmployeeApplicationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_employee_application, parent, false)

        return EmployeeApplicationViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmployeeApplicationViewHolder, position: Int) {
        val application = items[position]

        holder.titleTextView.text = "Заявление №${application.id}"
        holder.applicantTextView.text = "Заявитель: ${application.applicantFullName}"
        holder.statusTextView.text = "Статус: ${translateStatus(application.status)}"
        holder.detailsTextView.text =
            "${application.serviceTitle}\nАдрес: ${application.address}\nЦель: ${application.purpose}"

        holder.inProgressButton.setOnClickListener {
            onStatusClick(application, "IN_PROGRESS")
        }

        holder.approveButton.setOnClickListener {
            onStatusClick(application, "APPROVED")
        }

        holder.rejectButton.setOnClickListener {
            onStatusClick(application, "REJECTED")
        }

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
}