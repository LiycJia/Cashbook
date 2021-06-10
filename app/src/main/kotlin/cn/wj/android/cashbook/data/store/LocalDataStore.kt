package cn.wj.android.cashbook.data.store

import cn.wj.android.cashbook.base.ext.base.orElse
import cn.wj.android.cashbook.data.database.CashbookDatabase
import cn.wj.android.cashbook.data.database.dao.AssetDao
import cn.wj.android.cashbook.data.database.dao.BooksDao
import cn.wj.android.cashbook.data.database.dao.RecordDao
import cn.wj.android.cashbook.data.database.dao.TypeDao
import cn.wj.android.cashbook.data.database.table.BooksTable
import cn.wj.android.cashbook.data.database.table.RecordTable
import cn.wj.android.cashbook.data.database.table.TypeTable
import cn.wj.android.cashbook.data.entity.AssetClassificationListEntity
import cn.wj.android.cashbook.data.entity.AssetEntity
import cn.wj.android.cashbook.data.entity.BooksEntity
import cn.wj.android.cashbook.data.entity.RecordEntity
import cn.wj.android.cashbook.data.entity.TypeEntity
import cn.wj.android.cashbook.data.enums.AssetClassificationEnum
import cn.wj.android.cashbook.data.enums.ClassificationTypeEnum
import cn.wj.android.cashbook.data.enums.RecordTypeEnum
import cn.wj.android.cashbook.data.enums.TypeEnum
import cn.wj.android.cashbook.data.transform.toAssetEntity
import cn.wj.android.cashbook.data.transform.toAssetTable
import cn.wj.android.cashbook.data.transform.toBooksEntity
import cn.wj.android.cashbook.data.transform.toRecordTable
import cn.wj.android.cashbook.data.transform.toTypeEntity
import cn.wj.android.cashbook.data.transform.toTypeTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 本地数据存储
 *
 * @param database 数据库对象
 *
 * > [王杰](mailto:15555650921@163.com) 创建于 2021/5/19
 */
class LocalDataStore(private val database: CashbookDatabase) {

    /** 账本数据库操作对象 */
    private val booksDao: BooksDao by lazy {
        database.booksDao()
    }

    /** 资产数据库操作对象 */
    private val assetDao: AssetDao by lazy {
        database.assetDao()
    }

    /** 类型数据库操作对象 */
    private val typeDao: TypeDao by lazy {
        database.typeDao()
    }

    /** 记录数据库操作对象 */
    private val recordDao: RecordDao by lazy {
        database.recordDao()
    }

    /** 将账本数据 [books] 插入到数据库中 */
    suspend fun insertBooks(books: BooksEntity) = withContext(Dispatchers.IO) {
        booksDao.insert(books.toAssetTable())
    }

    /** 从数据库中删除 [books] */
    suspend fun deleteBooks(books: BooksEntity) = withContext(Dispatchers.IO) {
        booksDao.delete(books.toAssetTable())
    }

    /** 从数据库中删除 [books] */
    suspend fun updateBooks(vararg books: BooksEntity) = withContext(Dispatchers.IO) {
        val ls = arrayListOf<BooksTable>()
        books.forEach {
            ls.add(it.toAssetTable())
        }
        booksDao.update(*ls.toTypedArray())
    }

    /** 获取名称为 [name] 的账本数量 */
    suspend fun getBooksCountByName(name: String) = withContext(Dispatchers.IO) {
        booksDao.getCountByName(name)
    }

    /** 获取所有账本数据并返回 */
    suspend fun getBooksList(): List<BooksEntity> = withContext(Dispatchers.IO) {
        booksDao.queryAll().map {
            it.toBooksEntity()
        }
    }

    /** 获取并返回默认选中的账本，没有返回 `null` */
    suspend fun getDefaultBooks(): BooksEntity? = withContext(Dispatchers.IO) {
        booksDao.queryDefault().firstOrNull()?.toBooksEntity()
    }

    /** 获取资产分类列表 */
    suspend fun getAssetClassificationList() = withContext(Dispatchers.IO) {
        arrayListOf(
            // 资金账户
            AssetClassificationListEntity(
                classificationType = ClassificationTypeEnum.CAPITAL_ACCOUNT,
                classifications = arrayListOf(*AssetClassificationEnum.CAPITAL_ACCOUNT)
            ),
            // 信用卡账户
            AssetClassificationListEntity(
                classificationType = ClassificationTypeEnum.CREDIT_CARD_ACCOUNT,
                classifications = arrayListOf(*AssetClassificationEnum.CREDIT_CARD_ACCOUNT)
            ),
            // 充值账户
            AssetClassificationListEntity(
                classificationType = ClassificationTypeEnum.TOP_UP_ACCOUNT,
                classifications = arrayListOf(*AssetClassificationEnum.TOP_UP_ACCOUNT)
            ),
            // 投资理财账户
            AssetClassificationListEntity(
                classificationType = ClassificationTypeEnum.INVESTMENT_FINANCIAL_ACCOUNT,
                classifications = arrayListOf(*AssetClassificationEnum.INVESTMENT_FINANCIAL_ACCOUNT)
            ),
            // 债务
            AssetClassificationListEntity(
                classificationType = ClassificationTypeEnum.DEBT_ACCOUNT,
                classifications = arrayListOf(*AssetClassificationEnum.DEBT_ACCOUNT)
            )
        )
    }

