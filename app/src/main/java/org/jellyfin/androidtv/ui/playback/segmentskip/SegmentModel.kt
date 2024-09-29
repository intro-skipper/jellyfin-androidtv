package org.jellyfin.androidtv.ui.playback.segmentskip

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class SegmentType {
	INTRO, CREDITS, UNKNOWN
}

@Serializable
data class SegmentModel (
	var type: SegmentType = SegmentType.UNKNOWN,
	@SerialName("IntroStart")
	val startTime: Double,
	@SerialName("IntroEnd")
	val endTime: Double,
	@SerialName("ShowSkipPromptAt")
	val showAt: Double,
	@SerialName("HideSkipPromptAt")
	val hideAt: Double,
)
