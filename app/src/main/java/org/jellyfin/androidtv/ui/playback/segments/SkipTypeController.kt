package org.jellyfin.androidtv.ui.playback.segments

import org.jellyfin.androidtv.preference.UserPreferences;

enum class SegmentSkipType {
	ShowButton, AutoSkip, Hidden
}

class SkipTypeController(
	previousSkipSelection: SegmentSkipType,
	private val userPreferences: UserPreferences,
) {
	var currentSkipType = previousSkipSelection
		set(value) {
			userPreferences[UserPreferences.skipMode] = value
			field = value
		}

	fun setSkipType(skipType: SegmentSkipType) {
		currentSkipType = skipType
		userPreferences[UserPreferences.skipMode] = skipType
	}
}

