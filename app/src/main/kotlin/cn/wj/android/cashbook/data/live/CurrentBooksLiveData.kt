package cn.wj.android.cashbook.data.live

import androidx.lifecycle.MutableLiveData
import cn.wj.android.cashbook.data.entity.BooksEntity

/**
 * 当前选中账本数据
 *
 * > [王杰](mailto:15555650921@163.com) 创建于 2021/5/19
 */
object CurrentBooksLiveData : MutableLiveData<BooksEntity>() {

    val name: String
        get() = value?.name.orEmpty()
}