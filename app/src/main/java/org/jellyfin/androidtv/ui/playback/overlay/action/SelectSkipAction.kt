package org.jellyfin.androidtv.ui.playback.overlay.action

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.PopupMenu
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

	init {
		initializeWithIcon(preferences[UserPreferences.skipMode].icon())
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
				menu.add(0, it.ordinal, it.ordinal, context.getString(it.label())).apply {
					isChecked = preferences[UserPreferences.skipMode] == it
				}
			}
			menu.setGroupCheckable(0, true, true)

			setOnDismissListener {
				videoPlayerAdapter.leanbackOverlayFragment.setFading(true)
			}

			setOnMenuItemClickListener { item ->
				preferences[UserPreferences.skipMode] = SegmentMode.entries[item.itemId]

				initializeWithIcon(preferences[UserPreferences.skipMode].icon())
				customPlaybackTransportControlGlue1.notifyActionChanged(this@SelectSkipAction)
				true
			}
		}.show()
	}
}
