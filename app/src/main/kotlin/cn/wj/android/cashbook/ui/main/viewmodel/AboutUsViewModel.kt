package cn.wj.android.cashbook.ui.main.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cn.wj.android.cashbook.R
import cn.wj.android.cashbook.base.ext.base.logger
import cn.wj.android.cashbook.base.ext.base.string
import cn.wj.android.cashbook.base.ui.BaseViewModel
import cn.wj.android.cashbook.data.constants.EMAIL_ADDRESS
import cn.wj.android.cashbook.data.constants.GITEE_HOMEPAGE
import cn.wj.android.cashbook.data.constants.GITHUB_HOMEPAGE
import cn.wj.android.cashbook.data.entity.GiteeReleaseEntity
import cn.wj.android.cashbook.data.model.UiNavigationModel
import cn.wj.android.cashbook.data.store.WebDataStore
import cn.wj.android.cashbook.data.transform.toSnackbarModel
import cn.wj.android.cashbook.manager.UpdateManager
import kotlinx.coroutines.launch

/**
 * 关于我们 ViewModel
 *
 * > [王杰](mailto:15555650921@163.com) 创建于 2021/6/17
 */
class AboutUsViewModel(private val web: WebDataStore) : BaseViewModel() {

    /** 显示升级提示弹窗 */
    val showUpdateDialogData: MutableLiveData<GiteeReleaseEntity> = MutableLiveData()

    /** 跳转发送邮件数据 */
    val jumpSendEmailData: MutableLiveData<String> = MutableLiveData()

    /** 跳转浏览器打开数据 */
    val jumpBrowserData: MutableLiveData<String> = MutableLiveData()

    /** 返回按钮点击 */
    val onBackClick: () -> Unit = {
        // 退出当前界面
        uiNavigationData.value = UiNavigationModel.builder {
            close()
        }
    }

    /** 联系我点击 */
    val onContactMeClick: () -> Unit = {
        jumpSendEmailData.value = EMAIL_ADDRESS
    }

    /** Github 点击 */
    val onGithubClick: () -> Unit = {
        jumpBrowserData.value = GITHUB_HOMEPAGE
    }

    /** Gitee 点击 */
    val onGiteeClick: () -> Unit = {
        jumpBrowserData.value = GITEE_HOMEPAGE
    }

    /** 检查更新点击 */
    val onCheckUpdateClick: () -> Unit = {
        checkUpdate()
    }

    /** TODO 用户协议和隐私协议点击 */
    val onUserAgreementAndPrivacyPolicyClick: () -> Unit = {
        snackbarData.value = "用户协议和隐私协议".toSnackbarModel()
    }

    /** 检查更新 */
    private fun checkUpdate() {
        viewModelScope.launch {
            try {
                // 获取 Release 信息
                val info = web.giteeQueryLatestRelease()
                UpdateManager.checkFromInfo(info, {
                    // 显示升级提示弹窗
                    showUpdateDialogData.value = info
                }, {
                    // 不需要升级
                    snackbarData.value = R.string.it_is_the_latest_version.string.toSnackbarModel()
                })
            } catch (throwable: Throwable) {
                logger().e(throwable, "checkUpdate")
            }
        }
    }
}