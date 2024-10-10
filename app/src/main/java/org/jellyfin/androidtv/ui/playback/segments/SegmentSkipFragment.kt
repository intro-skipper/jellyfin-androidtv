package org.jellyfin.androidtv.ui.playback.segments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import org.jellyfin.androidtv.R
import org.jellyfin.androidtv.preference.UserPreferences
import org.jellyfin.androidtv.ui.playback.PlaybackControllerContainer
import org.jellyfin.sdk.api.client.ApiClient
import org.jellyfin.sdk.api.client.extensions.get
import org.jellyfin.sdk.model.UUID
import org.jellyfin.sdk.model.api.BaseItemDto
import org.koin.android.ext.android.inject
import org.koin.java.KoinJavaComponent.get

val Number.millis
    get() = this.toLong() * 1000L

class SegmentSkipFragment : Fragment() {

	private val preferences = get<UserPreferences>(UserPreferences::class.java)

	private val api: ApiClient by inject()
	private val playbackControllerContainer: PlaybackControllerContainer by inject()

	private lateinit var button: Button
	private var segments: List<SegmentModel>? = null
	private var buttonConfig: SegmentButtonConfig? = null
	private var lastSegment: SegmentModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_segment_skip, container, false)
    }

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		button = view.findViewById<Button>(R.id.skip_segment_button).apply {
			setOnClickListener {
				doSkip()
			}
		}
	}

	private fun doSkip() {
		lastSegment?.let { segment ->
			playbackControllerContainer.playbackController?.run {
				if ((segment.endTime + 3).millis > getDuration() && hasNextItem()) {
					next()
				} else {
					seek(segment.endTime.millis)
				}
			}
		}
	}

	private fun updateButtonText(segment: SegmentModel) {
		button.text = when (segment.type) {
			SegmentType.INTRO -> buttonConfig?.skipButtonIntroText
			SegmentType.CREDITS -> buttonConfig?.skipButtonEndCreditsText
			else -> "Skip"
		}
	}

	fun handleProgress(currentPosition: Long) {
		val currentSegment = getCurrentSegment(currentPosition) ?: lastSegment ?: return
		lastSegment = currentSegment

		val isSkipSegment = currentPosition >= currentSegment.showAt.millis && currentPosition < currentSegment.hideAt.millis

		if (!button.isVisible && isSkipSegment && preferences[UserPreferences.skipMode] == SegmentMode.SHOW_SKIP_BUTTON
			&& buttonConfig?.skipButtonVisible == true) {
			button.isVisible = true
			updateButtonText(currentSegment)
			button.requestFocus()
		} else if (button.isVisible && (!isSkipSegment || preferences[UserPreferences.skipMode] != SegmentMode.SHOW_SKIP_BUTTON)) {
			button.isVisible = false
		}

		if (isSkipSegment && preferences[UserPreferences.skipMode] == SegmentMode.AUTO_SKIP) {
			doSkip()
		}
	}

	suspend fun onStartItem(item: BaseItemDto) {
		button.visibility = View.GONE
		segments = getSegments(item.id)
		buttonConfig = getButtonConfig()
	}

	private suspend fun getButtonConfig(): SegmentButtonConfig {
		return api.get<SegmentButtonConfig>(
			pathTemplate = "Intros/UserInterfaceConfiguration",
		).content
	}

	private suspend fun getSegments(itemId: UUID): List<SegmentModel> {
		val segmentsMap = api.get<Map<String, SegmentModel>>(
			pathTemplate = "/Episode/{itemId}/IntroSkipperSegments",
			pathParameters = mapOf("itemId" to itemId),
		).content

		for ((type, segment) in segmentsMap) {
			segment.type = when (type) {
				"Introduction" -> SegmentType.INTRO
				"Credits" -> SegmentType.CREDITS
				else -> SegmentType.UNKNOWN
			}
		}

		return segmentsMap.values.toList()
	}

	private fun getCurrentSegment(currentPosition: Long): SegmentModel? {
		return segments?.firstOrNull {
			currentPosition >= it.startTime.millis && currentPosition < it.endTime.millis
		}
	}
}
