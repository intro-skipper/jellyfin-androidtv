package org.jellyfin.androidtv.ui.playback

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/*
 * https://stackoverflow.com/a/63302963/461982
 */
abstract class AsyncTaskCoroutine<I> {
	var job: Job? = null
	open fun onPreExecute() {}

	open fun onPostExecute() {}
	abstract fun doInBackground(vararg params: I)

	@OptIn(DelicateCoroutinesApi::class)
	fun <T> execute(vararg input: I) {
		job = GlobalScope.launch(Dispatchers.Main) {
			onPreExecute()
			callAsync(*input)
		}
	}

	@OptIn(DelicateCoroutinesApi::class)
	private suspend fun callAsync(vararg input: I) {
		GlobalScope.async(Dispatchers.IO) {
			 doInBackground(*input)
		}.await()
		GlobalScope.launch(Dispatchers.Main) {
			onPostExecute()
		}
	}
}
