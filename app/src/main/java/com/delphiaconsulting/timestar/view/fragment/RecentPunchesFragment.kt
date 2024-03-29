package com.delphiaconsulting.timestar.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.delphiaconsulting.timestar.R
import com.delphiaconsulting.timestar.event.OnOfflinePunchStored
import com.delphiaconsulting.timestar.event.OnOfflinePunchesSynced
import com.delphiaconsulting.timestar.event.OnPunchesDataSaved
import com.delphiaconsulting.timestar.event.OnPunchesLoaded
import com.delphiaconsulting.timestar.store.PunchStore
import com.delphiaconsulting.timestar.view.adapter.RecentPunchesAdapter
import com.delphiaconsulting.timestar.view.common.AdapterItem
import com.delphiaconsulting.timestar.view.service.PunchDataService
import kotlinx.android.synthetic.main.fragment_recent_punches.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject

class RecentPunchesFragment : BaseFragment() {

    @Inject lateinit var bus: EventBus
    @Inject lateinit var store: PunchStore

    private var adapter: RecentPunchesAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_recent_punches, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        component.inject(this)
        context?.let { if (!PunchDataService.isServiceRunning(it)) store.getPunches() }
    }

    override fun onStart() {
        super.onStart()
        bus.register(this)
    }

    override fun onStop() {
        bus.unregister(this)
        super.onStop()
    }

    @Subscribe
    fun onEvent(event: OnPunchesDataSaved) = store.getPunches()

    @Subscribe
    fun onEvent(event: OnOfflinePunchStored) = store.getPunches()

    @Subscribe
    fun onEvent(event: OnOfflinePunchesSynced) = store.getPunches()

    @Subscribe
    fun onEvent(event: OnPunchesLoaded) = setupPunches(event.punches)

    private fun setupPunches(adapterItems: List<AdapterItem>?) {
        adapterItems?.let {
            if (it.isEmpty()) return
            if (adapter == null) {
                adapter = RecentPunchesAdapter(context)
            }
            if (recyclerView.adapter == null) {
                recyclerView.layoutManager?.isAutoMeasureEnabled = true
                recyclerView.isNestedScrollingEnabled = false
                recyclerView.itemAnimator = null
                recyclerView.setHasFixedSize(false)
                recyclerView.adapter = adapter
            }
            adapter?.setAdapterItems(it)
            showPunchesList()
        }
    }

    private fun showPunchesList() {
        noRecentText.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
    }
}
