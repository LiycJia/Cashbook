package cn.wj.android.cashbook.data.config

import android.os.Parcelable
import cn.wj.android.cashbook.base.tools.getSharedBoolean
import cn.wj.android.cashbook.base.tools.getSharedInt
import cn.wj.android.cashbook.base.tools.getSharedLong
import cn.wj.android.cashbook.base.tools.getSharedParcelable
import cn.wj.android.cashbook.base.tools.getSharedString
import cn.wj.android.cashbook.base.tools.setSharedBoolean
import cn.wj.android.cashbook.base.tools.setSharedInt
import cn.wj.android.cashbook.base.tools.setSharedLong
import cn.wj.android.cashbook.base.tools.setSharedParcelable
import cn.wj.android.cashbook.base.tools.setSharedString
import cn.wj.android.cashbook.data.constants.SHARED_KEY_AGREE_USER_AGREEMENT
import cn.wj.android.cashbook.data.constants.SHARED_KEY_AUTO_CHECK_UPDATE
import cn.wj.android.cashbook.data.constants.SHARED_KEY_BACKUP_PATH
import cn.wj.android.cashbook.data.constants.SHARED_KEY_IGNORE_VERSION
import cn.wj.android.cashbook.data.constants.SHARED_KEY_LAST_ASSET_ID
import cn.wj.android.cashbook.data.constants.SHARED_KEY_LAST_BACKUP_MS
import cn.wj.android.cashbook.data.constants.SHARED_KEY_MOBILE_NETWORK_DOWNLOAD_ENABLE
import cn.wj.android.cashbook.data.constants.SHARED_KEY_THEME_MODE
import cn.wj.android.cashbook.data.constants.SHARED_KEY_TYPE_INITIALIZED
import cn.wj.android.cashbook.data.constants.SHARED_KEY_USE_GITEE
import kotlin.reflect.KProperty

/**
 * 应用配置属性
 *
 * > [王杰](mailto:15555650921@163.com) 创建于 2021/8/6
 */

/**
 * 非空配置代理类
 *
 * @param key 属性 key
 * @param default 属性默认值
 */
class NoNullProperties<T>(val key: String, val default: T)

@Suppress("IMPLICIT_CAST_TO_ANY")
inline operator fun <reified T> NoNullProperties<T>.getValue(thisRef: Any?, property: KProperty<*>): T {
    return (when (default) {
        is String -> {
            getSharedString(key, default)
        }
        is Int -> {
            getSharedInt(key, default)
        }
        is Long -> {
            getSharedLong(key, default)
        }
        is Boolean -> {
            getSharedBoolean(key, default)
        }
        is Parcelable -> {
            getSharedParcelable(key, null) ?: default
        }
        else -> {
            default
        }
    } as? T) ?: default
}

inline operator fun <reified T> NoNullProperties<T>.setValue(thisRef: Any?, property: KProperty<*>, value: T) {
    when (value) {
        is String -> {
            setSharedString(key, value)
        }
        is Int -> {
            setSharedInt(key, value)
        }
        is Long -> {
            setSharedLong(key, value)
        }
        is Boolean -> {
            setSharedBoolean(key, value)
        }
        is Parcelable -> {
            setSharedParcelable(key, value)
        }
    }
}

object AppConfigs {

    /** 分类是否初始化 */
    var typeInitialized: Boolean by NoNullProperties(SHARED_KEY_TYPE_INITIALIZED, false)

    /** 上次选择资产 id */
    var lastAssetId: Long by NoNullProperties(SHARED_KEY_LAST_ASSET_ID, 0L)

    /** 是否使用 gitee */
    var useGitee: Boolean by NoNullProperties(SHARED_KEY_USE_GITEE, true)

    /** 是否自动检查更新 */
    var autoUpdate: Boolean by NoNullProperties(SHARED_KEY_AUTO_CHECK_UPDATE, true)

    /** 是否可用使用移动网络下载 */
    var mobileNetworkDownloadEnable: Boolean by NoNullProperties(SHARED_KEY_MOBILE_NETWORK_DOWNLOAD_ENABLE, false)

    /** 主题 mode */
    var themeMode: Int by NoNullProperties(SHARED_KEY_THEME_MODE, -1)

    /** 是否同意用户协议 */
    var agreeUserAgreement: Boolean by NoNullProperties(SHARED_KEY_AGREE_USER_AGREEMENT, false)

    /** 备份路径 */
    var backupPath: String by NoNullProperties(SHARED_KEY_BACKUP_PATH, "")

    /** 上次备份时间 */
    var lastBackupMs: Long by NoNullProperties(SHARED_KEY_LAST_BACKUP_MS, 0L)

    /** 忽略版本号 */
    var ignoreVersion: String by NoNullProperties(SHARED_KEY_IGNORE_VERSION, "")
}


