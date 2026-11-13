import { ref } from 'vue'
import { getThumbnailUrls } from '@/api/extra/image_store.js'
/** 新增：每页清空旧签名；分批限制 200 ID，页码切换后丢弃旧请求响应。 */
export function useBusinessThumbnails() {
  const thumbnailUrls = ref({})
  let generation = 0
  async function refreshThumbnails(ids) {
    const current = ++generation
    thumbnailUrls.value = {}
    const unique = [...new Set(ids.filter(Boolean))]
    const next = {}
    for (let start = 0; start < unique.length; start += 200) {
      const response = await getThumbnailUrls(unique.slice(start, start + 200))
      if (response.code !== 200) throw new Error(response.msg || '缩略图加载失败')
      Object.assign(next, response.data || {})
    }
    if (current === generation) thumbnailUrls.value = next
  }
  return { thumbnailUrls, refreshThumbnails }
}
