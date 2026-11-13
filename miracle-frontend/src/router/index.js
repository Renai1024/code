import { createWebHistory, createRouter } from 'vue-router'
/* Layout */
import Layout from '@/layout'
import {useDark} from "@vueuse/core";

/**
 * Note: 路由配置项
 *
 * hidden: true                     // 当设置 true 的时候该路由不会再侧边栏出现 如401，login等页面，或者如一些编辑页面/edit/1
 * alwaysShow: true                 // 当你一个路由下面的 children 声明的路由大于1个时，自动会变成嵌套的模式--如组件页面
 *                                  // 只有一个时，会将那个子路由当做根路由显示在侧边栏--如引导页面
 *                                  // 若你想不管路由下面的 children 声明的个数都显示你的根路由
 *                                  // 你可以设置 alwaysShow: true，这样它就会忽略之前定义的规则，一直显示根路由
 * redirect: noRedirect             // 当设置 noRedirect 的时候该路由在面包屑导航中不可被点击
 * name:'router-name'               // 设定路由的名字，一定要填写不然使用<keep-alive>时会出现各种问题
 * query: '{"id": 1, "name": "ry"}' // 访问路由的默认传递参数
 * roles: ['admin', 'common']       // 访问路由的角色权限
 * permissions: ['a:a:a', 'b:b:b']  // 访问路由的菜单权限
 * meta : {
    noCache: true                   // 如果设置为true，则不会被 <keep-alive> 缓存(默认 false)
    title: 'title'                  // 设置该路由在侧边栏和面包屑中展示的名字
    icon: 'svg-name'                // 设置该路由的图标，对应路径src/assets/icons/svg
    breadcrumb: false               // 如果设置为false，则不会在breadcrumb面包屑中显示
    activeMenu: '/system/user'      // 当路由设置了该属性，则会高亮相对应的侧边栏。
  }
 */

