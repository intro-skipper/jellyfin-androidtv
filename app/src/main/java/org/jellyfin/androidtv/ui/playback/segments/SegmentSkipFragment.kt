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

	fun handleProgress(currentPosition: Long) {
		// Check if server is full auto
		if ((buttonConfig?.autoSkip == true && buttonConfig?.autoSkipCredits == true) || buttonConfig?.skipButtonVisible == null) {
			button.isVisible = false
			return
		}

		val currentSegment = getCurrentSegment(currentPosition) ?: lastSegment ?: return
		lastSegment = currentSegment

		val isSkipSegment = currentPosition >= currentSegment.showAt.millis && currentPosition < currentSegment.hideAt.millis

		preferences[UserPreferences.skipMode].let { setting ->
			when {
				isSkipSegment && setting == SegmentMode.AUTO_SKIP -> doSkip()
				isSkipSegment && setting == SegmentMode.SHOW_SKIP_BUTTON -> {
					if (!button.isVisible) {
						button.text = when (currentSegment.type) {
							SegmentType.INTRO -> buttonConfig?.skipButtonIntroText
							SegmentType.CREDITS -> buttonConfig?.skipButtonEndCreditsText
							else -> "Skip"
						}
						button.isVisible = true
						button.requestFocus()
					}
				}
				else -> {
					button.isVisible = false
				}
			}
		}
	}

	suspend fun onStartItem(item: BaseItemDto) {
		button.isVisible = false
		segments = getSegments(item.id)
		buttonConfig = getButtonConfig()
	}

	private suspend fun getButtonConfig(): SegmentButtonConfig {
		return api.get<SegmentButtonConfig>(
			pathTemplate = "Intros/UserInterfaceConfiguration",
		).content
	}

	private suspend fun getSegments(itemId: UUID): List<SegmentModel> {
		return api.get<Map<String, SegmentModel>>(
			pathTemplate = "/Episode/{itemId}/IntroSkipperSegments",
			pathParameters = mapOf("itemId" to itemId),
		).content.apply {
			for ((type, segment) in this) {
				segment.type = when (type) {
					"Introduction" -> SegmentType.INTRO
					"Credits" -> SegmentType.CREDITS
					else -> SegmentType.UNKNOWN
				}
			}
		}.values.toList()
	}

	private fun getCurrentSegment(currentPosition: Long): SegmentModel? {
		return segments?.firstOrNull {
			currentPosition >= it.startTime.millis && currentPosition < it.endTime.millis
		}
	}
}
