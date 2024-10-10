package org.jellyfin.androidtv.ui.playback.segments

import org.jellyfin.androidtv.R

enum class SegmentMode {
	SHOW_SKIP_BUTTON {
		override fun icon(): Int = R.drawable.ic_select_skip_show_button
		override fun label(): Int = R.string.lbl_show_skip_button
	},
	AUTO_SKIP {
		override fun icon(): Int = R.drawable.ic_select_skip_auto_skip
		override fun label(): Int = R.string.lbl_auto_skip
	},
	HIDE_SKIP_BUTTON {
		override fun icon(): Int = R.drawable.ic_select_skip_hide_button
		override fun label(): Int = R.string.lbl_hide_skip_button
	};

	abstract fun icon(): Int
	abstract fun label(): Int
}
