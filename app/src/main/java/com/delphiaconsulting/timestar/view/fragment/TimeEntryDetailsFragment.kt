package com.delphiaconsulting.timestar.view.fragment

import com.delphiaconsulting.timestar.event.OnTimeEntryDetailsReceived
import org.greenrobot.eventbus.Subscribe

class TimeEntryDetailsFragment : TimeEntryBaseFragment() {

    companion object {
        fun newInstance() = TimeEntryDetailsFragment()
    }

    override val trackerPage: String
        get() = "Detail"

    override fun registerInBus() = bus.register(this)

    override fun unregisterFromBus() = bus.unregister(this)

    @Subscribe(sticky = true)
    fun onEvent(event: OnTimeEntryDetailsReceived) = setupRecyclerView(event.properties, event.items)
}