package com.example.govservice.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.govservice.R
import com.example.govservice.dto.ServiceResponse

    class ServiceAdapter(
    private var items: List<ServiceResponse>,
    private val onClick: (ServiceResponse) -> Unit
) : RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>() {

    class ServiceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.serviceTitleTextView)
        val descriptionTextView: TextView = view.findViewById(R.id.serviceDescriptionTextView)
        val documentsTextView: TextView = view.findViewById(R.id.serviceDocumentsTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_service, parent, false)

        return ServiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        val service = items[position]

        holder.titleTextView.text = service.title
        holder.descriptionTextView.text = service.description
        holder.documentsTextView.text = "Документы: ${service.requiredDocuments}"

        holder.itemView.setOnClickListener {
            onClick(service)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<ServiceResponse>) {
        items = newItems
        notifyDataSetChanged()
    }
}