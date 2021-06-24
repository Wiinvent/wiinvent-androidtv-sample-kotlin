package tv.wiinvent.android.wiinvent_androidtv_sample_kotlin.model

data class AppConfigRes(
        var vod: List<ConfigRes>? = null,
        var livestream: List<ConfigRes>? = null
) {
    override fun toString(): String {
        return "*** vod size: " + vod?.size + " *** livestream size: " + livestream?.size
    }
}