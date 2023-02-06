package br.gohan.cifrafinder.domain.usecase

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import br.gohan.cifrafinder.domain.model.CurrentSongModel

class CifraScheduler(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {
    override fun doWork(): Result {
        return Result.success()
    }
    /*
    private fun setSongRefreshCycle(args: CurrentSongModel) {
        val workId = createRefreshSchedule(getRemainingTime(args.durationMs, args.progressMs))
        setObservers(workId)
    }

    private fun getRemainingTime(durationMs: Long, progressMs: Long): Long =
        (durationMs - progressMs) + 5000L

    private fun setObservers(workId: UUID) {
        workManager.getWorkInfoByIdLiveData(workId).observe(viewLifecycleOwner) {
            if (it.state.isFinished) {
                viewModel.getCurrentlyPlaying()
            }
        }
    }

    private fun createRefreshSchedule(refreshTime: Long): UUID {
        val wakeUpSchedule = OneTimeWorkRequestBuilder<CifraScheduler>()
            .setInitialDelay(refreshTime, TimeUnit.MILLISECONDS)
            .build()
        workManager.enqueue(wakeUpSchedule)
        return wakeUpSchedule.id
    }*/
}