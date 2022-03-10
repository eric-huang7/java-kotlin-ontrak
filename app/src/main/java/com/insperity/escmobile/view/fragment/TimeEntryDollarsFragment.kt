package com.insperity.escmobile.view.fragment

import com.insperity.escmobile.event.OnTimeEntryDollarsReceived
import org.greenrobot.eventbus.Subscribe

class TimeEntryDollarsFragment : TimeEntryBaseFragment() {

    companion object {
        fun newInstance() = TimeEntryDollarsFragment()
    }

    override val trackerPage: String
        get() = "Dollars"

    override fun registerInBus() = bus.register(this)

    override fun unregisterFromBus() = bus.unregister(this)

    @Subscribe(sticky = true)
    fun onEvent(event: OnTimeEntryDollarsReceived) = setupRecyclerView(event.properties, event.items)
}