/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package tv.wiinvent.android.wiinvent_androidtv_sample_kotlin.activity

import android.R
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import tv.wiinvent.android.wiinvent_androidtv_sample_kotlin.fragment.PlaybackVFragment
import tv.wiinvent.android.wiinvent_androidtv_sample_kotlin.fragment.PlaybackVideoFragment


/** Loads [PlaybackVideoFragment]. */
class PlaybackActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(
                    android.R.id.content,
                    PlaybackVFragment()
                )
                .commit()
        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_CENTER -> {
                val fm: FragmentManager = supportFragmentManager
                val fragment: PlaybackVFragment =
                    fm.findFragmentById(android.R.id.content) as PlaybackVFragment
                fragment.onEnterBtn()
            }
            else -> { // Note the block
                Log.e("*** pressed ", event.toString())
            }
        }
        return super.onKeyUp(keyCode, event)
    }
}