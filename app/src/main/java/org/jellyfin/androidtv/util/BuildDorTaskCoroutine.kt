package org.jellyfin.androidtv.util

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/*
 * https://stackoverflow.com/a/63302963/461982
 */
abstract class BuildDorTaskCoroutine<I, O> {
	var job: Job? = null
	var result: O? = null

	open fun onPostExecute(result: O?) {}
	abstract fun doInBackground(vararg params: I): O

	@OptIn(DelicateCoroutinesApi::class)
	fun <T> execute(vararg input: I) {
		job = GlobalScope.launch(Dispatchers.Main) {
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
