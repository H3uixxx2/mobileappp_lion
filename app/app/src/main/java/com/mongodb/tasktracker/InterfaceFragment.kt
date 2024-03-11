package com.mongodb.tasktracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mongodb.tasktracker.model.SlotAdapter
import com.mongodb.tasktracker.model.SlotInfo


class InterfaceFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var slotAdapter: SlotAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_interface, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Khởi tạo RecyclerView và SlotAdapter
        recyclerView = view.findViewById(R.id.my_recycler_view1)
        recyclerView.layoutManager = LinearLayoutManager(context)
        slotAdapter = SlotAdapter(emptyList())
        recyclerView.adapter = slotAdapter

        // Nhận dữ liệu từ arguments và cập nhật RecyclerView
        arguments?.getSerializable("slotsData")?.let {
            val slotsData = it as List<SlotInfo>
            slotAdapter.updateSlots(slotsData)
        }
    }
}