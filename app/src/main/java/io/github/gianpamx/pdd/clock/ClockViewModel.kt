package io.github.gianpamx.pdd.clock

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import io.github.gianpamx.pdd.domain.NextState
import io.github.gianpamx.pdd.domain.ObserveState
import io.github.gianpamx.pdd.domain.ObserveState.State
import io.github.gianpamx.pdd.domain.ObserveState.State.Idle.time
import io.github.gianpamx.pdd.domain.entity.Action
import io.github.gianpamx.pdd.toClock
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class ClockViewModel @Inject constructor(
    observeState: ObserveState,
    private val nextState: NextState,
    private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {
    private val errorChannel = BroadcastChannel<Throwable>(Channel.CONFLATED)

    val errors = errorChannel.asFlow()

    val viewState: LiveData<ClockViewState> = observeState()
        .map { it.toViewState() }
        .flowOn(defaultDispatcher)
        .asLiveData(viewModelScope.coroutineContext)

    fun start() = launchNextState(Action.START)

    fun stop() = launchNextState(Action.STOP)

    fun take() = launchNextState(Action.TAKE)

    private fun launchNextState(action: Action) {
        viewModelScope.launch(defaultDispatcher) {
            try {
                nextState(action)
            } catch (t: Throwable) {
                errorChannel.send(t)
            }
        }
    }

    private fun State.toViewState(): ClockViewState = when (this) {
        is State.Idle -> ClockViewState.Idle(time.toClock())
        is State.Pomodoro -> ClockViewState.Pomodoro(time.toClock())
        is State.Done -> ClockViewState.Done(0.toClock())
        is State.Break -> ClockViewState.Break(time.toClock())
    }
}