    /** 获取银行列表 */
    suspend fun getBankList() = withContext(Dispatchers.IO) {
        arrayListOf(*AssetClassificationEnum.BANK_LIST)
    }

    /** 插入新的资产 [asset] 到数据库 */
    suspend fun insertAsset(asset: AssetEntity) = withContext(Dispatchers.IO) {
        // 插入资产
        val assetId = assetDao.insert(asset.toAssetTable())
        // 插入余额记录
        recordDao.insert(RecordTable.newModifyBalance(assetId, asset.balance))
        assetId
    }

    /** 更新资产 [asset] 到数据库 */
    suspend fun updateAsset(asset: AssetEntity, balanceChanged: Boolean = false) = withContext(Dispatchers.IO) {
        assetDao.update(asset.toAssetTable())
        if (balanceChanged) {
            // 插入余额记录
            recordDao.insert(RecordTable.newModifyBalance(asset.id, asset.balance))
        }
    }

    /** 根据账本id [booksId] 获取资产数据并返回 */
    suspend fun getAssetListByBooksId(booksId: Long): List<AssetEntity> = withContext(Dispatchers.IO) {
        assetDao.queryByBooksId(booksId).map {
            // 获取余额
            val balance = getAssetBalanceById(it.id)
            it.toAssetEntity(balance)
        }
    }

    /** 根据账本id [booksId] 获取资产数据并返回 */
    suspend fun queryMaxSortByBooksId(booksId: Long): Int? = withContext(Dispatchers.IO) {
        assetDao.queryMaxSortByBooksId(booksId)
    }

    /** 根据账本id [booksId] 获取隐藏资产数据并返回 */
    suspend fun getInvisibleAssetListByBooksId(booksId: Long): List<AssetEntity> = withContext(Dispatchers.IO) {
        assetDao.queryInvisibleByBooksId(booksId).map {
            //  获取余额
            val balance = getAssetBalanceById(it.id)
            it.toAssetEntity(balance)
        }
    }

    /** 根据账本id [booksId] 获取未隐藏资产数据并返回 */
    suspend fun getVisibleAssetListByBooksId(booksId: Long): List<AssetEntity> = withContext(Dispatchers.IO) {
        assetDao.queryVisibleByBooksId(booksId).map {
            //  获取余额
            val balance = getAssetBalanceById(it.id)
            it.toAssetEntity(balance)
        }
    }

    /** 返回数据库是否存在类型数据 */
    suspend fun hasType(): Boolean = withContext(Dispatchers.IO) {
        typeDao.getCount() > 0
    }

    /** 将 [type] 插入数据库并返回 id */
    suspend fun insertType(type: TypeEntity): Long = withContext(Dispatchers.IO) {
        typeDao.insert(type.toTypeTable())
    }

    /** 将 [types] 插入数据库 */
    suspend fun insertTypes(vararg types: TypeEntity) = withContext(Dispatchers.IO) {
        val ls = arrayListOf<TypeTable>()
        types.forEach {
            ls.add(it.toTypeTable())
        }
        typeDao.insert(*ls.toTypedArray())
    }

    /** 清空类型数据 */
    suspend fun clearTypes() = withContext(Dispatchers.IO) {
        typeDao.deleteAll()
    }

    /** 查询并返回记录类型为 [type] 的类型数据列表 */
    suspend fun getTypeListByType(type: RecordTypeEnum): List<TypeEntity> = withContext(Dispatchers.IO) {
        typeDao.queryByPosition(TypeEnum.FIRST.name, type.position).map { first ->
            first.toTypeEntity().copy(
                childList = typeDao.queryByParentId(TypeEnum.SECOND.name, first.id.orElse(-1L)).map { second ->
                    second.toTypeEntity()
                }.sortedBy {
                    it.sort
                }
            )
        }.sortedBy {
            it.sort
        }
    }

    /** 获取 id 为 [assetId] 的资产余额 */
    suspend fun getAssetBalanceById(assetId: Long?): String = withContext(Dispatchers.IO) {
        if (null == assetId) {
            return@withContext "0"
        }
        val modifyList = recordDao.queryLastModifyRecord(assetId)
        if (modifyList.isEmpty()) {
            return@withContext "0"
        }
        // 获取最后一条修改数据
        val lastModify = modifyList.first()
        // 获取在此之后的所有记录
        var result = lastModify.amount.toFloatOrNull() ?: 0f
        val recordList = recordDao.queryAfterRecordTime(assetId, lastModify.recordTime)
        recordList.forEach {
            when (it.type) {
                RecordTypeEnum.INCOME.name -> {
                    // 收入
                    result += it.amount.toFloatOrNull() ?: 0f
                }
                RecordTypeEnum.EXPENDITURE.name -> {
                    // 支出
                    result -= it.amount.toFloatOrNull() ?: 0f
                }
                RecordTypeEnum.TRANSFER.name -> {
                    // 转账
                    result -= it.charge.toFloatOrNull() ?: 0f
                }
            }
        }
        result.toString()
    }

    /** 将记录 [record] 插入到数据库并返回生成的主键 id */
    suspend fun insertRecord(record: RecordEntity): Long = withContext(Dispatchers.IO) {
        recordDao.insert(record.toRecordTable())
    }
}