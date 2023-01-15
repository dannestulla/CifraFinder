package br.gohan.cifrafinder.model

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class CifraScheduler(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {
    override fun doWork(): Result {
        return Result.success()
    }
}