// 公共路由
export const constantRoutes = [
  {
    path: '/redirect',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '/redirect/:path(.*)',
        component: () => import('@/views/redirect/index.vue')
      }
    ]
  },
  {
    path: '/login',
    component: () => import('@/views/login'),
    hidden: true
  },
  {
    path: '/register',
    component: () => import('@/views/register'),
    hidden: true
  },
  {
    path: "/:pathMatch(.*)*",
    component: () => import('@/views/error/404'),
    hidden: true
  },
  {
    path: '/401',
    component: () => import('@/views/error/401'),
    hidden: true
  },
  {
    path: '',
    component: Layout,
    redirect: '/index',
    children: [
      {
        path: '/index',
        component: () => import('@/views/index'),
        name: 'Index',
        meta: { title: '首页', icon: 'dashboard', affix: true }
      }
    ]
  },
  {
    path: '/user',
    component: Layout,
    hidden: true,
    redirect: 'noredirect',
    children: [
      {
        path: 'profile/:activeTab?',
        component: () => import('@/views/system/user/profile/index'),
        name: 'Profile',
        meta: { title: '个人中心', icon: 'user' }
      }
    ]
  },
  {
    path: '/report/mes/product',
    component: () => import('@/views/extra/mes_report_iqc/index.vue'),
    hidden: true,
    meta: {
      title: 'iqc生产数据报表',
      requireLight: true
    }
  },
  {
    path: '/report/mes/inspection_record',
    component: () => import('@/views/extra/mes_inspection_record/report.vue'),
    hidden: true,
    meta: {
      title: '检验记录报表',
      requireLight: true
    }
  },
  {
    path: '/report/mes/torque',
    component: () => import('@/views/extra/torque/index.vue'),
    hidden: true,
    meta: {
      title: '碳化硅自动锁螺丝数据报表',
      requireLight: true
    }
  },
  {
    path: '/report/mes/weld_record',
    component: () => import('@/views/extra/weld/index.vue'),
    hidden: true,
    meta: {
      title: '焊接数据报表',
      requireLight: true
    }
  },
  {
    path: '/report/mes/byd',
    component: () => import('@/views/extra/mes_report_byd/index.vue'),
    hidden: true,
    meta: {
      title: '比亚迪数据报表',
      requireLight: true
    }
  },
  {
    path: '/report/mes/qimixing',
    component: () => import('@/views/extra/mes_qimixing_record/index.vue'),
    hidden: true,
    meta: {
      title: '气密性测试数据报表',
      requireLight: true
    }
  },
  {
    path: '/report/mes/duanziyajieji',
    component: () => import('@/views/extra/mes_report_duanziyajieji/index.vue'),
    hidden: true,
    meta: {
      title: '超声波焊接机(小)数据报表',
      requireLight: true
    }
  },
  {
    path: '/report/mes/chaoshengbohanjieji',
    component: () => import('@/views/extra/mes_report_chaoshengbohanjieji/index.vue'),
    hidden: true,
    meta: {
      title: '超声波焊接机(大)数据报表',
      requireLight: true
    }
  },
  {
    path: '/report/mes/transpose',
    component: () => import('@/views/extra/mes_workshop_transpose/report.vue'),
    hidden: true,
    meta: {
      title: '车间转序数据报表',
      requireLight: true
    }
  },
  {
    path: '/report/mes/lwsQiMiXing',
    component: () => import('@/views/extra/mes_report_lwsQiMiXing/index.vue'),
    hidden: true,
    meta: {
      title: '立维斯气密性数据报表',
      requireLight: true
    }
  },
  {
    path: '/report/mes/zcDaoTongGaoYaCeShi',
    component: () => import('@/views/extra/mes_report_zcDaoTongGaoYaCeShi/index.vue'),
    hidden: true,
    meta: {
      title: '中测导通高压测试数据报表',
      requireLight: true
    }
  },
  {
    path: '/report/mes/duanziyajie',
    component: () => import('@/views/extra/mes_report_duanziyajie/index.vue'),
    hidden: true,
    meta: {
      title: '端子压接机数据报表',
      requireLight: true
    }
  },
  {
    path: '/report/mes/diancexian',
    component: () => import('@/views/extra/mes_report_diancexian/index.vue'),
    hidden: true,
    meta: {
      title: '电测线数据报表',
      requireLight: true
    }
  },
  {
    path: '/report/mes/tsdzhinengdianpi',
    component: () => import('@/views/extra/mes_report_tsdZhiNengDianPi/index.vue'),
    hidden: true,
    meta: {
      title: 'TSD智能电批数据报表',
      requireLight: true
    }
  },
  {
    path: '/report/mes/accheckh/xmfl',
    component: () => import('@/views/extra/mes_report_xmfl_accheckH/index.vue'),
    hidden: true,
    meta: {
      title: '法拉耐压测试数据报表',
      requireLight: true
    }
  },
  {
    path: '/report/mes/accheckh/wkwuma',
    component: () => import('@/views/extra/mes_report_wkwuma_accheckH/index.vue'),
    hidden: true,
    meta: {
      title: '万肯(无码)耐压测试数据报表',
      requireLight: true
    }
  },
  {
    path: '/report/mes/accheckh/wkyouma',
    component: () => import('@/views/extra/mes_report_wkyouma_accheckH/index.vue'),
    hidden: true,
    meta: {
      title: '万肯(有码)耐压测试数据报表',
      requireLight: true
    }
  },
  {
    path: '/report/mes/accheckh/wkcheckprint',
    component: () => import('@/views/extra/mes_report_wk_checkprint/index.vue'),
    hidden: true,
    meta: {
      title: '万肯气密性测试数据报表',
      requireLight: true
    }
  },
  {
    path: '/report/mes/accheckh/wkcheckprint/cpk',
    component: () => import('@/views/extra/mes_report_wk_checkprint/cpk.vue'),
    hidden: true,
    meta: {
      title: '万肯气密性CPK分析',
      requireLight: true
    }
  },
  {
    path: '/report/mes/accheckh/ddh',
    component: () => import('@/views/extra/mes_report_ddh_accheckH/index.vue'),
    hidden: true,
    meta: {
      title: '东电化耐压测试数据报表',
      requireLight: true
    }
  },
  {
    path: '/report/mes/dzduanziyajieji',
    component: () => import('@/views/extra/mes_report_dzduanziyajieji/index.vue'),
    hidden: true,
    meta: {
      title: '典帧端子压接机数据报表',
      requireLight: true
    }
  },
  {
    path: '/report/mes/dianjiaoji',
    component: () => import('@/views/extra/mes_report_dianjiaoji/index.vue'),
    hidden: true,
    meta: {
      title: '点胶机数据报表',
      requireLight: true
    }
  },
  {
    path: '/report/mes/xscjLabelCheck',
    component: () => import('@/views/extra/mes_report_xscjLabelCheck/index.vue'),
    hidden: true,
    meta: {
      title: '线束车间外观检测条码录入数据报表',
      requireLight: true
    }
  },
  {
    path: '/report/mes/breakovercheckTH8601',
    component: () => import('@/views/extra/mes_report_breakoverCheckTH8601/index.vue'),
    hidden: true,
    meta: {
      title: '精密综合测试仪TH8601数据采集报表',
      requireLight: true
    }
  },
  {
    path: '/report/mes/breakovercheckTH9110',
    component: () => import('@/views/extra/mes_report_breakoverCheckTH9110/index.vue'),
    hidden: true,
    meta: {
      title: '精密综合测试仪TH9110数据采集报表',
      requireLight: true
    }
  },
  {
    path: '/report/mes/accheckh/afn',
    component: () => import('@/views/extra/mes_report_afn_accheckh/index.vue'),
    hidden: true,
    meta: {
      title: '安费诺耐压测试报表',
      requireLight: true
    }
  },
  {
    path: '/report/mes/accheckh/tkdr',
    component: () => import('@/views/extra/mes_report_tkdr_accheckh/index.vue'),
    hidden: true,
    meta: {
      title: '泰科电阻测试报表',
      requireLight: true
    }
  },
  {
    path: '/report/mes/shebeizu',
    component: () => import('@/views/extra/mes_report_shebeizu/index.vue'),
    hidden: true,
    meta: {
      title: '数据导通测试报表',
      requireLight: true
    }
  },
  {
    path: '/report/mes/hsnycs',
    component: () => import('@/views/extra/mes_report_hsnycs/index.vue'),
    hidden: true,
    meta: {
      title: '海思耐压测试报表',
      requireLight: true
    }
  },
  /* 历史独立报表路由保留，当前使用菜单动态路由和组件 extra/tyco_product_scan/report。
  {
    path: '/report/mes/tyco_product_scan',
    component: () => import('@/views/extra/tyco_product_scan/report.vue'),
    hidden: true,
    meta: { title: '泰科装箱追溯报表', requireLight: true }
  },
  */
  {
    // 独立免登录展示入口，系统菜单仍可加载同一报表组件。
    path: '/report/mes/tyco_product_scan',
    component: () => import('@/views/extra/tyco_product_scan/report.vue'),
    hidden: true,
    meta: { title: '泰科装箱追溯报表', requireLight: true }
  },
  {
    path: '/report/mes/outer_box_bind',
    component: () => import('@/views/extra/mes_outer_box_bind/index.vue'),
    hidden: true,
    meta: {
      title: '产品出货扫码绑定',
      requireLight: true
    }
  },
  {
    path: '/report/mes/outer_box_bind/list',
    component: () => import('@/views/extra/mes_outer_box_bind/list.vue'),
    hidden: true,
    meta: {
      title: '产品出货扫码绑定记录',
      requireLight: true
    }
  },
  {
    path: '/report/mes/barcode_bind',
    component: () => import('@/views/extra/barcode_bind/index.vue'),
    hidden: true,
    meta: {
      title: '双码绑定',
      requireLight: true
    }
  },
  {
    path: '/report/mes/barcode_bind/list',
    component: () => import('@/views/extra/barcode_bind/list.vue'),
    hidden: true,
    meta: {
      title: '双码绑定记录',
      requireLight: true
    }
  },
  {
    path: '/report/mes/customer_delivery_label_bind',
    component: () => import('@/views/extra/customer_delivery_label_bind/index.vue'),
    hidden: true,
    meta: {
      title: '客户出货标签绑定',
      requireLight: true
    }
  },
  {
    path: '/report/mes/customer_delivery_label_bind/list',
    component: () => import('@/views/extra/customer_delivery_label_bind/list.vue'),
    hidden: true,
    meta: {
      title: '客户出货标签绑定记录',
      requireLight: true
    }
  },
  {
    path: '/report/mes/customer_outer_box_rule',
    component: () => import('@/views/extra/customer_outer_box_rule/index.vue'),
    hidden: true,
    meta: {
      title: '客户外箱标签解析规则',
      requireLight: true
    }
  },
  {
    path: '/report/mes/chuXinNiuZhuanHan',
    component: () => import('@/views/extra/mes_report_chuXinNiuZhuanHan/index.vue'),
    hidden: true,
    meta: {
      title: '楚鑫扭转焊数据报表',
      requireLight: true
    }
  },
  {
    path: '/report/mes/chuXinDuanZiHan',
    component: () => import('@/views/extra/mes_report_chuXinDuanZiHan/index.vue'),
    hidden: true,
    meta: {
      title: '楚鑫端子焊接机数据报表',
      requireLight: true
    }
  },
  {
    path: '/report/mes/chuXinChaoShengBoHan',
    component: () => import('@/views/extra/mes_report_chuXinChaoShengBoHan/index.vue'),
    hidden: true,
    meta: {
      title: '楚鑫超声波焊接机数据报表',
      requireLight: true
    }
  },
  {
    path: '/report/mes/hengHeHongKaoXiang',
    component: () => import('@/views/extra/mes_report_hengHeHongKaoXiang/index.vue'),
    hidden: true,
    meta: {
      title: '横河烘烤箱报表',
      requireLight: true
    }
  },
  {
    path: '/report/mes/zhongCeChargeMode',
    component: () => import('@/views/extra/mes_report_zhongCeChargeMode/index.vue'),
    hidden: true,
    meta: {
      title: '中测充电模式二测试机报表',
      requireLight: true
    }
  },
  {
    path: '/report/mes/zhongCeLaoHuaRecord',
    component: () => import('@/views/extra/mes_report_zhongCeLaoHuaRecord/index.vue'),
    hidden: true,
    meta: {
      title: '中测老化测试记录报表',
      requireLight: true
    }
  },
  {
    path: '/report/mes/dengLiZiQingXi',
    component: () => import('@/views/extra/mes_report_dengLiZiQingXi/index.vue'),
    hidden: true,
    meta: {
      title: '等离子清洗机报表',
      requireLight: true
    }
  },
  {
    path: '/report/mes/zhanShengMangChaJi',
    component: () => import('@/views/extra/mes_report_zhanShengMangChaJi/index.vue'),
    hidden: true,
    meta: {
      title: '展昇盲插机报表',
      requireLight: true
    }
  },
  {
    path: '/report/mes/byd_device_operation_record',
    component: () => import('@/views/extra/mes_report_byd_device_operation_record/index.vue'),
    hidden: true,
    meta: {
      title: '比亚迪设备操作记录报表',
      requireLight: true
    }
  },
  {
    path: '/report/mes/aidc_device_operation_record',
    component: () => import('@/views/extra/mes_report_aidc_device_operation_record/index.vue'),
    hidden: true,
    meta: {
      title: '设备操作记录报表(AIDC)',
      requireLight: true
    }
  },
  {
    path: '/report/mes/liErLiChaBaLi',
    component: () => import('@/views/extra/mes_report_liErLiChaBaLi/index.vue'),
    hidden: true,
    meta: {
      title: '立而利插拔力报表',
      requireLight: true
    }
  },
  {
    path: '/report/mes/api_data_record',
    component: () => import('@/views/extra/mes_report_api_data_record/index.vue'),
    hidden: true,
    meta: {
      title: 'MesApi数据记录报表',
      requireLight: true
    }
  },
  {
    path: '/report/mes/xm_device_operation_record',
    component: () => import('@/views/extra/mes_report_xm_device_operation_record/index.vue'),
    hidden: true,
    meta: {
      title: 'XM设备操作记录报表',
      requireLight: true
    }
  },
  {
    path: '/report/mes/press_stripping_integrated_machine',
    component: () => import('@/views/extra/mes_report_pressStrippingIntegratedMachine/index.vue'),
    hidden: true,
    meta: {
      title: '压剥一体机报表',
      requireLight: true
    }
  }
]

