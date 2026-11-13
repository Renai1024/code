<template>
  <!-- 历史列表实现：v-if="thumbnail || (!imageId && fallback)" :src="thumbnail || fallback"。
       修正：fallback 仅供用户点击后预览，列表绝不将原图 URL 放入 img.src。 -->
  <img v-if="thumbnail && !thumbnailFailed" :src="thumbnail" loading="lazy"
    :width="size" :height="size" style="object-fit:cover;cursor:pointer;border-radius:4px"
    @error="thumbnailFailed = true" @click="openOriginal" />
  <!-- 新增：缺少缩略图或缩略图加载失败时保留查看入口，不自动改为下载原图。 -->
  <button v-else-if="imageId || fallback" type="button" :style="{ width: `${size}px`, height: `${size}px` }"
    style="padding:0;border:1px solid #dcdfe6;border-radius:4px;background:#f5f7fa;color:#606266;cursor:pointer;font-size:12px;vertical-align:middle"
    aria-label="查看图片" @click="openOriginal">查看</button>
  <el-image-viewer v-if="previewUrl" :url-list="[previewUrl]" teleported @close="previewUrl = ''" />
</template>
<script setup>
import { ref, watch } from 'vue'
import { getImagePreviewUrl } from '@/api/extra/image_store.js'
const props = defineProps({ imageId: [Number, String], thumbnail: String, fallback: String, size: { type: Number, default: 40 } })
const previewUrl = ref('')
// 新增：签名刷新后可重新加载缩略图，失败期间也不请求原图。
const thumbnailFailed = ref(false)
watch(() => props.thumbnail, () => { thumbnailFailed.value = false })
const emit = defineEmits(['error'])
// 新增：每次点击重新签名，避免上次预览地址到期后继续复用。
async function openOriginal() {
  try {
    if (!props.imageId) { previewUrl.value = props.fallback; return }
    const response = await getImagePreviewUrl(props.imageId)
    if (response.code !== 200 || !response.data) throw new Error(response.msg || '图片预览失败')
    previewUrl.value = response.data
  } catch (error) { emit('error', error) }
}
</script>
