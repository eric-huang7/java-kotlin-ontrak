package com.delphiaconsulting.timestar.view.fragment

import com.delphiaconsulting.timestar.event.OnTimeEntryHoursReceived
import org.greenrobot.eventbus.Subscribe

class TimeEntryHoursFragment : TimeEntryBaseFragment() {

    companion object {
        fun newInstance() = TimeEntryHoursFragment()
    }

    override val trackerPage: String
        get() = "Hours"

    override fun registerInBus() = bus.register(this)

    override fun unregisterFromBus() = bus.unregister(this)

    @Subscribe(sticky = true)
    fun onEvent(event: OnTimeEntryHoursReceived) = setupRecyclerView(event.properties, event.items)
}