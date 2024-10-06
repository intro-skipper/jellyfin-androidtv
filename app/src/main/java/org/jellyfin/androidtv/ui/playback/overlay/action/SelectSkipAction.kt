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
import org.jellyfin.androidtv.ui.playback.segments.SegmentSkipType
import org.jellyfin.androidtv.ui.playback.segments.SkipTypeController


class SelectSkipAction(
	context: Context,
	customPlaybackTransportControlGlue: CustomPlaybackTransportControlGlue,
	userPreferences: UserPreferences
) : CustomAction(context, customPlaybackTransportControlGlue) {
	private val skipTypeController = SkipTypeController(userPreferences[UserPreferences.skipMode], userPreferences)
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
				add(0, 0, 0, "Show Button").apply {
					isChecked = skipTypeController.currentSkipType == SegmentSkipType.ShowButton
				}

				add(0, 1, 1, "Auto Skip").apply {
					isChecked = skipTypeController.currentSkipType == SegmentSkipType.AutoSkip
				}

				add(0, 2, 2, "Hidden").apply {
					isChecked = skipTypeController.currentSkipType == SegmentSkipType.Hidden
				}

				setGroupCheckable(0, true, true)
			}

			setOnDismissListener { videoPlayerAdapter.leanbackOverlayFragment.setFading(true) }
			setOnMenuItemClickListener { item ->
				when (item.itemId) {
					0 -> skipTypeController.setSkipType(SegmentSkipType.ShowButton)
					1 -> skipTypeController.setSkipType(SegmentSkipType.AutoSkip)
					2 -> skipTypeController.setSkipType(SegmentSkipType.Hidden)
				}
				true
			}
		}.show()
	}
}
