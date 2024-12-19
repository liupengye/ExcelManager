<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getTableData, updateCell } from '../api/excel'

const route = useRoute()
const router = useRouter()
const tableId = route.params.id
const tableData = ref([])
const headers = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)

// 编辑弹窗相关
const dialogVisible = ref(false)
const editingValue = ref('')
const editingRow = ref(null)
const editingColumn = ref('')

const loadTableData = async () => {
  try {
    loading.value = true
    const data = await getTableData(tableId, currentPage.value, pageSize.value)
    tableData.value = data.records
    total.value = data.total
    if (data.records.length > 0) {
      headers.value = Object.keys(data.records[0]).filter(key => key !== 'id')
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  loadTableData()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  loadTableData()
}

// 双击单元格打开编辑弹窗
const handleCellDbClick = (row, column) => {
  editingRow.value = row
  editingColumn.value = column
  editingValue.value = row[column]
  dialogVisible.value = true
}

// 保存编辑内容
const handleSave = async () => {
  try {
    await updateCell(tableId, {
      id: editingRow.value.id,
      column: editingColumn.value,
      value: editingValue.value
    })
    // 更新本地数据
    editingRow.value[editingColumn.value] = editingValue.value
    ElMessage.success('更新成功')
    dialogVisible.value = false
  } catch (error) {
    ElMessage.error('更新失败')
  }
}

// 返回主页
const goBack = () => {
  router.push('/')
}

onMounted(() => {
  loadTableData()
})
</script>

<template>
  <div class="table-view">
    <el-card>
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-button 
              type="primary" 
              plain 
              size="small"
              @click="goBack"
            >
              返回主页
            </el-button>
            <span class="title">表格数据</span>
          </div>
        </div>
      </template>
      
      <el-table
        v-loading="loading"
        :data="tableData"
        style="width: 100%"
        border
      >
        <el-table-column
          v-for="header in headers"
          :key="header"
          :prop="header"
          :label="header"
          :min-width="120"
          show-overflow-tooltip
        >
          <template #default="{ row }">
            <div 
              class="cell-content"
              @dblclick="handleCellDbClick(row, header)"
            >
              {{ row[header] }}
            </div>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[20, 50, 100, 200]"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="编辑内容"
      width="30%"
      :close-on-click-modal="false"
    >
      <el-input
        v-model="editingValue"
        type="textarea"
        :rows="4"
        placeholder="请输入内容"
      />
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSave">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.table-view {
  padding: 20px;
}
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
.cell-content {
  padding: 8px;
  cursor: pointer;
}
.cell-content:hover {
  background-color: var(--el-fill-color-light);
}
:deep(.el-table__cell) {
  padding: 0 !important;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}
.title {
  font-size: 16px;
  font-weight: 500;
}
</style> 