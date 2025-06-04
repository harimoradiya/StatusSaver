package com.status.statussaver.data.model

data class AdsModel(
    var banner_id: String = "",
        var interstitial_id: String = "",
    var native_id: String = "",
    var appopen_id: String = "",
    var adx_appopen_id: String = "",
    var adx_native_id: String = "",
    var adx_interstitial_id: String = "",
    var adx_banner_id: String = "",
    var adStatus: Boolean
){
    constructor(): this("", "","","","","","","",false)
}