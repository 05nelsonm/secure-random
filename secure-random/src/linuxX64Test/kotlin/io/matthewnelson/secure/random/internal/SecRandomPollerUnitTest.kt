/*
 * Copyright (c) 2023 Matthew Nelson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/
package io.matthewnelson.secure.random.internal

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.test.runTest
import kotlin.native.concurrent.AtomicInt
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class SecRandomPollerUnitTest {

    private class TestPoller: SecRandomPoller() {
        private val lock = Mutex(locked = true)
        val count = AtomicInt(0)
        val calledBeforeCompletion = AtomicInt(0)

        suspend fun pollFailIfInvoked() {
            val calledBefore = count.value == 0

            lock.withLock {  }

            pollingResult { _, _ ->
                throw IllegalArgumentException("Polling was allowed to be invoked multiple times")
            }

            if (calledBefore) {
                calledBeforeCompletion.increment()
            }
        }

        fun pollTrigger() {
            pollingResult { _, _ ->

                lock.unlock()

                runBlocking {
                    delay(500L)
                }

                true
            }

            count.increment()
        }
    }

    @Test
    fun givenPollingResult_whenMultipleInvocations_thenOnlyPollsOnce() = runTest {
        val poller = TestPoller()

        val jobs = mutableListOf<Job>()

        val repeatTimes = 100
        repeat(repeatTimes) {

            launch {
                poller.pollFailIfInvoked()
            }.let { job ->
                jobs.add(job)
            }
        }

        delay(500L)

        launch(Dispatchers.Default) {
            poller.pollTrigger()
        }.join()

        for (job in jobs) {
            job.join()
        }


        assertEquals(1, poller.count.value)
        assertEquals(repeatTimes, poller.calledBeforeCompletion.value)
    }
}

