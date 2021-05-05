package tv.wiinvent.android.wiinvent_androidtv_sample_kotlin.model

//private int accountId;
//private int channelId;
//private int streamId;
//private String token;
//private String env;
//private String contentUrl;
data class ConfigRes (
    var accountId: Integer? = null,
    var channelId: Integer? = null,
    var streamId: Integer? = null,
    var token: String? = null,
    var env: String? = null,
    var contentUrl: String? = null
) {}