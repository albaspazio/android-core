/* =================================================================================================
Part of android-core module

https://github.com/albaspazio/android-core

Author: Alberto Inuggi
Copyright (©) 2019-2023
==================================================================================================*/

package org.albaspazio.core.updater

/**
 * Created by LuoWen on 2015/12/14.
 */
interface Constants {
    companion object {
        const val DOWNLOAD = 1
        const val DOWNLOAD_FINISH = 2
        const val DOWNLOAD_CLICK_START = 3

        const val VERSION_COMPARE_END = 200   // start to compare version
        const val VERSION_UP_TO_UPDATE = 202    // version up to date
        const val UPDATE_CANCELLED = 204        // update cancelled

        const val VERSION_PARSE_FAIL = 301    // version-xml file parse fail

        const val REMOTE_FILE_NOT_FOUND = 404
        const val NETWORK_ERROR = 405
        const val TIMEOUT_ERROR = 406
        const val CONNECTION_ERROR = 407

        const val NO_SUCH_METHOD = 501

        const val NETWORK_ABSENT = 601

        const val UNKNOWN_ERROR = 901
    }
}