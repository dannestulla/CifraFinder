package br.gohan.cifrafinder.domain.usecase

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import org.koin.core.component.KoinComponent

class CifraScheduler(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams), KoinComponent {
    override suspend fun doWork(): Result {
        return Result.success()
    }
}

/*
fun setAutoRefresh(songData: SongData, workManager: WorkManager, refresh: () -> Unit) {
    val refreshTime = (songData.durationMs - songData.progressMs) + 5000L
    val wakeUpSchedule = OneTimeWorkRequestBuilder<CifraScheduler>()
        .setInitialDelay(refreshTime, TimeUnit.MILLISECONDS)
        .build()

    workManager.enqueue(wakeUpSchedule)

    val workId = wakeUpSchedule.id

    workManager.getWorkInfoByIdLiveData(workId).observeForever {
        if (it?.state?.isFinished == true) {
            refresh.invoke()
        }
    }
}*/