// 动态路由，基于用户权限动态去加载
export const dynamicRoutes = [
  {
    path: '/system/user-auth',
    component: Layout,
    hidden: true,
    permissions: ['system:user:edit'],
    children: [
      {
        path: 'role/:userId(\\d+)',
        component: () => import('@/views/system/user/authRole'),
        name: 'AuthRole',
        meta: { title: '分配角色', activeMenu: '/system/user' }
      }
    ]
  },
  {
    path: '/system/role-auth',
    component: Layout,
    hidden: true,
    permissions: ['system:role:edit'],
    children: [
      {
        path: 'user/:roleId(\\d+)',
        component: () => import('@/views/system/role/authUser'),
        name: 'AuthUser',
        meta: { title: '分配用户', activeMenu: '/system/role' }
      }
    ]
  },
  {
    path: '/system/dict-data',
    component: Layout,
    hidden: true,
    permissions: ['system:dict:list'],
    children: [
      {
        path: 'index/:dictId(\\d+)',
        component: () => import('@/views/system/dict/data'),
        name: 'Data',
        meta: { title: '字典数据', activeMenu: '/system/dict' }
      }
    ]
  },
  {
    path: '/monitor/job-log',
    component: Layout,
    hidden: true,
    permissions: ['monitor:job:list'],
    children: [
      {
        path: 'index/:jobId(\\d+)',
        component: () => import('@/views/monitor/job/log'),
        name: 'JobLog',
        meta: { title: '调度日志', activeMenu: '/monitor/job' }
      }
    ]
  },
  {
    path: '/tool/gen-edit',
    component: Layout,
    hidden: true,
    permissions: ['tool:gen:edit'],
    children: [
      {
        path: 'index/:tableId(\\d+)',
        component: () => import('@/views/tool/gen/editTable'),
        name: 'GenEdit',
        meta: { title: '修改生成配置', activeMenu: '/tool/gen' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes: constantRoutes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    }
    return { top: 0 }
  },
})

// export function createThemeGuard(router) {
//   const isDark = useDark()
//
//   router.beforeEach((to, from, next) => {
//     // 根据路由元信息设置主题
//     if (to.meta.requireLight) {
//       isDark.value = false
//     } else {
//       // 如果没有特别指定，使用默认暗黑模式
//       isDark.value = true
//     }
//     next()
//   })
// }

export default router
