package io.github.gianpamx.pdd.clock

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.github.gianpamx.MainCoroutineRule
import io.github.gianpamx.observeForTesting
import io.github.gianpamx.pdd.domain.ObserveState
import io.github.gianpamx.pdd.domain.ObserveState.State
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

@ExperimentalCoroutinesApi
class ClockViewModelTest {
    private val observeState: ObserveState = mock()

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    @Test
    fun `Idle state`() = coroutineRule.testDispatcher.runBlockingTest {
        whenever(observeState.invoke()).thenReturn(flowOf(State.Idle))

        val viewModel = ClockViewModel(observeState, coroutineRule.testDispatcher)

        viewModel.viewState.observeForTesting {
            assertThat(viewModel.viewState.value).isInstanceOf(ClockViewState.Idle::class.java)
        }
    }

    @Test
    fun `Pomodoro state`() = coroutineRule.testDispatcher.runBlockingTest {
        whenever(observeState.invoke()).thenReturn(flowOf(State.Pomodoro(0)))

        val viewModel = ClockViewModel(observeState, coroutineRule.testDispatcher)

        viewModel.viewState.observeForTesting {
            assertThat(viewModel.viewState.value).isInstanceOf(ClockViewState.Pomodoro::class.java)
        }
    }

    @Test
    fun `Done state`() = coroutineRule.testDispatcher.runBlockingTest {
        whenever(observeState.invoke()).thenReturn(flowOf(State.Done))

        val viewModel = ClockViewModel(observeState, coroutineRule.testDispatcher)

        viewModel.viewState.observeForTesting {
            assertThat(viewModel.viewState.value).isInstanceOf(ClockViewState.Done::class.java)
        }
    }

    @Test
    fun `Break state`() = coroutineRule.testDispatcher.runBlockingTest {
        whenever(observeState.invoke()).thenReturn(flowOf(State.Break(0)))

        val viewModel = ClockViewModel(observeState, coroutineRule.testDispatcher)

        viewModel.viewState.observeForTesting {
            assertThat(viewModel.viewState.value).isInstanceOf(ClockViewState.Break::class.java)
        }
    }

    @Test
    fun `0 seconds clock`() = coroutineRule.testDispatcher.runBlockingTest {
        whenever(observeState.invoke()).thenReturn(flowOf(State.Pomodoro(0)))

        val viewModel = ClockViewModel(observeState, coroutineRule.testDispatcher)

        viewModel.viewState.observeForTesting {
            assertThat(viewModel.viewState.value?.clock).isEqualTo("0:00")
        }
    }

    @Test
    fun `60 seconds clock`() = coroutineRule.testDispatcher.runBlockingTest {
        whenever(observeState.invoke()).thenReturn(flowOf(State.Pomodoro(60)))

        val viewModel = ClockViewModel(observeState, coroutineRule.testDispatcher)

        viewModel.viewState.observeForTesting {
            assertThat(viewModel.viewState.value?.clock).isEqualTo("1:00")
        }
    }

    @Test
    fun `25 minutes clock`() = coroutineRule.testDispatcher.runBlockingTest {
        whenever(observeState.invoke()).thenReturn(flowOf(State.Pomodoro(25 * 60)))

        val viewModel = ClockViewModel(observeState, coroutineRule.testDispatcher)

        viewModel.viewState.observeForTesting {
            assertThat(viewModel.viewState.value?.clock).isEqualTo("25:00")
        }
    }
}
