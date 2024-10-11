package org.jellyfin.androidtv.ui.playback.overlay.action

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.PopupMenu
import org.jellyfin.androidtv.R
import org.jellyfin.androidtv.preference.UserPreferences
import org.jellyfin.androidtv.ui.playback.PlaybackController
import org.jellyfin.androidtv.ui.playback.overlay.CustomPlaybackTransportControlGlue
import org.jellyfin.androidtv.ui.playback.overlay.VideoPlayerAdapter
import org.jellyfin.androidtv.ui.playback.segments.SegmentMode

class SelectSkipAction(
	context: Context,
	customPlaybackTransportControlGlue: CustomPlaybackTransportControlGlue,
	userPreferences: UserPreferences
) : CustomAction(context, customPlaybackTransportControlGlue) {
	private val preferences = userPreferences
	private val customPlaybackTransportControlGlue1 = customPlaybackTransportControlGlue

	private val SegmentMode.icon: Int
		get() = when (this) {
			SegmentMode.SHOW_SKIP_BUTTON -> R.drawable.ic_select_skip_show_button
			SegmentMode.AUTO_SKIP -> R.drawable.ic_select_skip_auto_skip
			SegmentMode.HIDE_SKIP_BUTTON -> R.drawable.ic_select_skip_hide_button
		}

	init {
		initializeWithIcon(preferences[UserPreferences.skipMode].icon)
	}

	override fun handleClickAction(
		playbackController: PlaybackController,
		videoPlayerAdapter: VideoPlayerAdapter,
		context: Context,
		view: View,
	) {
		videoPlayerAdapter.leanbackOverlayFragment.setFading(false)
		PopupMenu(context, view, Gravity.END).apply {
			SegmentMode.entries.forEach {
				menu.add(0, it.ordinal, it.ordinal, context.getString(it.nameRes)).apply {
					isChecked = preferences[UserPreferences.skipMode] == it
				}
			}
			menu.setGroupCheckable(0, true, true)

			setOnDismissListener {
				videoPlayerAdapter.leanbackOverlayFragment.setFading(true)
			}

			setOnMenuItemClickListener { item ->
				preferences[UserPreferences.skipMode] = SegmentMode.entries[item.itemId]

				initializeWithIcon(preferences[UserPreferences.skipMode].icon)
				customPlaybackTransportControlGlue1.notifyActionChanged(this@SelectSkipAction)
				true
			}
		}.show()
	}
}
