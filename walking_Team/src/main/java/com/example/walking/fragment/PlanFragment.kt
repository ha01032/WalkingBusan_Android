package com.example.walking.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.walking.R
import com.example.walking.databinding.FragmentPlanBinding
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import java.util.*

class PlanFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentPlanBinding.inflate(inflater, container, false)
        binding.calendarView.selectedDate = CalendarDay.today() // 오늘 날짜 출력
        binding.calendarView.addDecorators(SaturdayDecorator(), SundayDecorator()) // 주말(토,일) 표시

        binding.calendarView.setOnDateChangedListener(object: OnDateSelectedListener {
            override fun onDateSelected(widget: MaterialCalendarView, date: CalendarDay, selected: Boolean) {
                binding.calendarView.addDecorator(EventDecorator(Collections.singleton(date)))
            } // 날짜 선택시 도트 출력
        })
        return binding.root
    }
}

