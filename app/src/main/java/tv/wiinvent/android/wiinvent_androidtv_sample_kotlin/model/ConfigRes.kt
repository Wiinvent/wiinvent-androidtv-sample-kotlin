package tv.wiinvent.android.wiinvent_androidtv_sample_kotlin.model

data class ConfigRes (
        var accountId: Int? = null,
        var channelId: Int? = null,
        var streamId: Int? = null,
        var token: List<String>? = null,
        var env: String? = null,
        var contentUrl: String? = null,
        var contentType: String ? = null,
        var title: String ? = null,
        var description: String ? = null,
        var backgroundImageUrl: String ? = null,
        var cardImageUrl: String ? = null,
        var studio: String ? = null
) {
    override fun toString(): String {
        return "Config{" +
                "accountId=" + accountId +
                ", channelId='" + channelId + '\'' +
                ", streamId='" + streamId + '\'' +
                ", token='" + token + '\'' +
                ", env='" + env + '\'' +
                ", contentUrl='" + contentUrl + '\'' +
                ", contentType='" + contentType + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", backgroundImageUrl='" + backgroundImageUrl + '\'' +
                ", cardImageUrl='" + cardImageUrl + '\'' +
                ", studio='" + studio + '\'' +
                '}'
    }
}