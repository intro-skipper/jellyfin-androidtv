package org.jellyfin.androidtv.ui.playback.overlay.action

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.PopupMenu
import org.jellyfin.androidtv.R
import org.jellyfin.androidtv.preference.UserPreferences
import org.jellyfin.androidtv.ui.playback.PlaybackController
import org.jellyfin.androidtv.ui.playback.VideoManager
import org.jellyfin.androidtv.ui.playback.overlay.CustomPlaybackTransportControlGlue
import org.jellyfin.androidtv.ui.playback.overlay.VideoPlayerAdapter


class SelectSkipAction(
	context: Context,
	customPlaybackTransportControlGlue: CustomPlaybackTransportControlGlue,
	userPreferences: UserPreferences
) : CustomAction(context, customPlaybackTransportControlGlue) {
	private val preferences = userPreferences
	init {
		initializeWithIcon(R.drawable.ic_select_skip)
	}

	override fun handleClickAction(
		playbackController: PlaybackController,
		videoPlayerAdapter: VideoPlayerAdapter,
		context: Context,
		view: View,
	) {
		videoPlayerAdapter.leanbackOverlayFragment.setFading(false)
		return PopupMenu(context, view, Gravity.END).apply {
			with(menu) {
				add(0, VideoManager.SHOW_SKIP_BUTTON, VideoManager.SHOW_SKIP_BUTTON, context.getString(R.string.lbl_show_skip_button)).apply {
					isChecked = preferences[UserPreferences.skipMode] == VideoManager.SHOW_SKIP_BUTTON
				}

				add(0, VideoManager.AUTO_SKIP, VideoManager.AUTO_SKIP, context.getString(R.string.lbl_auto_skip)).apply {
					isChecked = preferences[UserPreferences.skipMode] == VideoManager.AUTO_SKIP
				}

				add(0, VideoManager.HIDE_SKIP_BUTTON, VideoManager.HIDE_SKIP_BUTTON, context.getString(R.string.lbl_hide_skip_button)).apply {
					isChecked = preferences[UserPreferences.skipMode] == VideoManager.HIDE_SKIP_BUTTON
				}

				setGroupCheckable(0, true, true)
			}

			setOnDismissListener { videoPlayerAdapter.leanbackOverlayFragment.setFading(true) }
			setOnMenuItemClickListener { item ->
				preferences[UserPreferences.skipMode] = item.itemId
				true
			}
		}.show()
	}
}
