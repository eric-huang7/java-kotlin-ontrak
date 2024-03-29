package com.delphiaconsulting.timestar.view.activity

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.avast.android.dialogs.fragment.SimpleDialogFragment
import com.delphiaconsulting.timestar.App
import com.delphiaconsulting.timestar.R
import com.delphiaconsulting.timestar.action.creators.SessionActionsCreator
import com.delphiaconsulting.timestar.event.*
import com.delphiaconsulting.timestar.net.analytics.AnalyticsCategories
import com.delphiaconsulting.timestar.net.analytics.Tracker
import com.delphiaconsulting.timestar.store.SessionStore
import com.delphiaconsulting.timestar.util.AppUtil
import com.delphiaconsulting.timestar.util.Preferences
import com.delphiaconsulting.timestar.view.fragment.CustomDialogFragment
import com.delphiaconsulting.timestar.view.fragment.TimePickerDialogFragment
import kotlinx.android.synthetic.main.activity_onboarding.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject

class OnboardingActivity : AppCompatActivity() {

    companion object {
        fun getCallingIntent(context: Context): Intent = Intent(context, OnboardingActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    }

    @Inject lateinit var bus: EventBus
    @Inject lateinit var appUtil: AppUtil
    @Inject lateinit var preferences: Preferences
    @Inject lateinit var inputMethodManager: InputMethodManager
    @Inject lateinit var tracker: Tracker
    @Inject lateinit var actionsCreator: SessionActionsCreator
    @Inject lateinit var store: SessionStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (applicationContext as App).component.inject(this)
        setContentView(R.layout.activity_onboarding)
        tracker.trackScreenView("Onboarding")
        bus.register(this)

        versionText.text = appUtil.versionInfo
        versionText.visibility = VISIBLE

        codeField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                onSubmitButtonClicked(View(this))
            }
            false
        }
    }

    override fun onDestroy() {
        bus.unregister(this)
        super.onDestroy()
    }

    fun onNeedHelpTextClicked(v: View) {
        CustomDialogFragment.newInstance().show(supportFragmentManager, CustomDialogFragment.TAG)
    }

    fun onSubmitButtonClicked(v: View) {
        val code = codeField.text.toString()
        statusText.visibility = View.VISIBLE
        if (code.isEmpty()) {
            updateStatusText(R.color.insperity_orange, R.string.empty_code_error_text)
            return
        }
        progressBar.visibility = View.VISIBLE
        submitButton.isEnabled = false
        updateStatusText(R.color.insperity_blue_light, R.string.submitting_status_text)
        actionsCreator.submitAuthCode(code)
    }

    @Subscribe
    fun onEvent(event: OnAuthTokenReceived) {
        updateStatusText(R.color.insperity_blue_light, R.string.loading_session_status_text)
        inputMethodManager.hideSoftInputFromWindow(codeField.windowToken, 0)
        tracker.trackEvent(AnalyticsCategories.ONBOARDING, "Submit", "Success")
    }

    @Subscribe
    fun onEvent(event: OnSessionDataReceived) {
        if (event.noPunchMode) {
            startActivity(DashboardActivity.getCallingIntent(this))
            return
        }
        updateStatusText(R.color.insperity_blue_light, R.string.loading_punch_data_text)
    }

    @Subscribe
    fun onEvent(event: OnPunchesDataSaved) {
        startActivity(DashboardActivity.getCallingIntent(this))
        finish()
    }

    @Subscribe
    fun onEvent(event: OnTokenOrSessionDataError) {
        onBackgroundWorkFailed(event.message)
        tracker.trackEvent(AnalyticsCategories.ONBOARDING, "Submit", "Failed")
    }

    @Subscribe
    fun onEvent(event: OnPunchDataError) = onBackgroundWorkFailed(null)

    @Subscribe
    fun onEvent(event: OnNoPunchDataError) = onBackgroundWorkFailed(null)

    private fun onBackgroundWorkFailed(message: String?) {
        progressBar.visibility = View.GONE
        submitButton.isEnabled = true
        updateStatusText(R.color.insperity_orange, message ?: getString(R.string.service_not_available_error_text))
    }

    private fun updateStatusText(color: Int, errorTextRes: Int) = updateStatusText(color, getString(errorTextRes))

    private fun updateStatusText(color: Int, errorText: String) {
        statusText.setTextColor(ContextCompat.getColor(this, color))
        statusText.text = errorText
    }
}
