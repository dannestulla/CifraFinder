package br.gohan.cifrafinder.domain.usecase

import android.content.Context
import androidx.work.*
import br.gohan.cifrafinder.domain.model.SongData
import org.koin.core.component.KoinComponent
import java.util.concurrent.TimeUnit

class CifraScheduler(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams), KoinComponent {
    override suspend fun doWork(): Result {
        return Result.success()
    }
}

fun setAutoRefresh(songData: SongData, workManager: WorkManager, refresh: () -> Unit) {
    val refreshTime =  (songData.durationMs - songData.progressMs) + 5000L
    val wakeUpSchedule = OneTimeWorkRequestBuilder<CifraScheduler>()
        .setInitialDelay(refreshTime, TimeUnit.MILLISECONDS)
        .build()

    workManager.enqueue(wakeUpSchedule)

    val workId = wakeUpSchedule.id

    workManager.getWorkInfoByIdLiveData(workId).observeForever {
        if (it.state.isFinished) {
            refresh.invoke()
        }
    }
}