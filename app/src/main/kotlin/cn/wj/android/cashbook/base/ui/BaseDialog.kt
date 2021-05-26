@file:Suppress("MemberVisibilityCanBePrivate")

package cn.wj.android.cashbook.base.ui

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import cn.wj.android.cashbook.BR
import cn.wj.android.cashbook.R
import cn.wj.android.cashbook.base.ext.base.logger
import cn.wj.android.cashbook.base.ext.base.tag
import cn.wj.android.cashbook.data.model.SnackbarModel
import com.alibaba.android.arouter.launcher.ARouter
import com.google.android.material.snackbar.Snackbar
import com.gyf.immersionbar.ImmersionBar
import com.gyf.immersionbar.ktx.immersionBar

/**
 * 应用 [DialogFragment] 基类
 * - [VM] 为 [BaseViewModel] 泛型，[DB] 为 [ViewDataBinding] 泛型
 *
 * > [jiewang41](mailto:jiewang41@iflytek.com) 创建于 20201/3/8
 */
abstract class BaseDialog<VM : BaseViewModel, DB : ViewDataBinding> :
    DialogFragment() {

    /** 布局 id */
    protected abstract val layoutResId: Int

    /** 界面 [BaseViewModel] 对象 */
    protected abstract val viewModel: VM

    /** 界面 [ViewDataBinding] 对象 */
    protected lateinit var binding: DB

    /** [Snackbar] 转换接口 */
    protected var snackbarTransform: ((SnackbarModel) -> SnackbarModel)? = null

    /** Dialog 隐藏回调 */
    private var onDialogDismissListener: OnDialogDismissListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 设置样式
        setStyle(STYLE_NO_TITLE, R.style.Theme_Cashbook_Dialog)

        // 订阅基本数据
        observeBaseModel()

        // 订阅数据
        observe()
        logger().d("onCreate")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // 取消标题栏
//        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)

        // 加载布局
        binding = DataBindingUtil.inflate(inflater, layoutResId, container, false)

        // 绑定生命周期管理
        binding.lifecycleOwner = this

        // 绑定 ViewModel
        binding.setVariable(BR.viewModel, viewModel)

        // 初始化状态栏工具
        initImmersionbar()

        // 初始化布局
        initView()

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        logger().d("onStart")
    }

    override fun onResume() {
        super.onResume()
        logger().d("onResume")
    }

    override fun onPause() {
        super.onPause()
        logger().d("onPause")
    }

    override fun onStop() {
        super.onStop()
        logger().d("onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        logger().d("onDestroy")
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDialogDismissListener?.invoke()
    }

    override fun setCancelable(cancelable: Boolean) {
        super.setCancelable(cancelable)
        dialog?.setCanceledOnTouchOutside(cancelable)
    }

    /** 设置 Dialog 隐藏回调 [listener] */
    fun setOnDialogDismissListener(listener: OnDialogDismissListener?) {
        onDialogDismissListener = listener
    }

    /** 订阅数据 */
    protected open fun observe() {
    }

    /** 初始化状态栏相关配置 */
    protected open fun initImmersionbar(immersionBar: ImmersionBar) {
    }

    /** 初始化状态栏相关配置 */
    private fun initImmersionbar() {
        immersionBar {
            // 同步所在 Activity 状态栏
            getTag(requireActivity().tag)
            initImmersionbar(this)
        }
    }

    /** 订阅基本数据 */
    private fun observeBaseModel() {
        // snackbar 提示
        viewModel.snackbarData.observe(this, Observer {
            if (it.content.isNullOrBlank()) {
                return@Observer
            }

            // 转换处理
            val model = snackbarTransform?.invoke(it) ?: it
            logger().d("showSnackbar: $model")
            val view = if (model.targetId == 0) {
                binding.root
            } else {
                binding.root.findViewById(model.targetId)
            }
            val snackBar = Snackbar.make(view, model.content.orEmpty(), model.duration)
            snackBar.setTextColor(model.contentColor)
            snackBar.setBackgroundTint(model.contentBgColor)
            if (model.actionText != null && model.onAction != null) {
                snackBar.setAction(model.actionText, model.onAction)
                snackBar.setActionTextColor(model.actionColor)
            }
            if (model.onCallback != null) {
                snackBar.addCallback(model.onCallback)
            }
            snackBar.show()
        })
        // Ui 界面处理
        viewModel.uiNavigationData.observe(this, {
            logger().d("uiNavigation: $it")
            it.jump?.let { model ->
                ARouter.getInstance().build(model.path).with(model.data).navigation(context)
            }
            it.close?.let { model ->
                dismiss()
                if (model.both) {
                    requireActivity().run {
                        if (null == model.result) {
                            setResult(model.resultCode)
                        } else {
                            setResult(model.resultCode, Intent().putExtras(model.result))
                        }
                        finish()
                    }
                }

            }
        })
    }

    /**
     * 初始化布局
     */
    abstract fun initView()
}

/** 弹窗隐藏回调 */
typealias OnDialogDismissListener = () -> Unit