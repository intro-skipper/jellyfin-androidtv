package org.jellyfin.androidtv.ui.playback.segments

import org.jellyfin.androidtv.R
import org.jellyfin.preference.PreferenceEnum

enum class SegmentMode(
	override val nameRes: Int,
) : PreferenceEnum {
	/**
	 * Show a skip button
	 */
	SHOW_SKIP_BUTTON(R.string.lbl_show_skip_button),
	/**
	 * Automatically skip
	 */
	AUTO_SKIP(R.string.lbl_auto_skip),
	/**
	 * No action is taken
	 */
	HIDE_SKIP_BUTTON(R.string.lbl_hide_skip_button),
}
