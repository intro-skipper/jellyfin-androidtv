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
abstract class AsyncTaskCoroutine<I, O> {
	var job: Job? = null
	var result: O? = null
	open fun onPreExecute() {}

	open fun onPostExecute(result: O?) {}
	abstract fun doInBackground(vararg params: I): O

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
			result = doInBackground(*input)
		}.await()
		GlobalScope.launch(Dispatchers.Main) {
			onPostExecute(result)
		}
	}
}
