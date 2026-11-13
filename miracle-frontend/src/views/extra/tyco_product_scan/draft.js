/** 新增：同步保存到当前浏览器/账号的持久存储，不能依赖beforeunload才写入。 */
export const TTL = 24 * 60 * 60 * 1000
export const draftKey = userId => `tyco-scan:v1:${userId}`
export function readDraft(storage, key, now = Date.now()) {
  const raw = storage.getItem(key)
  if (!raw) return null
  try {
    const saved = JSON.parse(raw)
    if (saved.version !== 1 || !Number.isFinite(saved.expiresAt) || saved.expiresAt <= now || saved.expiresAt > now + TTL + 1000 ||
        !saved.data || typeof saved.data.requestId !== 'string' || typeof saved.data.boxInput !== 'string' ||
        typeof saved.data.productInput !== 'string' || typeof saved.data.box !== 'string' ||
        !Number.isInteger(saved.data.quantity) || saved.data.quantity < 0 || saved.data.quantity > 5000 ||
        typeof saved.data.uncertain !== 'boolean' || !Array.isArray(saved.data.products) ||
        saved.data.products.length > 5000 || saved.data.products.some(p => typeof p !== 'string' || p.length > 256)) throw Error('invalid')
    return saved
  } catch { storage.removeItem(key); return null }
}
export function writeDraft(storage, key, data, now = Date.now()) {
  // 新增：只有真实输入/扫码/提交动作延长有效期，打开页面和定时检查不续期。
  const saved = { version: 1, expiresAt: now + TTL, data: JSON.parse(JSON.stringify(data)) }
  storage.setItem(key, JSON.stringify(saved))
  return saved.expiresAt
}
