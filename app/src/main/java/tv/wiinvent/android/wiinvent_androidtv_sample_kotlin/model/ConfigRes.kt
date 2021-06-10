package tv.wiinvent.android.wiinvent_androidtv_sample_kotlin.model

data class ConfigRes (
    var accountId: Integer? = null,
    var channelId: Integer? = null,
    var streamId: Integer? = null,
    var token: String? = null,
    var env: String? = null,
    var contentUrl: String? = null
) {
    override fun toString(): String {
        return "Config{" +
                "accountId=" + accountId +
                ", channelId='" + channelId + '\'' +
                ", streamId='" + streamId + '\'' +
                ", token='" + token + '\'' +
                ", env='" + env + '\'' +
                ", contentUrl='" + contentUrl + '\'' +
                '}'
    }
}