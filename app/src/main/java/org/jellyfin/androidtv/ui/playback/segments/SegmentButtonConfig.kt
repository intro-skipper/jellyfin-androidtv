package org.jellyfin.androidtv.ui.playback.segments

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SegmentButtonConfig(
	@SerialName("SkipButtonIntroText")
	val skipButtonIntroText: String,
	@SerialName("SkipButtonEndCreditsText")
	val skipButtonEndCreditsText: String,
	@SerialName("SkipButtonVisible")
	val skipButtonVisible: Boolean,
	@SerialName("AutoSkip")
	val autoSkip: Boolean,
	@SerialName("AutoSkipCredits")
	val autoSkipCredits: Boolean
)
