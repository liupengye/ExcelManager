<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getTableData, updateCell } from '../api/excel'

const route = useRoute()
const tableId = route.params.id
const tableData = ref([])
const headers = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)

const loadTableData = async () => {
  try {
    loading.value = true
    const { data } = await getTableData(tableId, currentPage.value, pageSize.value)
    tableData.value = data
    if (data.length > 0) {
      headers.value = Object.keys(data[0]).filter(key => key !== 'id')
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const handleCellEdit = async (row, column) => {
  try {
    await updateCell(tableId, {
      id: row.id,
      column: column.property,
      value: row[column.property]
    })
    ElMessage.success('更新成功')
  } catch (error) {
    ElMessage.error('更新失败')
  }
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
          <span>表格数据</span>
        </div>
      </template>
      
      <el-table
        v-loading="loading"
        :data="tableData"
        style="width: 100%"
      >
        <el-table-column
          v-for="header in headers"
          :key="header"
          :prop="header"
          :label="header"
        >
          <template #default="{ row }">
            <el-input
              v-model="row[header]"
              @blur="handleCellEdit(row, { property: header })"
            />
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next"
          @size-change="loadTableData"
          @current-change="loadTableData"
        />
      </div>
    </el-card>
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
</style> 