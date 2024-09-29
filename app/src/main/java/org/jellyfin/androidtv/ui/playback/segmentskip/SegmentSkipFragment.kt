package org.jellyfin.androidtv.ui.playback.segmentskip

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import org.jellyfin.androidtv.R
import org.jellyfin.androidtv.ui.playback.PlaybackControllerContainer
import org.jellyfin.sdk.api.client.ApiClient
import org.jellyfin.sdk.api.client.extensions.get
import org.jellyfin.sdk.model.UUID
import org.jellyfin.sdk.model.api.BaseItemDto
import org.koin.android.ext.android.inject

class SegmentSkipFragment() : Fragment() {

	private val api: ApiClient by inject()
	private val playbackControllerContainer: PlaybackControllerContainer by inject()

	private lateinit var button: Button
	private var segments: List<SegmentModel>? = null
	private var lastSegment: SegmentModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_segment_skip, container, false)
    }

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		button = view.findViewById(R.id.skip_segment_button)
		button.setOnClickListener {
			buttonClicked()
		}
	}

	private fun buttonClicked() {
		lastSegment?.let { segment ->
			playbackControllerContainer.playbackController?.seek((segment.endTime * 1000).toLong())
		}
	}

	fun handleProgress(currentPosition: Long) {
		val currentSegment = getCurrentSegment(currentPosition) ?: lastSegment ?: return
		lastSegment = currentSegment

		if (currentPosition >= currentSegment.showAt * 1000 && currentPosition < currentSegment.hideAt * 1000 &&
			button.visibility != View.VISIBLE
		) {
			button.visibility = View.VISIBLE
			button.requestFocus()
		}

		if ((currentPosition < currentSegment.showAt * 1000 || currentPosition >= currentSegment.hideAt * 1000) &&
			button.visibility == View.VISIBLE
		) {
			button.visibility = View.GONE
		}
	}

	suspend fun onStartItem(item: BaseItemDto) {
		button.visibility = View.GONE
		segments = getSegments(item.id)
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
			currentPosition >= it.startTime * 1000 && currentPosition < it.endTime * 1000
		}
	}
}
