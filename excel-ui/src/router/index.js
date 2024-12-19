import { createRouter, createWebHistory } from 'vue-router'
import ExcelList from '../views/ExcelList.vue'
import TableView from '../views/TableView.vue'

const routes = [
  {
    path: '/',
    name: 'ExcelList',
    component: ExcelList
  },
  {
    path: '/table/:id',
    name: 'TableView',
    component: TableView
  }
]

export const router = createRouter({
  history: createWebHistory(),
  routes
}) 