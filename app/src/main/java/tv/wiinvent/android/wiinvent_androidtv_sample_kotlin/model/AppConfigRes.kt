package tv.wiinvent.android.wiinvent_androidtv_sample_kotlin.model

data class AppConfigRes (
    var vod: ConfigRes? = null,
    var livestream: ConfigRes? = null
) {
    override fun toString(): String {
        return "VOD{" +
                "accountId=" + vod?.accountId +
                ", channelId='" + vod?.channelId + '\'' +
                ", streamId='" + vod?.streamId + '\'' +
                ", token='" + vod?.token + '\'' +
                ", env='" + vod?.env + '\'' +
                ", contentUrl='" + vod?.contentUrl + '\'' +
                '}' +
                "Live{" +
                "accountId=" + livestream?.accountId +
                ", channelId='" + livestream?.channelId + '\'' +
                ", streamId='" + livestream?.streamId + '\'' +
                ", token='" + livestream?.token + '\'' +
                ", env='" + livestream?.env + '\'' +
                ", contentUrl='" + livestream?.contentUrl + '\'' +
                '}'
    }
}