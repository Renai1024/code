import router from './router'
import { ElMessage } from 'element-plus'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { getToken } from '@/utils/auth'
import { isHttp, isPathMatch } from '@/utils/validate'
import { isRelogin } from '@/utils/request'
import useUserStore from '@/store/modules/user'
import useSettingsStore from '@/store/modules/settings'
import usePermissionStore from '@/store/modules/permission'

NProgress.configure({ showSpinner: false })

const whiteList = ['/login', '/register',
                            '/report/mes/product',
                            '/report/mes/inspection_record',
                            '/report/mes/torque',
                            '/report/mes/weld_record',
                            '/report/mes/byd',
                            '/report/mes/qimixing',
                            '/report/mes/duanziyajieji',
                            '/report/mes/duanziyajie',
                            '/report/mes/diancexian',
                            '/report/mes/tsdzhinengdianpi',
                            '/report/mes/chaoshengbohanjieji',
                            '/report/mes/lwsQiMiXing',
                            '/report/mes/zcDaoTongGaoYaCeShi',
                            '/report/mes/transpose',
                            // 与车间转序报表一样，独立展示地址无需登录。
                            '/report/mes/tyco_product_scan',
                            '/report/mes/accheckh/xmfl',
                            '/report/mes/accheckh/wkwuma',
                            '/report/mes/accheckh/wkyouma',
                            '/report/mes/accheckh/wkcheckprint',
                            '/report/mes/accheckh/wkcheckprint/cpk',
                            '/report/mes/accheckh/ddh',
                            '/report/mes/dzduanziyajieji',
                            '/report/mes/dianjiaoji',
                            '/report/mes/xscjLabelCheck',
                            '/report/mes/breakovercheckTH8601',
                            '/report/mes/breakovercheckTH9110',
                            '/report/mes/accheckh/afn',
                            '/report/mes/accheckh/tkdr',
                            '/report/mes/shebeizu',
                            '/report/mes/hsnycs',
                            '/report/mes/outer_box_bind/list',
                            '/report/mes/chuXinNiuZhuanHan',
                            '/report/mes/chuXinDuanZiHan',
                            '/report/mes/chuXinChaoShengBoHan',
                            '/report/mes/hengHeHongKaoXiang',
                            '/report/mes/zhongCeChargeMode',
                            '/report/mes/zhongCeLaoHuaRecord',
                            '/report/mes/dengLiZiQingXi',
                            '/report/mes/zhanShengMangChaJi',
                            '/report/mes/byd_device_operation_record',
                            '/report/mes/aidc_device_operation_record',
                            '/report/mes/liErLiChaBaLi',
                            '/report/mes/xm_device_operation_record',
                            '/report/mes/press_stripping_integrated_machine'
                          ]

const isWhiteList = (path) => {
  return whiteList.some(pattern => isPathMatch(pattern, path))
}

router.beforeEach((to, from, next) => {
  NProgress.start()
  if (getToken()) {
    to.meta.title && useSettingsStore().setTitle(to.meta.title)
    /* has token*/
    if (to.path === '/login') {
      next({ path: '/' })
      NProgress.done()
    } else if (isWhiteList(to.path)) {
      next()
    } else {
      if (useUserStore().roles.length === 0) {
        isRelogin.show = true
        // 判断当前用户是否已拉取完user_info信息
        useUserStore().getInfo().then(() => {
          isRelogin.show = false
          usePermissionStore().generateRoutes().then(accessRoutes => {
            // 根据roles权限生成可访问的路由表
            accessRoutes.forEach(route => {
              if (!isHttp(route.path)) {
                router.addRoute(route) // 动态添加可访问路由表
              }
            })
            next({ ...to, replace: true }) // hack方法 确保addRoutes已完成
          })
        }).catch(err => {
          useUserStore().logOut().then(() => {
            ElMessage.error(err)
            next({ path: '/' })
          })
        })
      } else {
        next()
      }
    }
  } else {
    console.log(isWhiteList);
    // 没有token
    if (isWhiteList(to.path)) {
      // 在免登录白名单，直接进入
      next()
    } else {
      next(`/login?redirect=${to.fullPath}`) // 否则全部重定向到登录页
      NProgress.done()
    }
  }
})

router.afterEach(() => {
  NProgress.done()
})
