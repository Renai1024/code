
import { useDark } from '@vueuse/core'
import { useRoute } from 'vue-router'

export function useRouteTheme() {
    const route = useRoute()
    const isDark = useDark()

    // 需要亮色模式的路由配置
    const lightModeRoutes = [
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
        '/report/mes/QiMiXing',
        '/report/mes/zcDaoTongGaoYaCeShi',
        '/report/mes/transpose',
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
        '/report/mes/mes_outer_box_bind/list',
        '/report/mes/chuXinNiuZhuanHan',
        '/report/mes/chuXinDuanZiHan',
        '/report/mes/chuXinChaoShengBoHan',
        '/report/mes/henghehongkaoxiang',
        '/report/mes/zhongCeChargeMode',
        '/report/mes/zhongCeLaoHuaRecord',
        '/report/mes/dengLiZiQingXi',
        '/report/mes/zhanShengMangChaJi',
        '/report/mes/byd_device_operation_record',
        '/report/mes/aidc_device_operation_record',
        '/report/mes/liErLiChaBaLi',
        '/report/mes/api_data_record',
        '/report/mes/barcode_bind',
        '/report/mes/barcode_bind/list',
        '/report/mes/xm_device_operation_record',
        '/report/mes/customer_delivery_label_bind',
        '/report/mes/press_stripping_integrated_machine'
    ]

    const setThemeByRoute = (path = null) => {
        const currentPath = path || route.path
        const shouldUseLight = lightModeRoutes.some(route =>
            currentPath.startsWith(route)
        )

        isDark.value = !shouldUseLight
    }

    // 监听路由变化
    watch(() => route.path, (newPath) => {
        setThemeByRoute(newPath)
    }, { immediate: true })

    return {
        setThemeByRoute
    }
}
