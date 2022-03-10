package com.insperity.escmobile.net.gson

class TimeApproveRequest(val payPeriodId: String, val approvalRequests: List<ApprovalRequest>) {

    class ApprovalRequest(val status: String, val type: String, val employeeId: Int)
}
