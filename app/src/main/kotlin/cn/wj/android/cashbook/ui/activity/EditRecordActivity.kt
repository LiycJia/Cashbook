package cn.wj.android.cashbook.ui.activity

import android.os.Bundle
import cn.wj.android.cashbook.R
import cn.wj.android.cashbook.base.ui.BaseActivity
import cn.wj.android.cashbook.data.constants.ROUTE_PATH_EDIT_RECORD
import cn.wj.android.cashbook.databinding.ActivityEditRecordBinding
import cn.wj.android.cashbook.ui.adapter.EditRecordVpAdapter
import cn.wj.android.cashbook.ui.viewmodel.EditRecordViewModel
import com.alibaba.android.arouter.facade.annotation.Route
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * 编辑记录界面
 *
 * > [王杰](mailto:15555650921@163.com) 创建于 2021/5/28
 */
@Route(path = ROUTE_PATH_EDIT_RECORD)
class EditRecordActivity : BaseActivity<EditRecordViewModel, ActivityEditRecordBinding>() {

    override val viewModel: EditRecordViewModel by viewModel()

    /** 消费类型 ViewPager 适配器 */
    private val typesVpAdapter: EditRecordVpAdapter by lazy {
        EditRecordVpAdapter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_record)

        // 配置 ViewPager2
        binding.vpType.adapter = typesVpAdapter

    }
}