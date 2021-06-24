package tv.wiinvent.android.wiinvent_androidtv_sample_kotlin.model

import java.io.Serializable

/**
 * Movie class represents video entity with title, description, image thumbs and video url.
 */
data class Movie(
        var id: Int? = 0,
        var title: String? = null,
        var description: String? = null,
        var backgroundImageUrl: String? = null,
        var cardImageUrl: String? = null,
        var studio: String? = null,

        var accountId: Int? = null,
        var channelId: Int? = null,
        var streamId: Int? = null,
        var token: String? = null,
        var env: String? = null,
        var contentUrl: String? = null,
        var contentType: String? = null
) : Serializable {

    override fun toString(): String {
        return "Movie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", backgroundImageUrl='" + backgroundImageUrl + '\'' +
                ", cardImageUrl='" + cardImageUrl + '\'' +
                ", contentType='" + contentType + '\'' +
                ", accountId='" + accountId + '\'' +
                ", channelId='" + channelId + '\'' +
                ", streamId='" + streamId + '\'' +
                ", token='" + token + '\'' +
                ", env='" + env + '\'' +
                ", contentUrl='" + contentUrl + '\'' +
                '}'
    }

    companion object {
        internal const val serialVersionUID = 727566175075960653L
    }
}
